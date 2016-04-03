package com.example.colini.mobilecomputingproject;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import se.walkercrou.places.GooglePlaces;

/**
 * Created by JoeyFarrell on 2016-03-27.
 */
public class GPSLocationManager extends Service implements LocationListener {
    Context myContext;

    boolean gpsEnabled;
    boolean networkEnabled;
    boolean canGetLocation;

    LocationManager locationManager;
    Location myLocation;
    double lat;
    double lon;

    GooglePlaces client;
    public GPSLocationManager(Context c){
        myContext = c;
        client = new GooglePlaces("AIzaSyCSUGPn5OAK26WX5x9IbnnNoajQL2tn44w");
        getLocation();
    }

    public void getLocation(){
        try{
            locationManager = (LocationManager) myContext.getSystemService(LOCATION_SERVICE); //Android Location manager

            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check to see if GPS is Enabled
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //check to see if Network is Enabled
            Criteria criteria = new Criteria(); //to set Location Accuracy
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            if(!gpsEnabled && !networkEnabled){
                //can't get GPS Location, we should handle this.
            }
            else{
                canGetLocation = true;

                if(networkEnabled){
                    //Get Local via Network
                    int locationCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                    if(locationCheck != PackageManager.PERMISSION_GRANTED){
                        //no location permission, need to ask user for this!



                    }
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.myLooper());

                }

            }

        }
        catch(Exception e){

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
