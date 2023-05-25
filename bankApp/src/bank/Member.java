package bank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Member {
	
	String name;
	String id;
	String pw;
	
	// db에서 불러온 이름, 아이디, 비밀번호
	String uName;
	String uID;
	String uPW;
	
	int count;
	int num;
	
	Scanner scan = new Scanner(System.in);
	Users user = new Users(name, id, pw);
	Mysql mysql = new Mysql();
	
	void join() { // 회원가입
		System.out.print("사용자 이름> ");
		name = scan.next();
		
		while(true) {
			System.out.print("아이디 > ");
			id = scan.next();
			userLoad(id);
			if(count == 0) {
				break;
			} else {
				System.out.println("이미 존재하는 아이디입니다.");
			}
		}
		
		System.out.print("비밀번호 > ");
		pw = scan.next();
		
		try {
			String sql = "INSERT INTO users(userName, userID, userPW)" + "VALUES(?, ?, ?)";
			
			PreparedStatement pstmt = mysql.conn.prepareStatement(sql);
			
			pstmt.setString(1, name);
			pstmt.setString(2, id);
			pstmt.setString(3, pw);
			
			pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}
	
	public String memID;
	
	void login() {
		while(true) {
			System.out.println("---- 로그인 ----");
			System.out.print("아이디 > ");
			id = scan.next();
			
			System.out.print("비밀번호 > ");
			pw = scan.next();
			
			userLoad(id);
			
			if(id.equals(uID) & pw.equals(uPW)) {
				System.out.println("<< 로그인 되었습니다. >>");
				user.setName(uName);
				user.setId(uID);
				memID = id;
				break;
			} else {
				System.out.println("** 아이디와 비밀번호가 일치하지 않습니다.");
			}
		}
		
	}
	
	
	void userLoad(String id) { // 아이디 정보 가져오기
		try {
			Statement stmt = (Statement) mysql.conn.createStatement();
	           //save the select statement in a string
            String selectStat="SELECT userName, userID, userPW FROM users WHERE userID = '" + id +"'";

            //stmt.executeUpdate(selectStat);
            
            //create a result set
            ResultSet rows = ((java.sql.Statement) stmt).executeQuery(selectStat);

            //stmt.executeQuery(selectStat);
            
            count = 0;
            
            while (rows.next()) {
                count+=1;
                uName = rows.getString("userName");
                uID = rows.getString("userID");
                uPW = rows.getString("userPW");
                }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
