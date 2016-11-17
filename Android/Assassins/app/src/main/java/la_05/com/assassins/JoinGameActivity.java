package la_05.com.assassins;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

public class JoinGameActivity extends AppCompatActivity {

    private UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);

        //Fix editTextPassword Font
        EditText password = (EditText) findViewById(R.id.joinGameEditTextPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void joinGame(View view) {

    }
}
