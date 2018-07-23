package com.diaodu.db;

import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import com.diaodu.domain.Front;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class JDBCUtils {

	private static ComboPooledDataSource  ds= null;
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();


	static {
		try {
			ds = new ComboPooledDataSource();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static Connection getConnection() throws SQLException{
		Connection connection = threadLocal.get();
		if(connection == null){
			connection = ds.getConnection();
			threadLocal.set(connection);
		}
		return connection;
	}
	
	public static void startTransaction(){
		try {
			Connection conn = threadLocal.get();
			if(conn==null){
				conn=getConnection();
				threadLocal.set(conn);
			}
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollback(){
		try{
			Connection conn = threadLocal.get();
			if(conn!=null){
				conn.rollback();
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static void commit(){
		try {
			Connection conn = threadLocal.get();
			if(conn!=null){
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(){
		try {
			Connection conn = threadLocal.get();
			if(conn!=null){
				conn.close();
				threadLocal.remove();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static DataSource getDataSource(){
		return ds;
	}

	public static void main(String[] args) throws Exception {
		String PROP = "/jdbc.properties";
		Properties properteis = new Properties();
				InputStream inputStream = JDBCUtils.class.getResourceAsStream(PROP);
				properteis.load(inputStream);
		properteis.list(System.out);
		DbUtils.loadDriver(properteis.getProperty(Constants.DRIVER));
		Connection connection = ds.getConnection();
		QueryRunner runner = new QueryRunner();


		List<Batch> list = runner.query(connection,"select * from batch order by id asc", new BeanListHandler<>(Batch.class));
		//BigDecimal avg = (BigDecimal)runner.query(connection, "select avg(spend_time) from history where exit_code=0 and script='import_app.shx'" ,new ScalarHandler());
//		Object list=runner.query(connection, "select * from front" ,new BeanListHandler<>(Front.class));
//		System.out.println(list);
		//System.out.println(avg.intValue());
		
//		List<Batch> list = runner.query(connection,"select * from batch", new BeanListHandler<>(Batch.class));
//		for (Batch batch : list) {
//			System.out.println(batch);
//		}
		DbUtils.closeQuietly(connection);

	}

}
