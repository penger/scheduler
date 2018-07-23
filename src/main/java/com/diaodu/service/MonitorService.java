package com.diaodu.service;

import com.diaodu.dao.BatchDao;
import com.diaodu.dao.MonitorDao;
import com.diaodu.dao.TaskDao;
import com.diaodu.dao.impl.BatchDaoImpl;
import com.diaodu.dao.impl.MonitorDaoImpl;
import com.diaodu.dao.impl.TaskDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import com.diaodu.domain.Monitor;
import com.diaodu.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MonitorService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	public List<Monitor> getMonitor(String start_date,String end_date, String table_name,String type){
		List<Monitor> monitors = null;
		
		try {
			MonitorDao monitorDao=new MonitorDaoImpl();
			monitors = monitorDao.getMonitorByDateAndTableName(start_date, end_date, table_name, type);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return monitors;
	}
	

}
