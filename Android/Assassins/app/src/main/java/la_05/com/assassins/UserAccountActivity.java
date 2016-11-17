package la_05.com.assassins;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

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

        textViewRealName.setText("Name: " + user.getRealName());
        textViewUsername.setText("Username: " + user.getUserName());
        textViewTotalKills.setText("Total Kills: " + user.getTotalKills().toString());
        textViewGamesPlayed.setText("Games Played: " + user.getGamesPlayed().toString());

        final ImageView imageViewUserImage = (ImageView) findViewById(R.id.imageViewUserImage);

        ImageRequest ir = new ImageRequest(user.getImageURL(), new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {
                imageViewUserImage.setImageBitmap(response);
            }
        }, 0, 0, null, null);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(ir);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
