package la_05.com.assassins;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class CreateGameActivity extends AppCompatActivity {




    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String BASIC_CG = "CreateGameBasic";
    public static final String KEY_RESULT = "result";
    public static final String RESULT_GAME_CREATED = "success"; // Value of Result when account successfully created

    private String gameID;
    private String numPlayers;
    private String sizeofGame;
    private String password;

    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);

    }


    //called when create game button is pressed
    public void CreateGame(View view) {
        //creates all the values on the activity create game xml

        EditText GameID = (EditText) findViewById(R.id.editText);
        EditText NumPlayers = (EditText) findViewById(R.id.editTextPlayers);
        EditText SizeGame = (EditText) findViewById(R.id.editTextSize);
        EditText LengthGame = (EditText) findViewById(R.id.Length);
        EditText Password = (EditText) findViewById(R.id.PasswordGame);
        //Switch Mod = (Switch) findViewById(R.id.switchMod);
        //Switch PrivateGame = (Switch) findViewById(R.id.switchPrivate);


        this.gameID = GameID.getText().toString();
        this.numPlayers = NumPlayers.getText().toString();
        this.sizeofGame  = SizeGame.getText().toString();
        this.password  = Password.getText().toString();
        //String ModString = Mod.getText().toString();
        //String PrivateGameString = PrivateGame.getText().toString();


        //Here until server is fully connected and authentication is ready
        //Intent intent = new Intent(this, LobbyActivity.class);
        //startActivity(intent);
        //finish();
        createGame();




    }
    /*
    Upon getting required info from UI, creates the game and updates the server.
     */
    private void createGame() {
        String requestURL = JSON_URL + BASIC_CG;
        final ProgressDialog loading = ProgressDialog.show(this, "Creating Game...", "Please wait...", false, false);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){


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
            protected Map<String, String> getparameters() throws AuthFailureError{
                Map<String, String> parameters = new Hashtable<String, String>();
                parameters.put(Game.KEY_GAMEID, gameID);
                parameters.put(Game.KEY_PASSWORD, password);
                parameters.put(xcenter);
                parameters.put(ycenter);
                parameters.put(Game.KEY_RADIUS, sizeofGame);
                parameters.put(Game.KEY_HOSTID, )
                return parameters;
            }
        };
    }


    private void authenticate(JSONObject response) {
        String result = null;
        String error = "Unknown Error Occurred (1)";;
        try {
            result = (String) response.get(KEY_RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result != null && result.equals(RESULT_GAME_CREATED)){
            // Switch to the Main Menu Activity
            Game game = null;
            try{
                JSONObject gameinst = new JSONObject(response.getString(Game.KEY_GAME));
                game = new Game(gameinst);


            }catch(JSONException e){
                e.printStackTrace();
            }
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
            finish(); // Closes the current activity, stops user from returning to it with back button
            return;
        }


    }


    public void SwitchToMainMenu(View view){
        //switches to Main Menu when pressed
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();


    }
}
