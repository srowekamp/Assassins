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
     *  Open the join game activity
     */
    public void gotoJoinGame(View view){
        Intent intent = new Intent(this, JoinGameActivity.class);
        intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
        startActivity(intent);
    }

    /**
     * Open the create game activity
     */
    public void gotoCreateGame(View view){
        Intent intent = new Intent(this, CreateGameActivity.class);
        intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
        startActivity(intent);
    }

    public void gotoOptions(View view){
        if(user.getUserName().equals("admin")) {
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
            startActivity(intent);
        }
        Toast.makeText(this, "You must construct additional Pylons.", Toast.LENGTH_SHORT).show();
        return;
    }

    public void gotoAccount(View view){
        Intent intent = new Intent(this, UserAccountActivity.class);
        intent.putExtra(UserAccount.KEY_USER_ACCOUNT, user);
        startActivity(intent);
    }

    public void gotoGameEditor(View view){
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
