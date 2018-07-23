package com.diaodu.domain;

import java.io.IOException;
import java.util.Properties;

public class Constants {
	
	
	public static Integer MAX_THREAD_NUM=2;
	
	static{
		Properties properties = new Properties();
		String path="/config.properties";
		try {
			properties.load(
					Constants.class.getResourceAsStream(path)
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String x = (String) properties.get("max_thread_num");
		MAX_THREAD_NUM=Integer.parseInt(x);
		
	}
	
	
	
	public static final String DRIVER = "jdbc.driver";
	public static final String URL = "jdbc.url";
	public static final String USERNAME = "jdbc.username";
	public static final String PASSWORD = "jdbc.password";
	
	public static final String RUNNING_MESSAGE = "message";
	public static final String EXIT_CODE = "exit_code";
	public static final String SPEND_TIME = "spend_time";
	public static final String TASK_DATE="task_date";
	public static final String START_DATE="start_date";
	public static final String CMD="cmd";
	
	
	
	
	
	//无效
	public static final int BATCH_INVAIN=0;
	//已经运行完成
	public static final int BATCH_FREE=1;
	//正在运行
	public static final int BATCH_RUNNING=2;
	//运行失败
	public static final int BATCH_FAIL=3;
	//正在重试
	public static final int BATCH_RETRYING=4;
	//待运行状态
	public static final int BATCH_TO_RUN=5;
	
	
	public static final String TEST_COMMAND_PATH="/var/lib/hadoop-hdfs/schedulertest/bin";
	
	//测试系统
	public static final String TEST_IP="10.201.48.101";
	public static final String TEST_USERNAME="bl";
	public static final String TEST_PASSWORD="bl";
	public static final int TEST_PORT=22;
	
	
	//生产系统
	public static final String PRODUCT_IP="10.201.48.26";
	public static final String PRODUCT_USERNAME="bl";
	public static final String PRODUCT_PASSWORD="bl";
	public static final int PRODUCT_PORT= 22;
	
	
	//生产系统
	public static final String PRE_IP="10.201.48.101";
	public static final String PRE_USERNAME="";
	public static final String PRE_PASSWORD="";
	public static final int PRE_PORT= 22;
	
}
