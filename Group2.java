package com.group2.homework2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.group2.homework2.databinding.ActivityMainBinding;

public class Group2 extends AppCompatActivity {
    // Global location service manager
    private LocationManager mgr ;
    // Text field for showing speed
    private TextView speedView;
    private TextView unitView;
    // Button and Dialog for help
    private Button helpbutton;
    private Button switchMiles;
    private Button switchKilos;
    private Button plusfont;
    private Button minusfont;
    private AlertDialog helpDialog;
    private static float MPHspeed;
    private static float KPHspeed;
    private static float speedMS;
    private static float fontsize=20;
    // Android provide speed in m/s. 1m/s = 2.234 mph
    private static final float MPS_TO_MPH = 2.23694f;
    private static final float MPS_TO_KPH = 3.6f;

    // A location listener, when user's GPS location changed,
    // it will update the speed on the screen automatically.
    private LocationListener locationListener = new LocationListener() {
        @SuppressLint({"SetTextI18n", "SuspiciousIndentation"})
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(@NonNull Location location) {
            speedMS=location.getSpeed();
            MPHspeed=speedMS*MPS_TO_MPH;
            KPHspeed=speedMS*MPS_TO_KPH;
            if(unitView.getText().toString().equals("kph")){
                speedView.setText(KPHspeed+"");
                speedChangeTextColor(KPHspeed);
            }
            if(unitView.getText().toString().equals("mph")){
                speedView.setText(MPHspeed+"");
                speedChangeTextColor(MPHspeed);
            }
        }
    };

    private void speedChangeTextColor(float speed){
            if (speed == 0.0) {
                speedView.setTextColor(0xff888888); //grey
            }
            if (speed <= 35.0 && speed > 0.0) {
                speedView.setTextColor(0xff0000ff); //blue
            }
            if(speed>30.0&&speed<=65.0){
                speedView.setTextColor(0xff00ff00); //green
            }
            if(speed>=65.0&&speed<80.0){
                speedView.setTextColor(0xffffff00); //yellow
            }
            if(speed>=80.0){
                speedView.setTextColor(0xffff0000);//red
                ObjectAnimator animator=ObjectAnimator.ofInt(speedView,"textColor", Color.RED);
                animator.setDuration(500);
                animator.setEvaluator(new ArgbEvaluator());
                animator.setRepeatCount(Animation.INFINITE);
                animator.start();
            }

    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        setContentView(R.layout.activity_main);
        ActivityMainBinding binder = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Show help information on the page.
        speedView = binder.textView;
        helpbutton = binder.helpButton;
        switchMiles =binder.SwitchToMiles;
        switchKilos =binder.SwitchToKiloMeters;
        unitView=binder.UnitView;
        plusfont=binder.PlusFont;
        minusfont=binder.MinusFont;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Walk or running with your phone, the application will show your speed based on GPS.")
                .setTitle("Help").setNeutralButton("I know", null);

        helpDialog = builder.create();



        switchMiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedView.setText(speedMS*MPS_TO_MPH+"");
                unitView.setText("mph");
            }
        }) ;

        switchKilos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedView.setText(speedMS*MPS_TO_KPH+"");
                unitView.setText("kph");
            }
        }) ;

        plusfont.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                ++fontsize;
            speedView.setTextSize(fontsize);
            unitView.setTextSize(fontsize);

            }

        });

        minusfont.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                --fontsize;
                speedView.setTextSize(fontsize);
                unitView.setTextSize(fontsize);

            }

        });

        helpbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    helpDialog.show();
                }
            });
        // Ask user for location permission.
        // Unlike Apple and Google, we value your privacy :)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 114514);

        }
        // If we get location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the location for the first time/
            Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                speedView.setText(location.getSpeed()  * MPS_TO_MPH+ " mph");

            } else {
                speedView.setText("0.0");
            }
        }


        // Register location monitoring
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0.1f, locationListener);


    }
}