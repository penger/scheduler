package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.core.GlobalMap;
import com.diaodu.dao.BatchDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;

public class BatchDaoImpl implements BatchDao {


	@Override
	public List<Batch> getAllBatch() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Batch> list = queryRunner.query(connection,"select * from batch order by id asc", new BeanListHandler<>(Batch.class));
		return list;
	}

	@Override
	public Batch getBatchByID(String id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Batch batch = queryRunner.query(connection,"select * from batch where id = ?", new BeanHandler<>(Batch.class),id);
		return batch;
	}

	@Override
	public Integer addBatch(Batch b) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="insert into batch(bname,bdesc,beforeid,afterid,taskdate,"
				+ "flag,timeinterval,startdate,enddate,btype,"
				+ "nextexecutetime,cronexpression,cronexplain)"+
		" values (?,?,?,?,?, ?,?,?,?,?, ?,?,?)";
		Object[] params={b.getBname(),b.getBdesc(),b.getBeforeid(),
				b.getAfterid(),b.getTaskdate(),b.getFlag(),
				b.getTimeinterval(),b.getStartdate(),b.getEnddate()
				,b.getBtype(),b.getNextexecutetime(),b.getCronexpression(),b.getCronexplain()};
		int update = queryRunner.update(connection, sql, params);
		return update;
		
	}

	@Override
	public int deleteBatch(int id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="delete from batch where id= ?";
		int update = queryRunner.update(connection, sql, id);
		return update;
	}

	@Override
	public int updateBatch(Batch b) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner runner = new QueryRunner();
		String sql="update batch set bname=? ,bdesc = ?,beforeid= ?,afterid= ?,"
					+ "flag= ? , cronexpression = ? ,cronexplain = ? , nextexecutetime = ?"
					+ " where id=?";
		Object[] params={b.getBname(),b.getBdesc(),b.getBeforeid(),
				b.getAfterid(), b.getFlag(), b.getCronexpression(),b.getCronexplain(),
				b.getNextexecutetime(),b.getId()};
		int update = runner.update(connection, sql, params);
		return update;
		
	}


}
