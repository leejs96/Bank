package bank;

public class Accounts {
	private String name;
	private String id;
	private String account;
	private int accPW;
	private int balance;
	
	public Accounts(String name, String id, String account, int accPW, int balance) {
		this.name = name;
		this.id = id;
		this.account = account;
		this.accPW = accPW;
		this.balance = balance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getAccPW() {
		return accPW;
	}

	public void setAccPW(int accPW) {
		this.accPW = accPW;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
}
