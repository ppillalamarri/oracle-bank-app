package com.revature.data;


import java.util.List;

import com.revature.beans.Account;
import com.revature.beans.User;

public interface AccountDao {
	Account getAccount(int id);
	List<Account> getAccounts(int account_id);
	List<Account> getPending();
	void saveAccount(Account a);
	void updateBalance(Account a, double amt, boolean positive);
	void deleteAccount(int account_id);
	void updateApproved(int account_id);
	int getMaxID();
}
