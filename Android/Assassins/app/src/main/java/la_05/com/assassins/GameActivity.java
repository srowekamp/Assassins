package la_05.com.assassins;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

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

public class GameActivity extends AppCompatActivity implements SensorEventListener{

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String UPDATEGAME = "UpdateGame";
    public static final String KILL = "Kill";
    public static final String LEAVEGAME = "LeaveGame";
    //public static final String ENDGAME = "EndGame";

    public static final String KEY_RESULT = "result";

    // Values for UpdateGame
    public static final String RESULT_PLAYER_DEAD = "dead"; // Result when the player was killed since the last update
    public static final String RESULT_NORMAL = "normal"; // Result when the game is proceeding as normal
    public static final String RESULT_GAME_WIN = "win"; // Result when the player has won the game
    public static final String RESULT_GAME_NOT_EXIST_OR_END = "game_not_exist_or_end"; // Result when the game wasn't found in the database. Since we made it to game view, this means the game was ended
    public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur
    public static final String KEY_TARGET = "target"; // Key in the JSONObject response corresponding to the player's target represented by a JSONObject in String form
    public static final String KEY_IS_TOP = "istop"; // Key in the JSONObject response representing whether or not the player is at the top of the AlivePlayers list

    // Values for Kill
    public static final String RESULT_KILL_ERROR = "error"; // Result when there is an error. Shouldn't occur
    public static final String RESULT_KILL_SUCCESS = "success"; // Result when the kill is processed successfully

    // Values for LeaveGame
    public static final String RESULT_LEAVE_GAME_SUCCESS = "success"; // The player was successfully removed from the list of players in game
    public static final String RESULT_LEAVE_PLAYER_DEAD = "dead-success"; // Player can safely leave game when dead. That way they still get gamesPlayed++ when game ends
    public static final String RESULT_PLAYER_WON = "win"; // Player was the last one alive, don't let them leave. Wait for their device to call EndGame.

    private LocationListener locationListener;
    private Location lastLocation;
    private LatLng lastLatLng;

    private UserAccount user;
    private Game game;
    private UserAccount target;
    private boolean isTop;

    private Runnable updateGameRunnable;
    private Handler updateGameHandler;

    private boolean waitingForUpdate = true;
    private Button buttonAssassinate;

    private ImageView image;
    private float currDegree = 0f;
    private SensorManager mSensorManager;
    TextView textViewUp;
    private boolean updateReceived = false;
    //TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);
        game = (Game) getIntent().getSerializableExtra(Game.KEY_GAME);
        image = (ImageView) findViewById(R.id.targetCompass);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        buttonAssassinate = (Button) findViewById(R.id.AssassinateButton);
        buttonAssassinate.setEnabled(false); // initialize button as disabled

        textViewUp = (TextView) findViewById(R.id.textViewUp);
        textViewUp.setText("");

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

    @Override
    protected void onResume(){
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent se){
        float degree = Math.round(se.values[0]);
        //tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
        if(updateReceived){
            degree -= getbearing();
        }
        RotateAnimation ra = new RotateAnimation(currDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor s, int accuracy){
        // Not in use
    }

    public double getbearing(){
        Location targetL = new Location("");
        targetL.setLatitude(target.getYLocation());
        targetL.setLongitude(target.getXLocation());
        return (double) lastLocation.bearingTo(targetL);
    }

    /** Called when the user presses the assassinate button */
    public void assassinate(View view) {
        buttonAssassinate.setEnabled(false); // Disable the button so they can't spam
        // TODO Determine if the target is able to be killed
        boolean killable = true; // Set this with methods
        if (killable && !waitingForUpdate) {
            kill();
            waitingForUpdate = true;
        }
        else {
            buttonAssassinate.setEnabled(true);
        }
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

    /** The Kill has been validated with GPS/Perks/Weapons, so process the kill with the server */
    private void kill() {
        String requestURL = JSON_URL + KILL;
        // TODO Indicate to user that we are waiting for new Target and processing kill with server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // TODO no longer processing kill ^
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            authenticateKill(responseJSON); // Got a response from the server, check if valid
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
                        // TODO no longer processing kill ^

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

                return parameters;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Process JSON response from server */
    private void authenticateKill(JSONObject response) {
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
        if (result.equals(RESULT_KILL_SUCCESS)) {
            // Kill was successfully processed with server, wait for update
            // TODO indicate that kill was successful and that we are waiting for new target
            return;
        }
        switch (result) {
            case RESULT_KILL_ERROR:
                error = "An unknown server error occurred";
                break;
            default:
                break;
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
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
                            updateReceived = true;
                            textViewUp.setText(String.format("%f", getbearing()));
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
                waitingForUpdate = false;
                buttonAssassinate.setEnabled(true); // Reenable the assassinate button
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        if (result.equals(RESULT_PLAYER_DEAD)) {
            // TODO Alert player that they are dead and give game over screen or something
            Toast.makeText(this, "You have been assassinated", Toast.LENGTH_LONG).show();
            return;
        }
        if (result.equals(RESULT_GAME_WIN)) {
            // TODO Alert player that they have won the game, call EndGame after ~15 seconds so player's target gets alert that they were killed
            Toast.makeText(this, "You win!", Toast.LENGTH_LONG).show();
            return;
        }
        if (result.equals(RESULT_GAME_NOT_EXIST_OR_END)) {
            // TODO Alert player that the game is over (Time ran out, and player with isTop called EndGame)
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
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

    /** Leave the game with the server. Called when user double presses back button. */
    public void leaveGame() {
        String requestURL = JSON_URL + LEAVEGAME;
        final ProgressDialog loading = ProgressDialog.show(this, "Leaving Game...", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // Dismiss the progress dialog
                        loading.dismiss();
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            /*if (responseJSON.getString(KEY_RESULT) != null) {
                                // Left game in database, so close activity
                                leaveActivity();
                            }*/
                            authenticateLeaveGame(responseJSON);
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
                        //Dismiss the progress dialog
                        loading.dismiss();
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
                return parameters;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Process JSON response from server */
    private void authenticateLeaveGame(JSONObject response) {
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
        if (result.equals(RESULT_LEAVE_GAME_SUCCESS)) {
            // Successfully left game with server, leave the game view
            leaveActivity();
            return;
        }
        if (result.equals(RESULT_LEAVE_PLAYER_DEAD)) {
            // TODO Alert player that they are dead and give game over screen or something
            Toast.makeText(this, "You have been assassinated", Toast.LENGTH_LONG).show();
            leaveActivity();
            return;
        }
        if (result.equals(RESULT_PLAYER_WON)) {
            // TODO Alert player that they have won the game, call EndGame after ~15 seconds so player's target gets alert that they were killed
            Toast.makeText(this, "You win!", Toast.LENGTH_LONG).show();
            // End the game after x seconds
            leaveActivity();
            return;
        }
        if (result.equals(RESULT_GAME_NOT_EXIST_OR_END)) {
            // TODO Alert player that the game is over (Time ran out, and player with isTop called EndGame)
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
            leaveActivity();
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

    /** Called whenever we plan to leave this activity (back button leave and after game ends) */
    private void leaveActivity() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.removeUpdates(locationListener); // Stop the location service
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        updateGameHandler.removeCallbacks(updateGameRunnable); // Stop the looping call
        finish();
    }



    boolean doubleBackToExitPressedOnce = false;

    /** Code to control back button usage */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            leaveGame();
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