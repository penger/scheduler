package com.diaodu.dao.impl;

import com.diaodu.dao.MonitorDao;
import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Monitor;
import com.diaodu.domain.Oraclesid;
import com.google.common.base.Strings;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by GP39 on 2016/9/26.
 */
public class MonitorDaoImpl implements MonitorDao{

    @Override
    public List<Monitor> getMonitorByDateAndTableName(String start_date, String end_date, String table_name,String type) throws SQLException {
        Connection connection = JDBCUtils.getConnection();
        QueryRunner queryRunner = new QueryRunner();
        String type_condition="";
        if(!Strings.isNullOrEmpty(type)){
            type_condition=" and type like '%"+type+"%' ";
        }
        String table_conditon="";
        if(!Strings.isNullOrEmpty(table_name)){
            table_conditon=" and table_name like '%"+table_name+"%' ";
        }

        String sql = "select * from monitor where count_date >="+start_date+" and count_date <="+end_date +type_condition+table_conditon +" order by count_date desc , count desc ";
        List<Monitor> list = queryRunner.query(connection,sql, new BeanListHandler<>(Monitor.class));
        return list;
    }
}
