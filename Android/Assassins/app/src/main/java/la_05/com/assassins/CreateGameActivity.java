package la_05.com.assassins;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.maps.MapView;


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

    private MapView mapView;

    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game_new);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);

        duration = String.format("%d", DURATION_MIN_VALUE * 60); // initialize in case user doesn't touch seekbar
        radius = String.format("%d", RADIUS_MIN_VALUE);

        seekBarDuration = (SeekBar) findViewById(R.id.createGameSeekBarDuration);
        textViewDuration = (TextView) findViewById(R.id.createGameTextViewDuration);
        mapView = (MapView) findViewById(R.id.createGameMapView);
        textViewRadius = (TextView) findViewById(R.id.createGameTextViewRadius);
        seekBarRadius = (SeekBar) findViewById(R.id.createGameSeekBarRadius);

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
                int radiusMeters = ((RADIUS_MAX_VALUE - RADIUS_MIN_VALUE) * progress / 100) + RADIUS_MIN_VALUE;
                textViewRadius.setText(String.format("Radius: %d meters", radiusMeters));
                duration = String.format("%d", radiusMeters);
            }
        });

        //Fix editTextPassword Font
        EditText password = (EditText) findViewById(R.id.createGameEditTextPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void openMap(View view) {
        mapView.setVisibility(View.VISIBLE);
        textViewRadius.setVisibility(View.VISIBLE);
        seekBarRadius.setVisibility(View.VISIBLE);
    }

    public void createGame(View view) {
        EditText GameName = (EditText) findViewById(R.id.createGameEditTextGameName);
        EditText Password = (EditText) findViewById(R.id.createGameEditTextPassword);

        // Set local variables to hold user-entered values
        this.gameName = GameName.getText().toString();
        this.password = Password.getText().toString();
        // Duration is set with seekbar
        // Get center and radius from map

        // Attempt to create game with server
        createGame();
    }

    //called when create game button is pressed
    public void createGameOld(View view) {
        //creates all the values on the activity create game xml

        EditText GameName = (EditText) findViewById(R.id.createGameEditTextGameName);
        EditText Password = (EditText) findViewById(R.id.createGameEditTextPassword);
        EditText Duration = (EditText) findViewById(R.id.createGameEditTextDuration);
        EditText Radius = (EditText) findViewById(R.id.createGameEditTextRadius);
        EditText XCenter = (EditText) findViewById(R.id.createGameEditTextXCenter);
        EditText YCenter = (EditText) findViewById(R.id.createGameEditTextYCenter);

        // Set local variables to hold user-entered values
        this.gameName = GameName.getText().toString();
        this.password = Password.getText().toString();
        this.duration = Duration.getText().toString();
        this.radius   = Radius.getText().toString();
        this.xcenter  = XCenter.getText().toString();
        this.ycenter  = YCenter.getText().toString();

        // Attempt to create game with server
        createGame();
    }

    /**
    Upon getting required info from UI, creates the game and updates the server.
     */
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