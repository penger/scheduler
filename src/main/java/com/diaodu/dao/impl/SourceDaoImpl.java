package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.dao.SourceDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Source;

public class SourceDaoImpl implements SourceDao {

	@Override
	public List<Source> getAllSources() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Source> list = queryRunner.query(connection,"select * from source ", new BeanListHandler<>(Source.class));
		return list;
	}

	@Override
	public int addSource(Source s) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="insert into source(hive_table_name,system_id,table_name)"+
		" values (?,?,?)";
		Object[] params={s.getHive_table_name(),s.getSystem_id(),s.getTable_name()};
		return queryRunner.update(connection, sql, params);
	}

	@Override
	public Source getSourceByID(int sourceId) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Source source = queryRunner.query(connection,"select * from source where source_id = ?", new BeanHandler<>(Source.class), new Object[]{sourceId});
		return source;
	}

	@Override
	public List<Source> getSourceByName(String sourceName) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Source> list = queryRunner.query(connection,"select * from source where hive_table_name like ?", new BeanListHandler<>(Source.class), new Object[]{"%"+sourceName+"%"});
		return list;
	}

	
	@Override
	public Source getOnlySourceByName(String sourceName) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Source s = queryRunner.query(connection,"select * from source where hive_table_name = ?", new BeanHandler<>(Source.class), new Object[]{sourceName});
		return s;
	}

	@Override
	public List<Source> getSourcesInUse() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Source> list = queryRunner.query(connection,"select * from source where source_id in(SELECT DISTINCT source_id FROM relation WHERE TYPE IN (0,2))", new BeanListHandler<>(Source.class));
		return list;
	}

	@Override
	public List<Source> getSourcesNotInUse() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Source> list = queryRunner.query(connection,"select * from source where source_id not in(SELECT DISTINCT source_id FROM relation WHERE TYPE  IN (0,2)) ", new BeanListHandler<>(Source.class));
		return list;
	}
	
	
}
