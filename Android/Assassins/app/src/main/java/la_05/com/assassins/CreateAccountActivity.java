package la_05.com.assassins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String JSON_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/";
    public static final String BASIC_CA = "CreateAccount";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REAL_NAME = "real_name";
    public static final String KEY_B64_JPG = "b64_jpg";

    public static final String KEY_RESULT = "result";
    public static final String RESULT_ACCOUNT_CREATED = "success"; // Value of Result when account successfully created
    public static final String RESULT_ACCOUNT_EXISTS = "exists"; // Value of Result when user with username provided already exists
    public static final String RESULT_USERNAME_INVALID = "username_error"; // Value of Result when user enters an invalid username
    public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
    public static final String RESULT_IMAGE_INVALID = "image_error"; // Value of Result when an invalid Base64 encoded image is passed
    public static final String RESULT_NAME_INVALID = "name_error"; // Value of Result when an invalid real name is passed
    public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs

    private int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Bitmap bitmap;

    private String username;
    private String password;
    private String realName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        imageView = (ImageView) findViewById(R.id.imageView);

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
        EditText name = (EditText) findViewById(R.id.editTextCreateName);
        EditText username = (EditText) findViewById(R.id.editTextCreateUserName);
        EditText password = (EditText) findViewById(R.id.editTextCreatePassword);
        EditText confirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        this.realName = name.getText().toString();
        this.username = username.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        // Make sure user has selected an image to upload
        if (bitmap == null) {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make sure the fields are not blank
        if (realName.equals("") || this.username.equals("") || passwordString.equals("")) {
            return;
        }

        // Make sure the two password fields match
        if (!passwordString.equals(confirmPasswordString)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        this.password = passwordString;

        // Begin interaction with server
        createAccount();
    }

    /** Send the provided account information to the server to create a new account */
    private void createAccount() {
        String requestURL = JSON_URL + BASIC_CA;
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
                            Toast.makeText(CreateAccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CreateAccountActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "error => " + error.toString()); // Print the error to the device log
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to JPEG to Base64 String
                String b64image = getStringImage(bitmap);

                //Add the parameters to the request
                Map<String, String> params = new Hashtable<String, String>();
                params.put(KEY_B64_JPG, b64image);
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_REAL_NAME, realName);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /** Open a file chooser so the user can pick an image to use as a profile picture */
    public void showFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /** Get the image the user picked in the file chooser */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Convert a provided bitmap image to JPEG and then return the Base64 encoded image as a String */
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /** Check the JSON object from the server for result */
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
        else if (result != null && result.equals(RESULT_USERNAME_INVALID)) {
            error = "Invalid Username";
        }
        else if (result != null && result.equals(RESULT_PASSWORD_INVALID)) {
            error = "Invalid Password";
        }
        else if (result != null && result.equals(RESULT_IMAGE_INVALID)) {
            error = "Image Error";
        }
        else if (result != null && result.equals(RESULT_NAME_INVALID)) {
            error = "Name Error";
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
