package bank;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Bank { // acounts 사용하기 출금할 때 비번 사용 안함...

	Scanner scan = new Scanner(System.in);
	Member mem = new Member();
	Map<String, Integer> accList = new HashMap<>();
	
	String id;
	String name;
	String pw;
	String acc;
	String accDB;
	String record;
	
	Timestamp ts = new Timestamp(System.currentTimeMillis());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	int accpwDB; // db에서 받아오는 계좌 비밀번호
	int accpw; // 사용자가 입력한 계좌 비밀번호
	int balance;
	int count;
	int num; // 메뉴 선택 번호
	int deposit; // 입금액
	int withdraw; // 출금액
	int length; // 비밀번호 길이 확인
	
	boolean insert = false;
	boolean dep = true;

	Accounts accounts = new Accounts(name, id, accDB, accpwDB, balance);
	
	void accountOpen() { // 계좌 개설
		System.out.println("--------------- 계좌 개설 ---------------");
		while(true) {
			System.out.print("계좌주명> ");
			name = scan.next();
			if(!name.equals(mem.uName)) {
				System.out.println("사용자명과 일치하지 않습니다.");
			} else {
				break;
			}
		}
	
		while(true) {
			System.out.print("계좌번호> ");
			acc = scan.next();
			accountCheck(acc);
			if(count == 0) {
				break;
			} else {
				System.out.println("이미 존재하는 계좌번호입니다.");
			}
		}
		
		while(true) {
			System.out.print("계좌 비밀번호(4자리 입력)> ");
			try {
				accpw = scan.nextInt();
			} catch (Exception e) {
				System.out.println("숫자만 입력해주세요");
				scan = new Scanner(System.in);
			}
			length = (int)Math.log10(accpw)+1;
			if (length == 4) {
				break;
			} else {
				System.out.println("계좌 비밀번호는 4자리 숫자로 입력해주세요.");
			}
		}
		
		while(true) {
			System.out.print("초기 입금액> ");
			try {
				deposit = scan.nextInt();
				if (deposit > 0) {
					break;
				} else {
					System.out.println("올바른 입금액을 입력하세요.");
				}
			} catch (Exception e) {
				System.out.println("올바른 입금액을 입력하세요.");
				scan = new Scanner(System.in);
			}
		}
		
		System.out.print("메모> ");
		record = scan.next();
		
		balance = deposit;
		
		accountInsert();
		accountRecordInsert();
	}
	
	
	void accountInsert() { // 계좌 insert
		try {
			String sql = "INSERT INTO accounts(userID, userName, accountNum, accountPW, balance)" + "VALUES(?, ?, ?, ?, ?)";
			
			PreparedStatement pstmt = mem.mysql.conn.prepareStatement(sql);
			
			id = mem.user.getId();
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setString(3, acc);
			pstmt.setInt(4, accpw);
			pstmt.setInt(5, deposit);
			
			pstmt.executeUpdate();
			pstmt.close();
			
			System.out.println("계좌가 개설되었습니다.");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	void accountCheck(String acc) { // 계좌 중복 확인
		try {
			Statement stmt = (Statement) mem.mysql.conn.createStatement();
            String selectStat="SELECT userID FROM accounts WHERE accountNum = '" + acc +"'";
            ResultSet rows = ((java.sql.Statement) stmt).executeQuery(selectStat);
            
            count = 0;
            
            while (rows.next()) {
                count+=1;
           }
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void deposit() { // 입금
		System.out.println("--------------- 입금 ---------------");
		while(dep) {
			System.out.print("계좌번호 입력 > ");
			acc = scan.next();
			
			if(!accList.containsKey(acc)) {
				System.out.println("일치하는 계좌정보가 없습니다.");
			} else {
				while(true) {
					System.out.print("입금액 > ");
					try {
						deposit = scan.nextInt();
						if (deposit > 0) {
							break;
						} else {
							System.out.println("올바른 입금액을 입력하세요.");
						}
					} catch (Exception e) {
						System.out.println("올바른 입금액을 입력하세요.");
						scan = new Scanner(System.in);
					}
				}
					
				System.out.print("메모> ");
				record = scan.next();
				balance = accList.get(acc) + deposit;
				
				try {
					String sql = "UPDATE accounts SET balance = balance + ? WHERE accountNum =?";
					
					PreparedStatement pstmt = mem.mysql.conn.prepareStatement(sql);
					
					pstmt.setInt(1, deposit);
					pstmt.setString(2, acc);
					
					pstmt.executeUpdate();
					System.out.printf(">> %d원 입금되었습니다.\n", deposit);
					pstmt.close();
					
				} catch (SQLException e) {
					e.printStackTrace();
				} 
				
				withdraw = 0;
				balance = accList.get(acc) + deposit;
				accountRecordInsert();
				dep = false;
				break;
			}
		}
	}
	
	void withdraw() { // 출금하기
		while(dep) {
			System.out.println("--------------- 출금 ---------------");
			System.out.print("계좌번호 입력 > ");
			acc = scan.next();
			if(!accList.containsKey(acc)) {
				System.out.println("일치하는 계좌정보가 없습니다.");
			} else {
				balance = accList.get(acc);
				System.out.print("출금액 > ");
				withdraw = scan.nextInt();
				
				System.out.print("메모> ");
				record = scan.next();
				
				try {
					if (withdraw > 0) {
						if(balance - withdraw < 0) {
							System.out.println("--------------------------------");
							System.out.println("잔액 부족");
							System.out.printf("[잔고 : %d]\n", balance);
						} else {
							try {
								String sql = "UPDATE accounts SET balance = balance - ? WHERE accountNum =?";
								
								PreparedStatement pstmt = mem.mysql.conn.prepareStatement(sql);
								
								pstmt.setInt(1, withdraw);
								pstmt.setString(2, acc);
								
								System.out.printf(">> %d원 출금되었습니다.\n", withdraw);
								pstmt.executeUpdate();
								pstmt.close();
								
							} catch (SQLException e) {
								e.printStackTrace();
							} 
							
							deposit = 0;
							balance = balance - withdraw;
							accountRecordInsert();
							
							dep = false;
							break;
						}
					} else {
						System.out.println("**올바른 입금액을 입력하세요.**");
					}
				} catch (Exception e) {
					System.out.println("**올바른 입금액을 입력하세요.**");
					scan = new Scanner(System.in);
				}
			}
		}
	}
	
	void accountList() { // 계좌목록
		name = mem.user.getName();
		System.out.println("------" + name + "님의 [계좌 목록] ------");
		
		try {
			Statement stmt = (Statement) mem.mysql.conn.createStatement();
			id = mem.user.getId();
		
            String selectStat="SELECT accountNum, balance FROM accounts WHERE userID = '" + id +"'";
            ResultSet rows = ((java.sql.Statement) stmt).executeQuery(selectStat);

            count = 0;
            
            while (rows.next()) {
                count+=1;
                accDB = rows.getString("accountNum");
                balance = rows.getInt("balance");
                
                System.out.println(count + " 계좌 : " + accDB + "  잔고 : " + balance);
           }
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void delete() { // 계좌 삭제
		System.out.print("계좌번호 입력 > ");
		acc = scan.next();
		
		if(!accList.containsKey(acc)) {
			System.out.println("일치하는 계좌정보가 없습니다.");
		} else {
			while(true) {
				System.out.printf("%s님의 계좌(%s)를 삭제하시겠습니까? (1 YES 2 NO)\n", name, acc);
				System.out.print("선택> ");
				try {
					num = scan.nextInt();
					if (num == 1) {
						try {
							String sql = "DROP FROM accounts WHERE accountNum = ?";
							
							PreparedStatement pstmt = mem.mysql.conn.prepareStatement(sql);
							
							pstmt.setString(1, acc);
							
							System.out.printf(">> %s님의 계좌(%s)정보가 삭제되었습니다.", name, acc);
							pstmt.executeUpdate();
							pstmt.close();
							
						} catch (SQLException e) {
							e.printStackTrace();
						} 
						break;
					} else if (num == 2) {
						System.out.println("계좌삭제를 취소하였습니다.");
						break;
					} else {
						System.out.println("올바른 값을 입력해주세요.");
					}
				} catch (Exception e) {
					scan = new Scanner(System.in);
				}
			}
			
		}
	}
	
	void accountLoad(String id) { // 사용자의 계좌 정보 가져오기
		
		try {
			Statement stmt = (Statement) mem.mysql.conn.createStatement();
            String selectStat="SELECT accountNum, accountPW, balance FROM accounts WHERE userID = '" + id +"'";
            ResultSet rows = ((java.sql.Statement) stmt).executeQuery(selectStat);
            
            count = 0;
            
            while (rows.next()) {
                count+=1;
                accDB = rows.getString("accountNum");
                accpwDB = rows.getInt("accountPW");
                balance = rows.getInt("balance");
                
                accList.put(accDB, balance);
                
           }
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	void accountRecordInsert() { // 계좌내역 입력
		try {
			String sql = "INSERT INTO accountrecord(accountNum, accountRecord, deposit, withdraw, balance)" + "VALUES(?, ?, ?, ?, ?)";
			
			PreparedStatement pstmt = mem.mysql.conn.prepareStatement(sql);
			
			pstmt.setString(1, acc);
			pstmt.setString(2, record);
			pstmt.setInt(3, deposit);
			pstmt.setInt(4, withdraw);
			pstmt.setInt(5, balance);
			
			pstmt.executeUpdate();
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	void accountRecord() { // 계좌내역 불러오기
		System.out.print("계좌번호 입력 > ");
		acc = scan.next();
		System.out.println();
		
		System.out.println("|------- 거래날짜 ------- | -- 내용 -- | -- 입금 -- | -- 출금 -- | -- 잔액 -- |");
		try {
			Statement stmt = (Statement) mem.mysql.conn.createStatement();
            String selectStat="SELECT datetime, accountRecord, deposit, withdraw, balance FROM accountRecord WHERE accountNum = '" + acc +"'";
            ResultSet rows = ((java.sql.Statement) stmt).executeQuery(selectStat);
            
            count = 0;
            
            while (rows.next()) {
                count+=1;
                ts = rows.getTimestamp("datetime");
                record = rows.getString("accountRecord");
                deposit = rows.getInt("deposit");
                withdraw = rows.getInt("withdraw");
                balance = rows.getInt("balance");
                
                System.out.printf("%s\t%s\t\t%d\t\t%d\t%d\n", sdf.format(ts), record, deposit, withdraw, balance);
           }
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
