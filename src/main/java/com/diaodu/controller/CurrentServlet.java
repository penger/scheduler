package com.diaodu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Oraclesid;
import com.diaodu.domain.Task;
import com.diaodu.service.IndexService;

/**
 * 功能:当前任务查看
 * @author GP39
 *
 */
public class CurrentServlet extends HttpServlet {

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		IndexService allService = new IndexService();
		String op = req.getParameter("op");
		if(StringUtils.isEmpty(op)){
			resp.sendRedirect("index.jsp");
		}else if(op.equals("oraclelist")){
			List<Oraclesid> list = allService.getAllOracle();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else if(op.equals("batchlist")){
			List<Batch> list = allService.getAllBatch();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else if(op.equals("tasklist")){
			List<Task> list = allService.getAllTask();
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else{
//			resp.sendRedirect("./view/index.jsp");
			req.getRequestDispatcher("/WEB-INF/view/index.jsp").forward(req, resp);
		}
		
	}
	
}
