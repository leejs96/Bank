package bank;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Message {

	int num;
	boolean login = false;
	Bank bank = new Bank();
	Scanner scan = new Scanner(System.in);
	
	void main() {	
		while(true) {
			System.out.println("-------------- 은행 어플 시작 --------------");
			System.out.println("1 로그인 | 2 회원가입 | 0 프로그램 종료");
			System.out.println("------------------------------------------");
			System.out.print("선택> ");
			try {
				num = scan.nextInt();
			} catch (InputMismatchException e) {}
			
			if (num == 1) {
				bank.mem.login();
				login = true;
				break;
			} else if (num == 2) {
				bank.mem.join();
			} else if (num == 0) {
				System.out.println("프로그램 종료");
				bank.mem.mysql.connClose();
				break;
			} else {
				System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
			}
		}
	}
	
	void menu() {
		while(login) {
			bank.accountLoad(bank.mem.id);
			
			System.out.println("------------------------------------------------------------------------------------------------");
			System.out.println("1 계좌생성 | 2 입금 | 3 출금 | 4 계좌 목록 | 5 계좌 삭제 | 6 로그아웃 | 7 거래내역 | 0 프로그램 종료 ");
			System.out.println("------------------------------------------------------------------------------------------------");
			System.out.print("선택> ");
			try {
				num = scan.nextInt();
			} catch (InputMismatchException e) {}
			
			if (num == 1) {
				bank.dep = true;
				bank.accountOpen();
			} else if (num == 2) {
				bank.dep = true;
				bank.deposit();
			} else if (num == 3) {
				bank.dep = true;
				bank.withdraw();
			} else if (num == 4) {
				bank.accountList();
			} else if (num == 5) {
				bank.delete();
			} else if (num == 6) {
				System.out.println(">> 로그아웃되었습니다.");
				main();
			} else if (num == 7) {
				bank.accountRecord();
			} else if (num == 0) {
				System.out.println("프로그램 종료");
				bank.mem.mysql.connClose();
				break;
			} else {
				System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
			}
			
		}
	}
	
}
