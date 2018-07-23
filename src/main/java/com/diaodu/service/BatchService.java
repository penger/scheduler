package com.diaodu.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.diaodu.core.GlobalMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.dao.BatchDao;
import com.diaodu.dao.TaskDao;
import com.diaodu.dao.impl.BatchDaoImpl;
import com.diaodu.dao.impl.TaskDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import com.diaodu.domain.Task;

public class BatchService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	public List<Batch> getAllBatch(){
		List<Batch> allBatch = null;
		
		try {
			BatchDao batchdao=new BatchDaoImpl();
			allBatch = batchdao.getAllBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allBatch;
	}
	
	public int addBatch(Batch batch){
		int rows=0;
		JDBCUtils.startTransaction();
		try {
			BatchDao batchdao=new BatchDaoImpl();
			rows = batchdao.addBatch(batch);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		GlobalMap.refeshMap();
		return rows;
		
	}
	public int deleteBatch(int id){
		int rows=0;
		JDBCUtils.startTransaction();
		try {
			BatchDao batchdao=new BatchDaoImpl();
			rows = batchdao.deleteBatch(id);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		GlobalMap.refeshMap();
		return rows;
		
	}
	
	public Batch getBatchByID(String id){
		Batch b = null ;
		try {
			BatchDao batchdao=new BatchDaoImpl();
			b =batchdao.getBatchByID(id);
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return b;
		
	}
	
	public int updateBatch(Batch b){
		int rows=0;
		JDBCUtils.startTransaction();
		try {
			BatchDao batchdao=new BatchDaoImpl();
			rows = batchdao.updateBatch(b);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		GlobalMap.refeshMap();
		return rows;
		
	}
	
	//获取当前Batch下面的所有Task
	public List<Task> getAllTaskByBatchID(int batchid){
		List<Task> list =null;
		try {
			TaskDao taskdao =new TaskDaoImpl();;
			list = taskdao.getTaskListByBatchID(batchid);
			for (Task task : list) {
				task.getStatus();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		return list;
	}
	
	public boolean executeBatch(int batchid,String taskdate){
		List<Task> taskList = getAllTaskByBatchID(batchid);
		int size = taskList.size();
		for(int i=0;i<size;i++){
			Task task = taskList.get(i);
			TaskService taskService = new TaskService();
			Map<String, String> executeMap = taskService.executeTask(task, taskdate);
			//跳出剩下的任务
			if(!executeMap.get(Constants.EXIT_CODE).equals("0")){
				break;
			}
			
		}
		return true;
	}

	/**
	 * 根据批次id 获取批次内未正常完成的任务列表,便于邮件通知
	 * 2016-8-22 09:59:15
	 * @param batchid
	 * @return
	 */
    public List<Task> getFailTasksByBatchID(Integer batchid) {
		TaskDaoImpl taskDao = new TaskDaoImpl();
		List taskList = null;
		try {
			taskList = taskDao.getFailTasksByBatchID(batchid);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return taskList;
	}
}
