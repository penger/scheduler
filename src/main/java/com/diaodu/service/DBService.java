package com.diaodu.service;

import com.diaodu.dao.BatchDao;
import com.diaodu.dao.TaskDao;
import com.diaodu.dao.impl.BatchDaoImpl;
import com.diaodu.dao.impl.TaskDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBService {

	Logger log = LoggerFactory.getLogger(getClass());
	

    public List<String> getTablesByDateCondition(String start,String end) {
		List taskList = null;
		taskList=new ArrayList();
		taskList.add("a");
		taskList.add("a_1");
		taskList.add("a_2");
		taskList.add("a_3");
		return taskList;
	}

	public void resetDBstatusOfPdata() {

		BatchDao batchDao = new BatchDaoImpl();
		TaskDao taskDao = new TaskDaoImpl();
		Batch batch = null;
		try {
			//修改批次为正常运行状态
			batch = batchDao.getBatchByID("80");
			if (batch.getFlag()!=1){
				batch.setFlag(1);
				String nextexecutetime = batch.getNextexecutetime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				Date olddate = sdf.parse(nextexecutetime);
				Calendar instance = Calendar.getInstance();
				instance.setTime(olddate);
				instance.add(Calendar.DATE,1);
				Date time = instance.getTime();
				String newdate = sdf.format(time);
				batch.setNextexecutetime(newdate);
				batchDao.updateBatch(batch);
				System.out.println("修改批次为运行完成状态");
			}
			//修改子任务为正常运行状态
			int count = taskDao.updateNotVainTask2Free(80);
			System.out.println("修改了"+count+" 个子任务的状态为已经运行完成");
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}catch (ParseException e){
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}




	}
}
