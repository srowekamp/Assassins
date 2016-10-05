package test.com.volleytest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nathan on 10/5/2016.
 */

public class ParseJSON {
    public static String[] ids;
    public static String[] usernames;
    public static String[] passwords;
    public static final String KEY_ID = "idusers";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private JSONArray users;
    private String json;
    public ParseJSON(String json) { this.json = json; }

    protected void parseJSON() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONArray(json).getJSONObject(0);
            // users = jsonObject.getJSONArray(JSON_ARRAY);

            users = new JSONArray(json);
            ids = new String[users.length()];
            usernames = new String[users.length()];
            passwords = new String[users.length()];

            for (int i = 0; i < users.length(); i++) {
                JSONObject jo = users.getJSONObject(i);
                ids[i] = jo.getString(KEY_ID);
                usernames[i] = jo.getString(KEY_USERNAME);
                passwords[i] = jo.getString(KEY_PASSWORD);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
