package com.diaodu.dao;

import com.diaodu.domain.Monitor;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by GP39 on 2016/9/26.
 */
public interface MonitorDao {

    public List<Monitor> getMonitorByDateAndTableName(String start_date,String end_date,String table_name,String type) throws SQLException;

}
