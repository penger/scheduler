package com.diaodu.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainService {

	public static void main (String[] args){
		onlyTest();
//		runScheduler();
	}
	
	public static void onlyTest(){
		new App().checkAndRun();
	}
	
	public static void runScheduler(){
		ScheduledExecutorService stse = Executors.newSingleThreadScheduledExecutor();
		stse.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				new App().checkAndRun();
			}
		}, 20, 20, TimeUnit.SECONDS);
	}

}
