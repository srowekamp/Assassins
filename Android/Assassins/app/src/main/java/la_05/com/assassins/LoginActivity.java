package la_05.com.assassins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /** Called when user clicks the Login button */
    public void logIn(View view) {
        // Interact with server to log in and open the Home Page
    }

    /** Called when user clicks the create account text */
    public void switchToCreateAccount(View view) {
        // Switch to the create account activity
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }
}