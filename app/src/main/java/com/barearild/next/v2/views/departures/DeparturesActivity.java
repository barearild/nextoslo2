package com.barearild.next.v2.views.departures;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.barearild.next.v2.reisrest.Requests;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.place.Stop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import v2.next.barearild.com.R;

import static com.barearild.next.v2.StopVisitFilters.convertToListItems;
import static com.barearild.next.v2.StopVisitFilters.orderByWalkingDistance;
import static com.barearild.next.v2.StopVisitFilters.orderedByFirstDeparture;

public class DeparturesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutParams;
    private Long mLastUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mLayoutParams = new LinearLayoutManager(this);
        mLayoutParams.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.departure_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutParams);
        mRecyclerView.setAdapter(new DeparturesAdapter(new ArrayList<>(), getBaseContext()));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_departures, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("nextnext", "onConnected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation == null || secondsSince(mLastLocation.getTime()) > 60) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(1000);
            mLocationRequest.setNumUpdates(1);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            onLocationChanged(mLastLocation);
        }
    }

    private long secondsSince(long timestamp) {
        return (System.currentTimeMillis() - timestamp) / 1000;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("nextnext", "Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        String tidsstempel = DateFormat.getDateTimeInstance().format(new Date(location.getTime()));
        Log.d("nextnext", String.format("Location changed [%.3f,%.3f] accuracy=%.3f, time=%s", location.getLatitude(), location.getLongitude(), location.getAccuracy(), tidsstempel));


        if(mLastUpdate == null || secondsSince(mLastUpdate) > 30) {
            GetAllDeparturesTask allDeparturesTask = new GetAllDeparturesTask();
            allDeparturesTask.execute(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("nextnext", "Connection failed");
    }

    private class GetAllDeparturesTask extends AsyncTask<Location, Void, List<Object>> {

        private static final String GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES = "http://api.ruter.no/ReisRest/Stop/GetClosestStopsAdvancedByCoordinates/?coordinates=(x=%d,y=%d)"
                + "&proposals=%d&walkingDistance=%d";

        private static final String PLACE_GET_CLOSEST_STOPS = "http://api.ruter.no/ReisRest/Place/GetClosestStops?coordinates=(x=%d,y=%d)"
                + "&proposals=%d&walkingDistance=%d";

        @Override
        protected List<Object> doInBackground(Location... params) {

            List<Stop> closestStopsToLocation = Requests.getClosestStopsToLocation(params[0], 15, 1400);

            final List<StopVisit> allDepartures = new ArrayList<>();

            final ExecutorService es = Executors.newCachedThreadPool();
            for (final Stop stop : closestStopsToLocation) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                       List<StopVisit> departures;
                        departures = Requests.getAllDepartures(stop);
                        synchronized (allDepartures) {
                            allDepartures.addAll(departures);
                        }
                    }

                });
            }
            es.shutdown();
            try {
                es.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e("GetDepartures", e.getMessage(), e);
            }


            List<Object> data = new ArrayList<>();
            data.add(new Date());

            data.addAll(orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(allDepartures))));

            return data;
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);

            Log.d("nextnext", "OnPostExecute " + result.toString());
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, getBaseContext()), true);
        }
    }
}