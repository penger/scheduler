package com.diaodu.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Oraclesid;
import com.diaodu.domain.Task;
import com.diaodu.domain.TaskType;

/**
 * 命令生成工具类
 * 用于sqoop导出工具类生成
 * 
 * @author GP39
 *
 */
public class CommandCreateUtil {

	/**
	 * 
	 * export --connect jdbc:oracle:thin:@10.201.48.18:1521:report 
	 * --username idmdata --password bigdata915 
	 * --table IDMDATA.M_DA_UNDELIVER_ORDER_D 
	 * --export-dir /user/hive/warehouse/idmdata.db/m_da_undeliver_order_d/dt=${sdate}/ 
	 * --fields-terminated-by \t 
	 * --input-null-string \\N 
	 * --input-null-non-string \\N
	 * 
	 * 导出hive到oracle
	 * @param oracle
	 * @param tableName
	 * @return
	 */
	public static String createHive2OracleCommand(String tableName){
		String cmd = String.format("sqoop export --connect jdbc:oracle:thin:@%s:%s:%s"
				+ " --username %s --password %s "
				+ "--table %s.%s "
				+ "--export-dir /user/hive/warehouse/idmdata.db/%s/dt=${sdate}/ "
				+ "--fields-terminated-by '\t' "
				+ "--input-null-string '\\N'"
				+ "--input-null-non-string '\\N'",
                "10.201.48.18",
                "1521",
                "report", 
                "idmdata", 
                "bigdata915",
                "IDMDATA",
                tableName.toUpperCase() ,
               	tableName.toLowerCase()
                );

        return cmd;
	}
	
	
	public static String createOracle2HiveCommand(Oraclesid oracle){
		 String cmd = String.format("sqoop import --connect jdbc:oracle:thin:@%s:%s:%s"	+
				 "--hive-drop-import-delims "+
				 " --username %s --password %s "+
				 "--verbose --table %s.%s  " +
                 "--split-by %s  "+
				 "--target-dir /user/hdfs/output/%s  "+
                 "--hive-import --hive-overwrite "+
                 "--hive-database sourcedata "+
                 "--hive-table %s   " +
                 "--hive-partition-key dt "+
                 "--hive-partition-value ${sdate} "+
                 "--fields-terminated-by '\t' "+
                 "-m 1",
                 oracle.getIp(), 
                 oracle.getPort(), 
                 oracle.getDbname(),
                 oracle.getUsername(),
                 oracle.getPassword(),
                 oracle.getSign().toUpperCase(),
                 oracle.getOracle_source_table().toUpperCase(),
                 oracle.getMap_sign(),
                 oracle.getHive_table().toLowerCase(),
                 oracle.getHive_table());
		 return cmd;
	}
	
	public static String createCommandByTask(Task task) {
		String args = task.getArgs();
//		String[] split = args.split("#");
		String cmd="";
		String tasktype = task.getTasktype();
		if(tasktype.equals(TaskType.SHELL)){
			cmd=args;
		}else if(tasktype.equals(TaskType.HDFS_CHECK)){
			//原样输出
			cmd=args;
		}else if(tasktype.equals(TaskType.HDFS_CLEAN)){
			cmd=args;
		}else if(tasktype.equals(TaskType.HIVE_SQL_EXEC)){
			cmd=args;
		}else if(tasktype.equals(TaskType.ORACLE_TABLE_CLEAN)){
			cmd=args;
		}else if(tasktype.equals(TaskType.SQOOP_HIVE2ORACLE)){
			String tableName=args;
			cmd = createHive2OracleCommand(tableName);
		}else if(tasktype.equals(TaskType.SQOOP_ORACLE2HIVE)){
			String oracleTable=args;
			Oraclesid oracle;
			try {
				Connection connection = JDBCUtils.getConnection();
				QueryRunner runner = new QueryRunner();
				oracle = runner.query(connection, "select * from oraclesid where oracle_source_table = ?", new BeanHandler<>(Oraclesid.class),oracleTable);
				if(oracle==null){
					throw new IllegalArgumentException("无此源表");
				}
				cmd = createOracle2HiveCommand(oracle);
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				JDBCUtils.close();
			}
		}else{
			throw new IllegalArgumentException();
		}
		return cmd;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



}
