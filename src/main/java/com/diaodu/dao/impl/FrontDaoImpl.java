package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.dao.FrontDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Front;

public class FrontDaoImpl implements FrontDao {

	@Override
	public List<Front> getAllFront() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Front> list = queryRunner.query(connection,"\n" +
				"select  t.* from front t where is_show ='1' order by CAST(sort AS UNSIGNED INT ) ;", new BeanListHandler<>(Front.class));
		return list;
	}


	@Override
	public Front getFrontByID(String front_id) throws SQLException{
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="select * from front where front_id = ?";
		Object[] params={front_id};
		return queryRunner.query(connection, sql, new BeanHandler<>(Front.class) , params);
	}

//	@Override
//	public List<Front> getFrontsByCondition(Front f) throws SQLException {
//		Connection connection = JDBCUtils.getConnection();
//		QueryRunner queryRunner = new QueryRunner();
//		List<Front> list = queryRunner.query(connection,"select * from front where label like ? and creator like ?",
//				new BeanListHandler<>(Front.class), new Object[]{"%"+f.getLabel()+"%","%"+f.getCreator()+"%"});
//		return list;
//	}
//
	
	public static void main(String[] args) throws SQLException {
		FrontDaoImpl frontDao = new FrontDaoImpl();
		frontDao.getAllFront();
	}

}
