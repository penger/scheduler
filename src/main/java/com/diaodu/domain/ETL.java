package com.diaodu.domain;

public class ETL {
	
	private int etl_id;
	private int etl_type;
	private String etl_script;
	private String etl_crontab_info;
	private String hive_table;
	private String owner;
	private String remark;
	private String create_time;
	//add 2016-7-25 15:14:22 是否已经加入调度
	private int is_scheduling;
	private String etl_color;
	private String scheduling_color;
	private String crontab_info_color;
	
	
	//新建对象的时候默认一个任务类型
	public ETL() {
		super();
		etl_type=100;
	}
	//转换为文字
	private String etl_type_string;
	
	public int getEtl_id() {
		return etl_id;
	}
	public void setEtl_id(int etl_id) {
		this.etl_id = etl_id;
	}
	public String getEtl_script() {
		return etl_script;
	}
	public void setEtl_script(String etl_script) {
		this.etl_script = etl_script;
	}
	public String getEtl_crontab_info() {
		return etl_crontab_info;
	}
	public void setEtl_crontab_info(String etl_crontab_info) {
		this.etl_crontab_info = etl_crontab_info;
	}
	public String getHive_table() {
		return hive_table;
	}
	public void setHive_table(String hive_table) {
		this.hive_table = hive_table;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getEtl_type() {
		return etl_type;
	}
	public void setEtl_type(int etl_type) {
		this.etl_type = etl_type;
	}
	
	public String getEtl_type_string() {
		if(etl_type==1){
			return "shell";
		}else if(etl_type==0){
			return "oozie";
		}else{
			return "未知";
		}
	}
	public void setEtl_type_string(String etl_type_string) {
		this.etl_type_string = etl_type_string;
	}
	public String getEtl_color() {
		if(etl_type==1){
			return "#7373b9";
		}else if(etl_type==0){
			return "#9f4d95";
		}else{
			return "#afaf61";
		}
	}
	public void setEtl_color(String etl_color) {
		this.etl_color = etl_color;
	}
	public int getIs_scheduling() {
		return is_scheduling;
	}
	public void setIs_scheduling(int is_scheduling) {
		this.is_scheduling = is_scheduling;
	}
	public String getScheduling_color() {
		if(is_scheduling==3){
			return "#bebebe";
		}else if(is_scheduling==1){
			return "#d1e9e9";
		}else if(is_scheduling==0){
			return "#4f9d9d";
		}else {
			return "#f2e6e6";
		}
	}
	public void setScheduling_color(String scheduling_color) {
		this.scheduling_color = scheduling_color;
	}
	public String getCrontab_info_color() {
		
		if(etl_crontab_info.contains("day")){
			return "#0066cc";
		}else if(etl_crontab_info.contains("week")){
			return "#00aeae";
		}else if(etl_crontab_info.contains("month")){
			return "#02c874";
		}else{
			return "#00bb00";
		}
	}
	public void setCrontab_info_color(String crontab_info_color) {
		this.crontab_info_color = crontab_info_color;
	}
	@Override
	public String toString() {
		return "ETL [etl_id=" + etl_id + ", etl_script=" + etl_script + ", etl_crontab_info=" + etl_crontab_info
				+ ", hive_table=" + hive_table + ", owner=" + owner + ", remark=" + remark + ", create_time="
				+ create_time + "]";
	}
	
	
	
	
	
}
