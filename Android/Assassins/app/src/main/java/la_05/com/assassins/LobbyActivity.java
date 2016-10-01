package la_05.com.assassins;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.security.Permission;

import static java.security.AccessController.getContext;

public class LobbyActivity extends AppCompatActivity {

    //private static LocationManager locationManager;
    //private static LocationListener locationListener;
    //private static boolean GPS_PERMISSION = false;
    TextView txtLatLong;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
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
            // GPS_PERMISSION = true;
            // If app has permission, setup Location service
            LocationListener locationListener = new MyLocationListener();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            updateTxt();
        }


        // Make Profile ImageView Rounded
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar);
        roundDrawable.setCircular(true);
        imageView.setImageDrawable(roundDrawable);
    }

    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
            lastLocation = locFromGps;
            LobbyActivity.this.updateTxt();
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

    private void updateTxt() {
        LatLng lastLatLng;
        Double lat, lon;
        try {
            lat = lastLocation.getLatitude();
            lon = lastLocation.getLongitude();
            lastLatLng= new LatLng(lat, lon);
        } catch (NullPointerException e) {
            e.printStackTrace();
            txtLatLong.setText(R.string.location_null_error);
            return;
        }
        txtLatLong.setText(lastLatLng.toString());
    }

    /*public LatLng getLocation() {
        if (!GPS_PERMISSION) return null;
        // Get the location manager
        //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(bestProvider);
        }
        catch (SecurityException e) {
            return null;
        }
        Double lat, lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
            return new LatLng(lat, lon);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void refreshGPS(View view) {
        LatLng ll = getLocation();
        if (ll != null)
            txtLatLong.setText(ll.toString());
    }*/

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