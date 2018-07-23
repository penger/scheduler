package com.diaodu.util;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 2016-9-26 14:14:39
 * 获取oracle 中数据量到 mysql中
 */
public class GetOracleTableCount2Mysql {
    //test
    public static void main(String[] args) throws Exception {
        new GetOracleTableCount2Mysql().insertOracleTableCount2Mysql();

    }

    public String insertOracleTableCount2Mysql() throws SQLException{
        int success = 0;
        int fail = 0;
        GetOracleTableCount2Mysql sync = new GetOracleTableCount2Mysql();
        Connection oracleConn = sync.getOracleConnection();
        Connection mysqlConn = sync.getMySqlConnection();

        //设置不是自动提交
        mysqlConn.setAutoCommit(false);

        Statement etlStatement = mysqlConn.createStatement();
        PreparedStatement preparedStatement = mysqlConn.prepareStatement("insert into monitor(etl_id,table_name,type,count,count_date) values(?,?,?,?,?)");
        Statement oracleStat = oracleConn.createStatement();
        //mysql中获取所有的表名称
        ResultSet etlResultSet = etlStatement.executeQuery("select etl_id ,hive_table,etl_crontab_info from etl");
        String count_date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //清空之前的数据\
        Statement deleteStatement = mysqlConn.createStatement();
        boolean x = deleteStatement.execute("delete from monitor where count_date = " + count_date);
        deleteStatement.close();

        while (etlResultSet.next()){
            int etl_id = etlResultSet.getInt(1);
            String table_name = etlResultSet.getString(2);
            String type =etlResultSet.getString(3);
            String sql="select count(1) from "+table_name;
            System.out.println(sql+"-------------");
            ResultSet oracleResultSet = null;
            try {
                oracleResultSet = oracleStat.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(oracleResultSet==null){
                fail++;
                continue;
            }
            success++;
            oracleResultSet.next();
            int count = oracleResultSet.getInt(1);

            preparedStatement.setInt(1,etl_id);
            preparedStatement.setString(2,table_name);
            preparedStatement.setString(3,type);
            preparedStatement.setInt(4,count);
            preparedStatement.setString(5,count_date);

            preparedStatement.addBatch();
            oracleResultSet.close();
        }

        etlResultSet.close();
        etlStatement.close();
        preparedStatement.executeBatch();
        preparedStatement.close();
        mysqlConn.commit();
        //关闭mysql连接
        mysqlConn.close();

        oracleStat.close();
        oracleConn.close();

        return "update "+success+" records";
    }









    private Connection getMySqlConnection(){

        Connection connection =null;
        String url="jdbc:mysql://10.201.48.3:3306/scheduler?user=bl&password=bigdata";
//        String url="jdbc:mysql://localhost:3306/scheduler?user=root&password=root";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private Connection getOracleConnection(){
        Connection connection=null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
//            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.201.48.18:1521:report","idmdata","bigdata915");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.201.0.155:1521/hdporcl","idmdata","bigdata915");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //删除 monitor下面N天前的数据
    public String deleteRecordsNDaysBefore(int n) throws SQLException {
        GetOracleTableCount2Mysql sync = new GetOracleTableCount2Mysql();
        Connection mysqlConn = sync.getMySqlConnection();
        mysqlConn.setAutoCommit(false);
        Statement statement = mysqlConn.createStatement();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 0-n);
        Date time = calendar.getTime();
        String endupDate = new SimpleDateFormat("yyyyMMdd").format(time);
        String sql="delete from monitor where count_date < '"+endupDate+"'";
        System.out.println("sql : "+ sql);
        boolean execute = statement.execute(sql);
        mysqlConn.commit();
        if(execute){
            return "delete records "+n+" before "+endupDate+" success";
        }else{
            return "delete records "+n+" before "+endupDate+" fail";
        }
    }
}
