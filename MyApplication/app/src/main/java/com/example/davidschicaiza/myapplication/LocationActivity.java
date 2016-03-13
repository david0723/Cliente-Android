package com.example.davidschicaiza.myapplication;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by juansantiagoacev on 3/11/16.
 */
public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //ACÁ TE VOY A PONER LAS LIBRERÍAS QUE NECESITAS AGREGAR AL GRADLE PARA QUE FUNCIONE:

    //DE ESTA NO ESTOY SEGURO PERO CREO QUE ES NECESARIA: compile 'com.google.android.gms:play-services-maps:8.4.0'
    //compile 'com.google.android.gms:play-services-location:8.4.0'

    //ACÁ TE PONGO LOS PERMISOS PARA EL MANIFIESTO:

    //<uses-permission android:name="android.permission.INTERNET" />
    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 2301;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 2302;
    public static final String TAG = LocationActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) //Acá indicas si quieres location por GPS o Wifi-Red
                .setInterval(1000) //Este es el tiempo de intervalo máximo entre peticiones de location
                .setFastestInterval(16) //Este es el tiempo de intervalo mínimo entre peticiones de location (OPCIONAL)
                .setSmallestDisplacement(5); //Este indica si quieres recibir locations no por tiempo sino si avanza más de 5 metros en alguna dirección (OPCIONAL)
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Cuando apagas la pantalla apagas el Google Client que creaste
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            //ACÁ TE DESCONECTAS DE LOS UPDATES DE LOCATION (NO INTENTES DEJARLO PRENDIDO, NECESITAS MUCHOS CAMBIOS)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Cuando prendes la pantalla reconectas el Google Client
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LocationActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            //ACÁ TE CONECTAS DE LOS UPDATES DE LOCATION
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Cuando el task manager mata el app desconectas el Google Client
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            //ACÁ TE DESCONECTAS DE LOS UPDATES DE LOCATION (NO INTENTES DEJARLO PRENDIDO, NECESITAS MUCHOS CAMBIOS)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location & Activity services connected.");
        //Aqui vas a chequear que el usuario te haya dado permiso de usar el location (NECESARIO POR USAR API 23)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationActivity.MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        //Aqui vamos a pedir que el celular nos registre como clase que recibe los location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //Si necesitas saber en donde es la última ubicación que tiene Android registrada del celular:
        //Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //handleNewLocation(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Como no se conectó, le vas a pedir a Android que intente arreglar el problema
        // (¡¿Descargar un apk de camara del PlayStore es a veces una solución a no poder abrir un app para leer un código de barras?!)
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

    @Override
    public void onLocationChanged(Location location) {
        //LISTO, ACA TE VAN A LLEGAR LOS LOCATION CHANGES DEL CELULAR
        // DEBES DEJAR ESTE MÉTODO LIBRE POR BEST PRACTICES PORQUE VA A SER LLAMADO MUCHO Y NO SE PUEDE QUEDAR PEGADO
        // CALCULANDO ALGO
        handleNewLocation(location);
    }

    public void handleNewLocation(Location location) {
        //EN ESTE SI TE PUEDES ¿DIVERTIR? CON LA LOCATION
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        //EL OBJETO LATLNG ES MUCHO MENOS PESADO QUE LOCATION PUES LOCATION TIENE VELOCIDAD, ALTITUD, LAT, LONG, TIMEZONE, ETC.
        //LATLNG SOLO TIENE LATITUD Y LONGITUD Y ES DE GOOGLE.
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
    }
}