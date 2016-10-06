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

public class LoginActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/AssassinsLogin/";
    public static final String BASIC_LOGIN = "AssassinsLoginBasic";
    public static final String JSON_LOGIN = "AssassinsLoginJSON"; // Currently NOT implemented
    public static final String KEY_ID = "idusers";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String AUTH_SUCCESS = "success";
    public static final String AUTH_FAILURE = "fail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Fix editTextPassword Font
        EditText password = (EditText) findViewById(R.id.editTextCreatePassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }

    /** Called when user clicks the Login button */
    public void logIn(View view) {
        // Interact with server to log in and open the Home Page
        EditText username = (EditText) findViewById(R.id.editTextCreateUserName);
        EditText password = (EditText) findViewById(R.id.editTextCreatePassword);

        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        if (usernameString.equals("")|| passwordString.equals("")) {
            return;
        }

        loginString(usernameString, passwordString);
    }

    /** Send a formatted string using URL parameters to server for authentication */
    private void loginString(String username, String password) {
        //http://proj-309-la-05.cs.iastate.edu:8080/AssassinsLogin/AssassinsLoginBasic?username=nathan&password=password1

        String requestURL = JSON_URL + BASIC_LOGIN + "?username=" + username + "&password=" + password;

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
                        Log.d("ERROR","error => "+error.toString()); // Print the error to the device log
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    /** Send a JSON object to the string for authentication (Currently NOT implemented) */
    private void loginJSON(String username, String password) {
        JSONObject jsLogin = new JSONObject();
        try {
            jsLogin.put(KEY_USERNAME, username);
            jsLogin.put(KEY_PASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, JSON_URL, jsLogin,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        authenticate(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    /** Check the JSON object from the server to check if user has entered valid credentials */
    private void authenticate(JSONObject response) {
        String info = null;
        try {
            info = (String) response.get("info");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (info.equals(AUTH_SUCCESS)){
            // Switch to the Main Menu Activity
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish(); // Closes the current activity, stops user from returning to it with back button
        }
        else {
            Toast.makeText(LoginActivity.this, AUTH_FAILURE, Toast.LENGTH_LONG).show(); // indicate failure
        }
    }

    /** Called when user clicks the create account text */
    public void switchToCreateAccount(View view) {
        // Switch to the create account activity
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
        finish(); // Closes the current activity, stops user from returning to it with back button
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
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}