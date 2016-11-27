package la_05.com.assassins;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by Nathan on 10/20/2016.
 */

public class UserAccount implements Serializable{
    public static final String KEY_USER_ACCOUNT = "account";

    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REAL_NAME = "real_name";
    public static final String KEY_IMAGE_PATH = "image_filename";
    public static final String KEY_TOTAL_KILLS = "total_kills";
    public static final String KEY_GAMES_PLAYED = "games_played";
    public static final String KEY_X_LOCATION = "x_location";
    public static final String KEY_Y_LOCATION = "y_location";

    public static final String USER_IMAGE_URL = "http://proj-309-la-05.cs.iastate.edu:8080/userImages/";

    public static final double LAT_LONG_UNKNOWN = 200.0;

    private int id;
    private String username;
    private String password;
    private String realName;
    private String userImagePath;
    private int totalKills;
    private int gamesPlayed;
    private double xlocation;
    private double ylocation;

    private String accountJSONSerialized;

    public UserAccount (JSONObject account) {
        try {
            id = account.getInt(KEY_ID);
            username = account.getString(KEY_USERNAME);
            password = account.getString(KEY_PASSWORD);
            realName = account.getString(KEY_REAL_NAME);
            userImagePath = account.getString(KEY_IMAGE_PATH);
            totalKills = account.getInt(KEY_TOTAL_KILLS);
            gamesPlayed = account.getInt(KEY_GAMES_PLAYED);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            xlocation = account.getDouble(KEY_X_LOCATION);
            ylocation = account.getDouble(KEY_Y_LOCATION);
        } catch (Exception e) {
            // not initialized yet, ignore exception
            xlocation = ylocation = LAT_LONG_UNKNOWN;
        }
        accountJSONSerialized = account.toString();
    }

    public int getID() {
        return id;
    }

    public String getRealName () {
        return realName;
    }

    public String getUserName () {
        return username;
    }

    public Integer getTotalKills () {
        return totalKills;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public String getImageURL() {
        return USER_IMAGE_URL + userImagePath;
    }
}
