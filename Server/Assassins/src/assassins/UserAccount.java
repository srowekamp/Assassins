package assassins;

import java.sql.*;
import org.json.simple.JSONObject;

public class UserAccount {
	
	public static final String KEY_USER_ACCOUNT = "account";
	
	public static final String KEY_ID = "id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_REAL_NAME = "real_name";
	public static final String KEY_IMAGE_PATH = "image_filename";
	public static final String KEY_TOTAL_KILLS = "total_kills";
	public static final String KEY_GAMES_PLAYED = "games_played";
	
	public static final int USERNAME_MIN_LENGTH = 4;
	public static final int USERNAME_MAX_LENGTH = 32;
	public static final int PASSWORD_MIN_LENGTH = 5;
	public static final int PASSWORD_MAX_LENGTH = 32;
	
	private int id;
	private String username;
	private String password;
	private String realName;
	private String userImagePath;
	private int totalKills;
	private int gamesPlayed;
	
	public UserAccount(ResultSet rs) {
		try {
			id = rs.getInt(KEY_ID);
			username = rs.getString(KEY_USERNAME);
			password = rs.getString(KEY_PASSWORD);
			realName = rs.getString(KEY_REAL_NAME);
			userImagePath = rs.getString(KEY_IMAGE_PATH);
			totalKills = rs.getInt(KEY_TOTAL_KILLS);
			gamesPlayed = rs.getInt(KEY_GAMES_PLAYED);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject toJSON() {
		JSONObject j = new JSONObject();
		j.put(KEY_ID, id);
		j.put(KEY_USERNAME, username);
		j.put(KEY_PASSWORD, password);
		j.put(KEY_REAL_NAME, realName);
		j.put(KEY_IMAGE_PATH, userImagePath);
		j.put(KEY_TOTAL_KILLS, totalKills);
		j.put(KEY_GAMES_PLAYED, gamesPlayed);
		return j;
	}
	
	public String toJSONString() {
		JSONObject j = toJSON();
		return j.toJSONString().replaceAll("\\\\", "").replaceAll("\r", "");
	}
	
    /** Checks the provided username for validity */
    public static boolean isValidUsername(String username) {
    	if (username == null) return false;
    	return (username.length() > USERNAME_MIN_LENGTH && username.length() < USERNAME_MAX_LENGTH);
    }
    
    /** Checks the provided password for validity */
    public static boolean isValidPassword(String password) {
    	if (password == null) return false;
    	return (password.length() > PASSWORD_MIN_LENGTH && password.length() < PASSWORD_MAX_LENGTH);
    }

}
