package com.diaodu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.core.GlobalMap;
import com.diaodu.domain.Batch;
import com.diaodu.domain.History;
import com.diaodu.domain.Oraclesid;
import com.diaodu.domain.Task;
import com.diaodu.service.IndexService;
import com.diaodu.util.DateUtils;
import com.diaodu.util.FormatUtil;

/**
 * 功能:页面导航  数据库配置查看
 * @author GP39
 *
 */
public class IndexServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		IndexService indexService = new IndexService();
		String op = req.getParameter("op");
		if(("oraclelist").equals(op)){
			List<Oraclesid> list = indexService.getAllOracle();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
			out.close();
		}else if("batchlist".equals(op)){
			List<Batch> list = indexService.getAllBatch();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
			out.close();
		}else if("tasklist".equals(op)){
			List<Task> list = indexService.getAllTask();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
			out.close();
		}else if("refreshMap".equals(op)){
			GlobalMap.refeshMap();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("map", GlobalMap.getMap());
			String json2 = FormatUtil.formatJson(json.toJSONString());
			
			System.out.println(json.toJSONString());
			out.print(json2);
			out.flush();
			out.close();
		}else if("history".equals(op)){
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			int pageSize=20;
			List<History> list = indexService.getHistory(pageNum, pageSize);
			int length = indexService.getHistoryLength();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			json.put("length", length);
			int pageLength=(length+pageSize-1)/pageSize;
			json.put("pageLength", pageLength);
			out.print(json);
			out.close();
		}else if("email".equals(op)){
			req.getRequestDispatcher("/WEB-INF/view/mail/index.jsp");
		}else{
            Date d = DateUtils.getDateCalculate(-1);
            String yesterday = new SimpleDateFormat("yyyyMMdd").format(d);
            req.setAttribute("yesterday",yesterday);
			req.getRequestDispatcher("/WEB-INF/view/index.jsp").forward(req, resp);
		}
		
	}
	
}
