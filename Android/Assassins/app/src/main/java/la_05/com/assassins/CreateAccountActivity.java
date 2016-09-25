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

public class CreateAccountActivity extends AppCompatActivity {

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

        String nameString = name.getText().toString();
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        if (!passwordString.equals(confirmPasswordString)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
            Toast.makeText(this, "Passwords match", Toast.LENGTH_SHORT).show();
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
