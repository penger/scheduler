package com.diaodu.controller;

import com.diaodu.core.GlobalMap;
import com.diaodu.domain.Batch;
import com.diaodu.domain.ScheduleMail;
import com.diaodu.domain.Task;
import com.diaodu.service.BatchService;
import com.diaodu.util.GetUTF8Utils;
import com.diaodu.util.MailUtil;
import com.google.common.base.Strings;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 功能:发邮件
 * @author GP39
 *
 */
public class MailServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private static final long serialVersionUID = -8194525654889919217L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

		resp.setCharacterEncoding("UTF-8");
		String op = req.getParameter("op");
		String cclist=req.getParameter("cc");

		System.out.println("op:" + op + "cc:" +cclist);

		if("task".equals(op)){

			//shell请求链接,发送邮件
			HtmlEmail defaultMail = null;
			try {
				defaultMail = MailUtil.createMail(cclist);
			} catch (EmailException e1) {
				e1.printStackTrace();
			}
			defaultMail.setSubject(GetUTF8Utils.getUTF8(req.getParameter("subject")));
			String content = GetUTF8Utils.getUTF8(req.getParameter("content"));
			content = content.replace("\\n", "<br/>");
			try {
				defaultMail.setHtmlMsg(content);
			} catch (EmailException e) {
				e.printStackTrace();
			}
			try {
				defaultMail.send();
			} catch (EmailException e) {
				e.printStackTrace();
			}

		}else if("email".equals(op)){
			req.getRequestDispatcher("/WEB-INF/view/mail/index.jsp");
		}else if("getTaskInfo".equals(op)){
			try {
				HtmlEmail mail = MailUtil.createMail(cclist);
				String info = getTaskExecuteInfoFromDB("");
				info = new String(info.getBytes("UTF-8"), "ISO-8859-1");
				mail.setHtmlMsg(info);
				mail.send();
			} catch (EmailException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else{
//			resp.sendRedirect("./view/index.jsp");
			req.getRequestDispatcher("/WEB-INF/view/mail/index.jsp").forward(req, resp);
		}
		
	}

	
	private ScheduleMail getBeanFromReqeust(HttpServletRequest req){
		ScheduleMail sm = new ScheduleMail();
		sm.setCc(GetUTF8Utils.getUTF8(req.getParameter("cc")));
		sm.setContent(GetUTF8Utils.getUTF8(req.getParameter("content")));
		sm.setFrom(GetUTF8Utils.getUTF8(req.getParameter("from")));
		sm.setHost(GetUTF8Utils.getUTF8(req.getParameter("host")));
		sm.setUsername(GetUTF8Utils.getUTF8(req.getParameter("username")));
		sm.setPassword(GetUTF8Utils.getUTF8(req.getParameter("password")));
		sm.setSubject(GetUTF8Utils.getUTF8(req.getParameter("subject")));
		sm.setTo(GetUTF8Utils.getUTF8(req.getParameter("to")));
	
		return sm;
	}
	
	
	
	//访问URL,通过查询JOB的信息给邮箱,默认获取当前日期的运行结果
	//TODO 获取任务的完成时间
	//TODO 和每个task的成功和失败状态
	private String getTaskExecuteInfoFromDB(String date) throws EmailException, SQLException{
		StringBuffer buffer = new StringBuffer();
		GlobalMap.refeshMap();
		Map<Integer, List<Batch>> map = GlobalMap.getMap();
		Set<Entry<Integer, List<Batch>>> entrySet = map.entrySet();
		Iterator<Entry<Integer, List<Batch>>> iterator = entrySet.iterator();
		BatchService batchService = new BatchService();
		while(iterator.hasNext()){
			Entry<Integer, List<Batch>> entry = iterator.next();
			buffer.append("头部id为:").append(entry.getKey()).append("<br/>");
			List<Batch> list = entry.getValue();
			
			buffer.append("<table border='1'>");
			buffer.append("<tr><td>描述</td><td>运行状态</td><td>下次运行时间</td><td>失败任务数</td><td>失败任务详情</td></tr>");
			for (Batch batch : list) {
				//获取不是正常状态脚本的数量
				Integer batchid = batch.getId();
				List<Task> failTasks  = batchService.getFailTasksByBatchID(batchid);
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<failTasks.size();i++){
					Task tempTask= failTasks.get(i);
					sb.append(tempTask.getId()).append("--->").append(tempTask.getCommand()).append("<br/>");
				}
				buffer.append("<tr>");
				buffer.append("<td>").append(batch.getBdesc()).append("</td>");
				buffer.append("<td>").append(batch.getStatus()).append("</td>");
				buffer.append("<td>").append(batch.getNextexecutetime()).append("</td>");
				buffer.append("<td>").append(failTasks.size()).append("</td>");
				buffer.append("<td>").append(sb.toString()).append("</td>");
				buffer.append("</tr>");
			}
			buffer.append("</table>");
		}
		return buffer.toString();
	}

	
}
