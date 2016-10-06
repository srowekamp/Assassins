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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/AssassinsCreateAccount/";
    public static final String BASIC_CA = "AssassinsCreateAccountBasic";
    public static final String JSON_CA = "AssassinsCreateAccountJSON"; // Currently NOT implemented

    public static final String KEY_ID = "idusers";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_RESULT = "result";
    public static final String RESULT_ACCOUNT_CREATED = "success"; // Value of Result when account successfully created
    public static final String RESULT_ACCOUNT_EXISTS = "exists"; // Value of Result when user with username provided already exists
    public static final String RESULT_OTHER_ERROR = "fail"; // Value of Result when an error occurs


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Fix editTextPassword Font
        EditText password = (EditText) findViewById(R.id.editTextCreatePassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        password = (EditText) findViewById(R.id.editTextConfirmPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }

    /** Called when user clicks the Create Account button */
    public void createAccount(View view) {
        // Interact with server to create account and open the Home Page
        EditText name = (EditText) findViewById(R.id.editTextCreateName);
        EditText username = (EditText) findViewById(R.id.editTextCreateUserName);
        EditText password = (EditText) findViewById(R.id.editTextCreatePassword);
        EditText confirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        String nameString = name.getText().toString(); // Currently not Used
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        if (nameString.equals("") || usernameString.equals("") || passwordString.equals("")) {
            return;
        }

        if (!passwordString.equals(confirmPasswordString)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(this, "Passwords match", Toast.LENGTH_SHORT).show();
        createAccountString(usernameString, passwordString);
    }

    /** Send a formatted string using URL parameters to server for authentication */
    private void createAccountString(String username, String password) {
        String requestURL = JSON_URL + BASIC_CA + "?username=" + username + "&password=" + password;

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

    /** Check the JSON object from the server to check if user has entered valid credentials */
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
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish(); // Closes the current activity, stops user from returning to it with back button
            return;
        }
        if (result != null && result.equals(RESULT_OTHER_ERROR)) {
            error = "Unknown Error Occurred (2)";
        }
        else if (result != null && result.equals(RESULT_ACCOUNT_EXISTS)) {
            error = "An Account with that Username already Exists";
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
    }

    /** Called when user clicks the log in text */
    public void switchToLogIn(View view) {
        // Switch to the login activity
        Intent intent = new Intent(this, LoginActivity.class);
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
