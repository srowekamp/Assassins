package la_05.com.assassins;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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


import java.util.Hashtable;
import java.util.Map;


public class CreateGameActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String BASIC_CG = "CreateGame";

    public static final String KEY_RESULT = "result";
    public static final String RESULT_GAME_CREATED = "success"; // Value of Result when game successfully created
    public static final String RESULT_GAME_EXISTS = "exists"; // Value of Result when game with the name provided already exists
    public static final String RESULT_GAMEID_INVALID = "gameid_error"; // Value of Result when user enters an invalid gameID/name
    public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
    public static final String RESULT_CENTER_INVALID = "center_error"; // Value of Result when either center coordinate is not valid
    public static final String RESULT_RADIUS_INVALID = "radius_error"; // Value of Result when an invalid radius is passed
    public static final String RESULT_HOSTID_INVALID = "hostid_error"; // Value of Result when an invalid host ID is passed
    public static final String RESULT_DURATION_INVALID = "duration_error"; // Value of Result when an invalid duration is passed
    public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs

    private String gameName;
    private String password;
    private String duration;
    private String radius;
    private String xcenter;
    private String ycenter;

    public static final int DURATION_MIN_VALUE = 10; // Minimum duration of the game in minutes
    public static final int DURATION_MAX_VALUE = 60; // Maximum duration of the game in minutes
    public static final int RADIUS_MIN_VALUE = 100; // Minimum size of the game radius in m = 100m
    public static final int RADIUS_MAX_VALUE = 1000; // Maximum size of the game radius in m = 1000m

    private TextView textViewDuration;
    private SeekBar seekBarDuration;
    private TextView textViewRadius;
    private SeekBar seekBarRadius;
    private Button buttonCloseMap;
    private Button buttonCreateGame;
    private ImageView imageViewCenter;

    private int radiusMeters;

    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng lastLatLng;
    private LatLng centerLatLng;

    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);

        duration = String.format("%d", DURATION_MIN_VALUE * 60); // initialize in case user doesn't touch SeekBar
        radiusMeters = RADIUS_MAX_VALUE;
        radius = String.format("%d", radiusMeters);

        seekBarDuration = (SeekBar) findViewById(R.id.createGameSeekBarDuration);
        textViewDuration = (TextView) findViewById(R.id.createGameTextViewDuration);
        mapView = (MapView) findViewById(R.id.createGameMapView);
        textViewRadius = (TextView) findViewById(R.id.createGameTextViewRadius);
        seekBarRadius = (SeekBar) findViewById(R.id.createGameSeekBarRadius);
        buttonCloseMap = (Button) findViewById(R.id.createGameButtonCloseMap);
        buttonCreateGame = (Button) findViewById(R.id.createGameButtonCreateGame);
        imageViewCenter = (ImageView) findViewById(R.id.createGameImageViewCenter);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMap(googleMap);
            }
        });

        // Get the most recent location to center the map on the user
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
            // If app has permission, get the most recent location
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        // Set up listener for the duration SeekBar to track position updates
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int durationMinutes = ((DURATION_MAX_VALUE - DURATION_MIN_VALUE) * progress / 100) + DURATION_MIN_VALUE;
                textViewDuration.setText(String.format("Length of game: %d minutes", durationMinutes));
                duration = String.format("%d", durationMinutes * 60);
            }
        });

        // Set up listener for the radius SeekBar to track position updates
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusMeters = ((RADIUS_MAX_VALUE - RADIUS_MIN_VALUE) * progress / 100) + RADIUS_MIN_VALUE;
                textViewRadius.setText(String.format("Radius: %d meters", radiusMeters));
                radius = String.format("%d", radiusMeters);
                centerLatLng = googleMap.getCameraPosition().target;
                updateMap();
            }
        });

        //Fix editTextPassword Font
        EditText password = (EditText) findViewById(R.id.createGameEditTextPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
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
        // For zooming automatically to the location of the circle
        float cameraZoomLevel = getZoomLevel((double) RADIUS_MAX_VALUE);
        // Draw a circle at user's location to represent default values for radius, x_center, and y_center
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.addCircle(new CircleOptions().center(lastLatLng).radius(radiusMeters).strokeColor(Color.CYAN));
        centerLatLng = lastLatLng;
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // Shouldn't happen
        }
    }

    /** Calculate the GoogleMap zoom level based on given radius */
    private float getZoomLevel(Double circleRadius) {
        float zoomLevel = 0;
        if (circleRadius != null){
            double scale = circleRadius / LobbyActivity.MAP_ZOOM_SCALE_FACTOR; // TODO Does this work on other devices and screens?
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    /** Update the circle to the new center and/or radius. Called when radius SeekBar is changed */
    private void updateMap() {
        googleMap.clear();
        CircleOptions gameRadiusCircle = new CircleOptions().center(centerLatLng).radius(radiusMeters).strokeColor(Color.CYAN);
        googleMap.addCircle(gameRadiusCircle);
    }

    /** Open the map and relevant views so the user can choose center/radius of game */
    public void openMap(View view) {
        mapView.setVisibility(View.VISIBLE);
        textViewRadius.setVisibility(View.VISIBLE);
        textViewRadius.bringToFront();
        seekBarRadius.setVisibility(View.VISIBLE);
        seekBarRadius.bringToFront();
        buttonCloseMap.setVisibility(View.VISIBLE);
        buttonCloseMap.bringToFront();
        imageViewCenter.setVisibility(View.VISIBLE);
        imageViewCenter.bringToFront();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.createGameRelativeLayoutRadius);
        relativeLayout.bringToFront();
        relativeLayout.invalidate();
    }

    /** After the user has chosen the center and radius of the game from the map, close the map and other relevant views */
    public void closeMap(View view) {
        mapView.setVisibility(View.INVISIBLE);
        textViewRadius.setVisibility(View.INVISIBLE);
        seekBarRadius.setVisibility(View.INVISIBLE);
        buttonCloseMap.setVisibility(View.INVISIBLE);
        imageViewCenter.setVisibility(View.INVISIBLE);
        buttonCreateGame.setEnabled(true);
    }

    /** Called when the create game button is pressed */
    public void createGame(View view) {
        EditText GameName = (EditText) findViewById(R.id.createGameEditTextGameName);
        EditText Password = (EditText) findViewById(R.id.createGameEditTextPassword);

        // Set local variables to hold user-entered values
        this.gameName = GameName.getText().toString();
        this.password = Password.getText().toString();
        // Duration is set with SeekBar
        // Get center and radius from map
        this.xcenter = String.format("%f", centerLatLng.longitude);
        this.ycenter = String.format("%f", centerLatLng.latitude);

        if (gameName.length() > 0 && password.length() > 0)
        // Attempt to create game with server
        createGame();
    }

    /** Upon getting required info from UI, creates the game and updates the server. */
    private void createGame() {
        String requestURL = JSON_URL + BASIC_CG;
        final ProgressDialog loading = ProgressDialog.show(this, "Creating Game...", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // Dismiss the progress dialog
                        loading.dismiss();
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            authenticate(responseJSON); // Got a response from the server, check if valid
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //show a toast and log the error
                            Toast.makeText(CreateGameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CreateGameActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> parameters = new Hashtable<String, String>();
                parameters.put(Game.KEY_GAMEID, gameName);
                parameters.put(Game.KEY_PASSWORD, password);
                parameters.put(Game.KEY_X_CENTER, xcenter);
                parameters.put(Game.KEY_Y_CENTER, ycenter);
                parameters.put(Game.KEY_RADIUS, radius);
                parameters.put(Game.KEY_HOSTID, String.format("%d", user.getID()));
                parameters.put(Game.KEY_DURATION, duration);
                return parameters;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Upon receiving a response from the server, authenticate it and decide what to do next */
    private void authenticate(JSONObject response) {
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
        if (result.equals(RESULT_GAME_CREATED)){
            // Switch to the Lobby Activity
            Game game;
            try{
                JSONObject gameJSON = response.getJSONObject(Game.KEY_GAME);
                game = new Game(gameJSON);
            }catch(JSONException e){
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, LobbyActivity.class);
            intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
            intent.putExtra(Game.KEY_GAME, game);
            startActivity(intent);
            finish(); // Closes the current activity, stops user from returning to it with back button
            return;
        }
        // Most of these shouldn't happen using the app's interface
        switch (result) {
            case RESULT_OTHER_ERROR:
                error = "Unknown Error Occurred (2)";
                break;
            case RESULT_GAME_EXISTS:
                error = "A Game with that name already Exists";
                break;
            case RESULT_GAMEID_INVALID:
                error = "Invalid game name";
                break;
            case RESULT_PASSWORD_INVALID:
                error = "Invalid password";
                break;
            case RESULT_CENTER_INVALID:
                error = "Invalid center";
                break;
            case RESULT_RADIUS_INVALID:
                error = "Invalid radius";
                break;
            case RESULT_HOSTID_INVALID:
                error = "Invalid hostID";
                break;
            case RESULT_DURATION_INVALID:
                error = "Invalid Duration";
                break;
            default:
                break;
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
    }

    // Prevents the main menu from being reloaded when the top left back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}