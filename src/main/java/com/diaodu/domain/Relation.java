package com.diaodu.domain;

public class Relation {
	private int etl_id;
	private int source_id;
	private String front_id;
	//0 代表从源到etl,1代表etl到etl,2代表前段到源,3代表前段到etl
	private int type;

	//为了便于前台展示,开启冗余字段
	private String front_name;

	public String getFront_name() {
		return front_name;
	}

	public void setFront_name(String front_name) {
		this.front_name = front_name;
	}

	public static final int SOURCE2ETL=0;
	public static final int ETL2ETL=1;
	
	public static final int SOURCE2FRONT=2;
	public static final int ETL2FRONT=3;
	
	public int getEtl_id() {
		return etl_id;
	}
	public void setEtl_id(int etl_id) {
		this.etl_id = etl_id;
	}
	public int getSource_id() {
		return source_id;
	}
	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}

	public String getFront_id() {
		return front_id;
	}

	public void setFront_id(String front_id) {
		this.front_id = front_id;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Relation{" +
				"etl_id=" + etl_id +
				", source_id=" + source_id +
				", front_id='" + front_id + '\'' +
				", type=" + type +
				'}';
	}
}
