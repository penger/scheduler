package com.diaodu.domain;

public class Oraclesid {
	private int id;
	private String dbdesc;
	private String category;
	private String ip;
	private String port;
	private String dbname;
	private String username;
	private String password;
	private String sign;
	private String oracle_source_table;
	private String hive_table;
	private String map_sign;
	private String desc;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDbdesc() {
		return dbdesc;
	}
	public void setDbdesc(String dbdesc) {
		this.dbdesc = dbdesc;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
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
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getOracle_source_table() {
		return oracle_source_table;
	}
	public void setOracle_source_table(String oracle_source_table) {
		this.oracle_source_table = oracle_source_table;
	}
	public String getHive_table() {
		return hive_table;
	}
	public void setHive_table(String hive_table) {
		this.hive_table = hive_table;
	}
	public String getMap_sign() {
		return map_sign;
	}
	public void setMap_sign(String map_sign) {
		this.map_sign = map_sign;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "Oraclesid [id=" + id + ", dbdesc=" + dbdesc + ", category=" + category + ", ip=" + ip + ", port=" + port
				+ ", dbname=" + dbname + ", username=" + username + ", password=" + password + ", sign=" + sign
				+ ", oracle_source_table=" + oracle_source_table + ", hive_table=" + hive_table + ", map_sign="
				+ map_sign + ", desc=" + desc + "]";
	}
	
	
}
