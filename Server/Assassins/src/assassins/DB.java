package assassins;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
				+ Game.KEY_PLAYERS_LIST + ") "
    			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			game.prepareStatement(ps);
			ps.executeUpdate();
			return getGame(game.getGameID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
	}
	
	public static Game removeGame(int gameID, String gameName){
		Connection con = DBConnectionHandler.getConnection();
		String sql = "DELETE FROM " + DATABASE + "." + GAMES_TABLE + " WHERE " + Game.KEY_ID + "=?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, gameID);
			ps.executeUpdate();
			return getGame(gameName);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
	}
	
	/** Return an updated Game object after adding the End Time of a game given the time it was started,
	 *  using the duration specified on creation */
	public static Game setEndTime(Game game, String start_time) {
		int h, m, s, current_time, end_time_seconds;
		h = Integer.parseInt(start_time.substring(0, 2));
		m = Integer.parseInt(start_time.substring(2, 4));
		s = Integer.parseInt(start_time.substring(4, 6));
		current_time = 60 * 60 * h + 60 * m + s;
		end_time_seconds = current_time + game.getDuration();
		end_time_seconds = (end_time_seconds % 86400);
		int end_hour = end_time_seconds / 3600;
		int end_minute = (end_time_seconds % 3600) / 60;
		int end_second = end_time_seconds % 60;
		String endTime = String.format("%02d%02d%02d", end_hour, end_minute, end_second);
		Connection con = DBConnectionHandler.getConnection();
		//UPDATE `db309la05`.`active_games` SET `end_time`='235959' WHERE `id`='1';
		String sql = "UPDATE " + DATABASE + "." + GAMES_TABLE + " SET "
				+ Game.KEY_END_TIME + "=? WHERE " + Game.KEY_ID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, endTime);
			ps.setInt(2, game.getID());
			ps.executeUpdate();
			return getGame(game.getGameID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
	}

	/** Return an updated Game object after setting the target list (players_alive) in the database */
	public static Game setTargetList(Game game) {
		int players[] = game.getPlayers();
		Integer[] playersAlive = new Integer[players.length];
		for (int i = 0; i < players.length; i++) playersAlive[i] = new Integer(players[i]);
		Collections.shuffle(Arrays.asList(playersAlive));
		String players_alive = String.format("%d,", players[0]);
		for (int i = 0 ; i < players.length; i++) {
			if (playersAlive[i].intValue() != players[0]) {
				players_alive += String.format("%d,", playersAlive[i].intValue());
			}
		}
		Connection con = DBConnectionHandler.getConnection();
		//UPDATE `db309la05`.`active_games` SET `players_alive`='4,' WHERE `id`='2';
		String sql = "UPDATE " + DATABASE + "." + GAMES_TABLE + " SET "
				+ Game.KEY_PLAYERS_ALIVE + "=? WHERE " + Game.KEY_ID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, players_alive);
			ps.setInt(2, game.getID());
			ps.executeUpdate();
			return getGame(game.getGameID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
	}
	
	/** Update the alive_players list of the specified Game object with the provided new list in the database */
	public static Game updateAlivePlayers(Game game, String newPlayersAlive){
		Connection con = DBConnectionHandler.getConnection();
		String sql = "UPDATE " + DATABASE + "." + GAMES_TABLE + " SET "
				+ Game.KEY_PLAYERS_ALIVE + "=? WHERE " + Game.KEY_ID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, newPlayersAlive);
			ps.setInt(2, game.getID());
			ps.executeUpdate();
			return getGame(game.getGameID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
	}
	
	/**
	 * determines the number of players left alive in the game.
	 * 
	 * @param game to be checked
	 * @return number of players, 0 if unable to execute query
	 */
	public static int getNumberPlayersAlive(int gameID){
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + GAMES_TABLE + " WHERE " + Game.KEY_GAMEID + "?=";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, gameID);
			ResultSet rs = ps.executeQuery();
			Game tempGame = new Game(rs);
			return tempGame.getPlayersAlive().length;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return 0;
	}
	
	/**
	 * uses the game ID to grab game from database and determine how many seconds are left in the game.
	 * calculates seconds by subtracting current time from game's end time.
	 * 
	 * @param gameID
	 * @param currentTime HH MM SS
	 * @return seconds left in game
	 */
	public static int getTimeRemaining(int gameID, String currentTime){
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + GAMES_TABLE + " WHERE " + Game.KEY_GAMEID + "?=";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, gameID);
			ResultSet rs = ps.executeQuery();
			Game tempGame = new Game(rs);
			int currH = Integer.parseInt(currentTime.substring(0, 2)) * 60 * 60; /* current hours in seconds */
			int currM = Integer.parseInt(currentTime.substring(2, 4)) * 60; /* current minutes in seconds */
			int currS = Integer.parseInt(currentTime.substring(4, 6)); /* current seconds in seconds */
			int endH = Integer.parseInt(tempGame.getEndTime().substring(0, 2)) * 60 * 60; /* end hours in seconds */
			int endM = Integer.parseInt(tempGame.getEndTime().substring(2, 4)) * 60; /* end minutes in seconds */
			int endS = Integer.parseInt(tempGame.getEndTime().substring(4, 6)); /* end seconds in seconds */
			return (endH + endM + endS) - (currH + currM + currS);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return 0;
		
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return false;
	}
	
	/**
	 * attemptJoinGame checks the database for the given gameID and password and returns the game that it
	 * finds
	 * 
	 * @param gameID to look for
	 * @param password to look for
	 * @return game it finds
	 */
	public static Game attemptJoinGame(String gameID, String password){
		Connection con = DBConnectionHandler.getConnection();
		String sql = "SELECT * FROM " + DATABASE + "." + GAMES_TABLE + " WHERE " + Game.KEY_GAMEID + "=? and "
						+ Game.KEY_PASSWORD + "=?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, gameID);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return new Game(rs);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
	}
	
	/**
	 * updatePlayersList updates the players_list of the given game to the given list
	 * 
	 * @param game
	 * @param playersList
	 * @return game with new list
	 */
	public static Game updatePlayersList(Game game, String playersList){
		Connection con = DBConnectionHandler.getConnection();
		String sql = "UPDATE " + DATABASE + "." + GAMES_TABLE +  " SET " + Game.KEY_PLAYERS_LIST + "=? WHERE "
						+ Game.KEY_ID + "=?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, playersList);
			ps.setInt(2, game.getID());
			ps.executeUpdate();
			return getGame(game.getGameID());
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
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
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    /** Update the given user's GPS location within the database */
    public static UserAccount updateUserLocation(int userID, double xlocation, double ylocation) {
Connection con = DBConnectionHandler.getConnection();
		String sql = "UPDATE " + DATABASE + "." + USERS_TABLE
				+ " SET "
				+ UserAccount.KEY_X_LOCATION + "=?, "
				+ UserAccount.KEY_Y_LOCATION + "=?"
				+ " WHERE " + UserAccount.KEY_ID + "=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDouble(1, xlocation);
			ps.setDouble(2, ylocation);
			ps.setInt(3, userID);
			ps.executeUpdate();
			return getUser(userID); // Assumes update was successful
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
		return null;
    }
    
    /** Update the given user's total kills in the database by incrementing the existing value by 1 */
    public static UserAccount addKill(int playerID) {
    	UserAccount user = getUser(playerID);
    	int kills = user.getTotalKills();
    	kills = kills + 1;
    	Connection con = DBConnectionHandler.getConnection();
    	String sql = "UPDATE " + DATABASE + "." + USERS_TABLE
    			+ " SET " + UserAccount.KEY_TOTAL_KILLS + "=?"
    			+ " WHERE " + UserAccount.KEY_ID + "=?";
    	try {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setInt(1, kills);
    		ps.setInt(2, playerID);
    		ps.executeUpdate();
    		return getUser(playerID);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
    	return null;
    }
    
    /** Update the given user's games played in the database by incrementing the existing value by 1 */
    public static UserAccount addGamePlayed(int playerID) {
    	UserAccount user = getUser(playerID);
    	int games_played = user.getGamesPlayed();
    	games_played = games_played + 1;
    	Connection con = DBConnectionHandler.getConnection();
    	String sql = "UPDATE " + DATABASE + "." + USERS_TABLE
    			+ " SET " + UserAccount.KEY_GAMES_PLAYED + "=?"
    			+ " WHERE " + UserAccount.KEY_ID + "=?";
    	try {
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setInt(1, games_played);
    		ps.setInt(2, playerID);
    		ps.executeUpdate();
    		return getUser(playerID);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		finally {
			try { if (con != null) con.close(); } catch (Exception e) {};
		}
    	return null;
    }
}
