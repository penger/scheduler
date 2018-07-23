package com.diaodu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.diaodu.dao.RelationDao;
import com.diaodu.dao.SourceDao;
import com.diaodu.dao.impl.ETLDaoImpl;
import com.diaodu.dao.impl.RelationDaoImpl;
import com.diaodu.dao.impl.SourceDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Relation;
import com.diaodu.domain.Source;

public class InitETL {

	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("d://gp//b.txt"))));
		String line= null;
		Map <String,List<String>> map = new HashMap<String,List<String>>();
		String etl_table="";
		Set<String> valueSet= new TreeSet<String>();
		
		while((line=bufferedReader.readLine())!=null){
			System.out.println(line+"-----------");
			if(!line.startsWith("#gongpeng#")){
				etl_table=line;
				map.put(etl_table, new ArrayList<String>());
				System.out.println("增加key:"+etl_table);
			}else{
				line=line.replace("#gongpeng#", "");
				List<String> list = map.get(etl_table);
				list.add(line);
				System.out.println("增加value:"+line);
				valueSet.add(line);
			}
		}
	

		System.out.println("");
		for (String s : valueSet) {
			System.out.println(s);
			
		}
		System.out.println(map);
		
//		try {
//			checkifexist(valueSet);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		addByMap(map);
		
	}
	
	public static void checkifexist(Set<String> set) throws SQLException{
		
		ETLDaoImpl etldao = new ETLDaoImpl();
		SourceDao sourceDao = new SourceDaoImpl();
		RelationDao relationdao = new RelationDaoImpl();
		for (String s : set) {
			ETL e = etldao.getETLByName(s);
			if(e==null){
				Source ss = sourceDao.getOnlySourceByName(s);
				if(ss==null){
					System.out.println("error");
					System.out.println(s);
				}else{
					System.out.println("source:"+s);
				}
			}else{
				System.out.println("etl:"+s);
			}
		}
	}
	
	
	
	
	
	
	//根据map,添加relation到relation表中
	public static void addByMap(Map<String,List<String>> map){
		ETLDaoImpl etldao = new ETLDaoImpl();
		SourceDao sourceDao = new SourceDaoImpl();
		RelationDao relationdao = new RelationDaoImpl();
		Set<Entry<String, List<String>>> entrySet = map.entrySet();
		JDBCUtils.startTransaction();
		try {
			for (Entry<String, List<String>> entry : entrySet) {
				String key = entry.getKey();
				//增加etl
				ETL eeeeee = etldao.getETLByName(key);
				if(eeeeee!=null){
					System.out.println(eeeeee.getHive_table()+"已经存在跳过");
					continue;
				}
				ETL e=new ETL();
				e.setEtl_type(1);
				e.setEtl_script(key+".sh");
				e.setEtl_crontab_info("每日调度");
				e.setHive_table(key);
				e.setOwner("强哥");
				e.setRemark("etl初始化");
				etldao.addETL(e);
				ETL etl = etldao.getETLByName(key);
				int etl_id = etl.getEtl_id();
				List<String> sources = entry.getValue();
				for (String s : sources) {
					Relation r = new Relation();
					Source sss = sourceDao.getOnlySourceByName(s);
					if(sss==null){
						ETL eee = etldao.getETLByName(s);
						int etl_idx = eee.getEtl_id();
						r.setType(1);
						r.setSource_id(etl_idx);
					}else{
						r.setType(0);
						int source_id = sss.getSource_id();
						r.setSource_id(source_id);
					}
					//添加
					r.setEtl_id(etl_id);
					relationdao.addRelation(r);
				}
			}
			JDBCUtils.commit();
		} catch (SQLException e) {
			JDBCUtils.rollback();
			e.printStackTrace();
		}finally {
			JDBCUtils.close();
		}
		
		//增加关系
	}
	
	
	

}
