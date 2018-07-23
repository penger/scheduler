package com.diaodu.core;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.diaodu.domain.Task;
import com.diaodu.service.TaskService;
import org.quartz.JobExecutionException;

public class DemoRun {

	public static void main(String[] args) throws JobExecutionException, SQLException, ParseException {
		SchedulerTask task = new SchedulerTask();
		task.execute(null);
//		ExecutorService pool= Executors.newFixedThreadPool(3);
//		TaskService taskService = new TaskService();
//		Task task1 = taskService.getTaskByID(319);
//		Task task2 = taskService.getTaskByID(416);
//		ConcurrentHashMap<Integer, Task> map = new ConcurrentHashMap<>();
//		map.put(task1.getId(),task1);
//		map.put(task2.getId(),task2);
//
//		task.addTaskList2PoolAndWaitForFinished(pool,map);
	}
}
