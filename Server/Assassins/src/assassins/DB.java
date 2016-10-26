package assassins;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import assassins.DBConnectionHandler;
import assassins.UserAccount;

/** This Class will hold all of the database functions */
public class DB {
	public static final String DATABASE = "db309la05";
	public static final String USERS_TABLE = "users";
	public static final String GAMES_TABLE = "active_games"; //not created yet
	
	/** Return true if the game exists in the database. Assumes that if the gameID is in
	 *  the active_games table, a table representing the game with gameID provided also exists */
	public static boolean doesGameExist(String gameID) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + GAMES_TABLE + " WHERE " + Game.KEY_GAMEID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, gameID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/** Return the Game object matching the provided gameID */
	public static Game getGame(String gameID) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + GAMES_TABLE + " WHERE " + Game.KEY_GAMEID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, gameID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return new Game(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Create A game in the database. Doesn't check for validity or uniqueness. */
	public static Game createGame(Game game) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "INSERT INTO " + DATABASE + "." + GAMES_TABLE + "("
				+ Game.KEY_GAMEID + ", "
				+ Game.KEY_PASSWORD + ", "
				+ Game.KEY_X_CENTER + ", "
				+ Game.KEY_Y_CENTER + ", "
				+ Game.KEY_RADIUS + ", "
				+ Game.KEY_HOSTID + ", "
				+ Game.KEY_DURATION + ", "
				+ Game.KEY_PLAYERS_LIST + ", "
				+ Game.KEY_PLAYERS_ALIVE + ") "
    			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			game.prepareStatement(ps);
			ps.executeUpdate();
			return getGame(game.getGameID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Return true if the user exists in the database */
	public static boolean doesUserExist(String username) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + " WHERE " + UserAccount.KEY_USERNAME + "=?";
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
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + " WHERE " + UserAccount.KEY_USERNAME + 
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
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + " WHERE " + UserAccount.KEY_USERNAME + "=?";
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
		String sql = "SELECT * FROM " + DATABASE + "." + USERS_TABLE + " WHERE " + UserAccount.KEY_ID + 
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
	
	/** Add the user to the database and returns the new UserAccount with default image. Assumes all provided values are valid. */
	public static UserAccount addUser(String username, String password, String real_name) {
		Connection con = DBConnectionHandler.getConnection();
		String sql = "INSERT INTO " + DATABASE + "." + USERS_TABLE + "("
				+ UserAccount.KEY_USERNAME + ", "
				+ UserAccount.KEY_PASSWORD + ", "
				+ UserAccount.KEY_REAL_NAME + ", "
				+ UserAccount.KEY_IMAGE_PATH + ", "
				+ UserAccount.KEY_TOTAL_KILLS + ", "
				+ UserAccount.KEY_GAMES_PLAYED + ") "
    			+ "VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, real_name);
            ps.setString(4, UserAccount.DEFAULT_IMAGE);
            ps.setInt(5, 0);
            ps.setInt(6, 0);
            ps.executeUpdate();
            return getUser(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Save the jpg represented by the provided Base64 String to the server and update the filename in the database. Assumes both inputs are valid */
	public static UserAccount addUserImage(int userID, String b64Image){
		String filename = String.format("%d.jpg", userID);
		if (!saveB64Image(filename, b64Image)) return null;
		
		Connection con = DBConnectionHandler.getConnection();
		String sql = "UPDATE " + DATABASE + "." + USERS_TABLE + " SET " +
				UserAccount.KEY_IMAGE_PATH + "=? WHERE " + UserAccount.KEY_ID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, filename);
			ps.setInt(2, userID);
			ps.executeUpdate();
			return getUser(userID); // Assumes update was successful
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    /** Attempt to save the Base64 encoded image to the server using filename from database */
    private static boolean saveB64Image(String filename, String b64Image) {
    	String filepath = "/var/lib/tomcat/webapps/userImages/";
    	byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(b64Image);
    	try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File outputImage = new File(filepath + filename);
	    	ImageIO.write(img, "jpg", outputImage);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
}
