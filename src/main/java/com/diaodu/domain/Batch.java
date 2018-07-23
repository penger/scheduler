package com.diaodu.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 批次任务开始时间
 * @author GP39
 * @DATE 2016-2-22 15:50:02
 * @update 2017-2-9 14:19:10
 * 替换为corn表达式支持
 */
public class Batch {
	
	//批次id
	private Integer id;
	//批次名称
	private String bname;
	//批次描述
	private String bdesc;
	//上一个批次的id
	private int beforeid;
	//下一个批次的id
	private int afterid;
	
	//当前任务执行的批次
	private String taskdate;
	
	//任务执行开始时间
	private String starttime;
	
	//批次完成标志 0失效状态 1可执行状态2完成状态
	private int flag;
	
	private String status;
	
	//两次调度需要间隔的时间(分钟数)
	private Integer timeinterval;
	//调度发生的第一次时间
	private String startdate;
	//调度结束的时间
	private String enddate;
	//任务调度类型 day,month,year
	private String btype;
	//任务下次的执行时间
	private String nextexecutetime;

	private String readabletime;

	public String getReadabletime() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new SimpleDateFormat("yyyyMMddHHmm").parse(nextexecutetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "错误日期";
	}


	private int threadcount;

	//cron 表达式
	private String cronexpression;
	//cron 解释
	private String cronexplain;

	public String getCronexpression() {
		return cronexpression;
	}

	public void setCronexpression(String cronexpression) {
		this.cronexpression = cronexpression;
	}

	public String getCronexplain() {
		return cronexplain;
	}

	public void setCronexplain(String cronexplain) {
		this.cronexplain = cronexplain;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	public String getBdesc() {
		return bdesc;
	}
	public void setBdesc(String bdesc) {
		this.bdesc = bdesc;
	}

	public String getTaskdate() {
		return taskdate;
	}
	public void setTaskdate(String taskdate) {
		this.taskdate = taskdate;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public Integer getTimeinterval() {
		return timeinterval;
	}
	public void setTimeinterval(Integer timeinterval) {
		this.timeinterval = timeinterval;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	
	public String getBtype() {
		return btype;
	}
	public void setBtype(String btype) {
		this.btype = btype;
	}
	public String getNextexecutetime() {
		return nextexecutetime;
	}
	public void setNextexecutetime(String nextexecutetime) {
		this.nextexecutetime = nextexecutetime;
	}

	public int getBeforeid() {
		return beforeid;
	}
	public void setBeforeid(int beforeid) {
		this.beforeid = beforeid;
	}
	public int getAfterid() {
		return afterid;
	}
	public void setAfterid(int afterid) {
		this.afterid = afterid;
	}
//	if( flag==0失效状态 1可执行状态2完成状态)
	public String getStatus() {
		if(flag==0){
			return "无效";
		}else if(flag==1){
			return "已经完成等待下次执行";
		}else if(flag==2){
			return "正在运行";
		}else if(flag==3){
			return "运行失败";
		}else{
			return "未知状态";
		}
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getThreadcount() {
		return threadcount;
	}
	public void setThreadcount(int threadcount) {
		this.threadcount = threadcount;
	}


	@Override
	public String toString() {
		return "Batch{" +
				"id=" + id +
				", bname='" + bname + '\'' +
				", bdesc='" + bdesc + '\'' +
				", beforeid=" + beforeid +
				", afterid=" + afterid +
				", taskdate='" + taskdate + '\'' +
				", starttime='" + starttime + '\'' +
				", flag=" + flag +
				", status='" + status + '\'' +
				", timeinterval=" + timeinterval +
				", startdate='" + startdate + '\'' +
				", enddate='" + enddate + '\'' +
				", btype='" + btype + '\'' +
				", nextexecutetime='" + nextexecutetime + '\'' +
				", threadcount=" + threadcount +
				", cronexpression='" + cronexpression + '\'' +
				", cronexplain='" + cronexplain + '\'' +
				'}';
	}
}
