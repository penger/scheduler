package com.diaodu.core;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.diaodu.dao.BatchDao;
import com.diaodu.dao.impl.BatchDaoImpl;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;
import com.diaodu.util.MailUtil;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import static java.awt.SystemColor.info;

public class GlobalMap {
	
	private static Map<Integer,List<Batch>> romMap=null;
	//没有前置或者后置任务
	private static Integer NO_COMPANION=-1;
	
	public static Map <Integer,List<Batch>> getMap(){

		if(romMap==null ){
			romMap=new ConcurrentHashMap<Integer,List<Batch>>();
			if(romMap==null || romMap.isEmpty()){
				BatchDao batchDao =new BatchDaoImpl();
				List<Batch> batchs = null;
				try {
					batchs = batchDao.getAllBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					JDBCUtils.close();
				}
				Map<Integer,Batch> headMap=new TreeMap<Integer,Batch>();
				Map<Integer,Batch> tempMap=new HashMap<Integer,Batch>();
				for (Batch batch : batchs) {
					//没有头任务
					if(batch.getBeforeid()==NO_COMPANION){
						headMap.put(batch.getId(), batch);
					}
					tempMap.put(batch.getId(), batch);
				}
				Iterator<Entry<Integer, Batch>> iterator = headMap.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<Integer, Batch> entry = iterator.next();
					List<Batch> batchList = new LinkedList<Batch>();
					Batch head = entry.getValue();
					getNextAndAppend(batchList, head, tempMap);
					romMap.put(head.getId(), batchList);
				}

			}
		}
		if(romMap==null || romMap.isEmpty()){
			String connection_lost_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
			String lost_info= "cannot get connections !!! </br/>"+connection_lost_time;
			try {
				HtmlEmail mail = MailUtil.createMail("");
				String info="<font size=12 color=red >"+lost_info+" </font>";
				info = new String(info.getBytes("UTF-8"), "ISO-8859-1");
				mail.setHtmlMsg(info);
				mail.send();
			} catch (EmailException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return romMap;
	}
	
	//防止同时修改
	public static synchronized void refeshMap(){
		romMap=null;
		getMap();
	}
	
	
	//递归获取列
	private static void getNextAndAppend(List<Batch> list ,Batch current,Map<Integer,Batch> tempMap){
		int afterid = current.getAfterid();
		list.add(current);
		if(afterid==NO_COMPANION){
			return;
		}else{
			Batch next = tempMap.get(afterid);
			getNextAndAppend(list,next,tempMap);
		}
	}
	
	public static void main(String[] args){
		for(int i=0;i<1 ;i++){
			GlobalMap.refeshMap();
			try {
				System.out.println("sleep ----"+i);
				Thread.sleep(2000);
				System.out.println(GlobalMap.getMap());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
