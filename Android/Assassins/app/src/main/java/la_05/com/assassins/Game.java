package la_05.com.assassins;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by Paul Martinson on 11/7/2016.
 */

public class Game implements Serializable{
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

    private String GameJSONSerialized;

    public Game (JSONObject Game){
        try {
            id          = Game.getInt(KEY_ID);
            gameID      = Game.getString(KEY_GAMEID);
            password    = Game.getString(KEY_PASSWORD);
            xcenter     = Game.getDouble(KEY_X_CENTER);
            ycenter     = Game.getDouble(KEY_Y_CENTER);
            radius      = Game.getInt(KEY_RADIUS);
            hostID      = Game.getInt(KEY_HOSTID);
            duration    = Game.getInt(KEY_DURATION);

            players_list    = Game.getString(KEY_PLAYERS_LIST);
            players_alive   = Game.getString(KEY_PLAYERS_ALIVE);
            end_time        = Game.getString(KEY_END_TIME);

        } catch (JSONException e){
            e.printStackTrace();
        }
        GameJSONSerialized = Game.toString();
    }

    public int getRadius() {
        return radius;
    }

    public double getXCenter() {
        return xcenter;
    }

    public double getYCenter() {
        return ycenter;
    }

    public String getGameID (){
        return gameID;
    }

    public int getHostID(){
        return hostID;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getPlayers_list(){
        return players_list;
    }

    public String getPlayers_alive(){
        return players_alive;
    }
}
