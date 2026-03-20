package com.revature.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;


import com.revature.beans.Account;
import com.revature.beans.User;
import com.revature.util.ConnectionUtil;

import java.sql.*;

public class AccountFake implements AccountDao{
	private static ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
	private static Logger log = Logger.getLogger(Main.class);
	
	public Account getAccount(int id) {
		Account a = new Account();
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from account_table where account_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				a.setId(rs.getInt(1));
				a.setBalance(rs.getDouble(2));
				a.setType(rs.getString(3));
				a.setApproved(rs.getString(4));
				a.setJoint(rs.getInt(5));
			}
			return a;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;	
	}
	
	// Needs changing add account_owner to the call statement
	// so it can return all the accounts of a certain user
	public List<Account> getAccounts(int account_id) { 					
		List<Account> accounts = new ArrayList<Account>();
		/*
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from account_table where user_type = customer";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Account a = new Account();
				a.setId(rs.getInt(1));
				a.setBalance(rs.getDouble(2));
				a.setType(rs.getString(3));
				a.setApproved(rs.getString(4));
				a.setJoint(rs.getInt(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		return null;
	}
	
	public List<Account> getPending() {
		List<Account> Accounts = new ArrayList<Account>();
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from account_table where approved = 'pending'";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Account a = new Account();
				a.setId(rs.getInt(1));
				a.setBalance(rs.getDouble(2));
				a.setType(rs.getString(3));
				a.setApproved(rs.getString(4));
				a.setJoint(rs.getInt(5));
				Accounts.add(a);
			}
			return Accounts;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveAccount(Account a) {
		String sql = "insert into account_table(account_id, balance, account_type, approved, joint)"
				+ "values(?, ?, ?, ?, ?)";
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, a.getId());
			stmt.setDouble(2, a.getBalance());
			stmt.setString(3, a.getType());
			stmt.setString(4, a.getApproved());
			stmt.setInt(5, a.getJoint());
			
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateApproved(int account_id) {
		String sql = "update account_table set approved = 'accepted' where account_id = ?";
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account_id);
		    stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateBalance(Account a, double amt, boolean positive) {
		String sql = "update account_table set balance = ? where account_id = ?";
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			if(positive) {
				stmt.setDouble(1, a.getBalance() + amt);
			}else {
				stmt.setDouble(1, a.getBalance() - amt);
			}
			stmt.setDouble(2, a.getId());
		    stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void deleteAccount(int account_id) {
		String sql = "delete from account_table where account_id = ?";
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account_id);
		    stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getMaxID() {
		int account_id = 0;
		try(Connection conn = cu.getConnection()) { // make a function for this in the account dao
			Statement stmt = conn.createStatement();
			String sql = "select max(account_id) from account_table";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				//System.out.println(rs.getInt(1) + "FIRST");
				account_id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return account_id;
	}
	
}
