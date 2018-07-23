package com.diaodu.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.diaodu.dao.HistoryDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.History;

public class HistoryDaoImpl implements HistoryDao {

	@Override
	public List<History> getHistoryPage(int start, int pageSize) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<History> list = queryRunner.query(connection,"select * from history order by task_date desc limit ?,? ", new BeanListHandler<>(History.class),start,pageSize);
		return list;
	}

	@SuppressWarnings("rawtypes")
	public int getTotalRecodes() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		@SuppressWarnings("unchecked")
		int recodes =(int)(long) queryRunner.query(connection,"select count(1) from history ", new ScalarHandler());
		return recodes;
	}
	
	
	@Override
	public List<History> getHistoryPageByConditon(int start, int pageSize,String where_order) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="select * from history "+where_order+" limit ?,?";
		System.out.println(sql);
		List<History> list = queryRunner.query(connection,"select * from history "+where_order+" limit ?,? ", new BeanListHandler<>(History.class),start,pageSize);
		return list;
	}

	@SuppressWarnings("rawtypes")
	public int getTotalRecodesByConditon(String where) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		@SuppressWarnings("unchecked")
		int recodes =(int)(long) queryRunner.query(connection,"select count(1) from history "+where, new ScalarHandler());
		return recodes;
	}

	@Override
	public int getAvarageTimeByScript(String script) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Object x = queryRunner.query(connection, "select avg(spend_time) from history where exit_code=0 and script ='"+script+"'" ,new ScalarHandler());
		if(x==null){
			return 0;
		}else{
			BigDecimal avg = (BigDecimal)x;
			return avg.intValue();
		}
	}

	@Override
	public int deleteHistoryNdaysBefore(int n) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE,0-n);
		Date endupDate = calendar.getTime();
		String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(endupDate);
		int updateCount = queryRunner.update(connection, "delete from history where start_date <'" + formatedDate+"'");
		return updateCount;
	}


}
