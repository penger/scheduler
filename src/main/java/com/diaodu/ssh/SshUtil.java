package com.diaodu.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.diaodu.core.CenterConfig;
import com.diaodu.util.MailUtil;
import com.jcraft.jsch.*;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diaodu.domain.Constants;

public class SshUtil {
	
	private static final Logger log = LoggerFactory.getLogger(SshUtil.class);

	private static final int MAX_RETRY=10;
	private static final int RETRY_INTERVAL_MILLSECONDS=10*1000;

	


	  public static Map<String,String> exeLocal(String command){
	  	return exec(
	  			CenterConfig.getValueByKeyWithoutDecode(CenterConfig.LOCAL_IP)
	  			,CenterConfig.getValueByKeyWithoutDecode(CenterConfig.LOCAL_USERNAME)
				,CenterConfig.getValueByKeyWithoutDecode(CenterConfig.LOCAL_PASSWORD)
				,22
				,command);
	  }

	  public static Map<String ,String > exeRemote(String taskhost,String command){
		  return exec(
		  		taskhost
				  ,CenterConfig.getValueByKeyWithoutDecode(taskhost+CenterConfig.POSTFIX_USERNAME)
				  ,CenterConfig.getValueByKeyWithoutDecode(taskhost+CenterConfig.POSTFIX_PASSWORD)
				  ,22
				  ,command);
	  }


	public static void main(String[] args) {
		exeLocal("sudo -u hdfs hdfs -ls /");
	}



	/**
	   * 远程 执行命令并返回结果调用过程 是同步的（执行完才会返回）
	   * @param host	主机名
	   * @param user	用户名
	   * @param psw	密码
	   * @param port	端口
	   * @param command	命令
	   * @return
					key SPEND_TIME
					key EXIT_CODE
					key RUNNING_MESSAGE
					key CMD
					key START_DATE

	 ** 出现 connection reset 错误,故修改方法,如果获取不到连接那么设置最大重试连接数 ,并设置间隔
	 * 如果在 最大尝试次数后仍然出现问题,发送报警邮件

	 */
	  public static Map<String,String> exec(String host,String user,String psw,int port,String command){
	  	log.info("host: "+host+ ", user : "+ user + ", command : "+ command);
		Map <String,String> resultMap = new HashMap<String,String>();
		 resultMap.put(Constants.TASK_DATE,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		long start_time = System.currentTimeMillis();
	    String result="";
	    Session session =null;
	    ChannelExec openChannel =null;
	    try {
	    	JSch jsch=new JSch();
			int i=0;
			while (i < MAX_RETRY){
				if(i!=0){
					try {
						Thread.sleep(RETRY_INTERVAL_MILLSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try{
					session = jsch.getSession(user, host, port);
					java.util.Properties config = new java.util.Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.setPassword(psw);
					session.connect();
					openChannel = (ChannelExec) session.openChannel("exec");
					openChannel.setPty(true);
					openChannel.setCommand(command);
		//			System.out.println("-------------------------");
		//			System.out.println(command);
		//			System.out.println("-------------------------");
					openChannel.connect();
					break;
				}catch (JSchException e) {
					i++;
					e.printStackTrace();
					if(i >= MAX_RETRY){
						HtmlEmail mail = null;
						try {
							mail = MailUtil.createMail("");
							String info="<font size=12 color=red >"+"host: " +
									""+host+ ", user : "+ user + ", command : "+ command+" " +
									" connect fail after "+MAX_RETRY+" times try !!! </font>";
							info = new String(info.getBytes("UTF-8"), "ISO-8859-1");
							mail.setHtmlMsg(info);
							mail.send();
						} catch (EmailException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			InputStream in = openChannel.getInputStream();
			InputStream err = openChannel.getErrStream();
			BufferedReader errReader = new BufferedReader(new InputStreamReader(err));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
			String buf = null;
			//result+="标准输出<br/>";
			while ((buf = reader.readLine()) != null) {
//				result+= new String(buf.getBytes("gbk"),"UTF-8")+"<br/>";  
				result+= new String(buf.getBytes())+"<br/>";  
			}  
			//result+="错误输出<br/>";
			while ((buf = errReader.readLine()) != null) {
				result+= new String(buf.getBytes())+"<br/>";  
			}  
	    } catch (IOException e) {
	    	e.printStackTrace();
	      result+=e.getMessage();
	    }finally{
	      if(openChannel!=null&&!openChannel.isClosed()){
	        openChannel.disconnect();
	      }
	      if(session!=null&&session.isConnected()){
	        session.disconnect();
	      }
	      long end_time = System.currentTimeMillis();
	      resultMap.put(Constants.SPEND_TIME,""+(end_time-start_time));
	      resultMap.put(Constants.EXIT_CODE, ""+openChannel.getExitStatus());
	      resultMap.put(Constants.RUNNING_MESSAGE, result);
	      resultMap.put(Constants.CMD,command);
	      resultMap.put(Constants.START_DATE,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
	    }
	    log.info(resultMap.get(Constants.RUNNING_MESSAGE));
	    return resultMap;
	  }


}
