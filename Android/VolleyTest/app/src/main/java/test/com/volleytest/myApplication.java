package test.com.volleytest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Nathan on 10/5/2016.
 */

public class myApplication extends ArrayAdapter<String> {
    private String[] ids;
    private String[] usernames;
    private String[] passwords;
    private Activity context;

    public myApplication(Activity context, String[] ids, String[] usernames, String[] passwords) {
        super(context, R.layout.list_view_layout, ids);
        this.context = context;
        this.ids = ids;
        this.usernames = usernames;
        this.passwords = passwords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_view_layout, null, true);
        TextView textViewId = (TextView) listViewItem.findViewById(R.id.textViewId);
        TextView textViewUsername = (TextView) listViewItem.findViewById(R.id.textViewUsername);
        TextView textViewPassword = (TextView) listViewItem.findViewById(R.id.textViewPassword);

        textViewId.setText(ids[position]);
        textViewUsername.setText(usernames[position]);
        textViewPassword.setText(passwords[position]);

        return listViewItem;
    }
}
