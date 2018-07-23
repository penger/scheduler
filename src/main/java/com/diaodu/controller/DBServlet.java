package com.diaodu.controller;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.core.GlobalMap;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Batch;
import com.diaodu.service.DBService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 功能 任务组合
 * @author GP39
 *
 */

public class DBServlet extends HttpServlet {

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		DBService dbService = new DBService();
		String op = req.getParameter("op");
		if("get".equals(op)||"g".equalsIgnoreCase(op)){
			String start_date = req.getParameter("start_date");
			String end_date = req.getParameter("end_date");
			String salt = req.getParameter("salt");
			List<String> list = dbService.getTablesByDateCondition(start_date, end_date);
			JSONObject json = new JSONObject();
			json.put("data",list);
			writer.println(json);
			writer.close();
		}else if(op.equals("resetPDATA")){
			dbService.resetDBstatusOfPdata();
		}else{
			//刷新map,显示页面
			GlobalMap.refeshMap();
			req.getRequestDispatcher("/WEB-INF/view/batch/list.jsp").forward(req, resp);
		}
		
	}
	
	

	

	
}
