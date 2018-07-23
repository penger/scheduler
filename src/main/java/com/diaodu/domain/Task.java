package com.diaodu.domain;

/**
 * task 任务运行类
 * update : 2016-8-17 13:48:59
 * 			:添加任务重试功能,日志配置,最后一次执行花费的时间
 * 		:2017-2-9 15:55:25
 * 			:添加了 指定其他用户的调度策略
 */
public class Task {
	
	private int id;
	private int seq;
	private String tname;
	private String tdesc;
	private String batchid;
	private String tasktype;
	private String actor;
	private String commandpath;
	private String command;
	private String args;
	private int flag;
	private String status;
	private String color;
	//平均运行时间长度
	private int avaragetime;
	//最后一次运行花费秒数
	private int usedtime;
	//重试次数,默认为1次
	private int retry;
	//任务对应的主机名
	private String taskhost;
	//日志地址
	private String logpath;
	//任务最后一次更新时间<用于记录task最后一次运行的时间>
	private String updatetime;

	private int times=0;


	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public String getTaskhost() {
		return taskhost;
	}

	public void setTaskhost(String taskhost) {
		this.taskhost = taskhost;
	}

	public String getLogpath() {
		return logpath;
	}

	public void setLogpath(String logpath) {
		this.logpath = logpath;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public int getUsedtime() {
		return usedtime;
	}

	public void setUsedtime(int usedtime) {
		this.usedtime = usedtime;
	}

	public Task() {
		super();
		id=-1;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getTdesc() {
		return tdesc;
	}
	public void setTdesc(String tdesc) {
		this.tdesc = tdesc;
	}
	public String getBatchid() {
		return batchid;
	}
	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}
	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}
	public String getCommandpath() {
		return commandpath;
	}
	public void setCommandpath(String commandpath) {
		this.commandpath = commandpath;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getStatus() {
		if(flag==0){
			return "无效";
		}else if(flag==1){
			return "已经完成等待下次执行";
		}else if(flag==2){
			return "正在运行";
		}else if(flag==3){
			return "运行失败";
		}else if(flag==4){
			return "重试中";
		}else if(flag==5){
			return "待运行";
		}
		else{
			return "未知状态";
		}
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getColor() {
		if(flag==0){
			return "grey";
		}else if(flag==1){
			return "green";
		}else if(flag==2){
			return "yellow";
		}else if(flag==3){
			return "red";
		}else if(flag==4){
			return "#d2691e";
		}else if(flag==5){
			return "#7fffd4";
		}
		else{
			return "blue";
		}
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getAvaragetime() {
		return avaragetime;
	}
	public void setAvaragetime(int avaragetime) {
		this.avaragetime = avaragetime;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Override
	public String toString() {
		return "Task{" +
				"id=" + id +
				", seq=" + seq +
				", tname='" + tname + '\'' +
				", tdesc='" + tdesc + '\'' +
				", batchid='" + batchid + '\'' +
				", tasktype='" + tasktype + '\'' +
				", actor='" + actor + '\'' +
				", commandpath='" + commandpath + '\'' +
				", command='" + command + '\'' +
				", args='" + args + '\'' +
				", flag=" + flag +
				", status='" + status + '\'' +
				", color='" + color + '\'' +
				", avaragetime=" + avaragetime +
				", usedtime=" + usedtime +
				", retry=" + retry +
				", taskhost='" + taskhost + '\'' +
				", logpath='" + logpath + '\'' +
				", updatetime='" + updatetime + '\'' +
				", times=" + times +
				'}';
	}
}
