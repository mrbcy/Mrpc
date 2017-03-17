package tech.mrbcy.mrpc.test.domain;

import java.util.List;

public class UserListPack {
	List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "UserListPack [users=" + users + "]";
	}
	
	
}
