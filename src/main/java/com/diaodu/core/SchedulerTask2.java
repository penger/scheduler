//package com.diaodu.core;
//
//import com.diaodu.domain.Batch;
//import com.diaodu.domain.Constants;
//import com.diaodu.domain.Task;
//import com.diaodu.service.BatchService;
//import com.diaodu.service.HistoryService;
//import com.diaodu.service.TaskService;
//import com.diaodu.ssh.SshUtil;
//import com.google.common.base.Strings;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.SQLException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.Map.Entry;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//
///**
// * 定时任务,每隔一段时间扫描任务的完成状态,根据时间点和运行状态,执行批次任务
// * 处于service之上
// *
// * @author GP39
// *         2016-6-15 09:49:20
// */
//public class SchedulerTask2 implements Job {
//
//
//    final ConcurrentHashMap<Integer, Task> failMap = new ConcurrentHashMap<>();
//
//    String batch_flag = "0";
//
//    private synchronized void changeFlag(String result) {
//        batch_flag = result;
//    }
//
//
//    Logger log = LoggerFactory.getLogger(getClass());
//
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        Random random = new Random();
//        int i = random.nextInt();
//        failMap.put(i,new Task());
//        printMap(failMap);
//    }
//
//    //执行批次内的任务
//    private void runBatch(Batch batch) {
//        BatchService batchService = new BatchService();
//        TaskService taskService = new TaskService();
//        //1.修改运行标志为正在运行
//        batch.setFlag(Constants.BATCH_RUNNING);
//        batchService.updateBatch(batch);
//        //1.1 修改task为待运行状态
//        Integer batchId = batch.getId();
//        taskService.updateTaskFlagByBatchID(Constants.BATCH_TO_RUN, batchId);
//
//
//        List<Task> taskListByBatchID = taskService.getTaskListByBatchID(batchId);
//        if (taskListByBatchID.isEmpty()) {
//            log.info("batch has no task !!!");
//            return;
//        }
//        List<Integer> seqList = new LinkedList<>();
//        //2.分层级进行批量的任务运行,获取单独的seq 队列,由于在查询batch的时候
//        for (Task task : taskListByBatchID) {
//            int seq = task.getSeq();
//            if (!seqList.contains(seq)) {
//                seqList.add(seq);
//            }
//        }
//        //2.1 创建线程池<根据批次来决定线程池的最大容量>
//        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(batch.getThreadcount());
//        for (int i = 0; i < seqList.size(); i++) {
//            ConcurrentHashMap<Integer, Task> shouldRunMap = new ConcurrentHashMap<>();
//            //当前批次的seq
//            Integer seq = seqList.get(i);
//            System.out.println("seq ===============:" + seq);
//            for (int j = 0; j < taskListByBatchID.size(); j++) {
//                Task tempTask = taskListByBatchID.get(j);
//                if (tempTask.getSeq() == seq) {
//                    shouldRunMap.put(tempTask.getId(), tempTask);
//                }
//            }
//            //运行当前批次,并且获取重试列表
//            addTaskList2PoolAndWaitForFinished(fixedThreadPool, shouldRunMap);
//            try {
//                Thread.sleep(1000 * 3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //不断重试,需要重新运行的批次<重复利用线程池>
//            while (!failMap.isEmpty()) {
//                System.out.println("失败队列长度不为零,重新运行==========");
//                addTaskList2PoolAndWaitForFinished(fixedThreadPool, failMap);
//            }
//            //如果序列中有任务失败,跳出整个循环
//            if (!batch_flag.equals("0")) {
//                break;
//            } else {
//                log.info("运行完成batchid:" + batch.getId() + " seq: " + seq);
//            }
//
//        }
//        //关闭线程池
//        fixedThreadPool.shutdown();
//
//        //后续batch处理
//        //修改下一次运行的时间
//        if (batch_flag.equals("0")) {
//            String btype = batch.getBtype();
//            String nextexecutetime = batch.getNextexecutetime();
//            Date next = null;
//            try {
//                next = new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            if (Strings.isNullOrEmpty(btype)) {
//                long next_time = next.getTime() + batch.getTimeinterval() * 60 * 1000;
//                Date next_date = new Date(next_time);
//                nextexecutetime = new SimpleDateFormat("yyyyMMddHHmm").format(next_date);
//                batch.setNextexecutetime(nextexecutetime);
//            } else {
//                try {
//                    next = new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime);
//                    Calendar calendar = Calendar.getInstance();
//                    //添加一个月
//                    calendar.setTime(next);
//                    calendar.add(Calendar.MONTH, 1);
//                    batch.setNextexecutetime(new SimpleDateFormat("yyyyMMddHHmm").format(calendar.getTime()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            log.info("batch :" + batch + " run successfully !!!");
//            //更新batch信息,设置下次运行的时间
//            batch.setFlag(Constants.BATCH_FREE);
//            batchService.updateBatch(batch);
//        } else {
//            batch.setFlag(Constants.BATCH_FAIL);
//            batchService.updateBatch(batch);
//        }
//        //修改运行标志为完成状态或者失败状态,并且制定下次的运行时间
//
//    }
//
//    private void printFailList(List<Task> failTasks) {
//        for (int i = 0; i < failTasks.size(); i++) {
//            System.out.println(failTasks.get(i));
//        }
//    }
//
//    //线程添加到池子中,并且等待运行完成后销毁
//    //2016-8-17 17:43:21
//    private ConcurrentHashMap<Integer, Task> addTaskList2PoolAndWaitForFinished(ExecutorService pool, ConcurrentHashMap<Integer, Task> shouldRunMap) {
//
//        Set<Integer> keySet = shouldRunMap.keySet();
//        Iterator<Integer> keyIterator = keySet.iterator();
//        while (keyIterator.hasNext()) {
//            final Task task = shouldRunMap.get(keyIterator.next());
//            pool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    //如果任务状态不是成功 不是需要重试的状态,直接跳过
//                    if (!(task.getFlag() == Constants.BATCH_FREE || task.getFlag() == Constants.BATCH_TO_RUN)) {
//                        log.info(task.getId() + "非可运行状态");
//                        return;
//                    } else {
//                        if (task.getTimes() != 0) {
//                            System.out.println(task.getId() + "不是第一次运行 设置为:在重试中");
//                            task.setFlag(Constants.BATCH_RETRYING);
//                        } else {
//                            System.out.println(task.getId() + "第一次运行  设置为:在运行中");
//                            task.setFlag(Constants.BATCH_RUNNING);
//                        }
//                        TaskService taskService = new TaskService();
//                        taskService.updateTask(task);
//                        String true_command = taskService.getTrueCommand(task);
//                        Map<String, String> resultMap = null;
//                        if (task.getTaskhost().equals("10.201.48.101")) {
//                            resultMap = SshUtil.execTest(true_command);
//                        } else {
//                            resultMap = SshUtil.execProduct(true_command);
//                        }
//                        resultMap.put("script", task.getCommand());
//                        String task_result = resultMap.get(Constants.EXIT_CODE);
//                        System.out.println("task_result is :" + task_result);
//                        if (task_result.equals("0")) {
//                            task.setFlag(Constants.BATCH_FREE);
//                            System.out.println("运行成功:移除对应id" + task.getId());
//                            failMap.remove(task.getId());
//                        } else {
//                            task.setFlag(Constants.BATCH_FAIL);
//                            //失败次数+1
//                            task.setTimes(task.getTimes() + 1);
//                            //小于最大重试次数,需要重新执行
//                            //大于最大重试次数,本批次的后续seq不再执行,batch失败
//                            System.out.println("id is :" + task.getId() + " times is :" + task.getTimes() + " retry max is :" + task.getRetry());
//                            if (task.getTimes() < task.getRetry()) {
//                                log.info("任务第" + task.getTimes() + "次执行失败 ! 加入失败队列");
//                                task.setFlag(Constants.BATCH_TO_RUN);
//                                failMap.put(task.getId(), task);
//                            } else {
//                                //达到最大重试次数,从失败队列中移除
//                                System.out.println("remove task with id :" + task.getId());
//                                failMap.remove(task.getId());
//                                changeFlag(Constants.BATCH_FAIL + "");
//                            }
//                        }
//                        taskService.insertHistory(resultMap);
//                        //只有日常调度才会更改平均运行时长
//                        HistoryService historyService = new HistoryService();
//                        int avarageTime = historyService.getAvarageTime(task.getCommand());
//                        task.setAvaragetime(avarageTime);
//                        taskService.updateTask(task);
//
//                    }
//
//                }
//            });
//        }
//
//
//        ThreadPoolExecutor exec = (ThreadPoolExecutor) pool;
//        int active = exec.getActiveCount();
//        //每一秒查看一次,如果是已经完成,错误重试或者是运行下一批次
//        while (active != 0) {
//            try {
//                Thread.sleep(1000);
//                active = exec.getActiveCount();
//                //System.out.println("活动线程数为:" + active);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return failMap;
//    }
//
//    private void printMap(ConcurrentHashMap<Integer, Task> failMap) {
//        System.out.println("--------------------------------------------------------------------");
//        Iterator<Entry<Integer, Task>> iterator = failMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Entry<Integer, Task> next = iterator.next();
//            System.out.println(next.getKey() + "--------->" + next.getValue());
//        }
//    }
//
//
//    //功能:不考虑其他依赖任务的情况仅仅凭下次执行时间判断是不是应该执行任务
//    //2016-8-17 15:12:40
//    //1.任务的下次执行日期早于当前日期
//    boolean shouldRun(Batch batch) {
//        boolean run = true;
//        String nextexecutetime = batch.getNextexecutetime();
//        Date next = null;
//        Date now = new Date();
//        try {
//            next = new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (now.before(next)) {
//            run = false;
//        }
//        return run;
//    }
//
//
//    public static void main(String[] args) throws ParseException {
//        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Constants.MAX_THREAD_NUM);
//        for (int i = 0; i < 1; i++) {
//            fixedThreadPool.execute(new Runnable() {
//
//                @Override
//                public void run() {
//                    System.out.println(Thread.currentThread() + "start at :" + new Date());
//                    SshUtil.exec("10.201.48.101", "bl", "bl", 22, " sudo -u hdfs  sh /home/bl/bin/a.sh 3");
//                    System.out.println(Thread.currentThread() + "end at :" + new Date());
//                }
//            });
//        }
//        fixedThreadPool.shutdown();
//        //未完成进程阻塞
//        while (!fixedThreadPool.isTerminated()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("----------------------end-------------------");
//    }
//
//}
