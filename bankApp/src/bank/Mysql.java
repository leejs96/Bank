package bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mysql {
	
	public Connection conn;
	
	public Mysql() { // db연결
		try {
			String dbURL = "jdbc:mysql://localhost:3306/bank?serverTimezone=UTC&verifyServerCertificate=false&useSSL=true";
			String dbID = "root";
			String dbPassword = "MySQLqlalf7545!";
			
			Class.forName("com.mysql.jdbc.Driver");
			
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
			//stmt = con.createStatement(); 
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
//		e.printStackTrace();
		} 
	}
	
	void connClose() { // 연결끊기
		System.out.println("Connect  Final");
	}
	
}
