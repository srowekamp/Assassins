package la_05.com.assassins;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

public class UserAccountActivity extends AppCompatActivity {

    UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);

        TextView textViewRealName = (TextView) findViewById(R.id.textViewRealName);
        TextView textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        TextView textViewTotalKills = (TextView) findViewById(R.id.textViewTotalKills);
        TextView textViewGamesPlayed = (TextView) findViewById(R.id.textViewGamesPlayed);

        textViewRealName.setText(user.getRealName());
        textViewUsername.setText(user.getUserName());
        textViewTotalKills.setText(user.getTotalKills().toString());
        textViewGamesPlayed.setText(user.getGamesPlayed().toString());

        ImageView imageViewUserImage = (ImageView) findViewById(R.id.imageViewUserImage);
        //imageViewUserImage.setImageBitmap(userImage);

        //ImageRequest ir = new ImageRequest(user.getImageURL(), new Response.Listener<Bitmap>())
    }
}
