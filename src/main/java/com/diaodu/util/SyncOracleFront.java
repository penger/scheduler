package com.diaodu.util;

import java.sql.*;

/**
 * Created by GP39 on 2016/9/19.
 */
public class SyncOracleFront {


    //运行即复制 oracle 的 dim_sys_url 数据到 front中
    public static void  main(String[] args) throws SQLException {
        new SyncOracleFront().refreshFrontFromOracle();
    }

    public String refreshFrontFromOracle() throws SQLException{
        SyncOracleFront sync = new SyncOracleFront();
        Connection oracleConn = sync.getOracleConnection();
        Connection mysqlConn = sync.getMySqlConnection();

        mysqlConn.setAutoCommit(true);
        Statement statement = mysqlConn.createStatement();
        boolean execute = statement.execute("delete from front");
        if(execute){
            System.out.println("删除成功");
        }


        //设置不是自动提交
        mysqlConn.setAutoCommit(false);
        PreparedStatement preparedStatement = mysqlConn.prepareStatement("insert into front values(?,?,?,?,? ,?,?)");
        Statement oracleStat = oracleConn.createStatement();
        ResultSet oracleResultSet = oracleStat.executeQuery("select * from dim_sys_url");
        int count=0;

        while (oracleResultSet.next()){
            System.out.println("--------------------------------------------------"+count);
            System.out.println(oracleResultSet.getString(1));
            System.out.println(oracleResultSet.getString(2));
            System.out.println(oracleResultSet.getString(3));
            System.out.println(oracleResultSet.getString(4));
            System.out.println(oracleResultSet.getString(5));
            System.out.println(oracleResultSet.getString(6));
            System.out.println(oracleResultSet.getString(7));

            preparedStatement.setString(1,oracleResultSet.getString(1));
            preparedStatement.setString(2,oracleResultSet.getString(2));
            preparedStatement.setString(3,oracleResultSet.getString(3));
            preparedStatement.setString(4,oracleResultSet.getString(4));
            preparedStatement.setString(5,oracleResultSet.getString(5));
            preparedStatement.setString(6,oracleResultSet.getString(6));
            preparedStatement.setString(7,oracleResultSet.getString(7));
            preparedStatement.addBatch();
            count++;
        }
        preparedStatement.executeBatch();
        mysqlConn.commit();
        //关闭mysql连接
        preparedStatement.close();
        mysqlConn.close();
        //关闭oracle 连接
        oracleResultSet.close();
        oracleStat.close();
        oracleConn.close();
        return "delete table then insert:"+count+" records";
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

}
