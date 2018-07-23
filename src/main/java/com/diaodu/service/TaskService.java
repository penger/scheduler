package com.diaodu.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.diaodu.core.CenterConfig;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.dao.TaskDao;
import com.diaodu.dao.impl.TaskDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Constants;
import com.diaodu.domain.History;
import com.diaodu.domain.Task;
import com.diaodu.domain.TaskType;
import com.diaodu.ssh.SshUtil;

public class TaskService {
	
	private Logger log= LoggerFactory.getLogger(TaskService.class);
	
	public Task getTaskByID(int id){
		Task task=null;
		TaskDao taskdao=new TaskDaoImpl();
		try {
			task = taskdao.getTaskByID(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		
		return task;
	}
	

	public int deleteTask(int id){
		int rows=0;
		JDBCUtils.startTransaction();
		try {
			TaskDao taskdao=new TaskDaoImpl();
			rows = taskdao.deleteTaskByID(id);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return rows;
		
	}
	
	//根据输入的task 类型生产命令和参数
	public int addTask(Task task){
		int rows=0;
		JDBCUtils.startTransaction();
		try {
			TaskDao taskdao =new TaskDaoImpl();
			//String cmd = CommandCreateUtil.createCommandByTask(task);
			//task.setCommand(cmd);
			rows = taskdao.addTask(task);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return rows;
	}
	
	
	public int updateTask(Task t){
		int rows=0;
		JDBCUtils.startTransaction();
		try {
			TaskDao taskdao =new TaskDaoImpl();
			rows = taskdao.updateTask(t);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return rows;
		
	}

	//根据id执行,界面方式
	public Map<String,String> executeTask(Task task,String atpre) {
		Map<String,String> resultMap = null;
		String true_command = getTrueCommand(task);
		task.setFlag(Constants.BATCH_RUNNING);
		updateTask(task);
		String taskhost = task.getTaskhost();
		if(taskhost.equals(CenterConfig.getValueByKeyWithoutDecode(CenterConfig.LOCAL_IP))){
			//2017-5-3 15:36:01 前端提供执行pre上shell的命令
			// atpre=="1" 表示在pre ,如果不在pre 那么非"1"
			if("1".equals(atpre)){
				taskhost=CenterConfig.getValueByKeyWithoutDecode(CenterConfig.PRE_CLIENT_IP);
				resultMap = SshUtil.exeRemote(taskhost,true_command);
			}else{
				resultMap = SshUtil.exeLocal(true_command);
			}
		}else {
			resultMap = SshUtil.exeRemote(taskhost,true_command);
		}
		String exit_code = resultMap.get(Constants.EXIT_CODE);
		resultMap.put("script", task.getCommand());
		if(exit_code.equals("0")){
			task.setFlag(Constants.BATCH_FREE);
		}else{
			task.setFlag(Constants.BATCH_FAIL);
		}
		updateTask(task);
		insertHistory(resultMap);
		return resultMap;
	}

	public int insertHistory(Map<String, String> resultMap) {
		int rows=0;
		JDBCUtils.startTransaction();
		TaskDao taskdao =new TaskDaoImpl();
		try {
			History history = new History();
			history.setCmd(resultMap.get(Constants.CMD));
			history.setTask_date(resultMap.get(Constants.TASK_DATE));
			history.setExit_code(resultMap.get(Constants.EXIT_CODE));
			history.setMessage(resultMap.get(Constants.RUNNING_MESSAGE));
			history.setSpend_time(resultMap.get(Constants.SPEND_TIME));
			history.setStart_date(resultMap.get(Constants.START_DATE));
			history.setScript(resultMap.get("script"));
			rows = taskdao.addHistory(history);
			//更新shell文件的平均执行时间
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return rows;
	}
	
	public int insertHistory(String cmd,String script,String task_date,String exit_code,String message,
			String spend_time,String start_date) {
		int rows=0;
		JDBCUtils.startTransaction();
		TaskDao taskdao =new TaskDaoImpl();
		try {
			History history = new History();
			history.setCmd(cmd);
			history.setTask_date(task_date);
			history.setExit_code(exit_code);
			history.setMessage(message);
			history.setSpend_time(spend_time);
			history.setStart_date(start_date);
			history.setScript(script);
			rows = taskdao.addHistory(history);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return rows;
	}
	
	

	/**
	 * 根据任务类型,生成命令参数
	 * @param task
	 * @param task
	 * @return
	 */
	public String handleArguments(Task task) {
		String tasktype = task.getTasktype();
		String args = task.getArgs();
		if(args==null){
			args="";
		}
		String cmd = task.getCommand();
		//参数预处理,如果参数不符合规则,那么抛出异常弱校验
		if(tasktype.equals(TaskType.SHELL)){
			log.debug("shell : cmd "+cmd+" args: "+args);
			return "sudo -S -p 'bl' -u hive "+task.getCommandpath()+task.getCommand()+" "+args;
		}else{
			return "pwd";
		}
	}

	public String getTrueCommand(Task task){
		String command = task.getCommand();
		String true_command = "pwd";
		String args="";
		if(!Strings.isNullOrEmpty(task.getArgs())){
			args = task.getArgs();
		}
		if(task.getTaskhost().equals(CenterConfig.getValueByKeyWithoutDecode(CenterConfig.LOCAL_IP))){
			//本地执行
			String actor= task.getActor();
			true_command = ""+task.getCommandpath()+task.getCommand()+" "+args;
		}else{
			//非本地执行
			String actor = task.getActor();
			String password = CenterConfig.getValueByKeyWithoutDecode(task.getTaskhost()+ CenterConfig.POSTFIX_PASSWORD);
			true_command = "sudo -S -p '"+password+"' -u "+actor+" "+task.getCommandpath()+task.getCommand()+" "+args;
		}
		log.debug("true command is :	"+true_command);
		return  true_command;
	}


	public List<Task> getTaskListByBatchID(int batchid){
		List<Task> tasks = new ArrayList<>();
		TaskDaoImpl taskDao = new TaskDaoImpl();
		try {
			tasks = taskDao.getTaskListByBatchID(batchid);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return tasks;
	}



	public Task getTaskByScript(String etl_script) {
		Task task=null;
		TaskDao taskDao = new TaskDaoImpl();
		try {
			task =taskDao.getTaskByScript(etl_script);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return task;
	}

	/**
	 * 更改批次的task的运行状态
	 * @param flag
	 * @param batchid
	 * @return
	 */
	public int updateTaskFlagByBatchID(int flag,int batchid) {
		TaskDaoImpl taskDao = new TaskDaoImpl();
		int count = 0;
		JDBCUtils.startTransaction();
		try {
			count = taskDao.updateTaskFlag(flag,batchid);
			JDBCUtils.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			JDBCUtils.rollback();
		}finally {
			JDBCUtils.close();
		}
		return count;
	}

}
