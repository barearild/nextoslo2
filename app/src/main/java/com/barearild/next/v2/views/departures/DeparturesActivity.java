package com.barearild.next.v2.views.departures;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.Requests;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.StopVisit.StopVisitsResult;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.details.DetailsActivity;
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
import static com.barearild.next.v2.StopVisitFilters.onlyFavorites;
import static com.barearild.next.v2.StopVisitFilters.orderByWalkingDistance;
import static com.barearild.next.v2.StopVisitFilters.orderedByFirstDeparture;
import static com.barearild.next.v2.StopVisitFilters.removeTransportTypes;
import static com.barearild.next.v2.StopVisitFilters.withoutFavourites;

public class DeparturesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SwipeRefreshLayout.OnRefreshListener,
        DeparturesAdapter.OnDepartureItemClickListener {

    public static final String STATE_LAST_RESULT = "lastResult";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private DeparturesSwipeRefreshLayout mSwipeView;
    private DeparturesRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutParams;
    private Long mLastUpdate = null;
    private FloatingActionButton fab;
    private StopVisitsResult mLastResult;
    private GetAllDeparturesTask mAllDeparturesTask;

    private boolean isShowingFilters = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isShowingFilters) {
                    float heightDp = getResources().getDisplayMetrics().heightPixels / 3;
                    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    lp.height = (int) heightDp;
                } else {
                    float heightDp = getResources().getDisplayMetrics().heightPixels / 2;
                    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    lp.height = (int) heightDp;
                    expand(view);
                }

                isShowingFilters = !isShowingFilters;

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSwipeView = (DeparturesSwipeRefreshLayout) findViewById(R.id.departure_list_swipe);
        mSwipeView.setOnRefreshListener(this);

        mLayoutParams = new LinearLayoutManager(this);
        mLayoutParams.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (DeparturesRecyclerView) findViewById(R.id.departure_list);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mSwipeView.setEnabled(verticalOffset == 0);
            }
        });
    }

    public static void expand(final View v) {
        v.measure(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? AppBarLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_LAST_RESULT, mLastResult);
        outState.putBoolean("isRefreshing", mAllDeparturesTask != null && mAllDeparturesTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastResult = savedInstanceState.getParcelable(STATE_LAST_RESULT);

        if (mLastResult != null) {
            mLastUpdate = mLastResult.getTimeOfSearch().getTime();
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult), getBaseContext(), this), true);
            mSwipeView.setRefreshing(false);
        }
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
        if (mAllDeparturesTask != null && mAllDeparturesTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAllDeparturesTask.cancel(true);
        }
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("nextnext", "onConnected");
        updateData(false);
    }

    private void updateData(boolean force) {
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
        if (force) {
            mLastUpdate = null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null || secondsSince(mLastLocation.getTime()) > 60) {
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


        if (mLastUpdate == null || secondsSince(mLastUpdate) > 30) {
            if (mAllDeparturesTask != null) {
                mAllDeparturesTask.cancel(true);
            }
            mAllDeparturesTask = new GetAllDeparturesTask();
            mAllDeparturesTask.execute(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("nextnext", "Connection failed");
    }

    @Override
    public void onRefresh() {
        updateData(true);
    }

    @Override
    public void onItemClick(StopVisitListItem stopVisitListItem) {
        Intent details = new Intent(this, DetailsActivity.class);
        details.putExtra("stopvisit", stopVisitListItem);
        startActivity(details);
    }

    private class GetAllDeparturesTask extends AsyncTask<Location, Void, List<Object>> {

        private static final String GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES = "http://api.ruter.no/ReisRest/Stop/GetClosestStopsAdvancedByCoordinates/?coordinates=(x=%d,y=%d)"
                + "&proposals=%d&walkingDistance=%d";

        private static final String PLACE_GET_CLOSEST_STOPS = "http://api.ruter.no/ReisRest/Place/GetClosestStops?coordinates=(x=%d,y=%d)"
                + "&proposals=%d&walkingDistance=%d";

        @Override
        protected List<Object> doInBackground(Location... params) {

            List<Stop> closestStopsToLocation = Requests.getClosestStopsToLocation(params[0], 15, 1400);

            mLastResult = new StopVisitsResult(new Date());

            final ExecutorService es = Executors.newCachedThreadPool();
            for (final Stop stop : closestStopsToLocation) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<StopVisit> departures;
                        departures = Requests.getAllDepartures(stop);
                        synchronized (mLastResult) {
                            mLastResult.addAll(departures);
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

            return convertToListData(mLastResult);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeView.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);

            Log.d("nextnext", "OnPostExecute " + result.toString());
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, getBaseContext(), DeparturesActivity.this), true);
            mSwipeView.setRefreshing(false);
        }
    }

    private static List<Object> convertToListData(StopVisitsResult result) {
        List<Object> data = new ArrayList<>();
        data.add(new Date());

        List<StopVisitListItem> favourites = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(onlyFavorites(removeTransportTypes(result)))));
        List<StopVisitListItem> others = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(withoutFavourites(removeTransportTypes(result)))));

        if (favourites.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_NO_FAVOURITES);
        } else {

        }

        data.add(NextOsloApp.DEPARTURES_HEADER_OTHERS);
        data.addAll(others);

        return data;
    }
}
