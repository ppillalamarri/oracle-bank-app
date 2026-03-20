package com.revature.data;


import java.util.List;

import com.revature.beans.User;

public interface UserDao {
	User login(String username, String password);
	User getUser(int account_id);
	User getUserU(int user_id);
	List<User> getUsers();
	void saveUser(User u);
	void updateUser(User u);
	void deleteUser(int user_id, int account_id);
	void deleteUserAccount(int account_id);
	int getMAXID();
	void updateAccount2(int account_id, int user_id);
}

