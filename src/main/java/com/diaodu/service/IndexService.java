package com.diaodu.service;

import java.sql.SQLException;
import java.util.List;

import com.diaodu.dao.BatchDao;
import com.diaodu.dao.HistoryDao;
import com.diaodu.dao.OraclesidDao;
import com.diaodu.dao.TaskDao;
import com.diaodu.dao.impl.BatchDaoImpl;
import com.diaodu.dao.impl.HistoryDaoImpl;
import com.diaodu.dao.impl.OraclesidDaoImpl;
import com.diaodu.dao.impl.TaskDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;
import com.diaodu.domain.History;
import com.diaodu.domain.Oraclesid;
import com.diaodu.domain.Task;

public class IndexService {

	public List<Oraclesid> getAllOracle(){
		List<Oraclesid> allOraclesid = null;
	
		try {
			OraclesidDao oradao=new OraclesidDaoImpl();
			allOraclesid = oradao.getAllOraclesid();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allOraclesid;
	}
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
	public List<Task> getAllTask(){
		List<Task> allTask = null;
		try {
			TaskDao taskdao=new TaskDaoImpl();
			allTask = taskdao.getAllTask();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allTask;
	}
	
	public List<History> getHistory(int pageNum,int pageSize){
		//得到要查询的起始页和终止页
		int start=(pageNum-1)*pageSize;
		List<History> historys=null;
		try{
			HistoryDao historyDao = new HistoryDaoImpl();
			historys = historyDao.getHistoryPage(start, pageSize);
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return historys;
	}
	
	public int  getHistoryLength(){
		//得到要查询的起始页和终止页
		int length=0; 
		try{
			HistoryDao historyDao = new HistoryDaoImpl();
			length = historyDao.getTotalRecodes();
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return length;
	}
	
}
