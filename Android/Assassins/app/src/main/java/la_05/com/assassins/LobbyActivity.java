package la_05.com.assassins;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class LobbyActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String GETPLAYERS = "GetPlayers";
    public static final String GAMESTART = "GameStart";
    //public static final String LEAVEGAME = "LeaveGame";

    public static final String KEY_RESULT = "result";
    // Results for GameStart
    public static final String RESULT_PARAMETER_MISSING = "parameter_error";
    public static final String RESULT_GAME_STARTED = "success";
    public static final String RESULT_NOT_ENOUGH_PLAYERS = "not_enough_players";
    public static final String RESULT_GAMEID_INVALID = "gameid_error";
    public static final String RESULT_START_TIME_INVALID = "start_time_error"; // Value of Result when an invalid start_time is passed to GameStart

    // Data for GetPlayers
    public static final String KEY_NUM_PLAYERS = "num_players";
    public static final String RESULT_NORMAL = "normal";
    public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur

    public static final int LOCATION_UPDATE_INTERVAL = 10000; // Minimum time between location updates in milliseconds
    public static final float LOCATION_UPDATE_DISTANCE = 5; // Minimum distance between location updates in meters
    public static final int MAP_ZOOM_SCALE_FACTOR = 350; // Constant used to determine map zoom

    private TextView txtLatLong;
    private MapView mapView;
    private Location lastLocation;
    private LatLng lastLatLng;
    private GoogleMap googleMap;
    private float cameraZoomLevel = 0;
    private LatLng circleLatLng;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private UserAccount[] players;

    LocationListener locationListener;

    private UserAccount user;
    private Game game;

    private String start_time;
    boolean gameStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Get the Game and UserAccount objects from the previous activity
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);
        game = (Game) getIntent().getSerializableExtra(Game.KEY_GAME);

        // Set the center of the game radius circle
        circleLatLng = new LatLng(game.getYCenter(), game.getXCenter());

        // Setup the Map
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMap(googleMap);
                updateMap();
            }
        });

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        txtLatLong = (TextView) findViewById(R.id.textGPSTest);

        // Setup GPS Service
        // First Check if App has permission to access device location
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                context.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.location_permission_error, Toast.LENGTH_SHORT).show();
            txtLatLong.setText(R.string.location_permission_error);
        }
        else {
            // If app has permission, setup Location service
            locationListener = new MyLocationListener();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Request Location updates with static parameters
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_DISTANCE,
                    locationListener);
            updateTxt();
        }

        // Set game name
        TextView GameName = (TextView) findViewById(R.id.lobbyTextViewGameName);
        GameName.setText(game.getGameID());

        // Set number of players alive
        TextView NumPlayers = (TextView) findViewById(R.id.lobbyTextViewPlayerCount);
        String numPlayersString = String.format("%d Players in Lobby", game.getNumPlayers());
        NumPlayers.setText(numPlayersString);

        // Get Profile image
        ImageRequest ir = new ImageRequest(user.getImageURL(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ImageView imageView = (ImageView)findViewById(R.id.lobbyImageViewProfile);
                imageView.setImageBitmap(response);
                // Make Profile ImageView Rounded
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), response);
                roundDrawable.setCircular(true);
                imageView.setImageDrawable(roundDrawable);
            }
        }, 0, 0, null, null);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(ir);

        // Add a start game button if the user is the host
        Button ButtonStart = (Button) findViewById(R.id.lobbyButtonStart);
        if (game.getHostID() == user.getID()) {
            ButtonStart.setVisibility(View.VISIBLE);
        }
        else ButtonStart.setVisibility(View.INVISIBLE);

        // Start GetPlayers
        gameStart = false;
        getPlayers();
    }

    public void toggleDrawer(View view) {
        if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

    private void addDrawerItems() {
        String[] osArray = { "Radius", "Start Time", "Lobby Host", "Game Option", "Another Game Option" };
        osArray[0] = String.format("Radius = %dm", game.getRadius());
        osArray[1] = String.format("Lobby Host = %d", game.getHostID());
        osArray[2] = String.format("Duration = %d", game.getDuration());
        osArray[4] = String.format("Longitude = %f", game.getXCenter());
        osArray[3] = String.format("Latitude = %f", game.getYCenter());
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    /** Automatically added methods for the MapView */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /** Set up the map once it is ready*/
    private void setUpMap(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.addCircle(new CircleOptions().center(circleLatLng).radius(game.getRadius()).strokeColor(Color.CYAN)); // Add game radius circle to map
        cameraZoomLevel = getZoomLevel((double) game.getRadius());
        // For zooming automatically to the location of the circle
        CameraPosition cameraPosition = new CameraPosition.Builder().target(circleLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Update the user's location within the map (not sure if this is needed)*/
    private void updateMap() {
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // Shouldn't happen
        } // TODO check if user's location on map is updated automatically
    }

    /** Center the map to the user's current location*/
    public void centerMapToMe(View view) {
        if (lastLatLng == null) return;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Center the map to the game's circle*/
    public void centerMapToCircle(View view) {
        if (circleLatLng == null) return;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(circleLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Calculate the GoogleMap zoom level based on the radius of the game circle */
    private float getZoomLevel(Double circleRadius) {
        float zoomLevel = 0;
        if (circleRadius != null){
            double scale = circleRadius / MAP_ZOOM_SCALE_FACTOR; // TODO Does this work on other devices and screens?
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    /** Setup a LocationListener whose methods will be called on Location updates*/
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
            lastLocation = locFromGps;
            LobbyActivity.this.updateTxt();
            LobbyActivity.this.updateMap();
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

    /** Update the textView that displays the user's current lat/long (just for demo/testing*/
    private void updateTxt() {
        Double lat, lon;
        try {
            lat = lastLocation.getLatitude();
            lon = lastLocation.getLongitude();
            lastLatLng = new LatLng(lat, lon);
        } catch (NullPointerException e) {
            e.printStackTrace();
            txtLatLong.setText(R.string.location_null_error);
            return;
        }
        txtLatLong.setText(lastLatLng.toString());
    }

    /** Go to the list of players in the lobby/game */
    public void gotoPlayerList(View view) {
        // Switch to the Player List activity
        Intent intent = new Intent(this, PlayersListActivity.class);
        intent.putExtra(KEY_NUM_PLAYERS, players.length);
        for (int i = 0; i < players.length; i++) {
            String playerIKey = String.format("Player %d", i);
            intent.putExtra(playerIKey, players[i]);
        }
        startActivity(intent);
        // TODO Pass an array of UserAccounts to put in the view
    }

    private void updatePlayers(JSONObject response) {
        try {
            int numPlayers = response.getInt(KEY_NUM_PLAYERS);
            UserAccount[] newPlayers = new UserAccount[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                String playerIKey = String.format("Player %d", i);
                JSONObject tempJSON = response.getJSONObject(playerIKey);
                newPlayers[i] = new UserAccount(tempJSON);
            }
            players = newPlayers;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPlayers() {
        String requestURL = JSON_URL + GETPLAYERS;
        final ProgressDialog loading = ProgressDialog.show(this, "Updating Lobby...", "Please wait...", false, false); // TODO only for demo

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // Dismiss the progress dialog
                        loading.dismiss();
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            authenticateGetPlayers(responseJSON); // Got a response from the server, check if valid
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //show a toast and log the error
                            Toast.makeText(LobbyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("ERROR", "error => " + e.getMessage()); // Print the error to the device log
                        }
                        if (!gameStart) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    getPlayers();
                                }
                            }, 10000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the progress dialog
                        loading.dismiss();

                        //show a toast and log the error
                        Toast.makeText(LobbyActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
    private void authenticateGetPlayers(JSONObject response) {
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
        if (result.equals(RESULT_NORMAL)){
            // TODO update players list and check for start
            Toast.makeText(this, "normal", Toast.LENGTH_LONG).show();
            try {
                game = new Game(response.getJSONObject(Game.KEY_GAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (game.getEnd_time() != null && game.getPlayers_alive() != null) {
                // TODO Switch to game view and pass game and user object along
                return;
            }
            updatePlayers(response);
            return;
        }
        switch (result) {
            case RESULT_ERROR:
                error = "An unknown server error occured";
                break;
            default:
                break;
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
    }

    /** Attempt to start the game. Only available to host of game. */
    public void startGame(View view) {
        int h, m, s;
        int secondsPerDay = 60 * 60 * 24;
        int currentTimeSeconds = (int) (System.currentTimeMillis() / 1000) % secondsPerDay;
        h = currentTimeSeconds / 3600;
        m = (currentTimeSeconds % 3600) / 60;
        s = currentTimeSeconds % 60;
        start_time = String.format("%02d%02d%02d", h, m, s);
        Toast.makeText(this, start_time, Toast.LENGTH_LONG).show();
        startGame();
    }

    /** Attempt to start the game */
    private void startGame() {
        String requestURL = JSON_URL + GAMESTART;
        final ProgressDialog loading = ProgressDialog.show(this, "Starting Game...", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // Dismiss the progress dialog
                        loading.dismiss();
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            authenticateGameStart(responseJSON); // Got a response from the server, check if valid
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //show a toast and log the error
                            Toast.makeText(LobbyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LobbyActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new Hashtable<String, String>();
                parameters.put(Game.KEY_GAMEID, game.getGameID());
                parameters.put(Game.KEY_START_TIME, start_time);
                return parameters;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Process JSON response from server */
    private void authenticateGameStart(JSONObject response) {
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
        if (result.equals(RESULT_GAME_STARTED)){
            // Switch to the GameView Activity
            Game game;
            try{
                JSONObject gameJSON = response.getJSONObject(Game.KEY_GAME);
                game = new Game(gameJSON);
            }catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
            intent.putExtra(Game.KEY_GAME, game);
            // Cancel Location Updates for this activity
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            // Cancel the looping GetPlayers method
            gameStart = false; // TODO does this work? iastate down
            startActivity(intent);
            finish(); // Closes the current activity, stops user from returning to it with back button
            return;
        }
        switch (result) {
            case RESULT_NOT_ENOUGH_PLAYERS:
                error = "Not enough players to start game";
                break;
            case RESULT_START_TIME_INVALID: // shouldn't happen; for debugging
                error = "Start time was invalid";
                break;
            case RESULT_GAMEID_INVALID: // shouldn't happen; for debugging
                error = "Provided game name (gameID) was invalid";
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
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}