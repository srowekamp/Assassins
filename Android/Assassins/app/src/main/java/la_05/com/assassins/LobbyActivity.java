package la_05.com.assassins;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class LobbyActivity extends AppCompatActivity {

    public static final String RESULT_START_TIME_INVALID = "start_time_error"; // Value of Result when an invalid start_time is passed to GameStart

    public static final int LOCATION_UPDATE_INTERVAL = 10000; // Minimum time between location updates in milliseconds
    public static final float LOCATION_UPDATE_DISTANCE = 2; // Minimum distance between location updates in meters
    public static final int MAP_ZOOM_SCALE_FACTOR = 350; // Constant used to determine map zoom

    private TextView txtLatLong;
    private MapView mapView;
    private Location lastLocation;
    private LatLng lastLatLng;
    private GoogleMap googleMap;
    //private double gameCircleRadius = 300;
    private float cameraZoomLevel = 0;
    private LatLng circleLatLng;
    //private boolean mapReady = false;
    //private boolean mapCircleDrawn = false;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    LocationListener locationListener;

    UserAccount user;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        user = (UserAccount) getIntent().getSerializableExtra(UserAccount.KEY_USER_ACCOUNT);
        game = (Game) getIntent().getSerializableExtra(Game.KEY_GAME);
        circleLatLng = new LatLng(game.getYCenter(), game.getXCenter());
        // Setup the Map
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMap(googleMap);
                updateMap();
            }
        });

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        txtLatLong = (TextView) findViewById(R.id.textGPSTest);

        // Setup GPS Service
        // First Check if App has permission to access device location
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                context.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.location_permission_error, Toast.LENGTH_SHORT).show();
            txtLatLong.setText(R.string.location_permission_error);
        }
        else {
            // If app has permission, setup Location service
            locationListener = new MyLocationListener();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Request Location updates with static parameters
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_DISTANCE,
                    locationListener);
            updateTxt();
        }

        // Set game name
        TextView GameName = (TextView) findViewById(R.id.lobbyTextViewGameName);
        GameName.setText(game.getGameID());

        // Set number of players alive
        TextView NumPlayers = (TextView) findViewById(R.id.lobbyTextViewPlayerCount);
        String numPlayersString = String.format("%d Players in Lobby", game.getNumPlayers());
        NumPlayers.setText(numPlayersString);

        // Make Profile ImageView Rounded
        ImageView imageView = (ImageView)findViewById(R.id.lobbyImageViewProfile);
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar);
        roundDrawable.setCircular(true);
        imageView.setImageDrawable(roundDrawable);
    }

    public void toggleDrawer(View view) {
        if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

    private void addDrawerItems() {
        String[] osArray = { "Radius", "Start Time", "Lobby Host", "Game Option", "Another Game Option" };
        osArray[0] = String.format("Radius = %dm", game.getRadius());
        osArray[1] = String.format("Lobby Host = %d", game.getHostID());
        osArray[2] = String.format("Duration = %d", game.getDuration());
        osArray[4] = String.format("Longitude = %f", game.getXCenter());
        osArray[3] = String.format("Latitude = %f", game.getYCenter());
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    /** Automatically added methods for the MapView */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /** Set up the map once it is ready*/
    private void setUpMap(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.addCircle(new CircleOptions().center(circleLatLng).radius(game.getRadius()).strokeColor(Color.CYAN)); // Add game radius circle to map
        cameraZoomLevel = getZoomLevel((double) game.getRadius());
        // For zooming automatically to the location of the circle
        CameraPosition cameraPosition = new CameraPosition.Builder().target(circleLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Update the user's location within the map (not sure if this is needed)*/
    private void updateMap() {
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // Shouldn't happen
        } // TODO check if user's location on map is updated automatically
    }

    /** Center the map to the user's current location*/
    public void centerMapToMe(View view) {
        if (lastLatLng == null) return;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lastLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Center the map to the game's circle*/
    public void centerMapToCircle(View view) {
        if (circleLatLng == null) return;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(circleLatLng).zoom(cameraZoomLevel).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /** Calculate the GoogleMap zoom level based on the radius of the game circle */
    private float getZoomLevel(Double circleRadius) {
        float zoomLevel = 0;
        if (circleRadius != null){
            double scale = circleRadius / MAP_ZOOM_SCALE_FACTOR; // TODO Does this work on other devices and screens?
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    /** Setup a LocationListener whose methods will be called on Location updates*/
    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
            lastLocation = locFromGps;
            LobbyActivity.this.updateTxt();
            LobbyActivity.this.updateMap();
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
        }
    }

    /** Update the textView that displays the user's current lat/long (just for demo/testing*/
    private void updateTxt() {
        Double lat, lon;
        try {
            lat = lastLocation.getLatitude();
            lon = lastLocation.getLongitude();
            lastLatLng = new LatLng(lat, lon);
        } catch (NullPointerException e) {
            e.printStackTrace();
            txtLatLong.setText(R.string.location_null_error);
            return;
        }
        txtLatLong.setText(lastLatLng.toString());
    }

    public void gotoPlayerList(View view) {
        // Switch to the Player List activity
        Intent intent = new Intent(this, PlayersListActivity.class);
        startActivity(intent);
    }

    boolean doubleBackToExitPressedOnce = false;

    /** Code to control back button usage */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
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