package com.diaodu.core;

import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import com.diaodu.domain.Task;
import com.diaodu.service.BatchService;
import com.diaodu.service.HistoryService;
import com.diaodu.service.TaskService;
import com.diaodu.ssh.SshUtil;
import com.google.common.base.Strings;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 定时任务,每隔一段时间扫描任务的完成状态,根据时间点和运行状态,执行批次任务
 * 处于service之上
 *
 * @author GP39
 *         2016-6-15 09:49:20
 *         update 2017-2-9 16:19:16 添加cron 支持, 不同用户的支持
 *
 */
public class SchedulerTask implements Job {


    final ConcurrentHashMap<Integer, Task> failMap = new ConcurrentHashMap<>();

    String batch_flag = "0";

    private synchronized void changeFlag(String result) {
        batch_flag = result;
    }


    Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UUID uuid = UUID.randomUUID();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String full_day = sdf.format(new Date());
        long schedule_start_time = System.currentTimeMillis();

        Map<Integer, List<Batch>> map = GlobalMap.getMap();
        Iterator<Entry<Integer, List<Batch>>> iterator = map.entrySet().iterator();
        int size = map.entrySet().size();
        log.debug("have " + size + " jobs");
        //作为标志跳出双重循环
        Boolean hasRun=false;
        while (iterator.hasNext() && !hasRun) {
            Entry<Integer, List<Batch>> next = iterator.next();
            List<Batch> batchs = next.getValue();
            for (int i = 0; i < batchs.size(); i++) {
                Batch batch = batchs.get(i);
                if (batch.getFlag() == Constants.BATCH_INVAIN) {
                    //批次失效,跳过后续任务
                    log.debug("batch invain :" + batch.getId());
                    break;
                } else if (batch.getFlag() == Constants.BATCH_FREE) {
                    //任务成功运行
                    //如果有前置任务查看前置任务有没有完成
                    boolean shouldRun = false;
                    //前置任务不是已经完成状态,如果前置任务执行完成,按照前置任务的执行时间执行task
                    //如果前置batch没完成,直接跳过
                    if (i != 0) {
                        Batch preBatch = batchs.get(i - 1);
                        String pre_next = preBatch.getNextexecutetime();
                        String now_next = batch.getNextexecutetime();
                        //后批次的下次执行应该不大于上批次的下次执行时间
                        if (now_next.compareTo(pre_next) < 0) {
                            log.debug("pre-batch :" + preBatch + " run finished run next batch :" + batch);
                            if (shouldRun(batch)) {
                                shouldRun = true;
                            }
                        } else {
                            log.debug("pre-batch :" + preBatch + " run finished me too");
                            shouldRun = false;
                        }
                    } else {
                        shouldRun = shouldRun(batch);
                    }
                    if (shouldRun) {
                        log.info(uuid.toString() + " : task start at: " + full_day);
                        log.info(" 位于时间窗口-" + batch);
                        runBatch(batch,uuid.toString());
                        hasRun= true;
                        //阻塞进程等待运行完成,跳过后续的batch
                        break;
                    } else {
//                        log.info(full_day+ " skip !");
                        log.debug("前置任务未完成,或者未到达执行时间:" + batch);
                    }

                } else if (batch.getFlag() == Constants.BATCH_RUNNING) {
                    //任务任务正在运行,跳过后续的任务
                    log.debug("任务正在运行");
                    break;
                } else if (batch.getFlag() == Constants.BATCH_FAIL) {
                    //任务运行失败,跳过执行下一个批次的任务
                    log.debug("任务运行失败");
                    break;
                }
            }
        }
        long schedule_end_time = System.currentTimeMillis();
        //减少日志冗余
        long cost = schedule_end_time-schedule_start_time;

        if((schedule_end_time-schedule_start_time)>1000){
            log.info(uuid.toString() + ":(" + schedule_end_time + "-" + schedule_start_time + ")=" + cost + "ms");
        }else{
            log.info("skip in "+ cost + " ms");
        }


    }

    //执行批次内的任务
    //2016-12-26 14:50:27 添加UUID 验证没有出现并发问题
    private void runBatch(Batch batch,String uuid) {


        BatchService batchService = new BatchService();
        TaskService taskService = new TaskService();
        //1.修改运行标志为正在运行
        batch.setFlag(Constants.BATCH_RUNNING);
        batchService.updateBatch(batch);
        //1.1 修改task为待运行状态
        Integer batchId = batch.getId();
        //将批次的任务全部设置为待运行状态
        taskService.updateTaskFlagByBatchID(Constants.BATCH_TO_RUN, batchId);
        //根据批次ID ,获取该批次下所有的task
        List<Task> taskListByBatchID = taskService.getTaskListByBatchID(batchId);
        if (taskListByBatchID.isEmpty()) {
            log.info("batch has no task !!!");
            return;
        }
        //等到批次下面所有的任务seq
        List<Integer> seqList = new LinkedList<>();
        //2.分层级进行批量的任务运行,获取单独的seq 队列,由于在查询batch的时候
        for (Task task : taskListByBatchID) {
            int seq = task.getSeq();
            if (!seqList.contains(seq)) {
                seqList.add(seq);
            }
        }
        //2.1 创建线程池<根据批次来决定线程池的最大容量>
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(batch.getThreadcount());
        //分seq,依次执行该seq下的task
        for (int i = 0; i < seqList.size(); i++) {
            //得到 该seq下的task 根据ID 和 具体信息组成的 map
            ConcurrentHashMap<Integer, Task> shouldRunMap = new ConcurrentHashMap<>();
            //当前批次的seq
            Integer seq = seqList.get(i);
            System.out.println("seq ===============:" + seq);
            for (int j = 0; j < taskListByBatchID.size(); j++) {
                Task tempTask = taskListByBatchID.get(j);
                if (tempTask.getSeq() == seq) {
                    shouldRunMap.put(tempTask.getId(), tempTask);
                }
            }
            //运行当前批次,并且获取重试列表
            addTaskList2PoolAndWaitForFinished(fixedThreadPool, shouldRunMap,uuid);
            try {
                Thread.sleep(1000 * 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //不断重试,需要重新运行的批次<重复利用线程池>
            printMap(failMap);
            while (!failMap.isEmpty()) {
                System.out.println("失败队列长度不为零,重新运行==========");
                addTaskList2PoolAndWaitForFinished(fixedThreadPool, failMap,uuid);
            }
            //如果序列中有任务失败,跳出整个循环
            if (!batch_flag.equals("0")) {
                break;
            } else {
                log.info(uuid+ "运行完成batchid:" + batch.getId() + " seq: " + seq);
            }

        }
        //关闭线程池
        fixedThreadPool.shutdown();

        //后续batch处理
        //修改下一次运行的时间
        if (batch_flag.equals("0")) {
            String btype = batch.getBtype();
            String nextexecutetime = batch.getNextexecutetime();
            Date next = null;
            try {
                next = new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //获取cron表达式
            String cronexpression = batch.getCronexpression();
            CronTrigger trigger= new CronTrigger();
            //设置cron的 起点时间
		    trigger.setStartTime(next);
            try {
                trigger.setCronExpression(cronexpression);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            BaseCalendar baseCalendar = new BaseCalendar();
            //根据起点时间设置下次运行的时间
            List list = TriggerUtils.computeFireTimes(trigger, null, 2);
            nextexecutetime = new SimpleDateFormat("yyyyMMddHHmm").format(list.get(1));
            batch.setNextexecutetime(nextexecutetime);

//                long next_time = next.getTime() + batch.getTimeinterval() * 60 * 1000;
//                Date next_date = new Date(next_time);
//                nextexecutetime = new SimpleDateFormat("yyyyMMddHHmm").format(next_date);
//                batch.setNextexecutetime(nextexecutetime);

            log.info("batch :" + batch + " run successfully !!!");
            //更新batch信息,设置下次运行的时间
            batch.setFlag(Constants.BATCH_FREE);
            batchService.updateBatch(batch);
        } else {
            batch.setFlag(Constants.BATCH_FAIL);
            batchService.updateBatch(batch);
        }
        //修改运行标志为完成状态或者失败状态,并且制定下次的运行时间

    }

    private void printFailList(List<Task> failTasks) {
        for (int i = 0; i < failTasks.size(); i++) {
            System.out.println(failTasks.get(i));
        }
    }

    //线程添加到池子中,并且等待运行完成后销毁
    //2016-8-17 17:43:21
    private ConcurrentHashMap<Integer, Task> addTaskList2PoolAndWaitForFinished(ExecutorService pool, ConcurrentHashMap<Integer, Task> shouldRunMap,String uuid) {

        Set<Integer> keySet = shouldRunMap.keySet();
        Iterator<Integer> keyIterator = keySet.iterator();
        while (keyIterator.hasNext()) {
            final Task task = shouldRunMap.get(keyIterator.next());
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    //如果任务状态不是成功 不是需要重试的状态,直接跳过
                    if (!(task.getFlag() == Constants.BATCH_FREE || task.getFlag() == Constants.BATCH_TO_RUN)) {
                        log.info(task.getId() + "非可运行状态");
                        return;
                    } else {
                        if (task.getTimes() != 0) {
                            log.info(task.getId() + "不是第一次运行 设置为:在重试中");
                            task.setFlag(Constants.BATCH_RETRYING);
                        } else {
                            log.info(task.getId() + "第一次运行  设置为:在运行中");
                            task.setFlag(Constants.BATCH_RUNNING);
                        }
                        TaskService taskService = new TaskService();
                        taskService.updateTask(task);
                        String true_command = taskService.getTrueCommand(task);
                        Map<String, String> resultMap = null;
                        String taskhost = task.getTaskhost();
//                        String username = CenterConfig.getValueByKey(CenterConfig.)
                        if(taskhost.equals(CenterConfig.getValueByKeyWithoutDecode(CenterConfig.LOCAL_IP))){
                            resultMap = SshUtil.exeLocal(true_command);
                        }else {
                            resultMap = SshUtil.exeRemote(taskhost,true_command);
                        }
                        resultMap.put("script", task.getCommand());
                        String task_result = resultMap.get(Constants.EXIT_CODE);
                        log.info("task_result is :" + task_result);
                        if (task_result.equals("0")) {
                            task.setFlag(Constants.BATCH_FREE);
                           log.info("运行成功:移除对应id" + task.getId());
                            failMap.remove(task.getId());
                        } else {
                            task.setFlag(Constants.BATCH_FAIL);
                            //失败次数+1
                            task.setTimes(task.getTimes() + 1);
                            //小于最大重试次数,需要重新执行
                            //大于最大重试次数,本批次的后续seq不再执行,batch失败
                            log.info("id is :" + task.getId() + " times is :" + task.getTimes() + " retry max is :" + task.getRetry());
                            if (task.getTimes() < task.getRetry()) {
                                log.info("任务第" + task.getTimes() + "次执行失败 ! 加入失败队列");
                                task.setFlag(Constants.BATCH_TO_RUN);
                                failMap.put(task.getId(), task);
                            } else {
                                //达到最大重试次数,从失败队列中移除
                                log.info("remove task with id :" + task.getId());
                                failMap.remove(task.getId());
                                changeFlag(Constants.BATCH_FAIL + "");
                            }
                        }
                        taskService.insertHistory(resultMap);
                        //只有日常调度才会更改平均运行时长
                        HistoryService historyService = new HistoryService();
                        int avarageTime = historyService.getAvarageTime(task.getCommand());
                        task.setAvaragetime(avarageTime);
                        taskService.updateTask(task);

                    }

                }
            });
        }

        ThreadPoolExecutor exec = (ThreadPoolExecutor) pool;
        int active = exec.getActiveCount();
//        System.out.println("=======================第一次加入pool 中"+active);
        //每一秒查看一次,如果是已经完成,错误重试或者是运行下一批次
        //诡异的问题,必须重复获取activeCount 齐磊发现的问题
        //Fixed time 2016-9-9 17:01:27
       for(int i=0 ;i<100;i++){
           active = exec.getActiveCount();
           if(active!=0){
               break;
           }else{
//               System.out.println("----------------"+i+"      sleep    "+1+"s");
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }


        while (active != 0) {
            try {
                Thread.sleep(1000);
                active = exec.getActiveCount();
                //System.out.println("active count :" + active);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return failMap;
    }

    private void printMap(ConcurrentHashMap<Integer, Task> failMap) {
        System.out.println("--------------------------------------------------------------------");
        Iterator<Entry<Integer, Task>> iterator = failMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Task> next = iterator.next();
            System.out.println(next.getKey() + "--------->" + next.getValue());
        }
    }


    //功能:不考虑其他依赖任务的情况仅仅凭下次执行时间判断是不是应该执行任务
    //2016-8-17 15:12:40
    //1.任务的下次执行日期早于当前日期
    boolean shouldRun(Batch batch) {
        boolean run = true;
        String nextexecutetime = batch.getNextexecutetime();
        Date next = null;
        Date now = new Date();
        try {
            next = new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (now.before(next)) {
            run = false;
        }
        return run;
    }


    public static void main(String[] args) throws ParseException {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Constants.MAX_THREAD_NUM);
        for (int i = 0; i < 1; i++) {
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    System.out.println(Thread.currentThread() + "start at :" + new Date());
                    SshUtil.exec("10.201.48.101", "bl", "bl", 22, " sudo -u hdfs  sh /home/bl/bin/a.sh 3");
                    System.out.println(Thread.currentThread() + "end at :" + new Date());
                }
            });
        }
        fixedThreadPool.shutdown();
        //未完成进程阻塞
        while (!fixedThreadPool.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("----------------------end-------------------");
    }

}
