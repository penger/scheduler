package com.diaodu.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.diaodu.dao.ETLDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Front;
import com.diaodu.domain.Source;

public class ETLDaoImpl implements ETLDao {

	@Override
	public List<ETL> getAllETL() throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<ETL> list = queryRunner.query(connection,"select * from etl  order by etl_type", new BeanListHandler<>(ETL.class));
		return list;
	}

	@Override
	public int addETL(ETL etl) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="insert into etl(etl_type,etl_script,etl_crontab_info,hive_table,owner,remark,is_scheduling)"+
		" values (?,?,?,?,?,?,?)";
		Object[] params={etl.getEtl_type(),etl.getEtl_script(),etl.getEtl_crontab_info(),etl.getHive_table(),
				etl.getOwner(),etl.getRemark(),etl.getIs_scheduling()};
		return queryRunner.update(connection, sql, params);
	}
	
	
	

	@Override
	public ETL getETLByID(int etlId) throws SQLException{
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		ETL etl = queryRunner.query(connection,"select * from etl where etl_id = ?", new BeanHandler<>(ETL.class), new Object[]{etlId});
		return etl;
	}

	@Override
	public List<ETL> getETLsByName(String hiveName) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		List<ETL> list = queryRunner.query(connection,"select * from etl where hive_table like ?", new BeanListHandler<>(ETL.class), new Object[]{"%"+hiveName+"%"});
		return list;
	}

	@Override
	public ETL getETLByName(String hiveName) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		ETL etl = queryRunner.query(connection,"select * from etl where hive_table = ?", new BeanHandler<>(ETL.class), new Object[]{hiveName});
		return etl;
	}
	
	
	@Override
	public List<ETL> getETLsByCondition(ETL e) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String in_statement="(1,0,100)";
		if(e.getEtl_type()!=100){
			in_statement="("+e.getEtl_type()+")";
		}
		List<ETL> list = queryRunner.query(connection,"select * from etl where owner like ? and remark like ? and hive_table like ? and etl_crontab_info like ?  and etl_type in "+in_statement+" order by owner,etl_id,etl_type,etl_crontab_info", 
				new BeanListHandler<>(ETL.class), new Object[]{"%"+e.getOwner()+"%","%"+e.getRemark()+"%","%"+e.getHive_table()+"%","%"+e.getEtl_crontab_info()+"%"});
		return list;
	}
	
	
	public static void main(String[] s){
		ETL etl = new ETL();
		System.out.println(etl.getEtl_type());
	}

	@Override
	public int updateETL(ETL etl) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="update etl set etl_type=? ,etl_script=?,etl_crontab_info=?,hive_table=?,owner=?,remark=?,is_scheduling=?)"+
				" where etl_id=?";
		Object[] params={etl.getEtl_type(),etl.getEtl_script(),etl.getEtl_crontab_info(),etl.getHive_table(),
				etl.getOwner(),etl.getRemark(),etl.getIs_scheduling(),etl.getEtl_id()};
		return queryRunner.update(connection, sql, params);
	}

	@Override
	public int updateSchedulingByID(int etl_id,int is_scheduling) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="update etl set is_scheduling=?, etl_type =1 "+
				" where etl_id=?";
		Object[] params={is_scheduling,etl_id};
		return queryRunner.update(connection, sql, params);
	}
	
	@Override
	public int updateETL(int etl_id,String owner,String remark) throws SQLException {
		Connection connection = JDBCUtils.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		String sql="update etl set owner=?,remark=? "+
				" where etl_id=?";
		Object[] params={owner,remark,etl_id};
		return queryRunner.update(connection, sql, params);
	}
	
	
	
}
