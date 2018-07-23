package com.diaodu.controller;

import com.alibaba.fastjson.JSONObject;
import com.diaodu.core.GlobalMap;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Monitor;
import com.diaodu.domain.Task;
import com.diaodu.service.BatchService;
import com.diaodu.service.MonitorService;
import com.diaodu.util.DateUtils;
import com.diaodu.util.GetOracleTableCount2Mysql;
import com.diaodu.util.SyncOracleFront;
import com.google.common.base.Strings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * oracle 表数据监控,按照日期统计表
 * @author GP39
 *
 */

public class MonitorServlet extends HttpServlet {

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		BatchService batchService = new BatchService();
		String op = req.getParameter("op");
		//刷新前台的数据关系,生成前端对应的一个树形结构
		if("refreshFront".equals(op)){
			try {
				String message = new SyncOracleFront().refreshFrontFromOracle();
				writer.print(message);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("oracleCount2Mysql".equalsIgnoreCase(op)){
			// oracle中的数据量导入到mysql中
			//2016-11-28 09:37:21 添加60天前的数据
			try {
				String message = new GetOracleTableCount2Mysql().insertOracleTableCount2Mysql();
				String deleteMessage = new GetOracleTableCount2Mysql().deleteRecordsNDaysBefore(90);
				writer.print( message+ deleteMessage);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("query".equalsIgnoreCase(op)){

			String start_date=req.getParameter("start_date");
			String end_date = req.getParameter("end_date");
			String table_name=req.getParameter("table_name");
			String type = req.getParameter("type");
			MonitorService monitorService = new MonitorService();
			if(Strings.isNullOrEmpty(start_date)||Strings.isNullOrEmpty(end_date)){
				start_date = new SimpleDateFormat("yyyyMMdd").format(new Date());
				end_date=start_date;
			}
			List<Monitor> monitors = monitorService.getMonitor(start_date, end_date, table_name, type);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("list",monitors);
			writer.print(jsonObject);

		}else {
			req.getRequestDispatcher("/WEB-INF/view/monitor/index.jsp").forward(req, resp);
		}

	}



	
}
