package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.google.common.base.Strings;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.dao.RelationDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Relation;

public class RelationDaoImpl implements RelationDao {

	@Override
	public List<Relation> getETLRelation(int etl_id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Object[] param =new Object[]{etl_id};
		List<Relation> list = queryRunner.query(connection,"select * from relation where etl_id=? and type in(0,1) ",new BeanListHandler<>(Relation.class),param );
		return list;
	}

	@Override
	public List<Relation> getFrontRelation(String front_id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Object[] param =new Object[]{front_id};
		List<Relation> list = queryRunner.query(
				connection,
				"select *,e.hive_table front_name from relation r left join etl e  on r.etl_id=e.etl_id  where front_id=? and type =3  ",
				new BeanListHandler<>(Relation.class),
				param);
		return list;
	}
	
	@Override
	public List<Relation> getSourceRelation(int source_id) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Object[] param =new Object[]{source_id};
		List<Relation> list = queryRunner.query(connection,"select * from relation where source_id=? and type in(0,2) ", new BeanListHandler<>(Relation.class),param);
		return list;
	}

	@Override
	public int addRelation(Relation relation) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		Object[] param =new Object[]{relation.getEtl_id(),relation.getFront_id(),relation.getSource_id(),relation.getType()};
		return queryRunner.update(connection, "insert into relation(etl_id,front_id,source_id,type) values (?,?,?,?) ", param);
	}

	@Override
	public int deleteRelation(Relation relation) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		int count=0;
		if(Strings.isNullOrEmpty(relation.getFront_id())){
			Object[] param = new Object[]{relation.getEtl_id(),relation.getSource_id(),relation.getType()};
			String sql="delete from relation where etl_id = ? and source_id = ? and type = ?";
			count = queryRunner.update(connection,sql,param);
		}else{
			Object[] param = new Object[]{relation.getFront_id(),relation.getEtl_id(),relation.getType()};
			String sql="delete from relation where front_id = ? and etl_id = ? and type = ?";
			count = queryRunner.update(connection,sql,param);
		}
		return count;
	}

}
