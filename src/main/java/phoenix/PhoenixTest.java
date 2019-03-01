package phoenix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PhoenixTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
		String url = "jdbc:phoenix:node3:2181";
		Connection conn = DriverManager.getConnection(url);
		Statement statement = conn.createStatement();
		long start = System.currentTimeMillis();
		ResultSet resultSet = statement.executeQuery("select * from customers");
		while(resultSet.next()) {
			String info = resultSet.getString(1);
			System.out.println(info);
		}
		
		resultSet.close();
		statement.close();
		conn.close();

	}

}
