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
import com.diaodu.domain.History;
import com.diaodu.domain.Oraclesid;
import com.diaodu.domain.Task;
import com.diaodu.service.HistoryService;
import com.diaodu.service.IndexService;
import com.google.common.base.Strings;


/**
 * 功能:历史任务查看
 * @author GP39
 *
 */
public class HistoryServlet extends HttpServlet {

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HistoryService historyService = new HistoryService();
		String op = req.getParameter("op");
		if(Strings.isNullOrEmpty(op)){
			req.getRequestDispatcher("/WEB-INF/view/history/index.jsp").forward(req, resp);
		}else if(op.equals("query")){
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			int pageSize=20;
			
			String where_order=getHistorySearchStringFromRequest(req, true);
			System.out.println(where_order);
			String where_count=getHistorySearchStringFromRequest(req, false);
			System.out.println(where_count);

			List<History> list = historyService.getHistory(pageNum, pageSize,where_order);
			int length = historyService.getHistoryLength(where_count);
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
			json.put("list", list);
			json.put("length", length);
			int pageLength=(length+pageSize-1)/pageSize;
			json.put("pageLength", pageLength);
			out.print(json);
			out.close();
		}else if(op.equalsIgnoreCase("del")){
			String days = req.getParameter("days");
			int count = historyService.deleteHistoryNdaysBefore(Integer.parseInt(days));
			PrintWriter writer = resp.getWriter();
			writer.print("delete "+count+" records ");
		}


		else{
			req.getRequestDispatcher("/WEB-INF/view/history/index.jsp").forward(req, resp);
		}
		
	}
	
	//需要排序和不需要排序
	private String getHistorySearchStringFromRequest(HttpServletRequest req,boolean needOrder){
		HistorySearchBean h = new HistorySearchBean();
		
		h.setCmd(req.getParameter("cmd"));
		h.setPageNume(Integer.parseInt(req.getParameter("pageNum")));
		h.setStart_date(req.getParameter("start_date"));
		h.setTime(req.getParameter("time"));
		h.setStart(req.getParameter("start"));
		
		
		String where_content="";
		if(!Strings.isNullOrEmpty(h.getCmd())){
			where_content+=" cmd like '%"+h.getCmd()+"%' and";
		}
		if(!Strings.isNullOrEmpty(req.getParameter("exit_code"))){
			String exit_code=req.getParameter("exit_code");
			where_content+=" exit_code='"+exit_code +"' and";
		}
		if(!Strings.isNullOrEmpty(h.getStart_date())){
			where_content+=" start_date='"+h.getStart_date()+"' and";
		}

		if(where_content.endsWith("and")){
			where_content=where_content.substring(0, where_content.length()-3);
			where_content=" where "+where_content;
		}
		String order_content="";
		if(needOrder){
			if(!Strings.isNullOrEmpty(h.getStart())){
				if(h.getStart().equals("early")){
					order_content+=" order by task_date ";
				}else{
					order_content+=" order by task_date desc ";
				}
			}
			
			if(!Strings.isNullOrEmpty(h.getTime())){
				//已经有了order by 
				if(order_content.length()!=0){
					order_content+=",";
					if(h.getTime().equals("long")){
						order_content+="spend_time desc ";
					}else{
						order_content+="spend_time ";
					}
				}else{
					if(h.getTime().equals("long")){
						order_content+=" order by spend_time desc ";
					}else{
						order_content+=" order by spend_time ";
					}
				}
			}
			return where_content+order_content;
		}
		return where_content;
	}
	
	
}







class HistorySearchBean{
	
	private String start_date;
	private String cmd;
	private int exit_code;
	private String time;
	private String start;
	private int pageNume;
	
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public int getExit_code() {
		return exit_code;
	}
	public void setExit_code(int exit_code) {
		this.exit_code = exit_code;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public int getPageNume() {
		return pageNume;
	}
	public void setPageNume(int pageNume) {
		this.pageNume = pageNume;
	}

}