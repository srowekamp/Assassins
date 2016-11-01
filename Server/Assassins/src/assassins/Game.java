package assassins;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import assassins.DB;

public class Game {
	
	public static final String RESULT_GAME_CREATED = "success"; // Value of Result when game successfully created
	public static final String RESULT_GAME_EXISTS = "exists"; // Value of Result when game with the name provided already exists
	public static final String RESULT_GAMEID_INVALID = "gameid_error"; // Value of Result when user enters an invalid gameID/name
	public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
	public static final String RESULT_CENTER_INVALID = "center_error"; // Value of Result when either center coordinate is not valid
	public static final String RESULT_RADIUS_INVALID = "radius_error"; // Value of Result when an invalid radius is passed
	public static final String RESULT_HOSTID_INVALID = "hostid_error"; // Value of Result when an invalid host ID is passed
	public static final String RESULT_DURATION_INVALID = "duration_error"; // Value of Result when an invalid duration is passed
	public static final String RESULT_START_TIME_INVALID = "start_time_error"; // Value of Result when an invalid start_time is passed to GameStart
	public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs
	
	public static final String VALID = "valid";
	public static final String GAME_OVER = "game_over";
	
	public static final String KEY_GAME = "game";
	
	public static final String KEY_ID = "id";
	public static final String KEY_GAMEID = "gameid"; // Name of the game that users will see in game
	public static final String KEY_PASSWORD = "password"; // Password to enter a private (default) game
	public static final String KEY_X_CENTER = "xcenter"; // Longitude of the game area
	public static final String KEY_Y_CENTER = "ycenter"; // Latitude of the game area
	public static final String KEY_RADIUS = "radius"; // Radius of the game area in meters
	public static final String KEY_HOSTID = "hostid"; // Integer id of the user who created the game
	public static final String KEY_DURATION = "duration"; // Duration of the game in seconds 
	
	public static final String KEY_PLAYERS_LIST = "players_list";
	public static final String KEY_PLAYERS_ALIVE = "players_alive";
	public static final String KEY_END_TIME = "end_time";
	
	public static final String KEY_START_TIME = "start_time";
	
	public static final int GAMEID_MIN_LENGTH = 4;
	public static final int GAMEID_MAX_LENGTH = 32;
	public static final int PASSWORD_MIN_LENGTH = 5;
	public static final int PASSWORD_MAX_LENGTH = 32;
	public static final double LONGITUDE_MIN_VALUE = -180.0;
	public static final double LONGITUDE_MAX_VALUE = 180.0;
	public static final double LATITUDE_MIN_VALUE = -90.0;
	public static final double LATITUDE_MAX_VALUE = 90.0;
	public static final int RADIUS_MIN_VALUE = 100; // Minimum size of the game radius in m = 100m
	public static final int RADIUS_MAX_VALUE = 5000; // Maximum size of the game radius in m = 5km
	public static final int DURATION_MIN_VALUE = 60 * 10; // Minimum duration of the game in seconds = 10 minutes
	public static final int DURATION_MAX_VALUE = 60 * 60; // Maximum duration of the game in seconds = 1 hour
	public static final int GAME_START_LENGTH = 6; // Correct length of start_time string
	

	private int id;
	private String gameID;
	private String password;
	private double xcenter;
	private double ycenter;
	private int radius;
	private int hostID;
	private int duration;
	
	private String players_list;
	private String players_alive;
	private String end_time;
	
	/** Create a game object from a the SQL ResultSet in DB.getGame()*/
	public Game(ResultSet rs) throws SQLException {
		id 				= rs.getInt(KEY_ID);
		gameID 			= rs.getString(KEY_GAMEID);
        password 		= rs.getString(KEY_PASSWORD);
        xcenter 		= rs.getDouble(KEY_X_CENTER);
        ycenter 		= rs.getDouble(KEY_Y_CENTER);
        radius 			= rs.getInt(KEY_RADIUS);
        hostID 			= rs.getInt(KEY_HOSTID);
        duration  		= rs.getInt(KEY_DURATION);
        players_list	= rs.getString(KEY_PLAYERS_LIST);
        players_alive 	= rs.getString(KEY_PLAYERS_ALIVE);
        end_time		= rs.getString(KEY_END_TIME);
	}
	
	/** Create a game object from the user request through CreateGame.
	 *  Throws an Exception if any of the parameters are missing */
	public Game(HttpServletRequest request) throws Exception {
		gameID 			= request.getParameter(KEY_GAMEID);
        password 		= request.getParameter(KEY_PASSWORD);
        xcenter 		= Double.parseDouble(request.getParameter(KEY_X_CENTER)); // These throw exceptions if parameter is null
        ycenter 		= Double.parseDouble(request.getParameter(KEY_Y_CENTER));
        radius 			= Integer.parseInt(request.getParameter(KEY_RADIUS));
        hostID 			= Integer.parseInt(request.getParameter(KEY_HOSTID));
        duration  		= Integer.parseInt(request.getParameter(KEY_DURATION));
        players_list 	= null;
        players_alive	= null;
        end_time		= null;
        if (gameID == null || password == null) throw new Exception();
	}
	
	/** Only for use with DB.createGame(Game). Adds the fields stored in the game object to PreparedStatement */
	public void prepareStatement(PreparedStatement ps) throws SQLException {
		ps.setString(1, gameID);
		ps.setString(2, password);
		ps.setDouble(3, xcenter);
		ps.setDouble(4, ycenter);
		ps.setInt(5, radius);
		ps.setInt(6, hostID);
		ps.setInt(7, duration);
		ps.setString(8, String.format("%d,", hostID));
		// Don't forget to set alivePlayers and endTime on game start
	}
	
	/** Returns true if the playerID given is in the list of alive players */
	public boolean isPlayerAlive(int playerID) {
		return (getPlayerIndex(playerID) != -1); // If the player index within the players_alive list is not -1, they are alive
	}
	
	/** Returns the target of the playerID. Assumes playerID is in players_alive. Target is the next player in the list with wraparound. */
	public UserAccount getTarget(int playerID) {
		int[] alivePlayers = getPlayersAlive();
		int playerIndex = getPlayerIndex(playerID);
		if (playerIndex == -1) return null;
		int targetIndex = (playerIndex + 1) % alivePlayers.length;
		int target = alivePlayers[targetIndex];
		return DB.getUser(target);
	}
	
	/** Returns true if the player is at the top of the list of alive players */
	public boolean isTop(int playerID) {
		return (getPlayerIndex(playerID) == 0); // If the player index within the list of alive players is 0, they are at the top
	}
	
	/** Returns the index of the given player within the players_alive array. -1 if playerID is not found in the list */
	private int getPlayerIndex(int playerID) {
		int[] alivePlayers = getPlayersAlive();
		int playerIndex = -1;
		for (int i = 0; i < alivePlayers.length; i++) {
			if (alivePlayers[i] == playerID) {
				playerIndex = i;
				break;
			}
		}
		return playerIndex;
	}
	
	/** Returns an ArrayList of Integer objects representing the players_alive String  */
	private ArrayList<Integer> parseAlivePlayers() {
		if (players_alive == null) return null;
		int n = 0;
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i = 0; i < players_alive.length(); i++) {
			char c = players_alive.charAt(i);
			if (Character.isDigit(c)) n = n * 10 + Character.getNumericValue(c);
			if (c == ',') {
				al.add(new Integer(n));
				n = 0;
			}
		}
		if (n != 0) al.add(new Integer(n));
		return al;
	}
	
	/** Returns an int array representing the players_alive String */
	public int[] getPlayersAlive() {
		ArrayList<Integer> integers = parseAlivePlayers();
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	
	/** Returns an ArrayList of Integer objects representing the players_list String  */
	private ArrayList<Integer> parsePlayers() {
		if (players_list == null) return null;
		int n = 0;
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i = 0; i < players_list.length(); i++) {
			char c = players_list.charAt(i);
			if (Character.isDigit(c)) n = n * 10 + Character.getNumericValue(c);
			if (c == ',') {
				al.add(new Integer(n));
				n = 0;
			}
		}
		if (n != 0) al.add(new Integer(n));
		return al;
	}
	
	/** Returns an int array representing the players_list String */
	public int[] getPlayers() {
		ArrayList<Integer> integers = parsePlayers();
	    int[] ret = new int[integers.size()];
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
	
	/** Returns an updated Game object after removing the specified player from the alive_players list in the database */
	public Game kill(int playerID) {
		return null;
	}
	
	/** Converts this game object into a JSONObject */
	public JSONObject toJSON() {
		JSONObject j = new JSONObject();
		j.put(KEY_ID, id);
		j.put(KEY_GAMEID, gameID);
		j.put(KEY_PASSWORD, password);
		j.put(KEY_X_CENTER, xcenter);
		j.put(KEY_Y_CENTER, ycenter);
		j.put(KEY_RADIUS, radius);
		j.put(KEY_HOSTID, hostID);
		j.put(KEY_DURATION, duration);
		j.put(KEY_PLAYERS_LIST, players_list);
		j.put(KEY_PLAYERS_ALIVE, players_alive);
		j.put(KEY_END_TIME, end_time);
		return j;
	}
	
	/** Converts this game object into a JSONObject formatted as a string */
	public String toJSONString() {
		JSONObject j = toJSON();
		return j.toJSONString();
	}
	
	@Override
	public String toString() {
		return toJSONString();
	}
	
	/** Return the id of this Game */
	public int getID() {
		return id;
	}
	
	/** Return the gameID of this Game */
	public String getGameID() {
		return gameID;
	}
	
	/** Return the hostID of this Game */
	public int getHostID() {
		return hostID;
	}
	
	/** Return the duration of this Game */
	public int getDuration() {
		return duration;
	}
	
	/** Returns the EndTime of this Game */
	public String getEndTime() {
		return end_time;
	}
	
	/** Returns the validity of this Game object */
	public String checkValidity() {
		if (!isValidGameID(gameID)) return RESULT_GAMEID_INVALID;
		if (!isValidPassword(password)) return RESULT_PASSWORD_INVALID;
		if (!isValidXCenter(xcenter) || !isValidYCenter(ycenter)) return RESULT_CENTER_INVALID;
		if (!isValidRadius(radius)) return RESULT_RADIUS_INVALID;
		if (!isValidHostID(hostID)) return RESULT_HOSTID_INVALID;
		if (!isValidDuration(duration)) return RESULT_DURATION_INVALID;
		return VALID;
	}
	
    /** Checks the provided gameID for validity */
    public static boolean isValidGameID(String gameID) {
    	if (gameID == null) return false;
    	return (gameID.length() > GAMEID_MIN_LENGTH && gameID.length() < GAMEID_MAX_LENGTH);
    }
    
    /** Checks the provided password for validity */
    public static boolean isValidPassword(String password) {
    	if (password == null) return false;
    	return (password.length() > PASSWORD_MIN_LENGTH && password.length() < PASSWORD_MAX_LENGTH);
    }
    
    /** Checks the provided xCenter value for validity */
    public static boolean isValidXCenter(double x) {
    	return (x >= LONGITUDE_MIN_VALUE && x <= LONGITUDE_MAX_VALUE);
    }
    
    /** Checks the provided yCenter value for validity */
    public static boolean isValidYCenter(double y) {
    	return (y >= LATITUDE_MIN_VALUE && y <= LATITUDE_MAX_VALUE);
    }
    
    /** Checks the provided radius for validity */
    public static boolean isValidRadius(int r) {
    	return (r >= RADIUS_MIN_VALUE && r <= RADIUS_MAX_VALUE);
    }
    
    /** Checks the provided hostID for validity */
    public static boolean isValidHostID(int hostID) {
    	return (DB.getUser(hostID) != null);
    }
    
    /** Checks the provided duration for validity */
    public static boolean isValidDuration(int d) {
    	return (d >= DURATION_MIN_VALUE && d <= DURATION_MAX_VALUE);
    }
    
    /** Checks the provided start time for validity */
    public static boolean isValidStartTime(String start_time) {
    	if (start_time == null) return false;
    	if (start_time.length() != GAME_START_LENGTH) return false;
    	try {
    		int h = Integer.parseInt(start_time.substring(0, 2));
    		int m = Integer.parseInt(start_time.substring(2, 4));
    		int s = Integer.parseInt(start_time.substring(4, 6));
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
}
