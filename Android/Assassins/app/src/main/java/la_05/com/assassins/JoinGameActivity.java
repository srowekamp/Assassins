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
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Hashtable;
import java.util.Map;

public class JoinGameActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String BASIC_JG = "JoinGame";

    public static final String KEY_RESULT = "result";
    public static final String RESULT_JOIN_GAME_SUCCESS = "success";
    public static final String RESULT_ALREADY_JOINED = "already_joined";
    public static final String RESULT_PASSWORD_INCORRECT = "password_incorrect";
    public static final String RESULT_GAME_NOT_FOUND = "game_not_found";
    public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur

    private String gameName;
    private String password;

    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);

        //Fix editTextPassword Font
        EditText password = (EditText) findViewById(R.id.joinGameEditTextPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void joinGame(View view) {
        EditText GameName = (EditText) findViewById(R.id.joinGameEditTextGameName);
        EditText Password = (EditText) findViewById(R.id.joinGameEditTextPassword);

        // Set local variables to hold user-entered values
        this.gameName = GameName.getText().toString();
        this.password = Password.getText().toString();

        // Attempt to create game with server
        joinGame();
    }

    private void joinGame() {
        String requestURL = JSON_URL + BASIC_JG;
        final ProgressDialog loading = ProgressDialog.show(this, "Joining Game...", "Please wait...", false, false);

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
                            Toast.makeText(JoinGameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(JoinGameActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new Hashtable<String, String>();
                parameters.put(Game.KEY_GAMEID, gameName);
                parameters.put(Game.KEY_PASSWORD, password);
                parameters.put(UserAccount.KEY_ID, String.format("%d", user.getID()));
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
        if (result.equals(RESULT_JOIN_GAME_SUCCESS) || result.equals(RESULT_ALREADY_JOINED)){
            if (result.equals(RESULT_ALREADY_JOINED)){
                Toast.makeText(this, "You have already joined this game", Toast.LENGTH_LONG).show();
            }
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
            case RESULT_ERROR:
                error = "Unknown Error Occurred (2)";
                break;
            case RESULT_PASSWORD_INCORRECT:
                error = "Password incorrect";
                break;
            case RESULT_GAME_NOT_FOUND:
                error = "No game found with that name";
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
