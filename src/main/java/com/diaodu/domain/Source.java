package com.diaodu.domain;

public class Source {
	private int source_id;
	private String table_name;
	private String hive_table_name;
	private String system_id;
	private String db_ip;
	private String db_port;
	private String db_name;
	private String user_name;
	private String password;
	private int is_inc;
	private String map_sign;
	private String remark;
	private String create_time;
	
	public int getSource_id() {
		return source_id;
	}
	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getHive_table_name() {
		return hive_table_name;
	}
	public void setHive_table_name(String hive_table_name) {
		this.hive_table_name = hive_table_name;
	}
	public String getSystem_id() {
		return system_id;
	}
	public void setSystem_id(String system_id) {
		this.system_id = system_id;
	}
	public String getDb_ip() {
		return db_ip;
	}
	public void setDb_ip(String db_ip) {
		this.db_ip = db_ip;
	}
	public String getDb_port() {
		return db_port;
	}
	public void setDb_port(String db_port) {
		this.db_port = db_port;
	}
	public String getDb_name() {
		return db_name;
	}
	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getIs_inc() {
		return is_inc;
	}
	public void setIs_inc(int is_inc) {
		this.is_inc = is_inc;
	}
	public String getMap_sign() {
		return map_sign;
	}
	public void setMap_sign(String map_sign) {
		this.map_sign = map_sign;
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
	@Override
	public String toString() {
		return "Source [source_id=" + source_id + ", table_name=" + table_name + ", hive_table_name=" + hive_table_name
				+ ", system_id=" + system_id + ", db_ip=" + db_ip + ", db_port=" + db_port + ", db_name=" + db_name
				+ ", user_name=" + user_name + ", password=" + password + ", is_inc=" + is_inc + ", map_sign="
				+ map_sign + ", remark=" + remark + ", create_time=" + create_time + "]";
	}
	
	
	

}
