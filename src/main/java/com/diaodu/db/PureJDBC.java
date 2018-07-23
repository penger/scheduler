package com.diaodu.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class PureJDBC {

	public static void main(String[] args) throws SQLException {
		String url="jdbc:mysql://10.201.48.3:3306/scheduler?user=bl&password=bigdata";
//		String url="jdbc:mysql://localhost:3306/scheduler?user=root&password=root";

		Connection con = DriverManager.getConnection(url);
		Statement stmt = con.createStatement();
		ResultSet resultSet = stmt.executeQuery("select * from front");
		while(resultSet.next()){
			String str = resultSet.getString(2);
			System.out.println(str);
		}
		resultSet.close();
		stmt.close();
		con.close();

	}

}
