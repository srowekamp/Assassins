package la_05.com.assassins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PlayersListActivity extends AppCompatActivity {

    private UserAccount[] players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        setTitle("Players");

        int numPlayers = (int) getIntent().getSerializableExtra(LobbyActivity.KEY_NUM_PLAYERS);
        String[] lsArray = new String[numPlayers];
        players = new UserAccount[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            String playerIKey = String.format("Player %d", i);
            players[i] = (UserAccount) getIntent().getSerializableExtra(playerIKey);
            lsArray[i] = players[i].getUserName();
        }

        //RelativeLayout mListLayout = (RelativeLayout) findViewById(R.id.layout_players_list);
        ListView mListView = (ListView) findViewById(R.id.playersListListView);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lsArray);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayersListActivity.this, UserAccountActivity.class);
                intent.putExtra(UserAccount.KEY_USER_ACCOUNT, players[position]);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
