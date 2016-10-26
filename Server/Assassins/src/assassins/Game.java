package assassins;

import java.sql.*;
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
	public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs
	
	public static final String VALID = "valid";
	
	public static final String KEY_GAME = "game";
	
	public static final String KEY_GAMEID = "gameid"; // Name of the game that users will see in game
	public static final String KEY_PASSWORD = "password"; // Password to enter a private (default) game
	public static final String KEY_X_CENTER = "xcenter"; // Longitude of the game area
	public static final String KEY_Y_CENTER = "ycenter"; // Latitude of the game area
	public static final String KEY_RADIUS = "radius"; // Radius of the game area in meters
	public static final String KEY_HOSTID = "hostid"; // Integer id of the user who created the game
	public static final String KEY_DURATION = "duration"; // Duration of the game in seconds 
	
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
	

	private String gameID;
	private String password;
	private double xcenter;
	private double ycenter;
	private int radius;
	private int hostID;
	private int duration;
	
	private String playerList;
	private String playersAlive;
	private String endTime;
	
	public Game(HttpServletRequest request) {
		gameID 		= request.getParameter(KEY_GAMEID);
        password 	= request.getParameter(KEY_PASSWORD);
        xcenter 	= Double.parseDouble(request.getParameter(KEY_X_CENTER));
        ycenter 	= Double.parseDouble(request.getParameter(KEY_Y_CENTER));
        radius 		= Integer.parseInt(request.getParameter(KEY_RADIUS));
        hostID 		= Integer.parseInt(request.getParameter(KEY_HOSTID));
        duration  	= Integer.parseInt(request.getParameter(KEY_DURATION));
	}
	
	public String checkValidity() {
		// TODO
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

}
