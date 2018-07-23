package com.diaodu.domain;

/**
 * Created by GP39 on 2016/9/26.
 */
public class Monitor {

    private int etl_id;
    private String table_name;
    private String type;
    private int count;
    private String count_date;
    private String insert_date;


    public int getEtl_id() {
        return etl_id;
    }

    public void setEtl_id(int etl_id) {
        this.etl_id = etl_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCount_date() {
        return count_date;
    }

    public void setCount_date(String count_date) {
        this.count_date = count_date;
    }

    public String getInsert_date() {
        return insert_date;
    }

    public void setInsert_date(String insert_date) {
        this.insert_date = insert_date;
    }


    @Override
    public String toString() {
        return "Monitor{" +
                "etl_id=" + etl_id +
                ", table_name='" + table_name + '\'' +
                ", type='" + type + '\'' +
                ", count=" + count +
                ", count_date='" + count_date + '\'' +
                ", insert_date='" + insert_date + '\'' +
                '}';
    }
}
