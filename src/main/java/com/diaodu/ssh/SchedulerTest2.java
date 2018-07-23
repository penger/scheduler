package com.diaodu.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class SchedulerTest2 {
	/**
	   * 远程 执行命令并返回结果调用过程 是同步的（执行完才会返回）
	   * @param host	主机名
	   * @param user	用户名
	   * @param psw	密码
	   * @param port	端口
	   * @param command	命令
	   * @return
	   */
	  public static String exec(String host,String user,String psw,int port,String command){
	    String result="";
	    Session session =null;
	    ChannelExec openChannel =null;
	    try {
	      JSch jsch=new JSch();
//	      com.jcraft.jsch.Session session2 = jsch.getSession(user, host, port);
	      session = jsch.getSession(user, host, port);
	      java.util.Properties config = new java.util.Properties();
	      config.put("StrictHostKeyChecking", "no");
	      session.setConfig(config);
	      session.setPassword(psw);
	      session.connect();
	      openChannel = (ChannelExec) session.openChannel("shell");
	      openChannel.setCommand(command);
	      int exitStatus = openChannel.getExitStatus();
	      System.out.println(exitStatus);
	      openChannel.connect();  
	            InputStream in = openChannel.getInputStream();  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(in));  
	            String buf = null;
	            while ((buf = reader.readLine()) != null) {
	            	result+= new String(buf.getBytes("gbk"),"UTF-8")+"\r\n";  
	            }  
	    } catch (JSchException | IOException e) {
	      result+=e.getMessage();
	    }finally{
	      if(openChannel!=null&&!openChannel.isClosed()){
	        openChannel.disconnect();
	      }
	      if(session!=null&&session.isConnected()){
	        session.disconnect();
	      }
	      System.out.println(openChannel.getExitStatus()+"---");
	    }
	    return result;
	  }
	  
	  
	  
	  public static void main(String args[]) throws InterruptedException{
//		for(int i=0;i<100;i++){
//			Thread.sleep(100);
//			System.out.println(exec);	
//		}
		String exec = exec("master", "root", "syj125", 22, "su - gp \n whoami \n whoami \n");
		System.out.println(exec);
//	    String exec2 = exec("master", "gp", "gp", 22, "ps -fe");
//	    String exec2 = exec("master", "gp", "gp", 22, "ps -fe");
//	    System.out.println(exec2);	
	  }
}
