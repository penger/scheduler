package com.diaodu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.domain.Batch;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Front;
import com.diaodu.domain.Relation;
import com.diaodu.domain.Source;
import com.diaodu.domain.Task;
import com.diaodu.service.BatchService;
import com.diaodu.service.ObserverService;
import com.diaodu.service.TaskService;
import com.diaodu.util.DateUtils;
import com.diaodu.util.GetUTF8Utils;

/*
 * 2016-6-26 14:37:51
 * 用于查看前端到etl的依赖
 */
public class ObserverServlet  extends HttpServlet{
	

	private static final long serialVersionUID = 4316315500196031344L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		ObserverService obService = new ObserverService();
		String op = req.getParameter("op");
		if("source".equals(op)){
			req.getRequestDispatcher("/WEB-INF/view/ob/source.jsp").forward(req, resp);
		}else if("etl".equals(op)){
			req.getRequestDispatcher("/WEB-INF/view/ob/etl.jsp").forward(req, resp);
		}else if("front".equals(op)){
			req.getRequestDispatcher("/WEB-INF/view/ob/front.jsp").forward(req, resp);
		}else if("viewsource".equals(op)){
			String inUse = req.getParameter("in_use");
			List list =null;
			if(inUse==null || inUse.equals("")){
				list = obService.getAllSources();
			}else if(inUse.equals("yes")){
				list = obService.getSourcesInUse();
			}else if(inUse.equals("no")){
				list = obService.getSourcesNotInUse();
			}else{
				list=null;
			}
			//对list分类得到map
			Map<String,String> tree = analysisList(list);
			
			JSONObject json = new JSONObject();
			json.put("list", list);
			json.put("tree", tree);
			out.print(json);
		}else if("viewETL".equals(op)){
			ETL e = getETLFromRequest(req);
			List<ETL> list = obService.getETLsByParams(e);
			JSONObject json = new JSONObject();
			json.put("list", list);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String one_day_before = sdf.format(DateUtils.getDateCalculate(-1));
			json.put("one_day_before", one_day_before);
			out.print(json);
		}else if("getZtree".equals(op)){
			List<Front > list=null;
			list = obService.getAllfronts();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else if ("createETL".equals(op)){
			ETL etl=getETLFromRequest(req);
			int success=obService.addETL(etl);
			out.print(success);
		}else if ("createsource".equals(op)){
			Source source=getSourceFromRequest(req);
			int success=obService.addSource(source);
			out.print(success);
		}else if("updateETL".equals(op)){
			int etl_id = Integer.parseInt(req.getParameter("etl_id"));
			String owner=GetUTF8Utils.getUTF8(req.getParameter("owner"));
			String remark=GetUTF8Utils.getUTF8(req.getParameter("remark"));
			int success = obService.updateETL(etl_id,owner,remark);
			out.print(success);
		}else if("toUpdateETL".equals(op)){
			//根据etl_id获取etl依赖的源表
			int etl_id = Integer.parseInt(req.getParameter("etl_id"));
			String hive_table = req.getParameter("hive_table");
			//获取依赖的oracle表,获取依赖的hive表
			Map<Integer,List<Object>> map=obService.getMapByETLID(etl_id);
			req.setAttribute("isEmpty", map==null?"1":"0");
			if(map!=null){
				req.setAttribute("source2etl", map.get(Relation.SOURCE2ETL));
				req.setAttribute("etl2etl",map.get(Relation.ETL2ETL));
			}
			req.setAttribute("hive_table", hive_table);
			req.setAttribute("etl_id", etl_id);
			req.getRequestDispatcher("/WEB-INF/view/ob/etl_relation.jsp").forward(req, resp);
		}else if("toUpdateFront".equals(op)){
			String front_id = req.getParameter("front_id");
			List<Relation> list = obService.getListByFrontID(front_id);
			JSONObject json = new JSONObject();
			json.put("list",list);
			out.print(json);
		}else if("searchSource".equals(op)){
			int source_id = Integer.parseInt(req.getParameter("source_id"));
			String hive_table= req.getParameter("hive_table");
			Map<Integer, List<Object>> map = obService.getMapBySourceID(source_id);
			req.setAttribute("isEmpty", map==null?"1":"0");
			if(map!=null){
				req.setAttribute("source2front", map.get(Relation.SOURCE2FRONT));
				req.setAttribute("source2etl", map.get(Relation.SOURCE2ETL));
			}
			req.setAttribute("source_id", source_id);
			req.setAttribute("hive_table", hive_table);
			req.getRequestDispatcher("/WEB-INF/view/ob/source_relation.jsp").forward(req, resp);
		}else if("toEditTask".equals(op)){
			//把任务加入到批次的task中
			int etl_id=Integer.parseInt(req.getParameter("etl_id"));
			ETL etl = obService.getETLByID(etl_id);
			Map<String,List<String>> map =new TreeMap<String,List<String>>();
			//获取batchlist 和 batchid 对应的seq列表
			BatchService batchService = new BatchService();
			List<Batch> batchs = batchService.getAllBatch();
			for (Batch b : batchs) {
				List<String> l=new ArrayList<String>();
				int last=1;
				List<Task> task = batchService.getAllTaskByBatchID(b.getId());
				for (Task t : task) {
					int seq = t.getSeq();
					if(!l.contains(seq+"")){
						l.add(seq+"");
						last=seq+1;
					}
				}
				if(l.isEmpty()){
					l.add("1");
				}else{
					l.add(last+"");
				}
				map.put(b.getId()+"", l);
			}
			
			String etl_script = etl.getEtl_script();
			TaskService taskService = new TaskService();
			Task task = taskService.getTaskByScript(etl_script);
			
			
			JSONObject json = new JSONObject();
			json.put("task", task);
			json.put("batchs", batchs);
			json.put("map", map);
			json.put("etl", etl);
			
			req.setAttribute("json", json);

			req.getRequestDispatcher("/WEB-INF/view/ob/etl_task.jsp").forward(req, resp);
			
			
		}else{
			req.getRequestDispatcher("/WEB-INF/view/ob/index.jsp").forward(req, resp);
		}
		
	}

	private Map<String, String> analysisList(List<Source> list) {
		Map<String,String> map =new HashMap<String,String>();
		for (Source s : list) {
			if(s.getSystem_id()!=null){
				map.put(s.getSystem_id(), s.getHive_table_name());
			}else{
				map.put("0", s.getHive_table_name());
			}
		}
		return map;
	}

//	private Front getFrontFromRequest(HttpServletRequest req) {
//		Front f =new Front();
//		f.setLabel(GetUTF8Utils.getUTF8(req.getParameter("label")==null?"":req.getParameter("label")));
//		f.setUrl(GetUTF8Utils.getUTF8(req.getParameter("url")==null?"":req.getParameter("url")));
//		f.setCreator(GetUTF8Utils.getUTF8(req.getParameter("creator")));
//		f.setRemark(GetUTF8Utils.getUTF8(req.getParameter("remark")));
//		return f;
//	}
	
	private ETL getETLFromRequest(HttpServletRequest req) {
		ETL e =new ETL();
		if(req.getParameter("etl_type")!=null && req.getParameter("etl_type").length()>0){
			e.setEtl_type(Integer.parseInt(req.getParameter("etl_type")));
		}
		e.setEtl_crontab_info(GetUTF8Utils.getUTF8(req.getParameter("etl_crontab_info")));
		e.setEtl_script(req.getParameter("etl_script"));
		e.setHive_table(req.getParameter("hive_table"));
		e.setOwner(GetUTF8Utils.getUTF8(req.getParameter("owner")));
		e.setRemark(GetUTF8Utils.getUTF8(req.getParameter("remark")));
		return e;
	}
	
	private Source getSourceFromRequest(HttpServletRequest req) {
		Source s =new Source();
		s.setHive_table_name(req.getParameter("hive_table_name"));
		s.setSystem_id(req.getParameter("system_id"));
		s.setTable_name(req.getParameter("table_name"));
		return s;
	}
	


}
