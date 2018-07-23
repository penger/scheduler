package com.diaodu.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.dao.ETLDao;
import com.diaodu.dao.RelationDao;
import com.diaodu.dao.SourceDao;
import com.diaodu.dao.impl.ETLDaoImpl;
import com.diaodu.dao.impl.RelationDaoImpl;
import com.diaodu.dao.impl.SourceDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Relation;
import com.diaodu.domain.Source;

public class RelationService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	public List<Source> getSourcesByName(String sourceName){
		List<Source> sources = null;
		try {
			SourceDao sourcedao = new SourceDaoImpl();
			sources = sourcedao.getSourceByName(sourceName);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return sources;
	}

	public List<ETL> getHivesByName(String hiveName){
		List<ETL> elts = null;
		try {
			ETLDao etldao = new ETLDaoImpl();
			elts = etldao.getETLsByName(hiveName);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return elts;
	}

	public int insertRelation(Relation r){
		int count=0;
		JDBCUtils.startTransaction();
		try {
			RelationDao relationDao = new RelationDaoImpl();
			count = relationDao.addRelation(r);
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return count;
	}

	public int deleteRelation(Relation relation) {
		int count = 0;
		JDBCUtils.startTransaction();
		try {
			RelationDao relationDao = new RelationDaoImpl();
			count = relationDao.deleteRelation(relation);
			JDBCUtils.commit();
		} catch (Exception e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		return count;
	}
	
	
	
}
