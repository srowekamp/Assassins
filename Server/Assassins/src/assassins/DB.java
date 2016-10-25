package assassins;

import java.sql.*;
import assassins.DBConnectionHandler;
import assassins.UserAccount;

/** This Class will hold all of the database functions */
public class DB {
	public static final String DATABASE = "db309la05";
	
	public static final String USERS_TABLE = "users";
	public static final String GAMES_TABLE = "active_games"; //not created yet
	
	/** Return true if the user exists in the database */
	public static boolean doesUserExist(String username) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + "where " + UserAccount.KEY_USERNAME + 
				"=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** Return true if the username and password provided match a user in the database */
	public static boolean isValidLogin(String username, String password) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + "where " + UserAccount.KEY_USERNAME + 
				"=? and " + UserAccount.KEY_PASSWORD + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** Return the UserAccount object that matches the provided username. */
	public static UserAccount getUser(String username) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + "where " + UserAccount.KEY_USERNAME + 
				"=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return new UserAccount(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Return the UserAccount object that matches the provided userID */
	public static UserAccount getUser(int userID) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + "where " + UserAccount.KEY_ID + 
				"=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return new UserAccount(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
