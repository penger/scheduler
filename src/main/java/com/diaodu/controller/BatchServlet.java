package com.diaodu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.core.GlobalMap;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Task;
import com.diaodu.service.BatchService;
import com.diaodu.util.DateUtils;
/**
 * 功能 任务组合
 * @author GP39
 *
 */

public class BatchServlet extends HttpServlet {

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		BatchService batchService = new BatchService();
		String op = req.getParameter("op");
		if("create".equals(op)||"c".equalsIgnoreCase(op)){
			Batch batch = getBeanFromReqeust(req);
			int rows = batchService.addBatch(batch);
			String message="add "+rows+"  rows! ";
			writer.print(message);
			writer.close();
		}else if("update".equals(op)||"u".equalsIgnoreCase(op)){
			Batch batch = getBeanFromReqeust(req);
			int rows = batchService.updateBatch(batch);
			writer.println(rows);
			writer.close();
		}else if("delete".equals(op)||"d".equalsIgnoreCase(op)){
			String id = req.getParameter("id");
			int row = batchService.deleteBatch(Integer.parseInt(id));
			writer.println(row);
			writer.close();
		}else if("read".equals(op)||"r".equalsIgnoreCase(op)){
			List<Batch> list = new LinkedList<Batch>();

			Map<Integer, List<Batch>> map = GlobalMap.getMap();
			Set<Entry<Integer, List<Batch>>> entrySet = map.entrySet();
			Iterator<Entry<Integer, List<Batch>>> iterator = entrySet.iterator();
			while(iterator.hasNext()){
				Entry<Integer, List<Batch>> next = iterator.next();
				List<Batch> bs = next.getValue();
//				Integer key = next.getKey();
//				System.out.println("----------------"+key+"------------");
//				System.out.println(bs);
//				System.out.println("-===================");
				list.addAll(bs);
			}

			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			out.print(json);
		}else if("toCreatePage".equals(op)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String d = sdf.format(new Date());
			req.setAttribute("today",d);
			req.setAttribute("nextexecute",d+"0800");
			req.getRequestDispatcher("/WEB-INF/view/batch/create.jsp").forward(req, resp);
		}else if("toUpdatePage".equals(op)){
			String id = req.getParameter("id");
			Batch batch = batchService.getBatchByID(id);
			req.setAttribute("batch", batch);
			req.getRequestDispatcher("/WEB-INF/view/batch/update.jsp").forward(req, resp);
		}else if("toEditTaskPage".equals(op)){
			String id = req.getParameter("id");
			List<Task> tasklist = batchService.getAllTaskByBatchID(Integer.parseInt(id));
			req.setAttribute("id", id);
			req.setAttribute("tasks", tasklist);
			req.setAttribute("length", tasklist.size());
			req.getRequestDispatcher("/WEB-INF/view/task/edit.jsp").forward(req, resp);
		}else if("toExecutePage".equals(op)){
			String id = req.getParameter("id");
			List<Task> tasklist = batchService.getAllTaskByBatchID(Integer.parseInt(id));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String one_day_before = sdf.format(DateUtils.getDateCalculate(-1));
			req.setAttribute("one_day_before", one_day_before);
			req.setAttribute("id", id);
			req.setAttribute("tasks", tasklist);
			
			JSONObject json = new JSONObject();
			json.put("tasks", tasklist);
			req.setAttribute("jsontasks", json);
			
			req.setAttribute("taskdate", req.getParameter("taskdate"));
			req.getRequestDispatcher("/WEB-INF/view/task/execute.jsp").forward(req, resp);
		}else{
			//刷新map,显示页面
			GlobalMap.refeshMap();
			req.getRequestDispatcher("/WEB-INF/view/batch/list.jsp").forward(req, resp);
		}
		
	}
	
	
	private Batch getBeanFromReqeust(HttpServletRequest req){
		Batch b = new Batch();
		if(req.getParameter("id")!=null){
			b.setId(Integer.parseInt(req.getParameter("id")));
		}
		b.setAfterid(Integer.parseInt(req.getParameter("afterid")));
		b.setBeforeid(Integer.parseInt(req.getParameter("beforeid")));
		b.setBdesc(ifNullBlank(req.getParameter("bdesc")));
		b.setFlag(Integer.parseInt(req.getParameter("flag")));
		b.setBname(ifNullBlank(req.getParameter("bname")));
		b.setNextexecutetime(ifNullBlank(req.getParameter("nextexecutetime")));
		b.setCronexpression(ifNullBlank(req.getParameter("cronexpression")));
		b.setCronexplain(ifNullBlank(req.getParameter("cronexplain")));
		return b;
	}
	
	private String ifNullBlank(Object object){
		if (object==null){
			return "-";
		}else{
			try {
				return URLDecoder.decode(""+object, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "";
		}
		
	}
	
}
