package com.diaodu.util;

import java.io.*;
import java.util.Properties;

import com.google.common.base.Strings;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;


/*
 * 功能发邮件
 * 2016-6-24 11:54:02
 * update 2016-10-8 14:13:37 添加加密解密功能
 * update 2016-12-26 10:12:04  加密功能能转移到 Encipherer 类中
 */

public class MailUtil {

	public static void main(String[] args) throws IOException, EmailException {
		//shell请求链接,发送邮件
		HtmlEmail defaultMail = null;
		try {
			defaultMail = MailUtil.createMail("Peng.Gong@bl.com");
		} catch (EmailException e1) {
			e1.printStackTrace();
		}
		defaultMail.setSubject("dfasdfasdfdasf");
		String content = "happpy";
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
		}}


	public static HtmlEmail createMail(String mailList) throws EmailException, IOException {
		InputStream input = MailUtil.class.getClassLoader().getResourceAsStream("mail.properties");

		Properties properties = new Properties();
		properties.load(input);
		String to = (String)properties.get("to");
		String from = (String)properties.get("from");
		String password = (String)properties.get("password");
		String username = (String)properties.get("username");
		HtmlEmail email = new HtmlEmail();
//		email.setSmtpPort(35);
		email.setHostName("10.201.144.67");
		email.setAuthenticator(new DefaultAuthenticator(username, password));
		email.setFrom(from);
		if(!Strings.isNullOrEmpty(mailList)){
			for(int i=0;i<mailList.split("_").length;i++){
				email.addCc(mailList.split("_")[i]);
			}
		}
		if(!Strings.isNullOrEmpty(to)){
			for(int j=0;j<to.split(",").length;j++){
				email.addTo(to.split(",")[j]);
			}
		}
		return email;
	}

}
