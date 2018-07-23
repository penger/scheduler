package com.diaodu.domain;

public class ScheduleMail {



	private String from;
	private String to;
	private String cc;
	private String username;
	private String password;
	private String subject;
	private String content;
	private String host;
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "ScheduleMail [from=" + from + ", to=" + to + ", cc=" + cc + ", username=" + username + ", password="
				+ password + ", subject=" + subject + ", content=" + content + ", host=" + host + "]";
	}

	
	
	
}
