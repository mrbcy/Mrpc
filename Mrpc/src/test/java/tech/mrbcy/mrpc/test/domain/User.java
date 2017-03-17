package tech.mrbcy.mrpc.test.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.NEW;

import tech.mrbcy.mrpc.test.enumm.UserType;

public class User {
	private int userId; 
	private String userName;
	private boolean lockState;
	private UserType userType;
	private List<String> addresses = new ArrayList<String>();
	private Map<String, String> favoriteMap = new HashMap<String, String>();
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
	public boolean isLockState() {
		return lockState;
	}
	public void setLockState(boolean lockState) {
		this.lockState = lockState;
	}
	public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public List<String> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
	public Map<String, String> getFavoriteMap() {
		return favoriteMap;
	}
	public void setFavoriteMap(Map<String, String> favoriteMap) {
		this.favoriteMap = favoriteMap;
	}
	
	public void addAddress(String address){
		addresses.add(address);
	}
	
	public void putFavor(String key,String value){
		favoriteMap.put(key, value);
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName
				+ ", lockState=" + lockState + ", userType=" + userType
				+ ", addresses=" + addresses + ", favoriteMap=" + favoriteMap
				+ "]";
	}
	
	
}