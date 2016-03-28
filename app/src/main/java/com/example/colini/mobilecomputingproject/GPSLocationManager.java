package com.example.colini.mobilecomputingproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by JoeyFarrell on 2016-03-27.
 */
public class GPSLocationManager extends Service implements LocationListener {
    Context myContext;

    public GPSLocationManager(Context c){
        myContext = c;
        getLocation();
    }

    public void getLocation(){

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
