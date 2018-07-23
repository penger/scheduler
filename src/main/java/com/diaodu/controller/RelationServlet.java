package com.diaodu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.domain.ETL;
import com.diaodu.domain.Front;
import com.diaodu.domain.Relation;
import com.diaodu.domain.Source;
import com.diaodu.service.RelationService;
import com.diaodu.util.GetUTF8Utils;

/*
 * 2016-6-26 14:37:51
 * 用于查看前端到etl的依赖
 */
public class RelationServlet  extends HttpServlet{
	

	private static final long serialVersionUID = 4316315500196031344L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		RelationService relationService = new RelationService();
		String op = req.getParameter("op");
		if("searchSource".equals(op)){
			String sourceName=req.getParameter("tableName");
			List<Source> list = relationService.getSourcesByName(sourceName);
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else if("searchHive".equals(op)){
			String hiveName=req.getParameter("tableName");
			List<ETL> list = relationService.getHivesByName(hiveName);
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else if("addRelation".equals(op)){
			Relation relation = getRelationFromRequest(req);
			int count = relationService.insertRelation(relation);
			out.print(count);
		}else if("deleteRelation".equals(op)){
			Relation relation = getRelationFromRequest(req);
			int count = relationService.deleteRelation(relation);
			out.print(count);

		}
		else{
			req.getRequestDispatcher("/WEB-INF/view/ob/index.jsp").forward(req, resp);
		}
		
	}

	private Relation getRelationFromRequest(HttpServletRequest req){
		Relation r = new Relation();
		Integer etl_id=change2number(req.getParameter("etl_id"));
		if(etl_id!=null	){
			r.setEtl_id(etl_id);
		}
		String front_id=req.getParameter("front_id");
		if(front_id!=null){
			r.setFront_id(front_id);
		}
		Integer source_id = change2number(req.getParameter("source_id"));
		if(source_id!=null){
			r.setSource_id(source_id);
		}
		Integer type = change2number(req.getParameter("type"));
		if(type!=null){
			r.setType(type);
		}
		
		return r;
	}
	
	private Integer change2number(String s){
		if(s==null||s.length()==0){
			return null;
		}else{
			return Integer.parseInt(s);
		}
	}
	
	
	
//	private Front getFrontFromRequest(HttpServletRequest req) {
//		Front f =new Front();
//		f.setLabel(GetUTF8Utils.getUTF8(req.getParameter("label")));
//		f.setUrl(GetUTF8Utils.getUTF8(req.getParameter("url")));
//		f.setCreator(GetUTF8Utils.getUTF8(req.getParameter("creator")));
//		f.setRemark(GetUTF8Utils.getUTF8(req.getParameter("remark")));
//		return f;
//	}
	
	private ETL getETLFromRequest(HttpServletRequest req) {
		ETL e =new ETL();
		e.setEtl_type(Integer.parseInt(req.getParameter("etl_type")));
		e.setEtl_crontab_info(GetUTF8Utils.getUTF8(req.getParameter("etl_crontab_info")));
		e.setEtl_script(req.getParameter("etl_script"));
		e.setHive_table(req.getParameter("hive_table"));
		e.setOwner(GetUTF8Utils.getUTF8(req.getParameter("owner")));
		e.setRemark(GetUTF8Utils.getUTF8(req.getParameter("remark")));
		return e;
	}
	
	private Source getSourceFromRequest(HttpServletRequest req) {
		Source s =new Source();
		s.setDb_ip(GetUTF8Utils.getUTF8(req.getParameter("db_ip")));
		s.setDb_name(GetUTF8Utils.getUTF8(req.getParameter("db_name")));
		s.setDb_port(req.getParameter("db_port"));
		s.setHive_table_name(req.getParameter("hive_table_name"));
		s.setIs_inc(Integer.parseInt(req.getParameter("is_inc")));
		s.setMap_sign(req.getParameter("map_sign"));
		s.setPassword(req.getParameter("password"));
		s.setRemark(GetUTF8Utils.getUTF8(req.getParameter("remark")));
		s.setSystem_id(req.getParameter("system_id"));
		s.setTable_name(req.getParameter("table_name"));
		s.setUser_name(req.getParameter("user_name"));
		return s;
	}
	


}
