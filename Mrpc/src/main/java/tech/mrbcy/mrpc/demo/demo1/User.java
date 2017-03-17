package tech.mrbcy.mrpc.demo.demo1;

import java.util.ArrayList;
import java.util.List;



public class User {
	private int userId;
	private String userName;
	private String pw;
	private List<String> addresses = new ArrayList<String>();
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	
	public void addAddress(String address){
		addresses.add(address);
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", pw="
				+ pw + ", addresses=" + addresses + "]";
	}
	
	
	
}