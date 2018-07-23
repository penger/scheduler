package com.diaodu.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.dao.ETLDao;
import com.diaodu.dao.FrontDao;
import com.diaodu.dao.RelationDao;
import com.diaodu.dao.SourceDao;
import com.diaodu.dao.impl.ETLDaoImpl;
import com.diaodu.dao.impl.FrontDaoImpl;
import com.diaodu.dao.impl.RelationDaoImpl;
import com.diaodu.dao.impl.SourceDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Front;
import com.diaodu.domain.Relation;
import com.diaodu.domain.Source;

public class ObserverService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	public List<Source> getAllSources(){
		List<Source> allSources = null;
		try {
			SourceDao sourcedao = new SourceDaoImpl();
			allSources = sourcedao.getAllSources();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allSources;
	}
	
	public ETL getETLByID(Integer etlId){
		ETL etl =null;
		ETLDao etlDao =new ETLDaoImpl();
		try {
			etl= etlDao.getETLByID(etlId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return etl;
	}
	
	
	

	public List<ETL> getAllETLs() {
		List<ETL> allETLs = null;
		try {
			ETLDao ETLdao = new ETLDaoImpl();
			allETLs = ETLdao.getAllETL();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allETLs;
	}

	public List<Front> getAllfronts() {
		List<Front> allFronts = null;
		try {
			FrontDao frontdao = new FrontDaoImpl();
			allFronts = frontdao.getAllFront();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allFronts;
	}


	public int addETL(ETL etl) {
		int success=0;
		JDBCUtils.startTransaction();
		try {
			ETLDao etldao = new ETLDaoImpl();
			success = etldao.addETL(etl);
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return success;
	}

	public int addSource(Source s) {
		int success=0;
		JDBCUtils.startTransaction();
		try {
			SourceDao sourcedao = new SourceDaoImpl();
			success = sourcedao.addSource(s);
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return success;
	}

	public Map<Integer, List<Object>> getMapByETLID(int etl_id) {
		Map<Integer,List<Object>> map = new HashMap<Integer,List<Object>>();
		JDBCUtils.startTransaction();
		try{
			RelationDao relationDao = new RelationDaoImpl();
			List<Relation> etlRelation = relationDao.getETLRelation(etl_id);
			if(etlRelation.isEmpty()){
				return null;
			}else{
				ETLDao etlDao = new ETLDaoImpl();
				SourceDao sourceDao = new SourceDaoImpl();
				for ( Relation relation : etlRelation) {
					if(relation.getType()==Relation.ETL2ETL){
						int etlId = relation.getSource_id();
						ETL etl= etlDao.getETLByID(etlId);
						if(map.get(Relation.ETL2ETL)==null){
							List<Object> etlList = new ArrayList<Object>();
							etlList.add(etl);
							map.put(Relation.ETL2ETL,etlList);
						}else{
							map.get(Relation.ETL2ETL).add(etl);
						}
						
					}else if (relation.getType()==Relation.SOURCE2ETL){
						int sourceId = relation.getSource_id();
						Source source=sourceDao.getSourceByID(sourceId);
						if(map.get(Relation.SOURCE2ETL)==null){
							List<Object> sourceList = new ArrayList<Object>();
							sourceList.add(source);
							map.put(Relation.SOURCE2ETL, sourceList);
						}else{
							map.get(Relation.SOURCE2ETL).add(source);
						}
					}else{
						throw new IllegalArgumentException("错误的参数:"+relation);
					}
				}
			}
			
		}catch(SQLException e){
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		return map;
	}

	//只获取一种类型的task
	public List<Relation> getListByFrontID(String front_id) {
		List list= new ArrayList();
		RelationDao relationDao = new RelationDaoImpl();
		try {
			list = relationDao.getFrontRelation(front_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	
	
	public Map<Integer, List<Object>> getMapBySourceID(int source_id){
		Map<Integer,List<Object>> map = new HashMap<Integer,List<Object>>();
		RelationDao relationDao = new RelationDaoImpl();
		try {
			List<Relation> sourceRelation = relationDao.getSourceRelation(source_id);
			if(sourceRelation.isEmpty()){
				return null;
			}else{
				ETLDao etlDao = new ETLDaoImpl();
				FrontDao frontDao = new FrontDaoImpl();
				for (Relation r : sourceRelation) {
					int type =r.getType();
					if(type==Relation.SOURCE2ETL){
						int etl_id = r.getEtl_id();
						ETL etlByID = etlDao.getETLByID(etl_id);
						if(map.get(Relation.SOURCE2ETL)!=null){
							map.get(Relation.SOURCE2ETL).add(etlByID);
						}else{
							List<Object> list =new ArrayList<Object>();
							list.add(etlByID);
							map.put(Relation.SOURCE2ETL, list);
						}
					}else if(type==Relation.SOURCE2FRONT){
						String front_id = r.getFront_id();
						Front frontByID = frontDao.getFrontByID(front_id);
						if(map.get(Relation.SOURCE2FRONT)!=null){
							map.get(Relation.SOURCE2FRONT).add(frontByID);
						}else{
							 List<Object> list = new ArrayList<Object>();
							 list.add(frontByID);
							 map.put(Relation.SOURCE2FRONT,list);
						}
					}
					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		
		return map;
	}



	public List<ETL> getETLsByParams(ETL e) {
		List<ETL> etls=null;
		try{
			ETLDao etldao =new ETLDaoImpl();
			etls= etldao.getETLsByCondition(e);
		}catch(SQLException x){
			x.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		return etls;
	}

	public List<Source> getSourcesInUse() {
		List<Source> allSources = null;
		try {
			SourceDao sourcedao = new SourceDaoImpl();
			allSources = sourcedao.getSourcesInUse();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allSources;
		
	}

	public List<Source> getSourcesNotInUse() {
		List<Source> allSources = null;
		try {
			SourceDao sourcedao = new SourceDaoImpl();
			allSources = sourcedao.getSourcesNotInUse();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		return allSources;
		
	}

	public int updateETL(int etl_id, String owner, String remark) {
		int success=0;
		
		try {
			ETLDao etlDao = new ETLDaoImpl();
			success = etlDao.updateETL(etl_id, owner, remark);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JDBCUtils.close();
		}
		return success;
	}

	

}
