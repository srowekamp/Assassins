package la_05.com.assassins;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class GameActivity extends AppCompatActivity{

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String UPDATEGAME = "UpdateGame";
    //public static final String KILL = "Kill";
    //public static fianl String LEAVEGAME = "LeaveGame";
    //public static final String ENDGAME = "EndGame";

    public static final String KEY_RESULT = "result";

    // Values for UpdateGame
    public static final String RESULT_PLAYER_DEAD = "dead"; // Result when the player was killed since the last update
    public static final String RESULT_NORMAL = "normal"; // Result when the game is proceeding as normal
    public static final String RESULT_GAME_WIN = "win"; // Result when the player has won the game
    public static final String RESULT_GAME_OVER = "game_over"; // Result when the game has ended because time ran out or host ended game early
    public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur
    public static final String KEY_TARGET = "target"; // Key in the JSONObject response corresponding to the player's target represented by a JSONObject in String form
    public static final String KEY_IS_TOP = "istop"; // Key in the JSONObject response representing whether or not the player is at the top of the AlivePlayers list


    private LocationListener locationListener;
    private Location lastLocation;
    private LatLng lastLatLng;

    private UserAccount user;
    private Game game;
    private UserAccount target;
    private boolean isTop;

    private Runnable updateGameRunnable;
    private Handler updateGameHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);
        game = (Game) getIntent().getSerializableExtra(Game.KEY_GAME);

        // Setup GPS Service
        // First Check if App has permission to access device location
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                context.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.location_permission_error, Toast.LENGTH_SHORT).show();
        }
        else {
            // If app has permission, setup Location service
            locationListener = new GameActivity.MyLocationListener();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            // Request Location updates with static parameters
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LobbyActivity.LOCATION_UPDATE_INTERVAL,
                    LobbyActivity.LOCATION_UPDATE_DISTANCE,
                    locationListener);
        }

        // Set up looping UpdateGame call
        updateGameHandler = new Handler();
        updateGameRunnable = new Runnable() {
            @Override
            public void run() {
                updateGame();
            }
        };
        updateGame();

    }

    public void assassinate(View view){
        // TODO
    }

    /** Setup a LocationListener whose methods will be called on Location updates*/
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
            lastLocation = locFromGps;
            lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
        }
    }

    /** Called every 10 seconds by every player in the game.
     * Sends this player's location data and gets the latest game status/data from server */
    private void updateGame() {
        String requestURL = JSON_URL + UPDATEGAME;
        // Make the loading indicator visible
        //imageViewUpdating.setVisibility(View.VISIBLE);// TODO add this to the game view xml

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // Remove the loading indicator
                        //imageViewUpdating.setVisibility(View.INVISIBLE); // TODO ^
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            // Call UpdateGame again in 10,000 ms (10s)
                            updateGameHandler.postDelayed(updateGameRunnable, LobbyActivity.SERVER_UPDATE_INTERVAL);
                            authenticateUpdateGame(responseJSON); // Got a response from the server, check if valid
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //show a toast and log the error
                            Toast.makeText(GameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("ERROR", "error => " + e.getMessage()); // Print the error to the device log
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Remove the loading indicator
                        //imageViewUpdating.setVisibility(View.INVISIBLE); // TODO ^^

                        //show a toast and log the error
                        Toast.makeText(GameActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new Hashtable<String, String>();
                parameters.put(Game.KEY_GAMEID, game.getGameID());
                parameters.put(UserAccount.KEY_ID, String.format("%d", user.getID()));
                parameters.put(UserAccount.KEY_X_LOCATION, String.format("%f", lastLatLng.longitude));
                parameters.put(UserAccount.KEY_Y_LOCATION, String.format("%f", lastLatLng.latitude));

                return parameters;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Process JSON response from server */
    private void authenticateUpdateGame(JSONObject response) {
        String result = null;
        String error = "Unknown Error Occurred (1)";
        try {
            result = (String) response.get(KEY_RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
            return;
        }
        if (result.equals(RESULT_NORMAL)) {
            // Game is continuing like normal, update local variables
            try {
                game = new Game(response.getJSONObject(Game.KEY_GAME));
                target = new UserAccount(response.getJSONObject(KEY_TARGET));
                isTop = response.getBoolean(KEY_IS_TOP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if (result.equals(RESULT_PLAYER_DEAD)) {
            // TODO Alert player that they are dead and give game over screen or something
            return;
        }
        if (result.equals(RESULT_GAME_WIN)) {
            // TODO Alert player that they have won the game, call EndGame
            return;
        }
        if (result.equals(RESULT_GAME_OVER)) {
            // TODO Alert player that the game is over (Time ran out, and player with isTop called EndGame)
            // TODO this won't work currently. Server removes game on EndGame, other players will get error when host ends game
            return;
        }
        switch (result) {
            case RESULT_ERROR:
                error = "An unknown server error occurred";
                break;
            default:
                break;
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
    }

    boolean doubleBackToExitPressedOnce = false;

    /** Code to control back button usage */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to Leave Game", Toast.LENGTH_SHORT).show();
        // TODO implement LeaveGame

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}