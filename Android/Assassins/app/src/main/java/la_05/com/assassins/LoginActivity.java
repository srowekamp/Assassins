package la_05.com.assassins;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

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
    }

    /** Called when user clicks the create account text */
    public void switchToCreateAccount(View view) {
        // Switch to the create account activity
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
        finish(); // Closes the current activity, stops user from returning to it with back button
    }

    /** Called when user clicks the forgot password text */
    public void forgotPassword(View view) {
        //Probably not doing this but it's here for now
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