package la_05.com.assassins;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity{

    UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);
    }

    /**
     *  This method will bring the user to the Join Game Screen
     *  10/05/2016 - brings you to the lobby
     */
    public void joinGame(View view){
        //currently not what it is going to do, however, this is here for just now purposes
        // Switch to the Lobby Activity
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish(); // Closes the current activity, stops user from returning to it with back button
    }

    public void createGame(View view){
        Intent intent = new Intent(this, CreateGameActivity.class);
        intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
        startActivity(intent);
    }

    public void options(View view){
        // TODO
    }

    public void account(View view){
        Intent intent = new Intent(this, UserAccountActivity.class);
        intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
        startActivity(intent);
        // TODO
    }

    public void gameEditor(View view){
        // TODO
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
