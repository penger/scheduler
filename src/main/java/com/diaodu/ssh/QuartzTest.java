package com.diaodu.ssh;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzTest implements Job{
	public static void main(String[] args) {
		
		
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("quartz test :");
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		
	}
}
