package com.example.davidschicaiza.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import android.os.SystemClock;

public class TCPActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 2301;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 2302;
    public static final String TAG = LocationActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static double altitude;
    public static double speed;
    public static double latitude;
    public static double longitude;

    private static boolean send;
    private static int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Interfaz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Localizacion
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000);

        //TCP
        send = true;
        id = 0;
        new Thread(new Runnable() {
            public void run() {

                Log.i("TCP button", "New TCP client");
                try {
                    TCPClient tcp = new TCPClient();
                    while(send){
                        tcp.send(id + "," + latitude + "," + longitude + "," + altitude + "," + speed);
                        id++;
                        SystemClock.sleep(1000);
                    }
                    tcp.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void stop(View view){
        send = false;
        id = 0;
        Log.i("TCP Stop button", "Button clicked");
        Intent intent = new Intent(this, act.class);
        startActivity(intent);
    }

    //TODO Ver si hay que hacer algo con el thread aqui
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //TODO Ver si hay que hacer algo con el thread aqui
    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LocationActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    //TODO Ver si hay que hacer algo con el thread aqui
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //TODO Ver si hay que hacer algo con el thread aqui
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location & Activity services connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        handleNewLocation(lastLocation);
    }

    //TODO Ver si hay que hacer algo con el thread aqui
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    //TODO Ver si hay que hacer algo con el thread aqui
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    //Listo
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    //Listo
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        speed = location.getSpeed();

    }
}
