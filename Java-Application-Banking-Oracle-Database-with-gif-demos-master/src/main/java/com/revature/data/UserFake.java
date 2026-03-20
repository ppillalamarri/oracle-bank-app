package com.revature.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;


import com.revature.beans.Account;
import com.revature.beans.User;
import com.revature.util.ConnectionUtil;

public class UserFake implements UserDao{
	private static ConnectionUtil cu = ConnectionUtil.getConnectionUtil();
	private static Logger log = Logger.getLogger(Main.class);
	
	public User login(String username, String password) {
		User u = new User();
		String sql = "select * from user_table where username = ? and password = ?";
		log.trace(sql);
		System.out.println(username);
		System.out.println(password);
		try(Connection conn = cu.getConnection()) {
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				u.setId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setFirstname(rs.getString(4));
				u.setLastname(rs.getString(5));
				u.setEmail(rs.getString(6));
				u.setPhone(rs.getString(7));
				u.setType(rs.getString(8));
				u.setAddress(rs.getString(9));
				u.setCity(rs.getString(10));
				u.setState(rs.getString(11));
				u.setZip(rs.getString(12));
				u.setAccount_id(rs.getInt(13));
				u.setAccount_id2(rs.getInt(14));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(u.getId() != null) {
			return u;
		}else {
			return null;
		}
	}

	public User getUser(int account_id) {
		User u = new User();
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from user_table where account_table_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account_id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				u.setId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setFirstname(rs.getString(4));
				u.setLastname(rs.getString(5));
				u.setEmail(rs.getString(6));
				u.setPhone(rs.getString(7));
				u.setType(rs.getString(8));
				u.setAddress(rs.getString(9));
				u.setCity(rs.getString(10));
				u.setState(rs.getString(11));
				u.setZip(rs.getString(12));
				u.setAccount_id(rs.getInt(13));
				u.setAccount_id2(rs.getInt(14));
			}
			return u;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getUserU(int user_id) {
		User u = new User();
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from user_table where user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, user_id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				u.setId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setFirstname(rs.getString(4));
				u.setLastname(rs.getString(5));
				u.setEmail(rs.getString(6));
				u.setPhone(rs.getString(7));
				u.setType(rs.getString(8));
				u.setAddress(rs.getString(9));
				u.setCity(rs.getString(10));
				u.setState(rs.getString(11));
				u.setZip(rs.getString(12));
				u.setAccount_id(rs.getInt(13));
				u.setAccount_id2(rs.getInt(14));
			}
			return u;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<User> getUsers() {
		List<User> Users = new ArrayList<User>();
		
		try(Connection conn = cu.getConnection()) {
			String sql = "select * from user_table where user_type = 'customer' order by user_id";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				User u = new User();
				u.setId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.setPassword(rs.getString(3));
				u.setFirstname(rs.getString(4));
				u.setLastname(rs.getString(5));
				u.setEmail(rs.getString(6));
				u.setPhone(rs.getString(7));
				u.setType(rs.getString(8));
				u.setAddress(rs.getString(9));
				u.setCity(rs.getString(10));
				u.setState(rs.getString(11));
				u.setZip(rs.getString(12));
				u.setAccount_id(rs.getInt(13));
				u.setAccount_id2(rs.getInt(14));
				Users.add(u);
			}
			return Users;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveUser(User u) {
		
		String sql = "insert into user_table(user_id, username, password, firstname, lastname,"
				+ "email, phone, user_type, address, city, state, zip, account_table_id)"
				+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, u.getId());
			stmt.setString(2, u.getUsername());
			stmt.setString(3, u.getPassword());
			stmt.setString(4, u.getFirstname());
			stmt.setString(5, u.getLastname());
			stmt.setString(6, u.getEmail());
			stmt.setString(7, u.getPhone());
			stmt.setString(8, u.getType());
			stmt.setString(9, u.getAddress());
			stmt.setString(10, u.getCity());
			stmt.setString(11, u.getState());
			stmt.setString(12, u.getZip());
			stmt.setInt(13, u.getAccount_id());

			stmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateUser(User u) {
		// This is unnecessary without a real persistence layer.
	}

	public void deleteUser(int user_id, int account_id) {
		
		try(Connection conn = cu.getConnection()) {
			conn.setAutoCommit(false);
			String sql = "call DeleteUser(?, ?)";
			log.trace(sql);
			CallableStatement stmt = conn.prepareCall(sql);
			stmt.setInt(1, user_id);
			stmt.setInt(2, account_id);
		    int number = stmt.executeUpdate();
		    if(number == 1) {
		    	log.trace("user not deleted");
		    	conn.rollback();
		    }else {
		    	log.trace("user deleted");
		    	conn.commit();
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteUserAccount(int account_id) {
		String sql = "delete from user_table where account_table_id = ?";
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account_id);
		    stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getMAXID() {
		int ID = 0;
		try(Connection conn = cu.getConnection()) { // make a function for this in the user dao
			Statement stmt = conn.createStatement();
			String sql = "select max(user_id) from user_table";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				//System.out.println(rs.getInt(1));
				ID = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ID;
	}
	
	public void updateAccount2(int account_id, int user_id) {
		String sql = "update user_table set account_table_id2 = ? where user_id = ?";
		log.trace(sql);
		
		try(Connection conn = cu.getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, account_id);
			stmt.setInt(2, user_id);
		    stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
