package com.diaodu.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.dao.HistoryDao;
import com.diaodu.dao.impl.HistoryDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.History;

public class HistoryService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	public List<History> getHistory(int pageNum,int pageSize,String where_order){
		//得到要查询的起始页和终止页
		int start=(pageNum-1)*pageSize;
		List<History> historys=null;
		try{
			HistoryDao historyDao = new HistoryDaoImpl();
			historys = historyDao.getHistoryPageByConditon(start, pageSize,where_order);
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return historys;
	}
	
	public int  getHistoryLength(String where){
		//得到要查询的起始页和终止页
		int length=0; 
		try{
			HistoryDao historyDao = new HistoryDaoImpl();
			length = historyDao.getTotalRecodesByConditon(where);
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return length;
	}
	
	
	public int getAvarageTime(String script){
		HistoryDao historyDao = new HistoryDaoImpl();
		int avarageTime=0;
		try {
			avarageTime = historyDao.getAvarageTimeByScript(script);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		return avarageTime;
	}


	public int deleteHistoryNdaysBefore(int n){
		HistoryDaoImpl historyDao = new HistoryDaoImpl();
		int count = 0 ;
		try {
			count = historyDao.deleteHistoryNdaysBefore(n);
			JDBCUtils.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return  count;
	}
	
	
	
	
}
