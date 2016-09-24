package la_05.com.assassins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    /** Called when user clicks the Create Account button */
    public void createAccount(View view) {
        // Interact with server to create account and open the Home Page
    }

    /** Called when user clicks the log in text */
    public void switchToLogIn(View view) {
        // Switch to the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
