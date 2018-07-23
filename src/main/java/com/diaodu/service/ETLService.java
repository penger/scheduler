package com.diaodu.service;

import java.sql.SQLException;

import com.diaodu.dao.ETLDao;
import com.diaodu.dao.impl.ETLDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;

public class ETLService {

	public int updateETL(ETL etl){
		int elts = 0;
		JDBCUtils.startTransaction();
		try {
			ETLDao etldao = new ETLDaoImpl();
			elts = etldao.updateETL(etl);
		} catch (SQLException e) {
			e.printStackTrace();
			JDBCUtils.rollback();
		}finally {
			JDBCUtils.close();
		}
		return elts;
	}

	
	public int updateSchedulingByID(int etl_id,int is_scheduling){
		int elts = 0;
		JDBCUtils.startTransaction();
		try {
			ETLDao etldao = new ETLDaoImpl();
			elts = etldao.updateSchedulingByID(etl_id,is_scheduling);
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return elts;
	}
}
