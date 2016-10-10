package la_05.com.assassins;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateGameActivity extends AppCompatActivity {


    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String BASIC_CG = "CreateGameBasic";
    public static final String KEY_RESULT = "result";
    public static final String RESULT_ACCOUNT_CREATED = "success"; // Value of Result when account successfully created

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

    }

    //called when create game button is pressed
    public void CreateGame(View view) {
        //creates all the values on the activity create game xml
        EditText GameID = (EditText) findViewById(R.id.editText);
        EditText NumPlayers = (EditText) findViewById(R.id.editTextPlayers);
        EditText SizeGame = (EditText) findViewById(R.id.editTextSize);
        EditText LengthGame = (EditText) findViewById(R.id.Length);
        Switch Mod = (Switch) findViewById(R.id.switchMod);
        Switch PrivateGame = (Switch) findViewById(R.id.switchPrivate);

        String GameIDString = GameID.getText().toString();
        String NumPlayersString = NumPlayers.getText().toString();
        String SizeGameString = SizeGame.getText().toString();
        String ModString = Mod.getText().toString();
        String PrivateGameString = PrivateGame.getText().toString();

        //Here until server is fully connected and authentication is ready
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish();
        //createGameString(GameIDString, NumPlayersString, SizeGameString, ModString, PrivateGameString);


    }
    //Not yet fully implemented
    private void createGameString(String GameID, String NumPlayers, String SizeGame, String Mod, String PrivateGame) {
        String requestURL = JSON_URL + BASIC_CG + "?GameID" + GameID + "?NumPlayers" + NumPlayers + "?SizeGame" + SizeGame + "?Moderator" + Mod + "?PrivateGame" +PrivateGame;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        authenticate(response); // Got a response from the server, check if valid
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void authenticate(JSONObject response) {
        String result = null;
        String error = "Unknown Error Occurred (1)";;
        try {
            result = (String) response.get(KEY_RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result != null && result.equals(RESULT_ACCOUNT_CREATED)){
            // Switch to the Main Menu Activity
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
