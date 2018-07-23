package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.dao.OraclesidDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Oraclesid;

public class OraclesidDaoImpl implements OraclesidDao {


	@Override
	public List<Oraclesid> getAllOraclesid() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<Oraclesid> list = queryRunner.query(connection,"select * from oraclesid order by id asc", new BeanListHandler<>(Oraclesid.class));
		return list;
	}


}
