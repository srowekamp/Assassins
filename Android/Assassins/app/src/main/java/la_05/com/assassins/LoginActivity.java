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

public class LoginActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String BASIC_LOGIN = "Login";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_RESULT = "result";
    public static final String RESULT_LOGIN_SUCCESS = "success"; // Value of Result when user enters a valid login
    public static final String RESULT_LOGIN_FAIL = "fail"; // Value of Result when user enters an invalid login
    public static final String RESULT_USERNAME_INVALID = "username_error"; // Value of Result when user enters an invalid username
    public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
    public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs

    private String username;
    private String password;

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
    public void login(View view) {
        // Interact with server to log in and open the Home Page
        EditText editTextUsername = (EditText) findViewById(R.id.editTextCreateUserName);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextCreatePassword);

        username = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();

        if (username.equals("")|| password.equals("")) {
            return;
        }

        // Attempt Server Authentication
        login();
    }

    /** Send the provided account information to the server for authentication */
    private void login() {
        String requestURL = JSON_URL + BASIC_LOGIN;
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Dismiss the progress dialog
                        loading.dismiss();
                        try {
                            JSONObject responseJSON = new JSONObject(response);
                            authenticate(responseJSON); // Got a response from the server, check if valid
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //show a toast and log the error
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Add the parameters to the request
                Map<String, String> params = new Hashtable<String, String>();
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Check the JSON object from the server to check if user has entered valid credentials */
    private void authenticate(JSONObject response) {
        String result = null;
        String error = "Unknown Error Occurred (1)";

        try {
            result = (String) response.get(KEY_RESULT);
            //test = response.getString(UserAccount.KEY_USER_ACCOUNT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result != null && result.equals(RESULT_LOGIN_SUCCESS)){
            UserAccount user = null;
            try {
                JSONObject account = new JSONObject (response.getString(UserAccount.KEY_USER_ACCOUNT));
                user = new UserAccount(account);
                Toast.makeText(this, "Welcome, " + account.getString(UserAccount.KEY_REAL_NAME), Toast.LENGTH_LONG).show(); // indicate failure
            }catch (JSONException e) {
                e.printStackTrace();
            }

            // Switch to the Main Menu Activity
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
            startActivity(intent);
            finish(); // Closes the current activity, stops user from returning to it with back button
            return;
        }
        if (result != null && result.equals(RESULT_OTHER_ERROR)) {
            error = "Unknown Error Occurred (2)";
        }
        else if (result != null && result.equals(RESULT_LOGIN_FAIL)) {
            error = "Unknown Username or Password";
        }
        else if (result != null && result.equals(RESULT_USERNAME_INVALID)) {
            error = "Invalid Username";
        }
        else if (result != null && result.equals(RESULT_PASSWORD_INVALID)) {
            error = "Invalid Password";
        }
        Toast.makeText(this, error, Toast.LENGTH_LONG).show(); // indicate failure
    }

    public void enterAdminCred(View view) {
        EditText editTextUsername = (EditText) findViewById(R.id.editTextCreateUserName);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextCreatePassword);
        editTextUsername.setText("admin");
        editTextPassword.setText("password");
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