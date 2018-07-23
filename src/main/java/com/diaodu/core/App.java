package com.diaodu.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import com.diaodu.service.BatchService;
import com.google.common.base.Strings;

public class App {
	
	
	Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
//		System.out.println("201510121412".substring(0,8));
		
		
		App app = new App();
		//从当前日期开始执行
		app.checkAndRun("20160229");
	}

	//检查以及运行(只处理day1的任务)
	public void checkAndRun(String taskdate)  {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat full_sdf = new SimpleDateFormat("yyyyMMddHHmm");
		
		
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(taskdate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		calendar.set(Calendar.DATE, 1);
		String nextdate = sdf.format(calendar.getTime());
		
		try {
			BatchService batchService = new BatchService();
			List<Batch> allBatchs = batchService.getAllBatch();
		
			//空闲状态的batch
			Map<Integer,Batch> batchMap =new HashMap<Integer,Batch>();
			//如果任务处于空闲状态()
			for (Batch batch : allBatchs) {
				if(batch.getFlag()==Constants.BATCH_FREE){
					//当前时间大于批次的下次执行时间
					String nextexecutetime = batch.getNextexecutetime();
					if(Strings.isNullOrEmpty(nextexecutetime)){
						batchMap.put(batch.getId(), batch);
					}else{
						Date nextDate = full_sdf.parse(batch.getNextexecutetime());
						if(new Date().after(nextDate)){
							batchMap.put(batch.getId(), batch);
						}
					}
				}
			}
			Set<Entry<Integer, Batch>> entrySet = batchMap.entrySet();
			Iterator<Entry<Integer, Batch>> iterator = entrySet.iterator();
			while(iterator.hasNext()){
				Entry<Integer, Batch> entry = iterator.next();
				Batch batch = entry.getValue();
				Integer key = entry.getKey();
				int beforeid = batch.getBeforeid();
				if(batchMap.get(beforeid)==null){
					long innerStart = System.currentTimeMillis();
					//无前置任务
					batchService.executeBatch(key,taskdate);
					log.info("任务"+batch+"花费时间为:"+(System.currentTimeMillis()-innerStart)+"ms");
					//batch.setTaskdate(taskdate);
					//batch.setFlag(Constants.BATCH_FREE);
					//设置下次执行的时间
					//batch.setNextexecutetime(nextdate+batch.getStarttime());
					//batchService.updateBatch(batch);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**每分钟调用一次运行
	 * 如果下一次运行的日期小于当前的日期或者小于目标日期那么运行
	 * 如果当前任务正在运行,或者无效状态,或者有前置任务
	 */
	
	public void checkAndRun(){
		BatchService batchService = new BatchService();
		List<Batch> allBatchs = batchService.getAllBatch();
		//Map<Integer,Batch> batchMap =new HashMap<Integer,Batch>();
		Map<Integer,Batch> allBatchMap =new HashMap<Integer,Batch>();
		//全部的批次
		for (Batch batch : allBatchs) {
			allBatchMap.put(batch.getId(), batch);
		}
		//如果批次处于空闲状态
		for (Batch batch : allBatchs) {
			if(batch.getFlag()==Constants.BATCH_FREE){
				String nextexecutetime = batch.getNextexecutetime();
				String taskdate = batch.getTaskdate();
				String starttime = batch.getStarttime();
				//下次运行的时间 大于当前时间 并且大于上次执行完成时间
				try {
					
					
					String runningDate=taskdate+starttime;
					Date nextDate=new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime);
					if(nextDate.after(new Date())){
						System.out.println(batch);
						System.out.println("已经到达最新执行日期"+"nextDate:"+nextDate+" nextexecutetime:"+nextexecutetime);
						continue;
					}
					
					Date taskDate=new SimpleDateFormat("yyyyMMddHHmm").parse(runningDate);
					
					if(taskDate.before(nextDate)){
						System.out.println("任务下次运行时间为:"+nextDate + "当前运行的任务时间为:"+runningDate);
						int beforeid = batch.getBeforeid();
						Batch beforeBatch = allBatchMap.get(beforeid);
						if(beforeBatch==null||(beforeBatch.getFlag()==Constants.BATCH_FREE)&& beforeBatch.getNextexecutetime().equals(runningDate)){
							RunTaskThread runTaskThread = new RunTaskThread(batchService, batch, nextexecutetime, taskdate, nextDate, log);
							runTaskThread.start();
						}else{
							log.info("当前任务的前置任务未完成");
						}
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
			}else{
				log.debug("任务执行有问题"+batch);
			}
		}
	}

	
}

class RunTaskThread extends Thread{
	
	private BatchService batchService;
	private Batch batch;
	private String nextexecutetime;
	private String taskdate;
	private Date nextDate;
	private Logger log;

	public RunTaskThread(BatchService batchService, Batch batch, String nextexecutetime, String taskdate, Date nextDate,
			Logger log) {
		super();
		this.batchService = batchService;
		this.batch = batch;
		this.nextexecutetime = nextexecutetime;
		this.taskdate = taskdate;
		this.nextDate = nextDate;
		this.log = log;
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();
		//设置任务为     running
		batch.setFlag(Constants.BATCH_RUNNING);
		batchService.updateBatch(batch);
		//执行任务
		batchService.executeBatch(batch.getId(), taskdate);
		
		batch.setTaskdate(nextexecutetime.substring(0,8));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nextDate);
		System.out.println(nextDate);
		calendar.add(Calendar.DATE, 1);
		batch.setNextexecutetime(new SimpleDateFormat("yyyyMMddHHmm").format(calendar.getTime()));
		//任务运行完设置为 空闲
		batch.setFlag(Constants.BATCH_FREE);
		batchService.updateBatch(batch);
		
		log.info("执行完成任务"+batch);
		log.info("执行花费时间 "+(System.currentTimeMillis()-start)+" ms");
		log.info("本次执行的任务时间 "+batch.getTaskdate());
		log.info("下次执行时间 "+batch.getNextexecutetime());
	}
	
}
