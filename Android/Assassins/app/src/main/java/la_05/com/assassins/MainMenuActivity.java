package la_05.com.assassins;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
}
