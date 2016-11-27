package la_05.com.assassins;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity{

    private ImageView image;
    private float degree = 0f;
    private SensorManager mSensorManager;
    TextView tvheading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    protected void onResume(){

    }

    protected void onPause(){

    }

    public void onSensorChanged(SensorEvent e){

    }

    public void onAccuracyChanged(Sensor s, int accuracy){

    }

    public void assassinate(View view){
        // TODO
    }



    boolean doubleBackToExitPressedOnce = false;

    /** Code to control back button usage */
   /* @Override
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
    }*/
}
