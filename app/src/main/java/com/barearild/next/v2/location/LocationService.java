package com.barearild.next.v2.location;

import android.app.Activity;

import com.google.android.gms.location.LocationRequest;

public class LocationService {

    private OnLocationServiceCallback callback;

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private Activity activity;

    public LocationService(Activity activity, OnLocationServiceCallback callback) {
        this.activity = activity;
        this.callback = callback;

        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        mLocationRequest.setNumUpdates(LocationUtils.NUMBER_OF_UPDATES);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
    }

    public void getLocation() {
//        Location lastLocation = mLocationClient.getLastLocation();

        //for debugging
//        lastLocation = new Location("GPS");
//        lastLocation.setLongitude(10.752245);
//        lastLocation.setLatitude(59.913869);
//        lastLocation.setTime(System.currentTimeMillis());

//        if (lastLocation == null || secondsSince(lastLocation.getTime()) > 60) {
//            callback.updatingLocation();
//            mLocationClient.requestLocationUpdates(mLocationRequest, callback);
//        } else {
//            callback.onLocationChanged(lastLocation);
//        }
    }

    private long secondsSince(long timestamp) {
        return (System.currentTimeMillis() - timestamp) / 1000;
    }

//    public boolean isConnected() {
//        return mLocationClient.isConnected();
//    }

//    public void connect() {
//        mLocationClient.connect();
//    }
}
