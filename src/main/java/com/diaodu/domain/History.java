package com.diaodu.domain;

public class History {

	private String start_date;
	private String task_date;
	//命令
	private String cmd;
	//脚本,便于和 task 关联 add at 2016-8-1 09:52:51
	private String script;
	private String exit_code;
	private String spend_time;
	private String message;
	
	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getTask_date() {
		return task_date;
	}

	public void setTask_date(String task_date) {
		this.task_date = task_date;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getExit_code() {
		return exit_code;
	}

	public void setExit_code(String exit_code) {
		this.exit_code = exit_code;
	}

	public String getSpend_time() {
		return spend_time;
	}

	public void setSpend_time(String spend_time) {
		this.spend_time = spend_time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public String toString() {
		return "History [start_date=" + start_date + ", task_date=" + task_date + ", cmd=" + cmd + ", exit_code="
				+ exit_code + ", spend_time=" + spend_time + ", message=" + message + "]";
	}
	
}
