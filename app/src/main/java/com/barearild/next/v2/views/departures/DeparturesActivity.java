package com.barearild.next.v2.views.departures;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.StopVisitFilters;
import com.barearild.next.v2.delete.StopVisitListItem;
import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.reisrest.requests.Requests;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.StopVisit.StopVisitsResult;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.reisrest.place.StopFilter;
import com.barearild.next.v2.search.SearchSuggestion;
import com.barearild.next.v2.search.SearchSuggestionProvider;
import com.barearild.next.v2.search.SearchSuggestionsAdapter;
import com.barearild.next.v2.tasks.GetAllDeparturesNearLocationTask;
import com.barearild.next.v2.views.NextOsloStore;
import com.barearild.next.v2.views.departures.items.DepartureViewItem;
import com.barearild.next.v2.views.departures.items.SpaceItem;
import com.barearild.next.v2.views.details.DetailsActivity;
import com.barearild.next.v2.views.stop.StopActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import v2.next.barearild.com.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.barearild.next.v2.NextOsloApp.LOG_TAG;
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
        DeparturesAdapter.OnDepartureItemClickListener, SearchSuggestionsAdapter.OnSuggestionClickListener, NextOsloStore.StateListener {

    public static final String STATE_LAST_RESULT = "lastResult";

    private static final int MODE_STOP_VISITS = 0;
    private static final int MODE_SUGGESTIONS = 1;
    private static final int MODE_LINE_SEARCH = 2;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    public DeparturesSwipeRefreshLayout mSwipeView;
    private DeparturesRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutParams;
    private Long mLastUpdate = null;
    private FloatingActionButton fab;
    private StopVisitsResult mLastNearbyResults;
    private GetAllDeparturesNearLocationTask mAllDeparturesTask;
    private FavouritesService mFavouriteService;

    private boolean mIsShowingFilters = false;

    private NextOsloApp mApplication;
    private SearchView searchView;

    private int mode = MODE_STOP_VISITS;
    private String mLastQuery;
    private SearchManager mSearchManager;

    private final NextOsloStore mStore = new NextOsloStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);

        mApplication = (NextOsloApp) getApplication();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d(NextOsloApp.LOG_TAG, "Search performed " + intent.getStringExtra(SearchManager.QUERY));
            new SearchLineTask().execute(intent.getStringExtra(SearchManager.QUERY));
        }

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> showFilters());

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

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> mSwipeView.setEnabled(verticalOffset == 0));

        mFavouriteService = new FavouritesService(getApplicationContext());
        mStore.addListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        mLastQuery = intent.getStringExtra(SearchManager.QUERY);
        Log.d(LOG_TAG, "Intent action " + action);
        search(action, mLastQuery);
    }

    private void search(String action, String query) {
        if(query.equalsIgnoreCase("alpha beta omega proxy")) {
            Toast.makeText(this, "Hei IDA!!! :)", Toast.LENGTH_LONG).show();
        }

        if (NextOsloApp.SEARCH_LINE.equals(action)) {
            new SearchLineTask().execute(query);
            mode = MODE_LINE_SEARCH;
        } else if (NextOsloApp.SEARCH_ADDRESS.equals(action)) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + query + "&dirflg=r"));
            startActivity(mapIntent);
        } else if (Intent.ACTION_SEARCH.equals(action)) {
            mode = MODE_SUGGESTIONS;
            new SearchSuggestionsTask().execute(query);
        }
    }

    private void showPullToUpdateInfo() {
        if (mApplication.getPrefs().getBoolean(NextOsloApp.SHOW_PULL_DOWN_INFO, true)) {
            Snackbar
                    .make(findViewById(R.id.coordinatorLayout), getString(R.string.info_pull_down_to_update), Snackbar.LENGTH_LONG)
                    .setAction(R.string.info_do_not_show_again, v -> mApplication.getPrefs().edit().putBoolean(NextOsloApp.SHOW_PULL_DOWN_INFO, false).apply())
                    .show();
        }
    }

    private void showFilters() {
        mIsShowingFilters = !mIsShowingFilters;

        List<Object> data = new ArrayList<>();

        if (mLastNearbyResults != null) {
            data.addAll(convertToListData(mLastNearbyResults, mIsShowingFilters));
        } else {
            data.add(new FilterView.FilterType());
        }

        mRecyclerView.swapAdapter(new DeparturesAdapter(data, this, DeparturesActivity.this), false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_LAST_RESULT, mLastNearbyResults);
        outState.putString("searchQuery", searchView != null ? searchView.getQuery().toString() : null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastNearbyResults = savedInstanceState.getParcelable(STATE_LAST_RESULT);

        if (mLastNearbyResults != null) {
            mLastUpdate = mLastNearbyResults.getTimeOfSearch().getTime();
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastNearbyResults, mIsShowingFilters), this, this), true);
            mSwipeView.setRefreshing(false);
        }

        String query = savedInstanceState.getString("searchQuery");
        if (searchView != null && !searchView.isIconified() && query != null && !query.isEmpty()) {
            searchView.setQuery(query, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_departures, menu);

        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                searchView.setQuery(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)), false);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                searchView.setQuery(cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)), false);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                searchView.setQuery(query, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query == null || query.isEmpty()) {
                    filterResult(null);
                    return false;
                }
                search(Intent.ACTION_SEARCH, query);
//                filterResult(query);
                return false;
            }
        });

        return true;
    }

    private void filterResult(String query) {

        if (query == null) {
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastNearbyResults, mIsShowingFilters), this, this), false);
        } else {
            StopVisitsResult filtered = StopVisitFilters.filterLineRef(query, mLastNearbyResults);
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(filtered, mIsShowingFilters), this, this), false);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            showPullToUpdateInfo();
            updateData(true);
            return true;
        } else if (id == R.id.action_search) {

        } else if (id == R.id.action_license) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.cc_icon)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        switch (mode) {
            case MODE_STOP_VISITS:
                if (mLastNearbyResults != null && !mLastNearbyResults.stopVisits.isEmpty()) {
                    mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastNearbyResults, mIsShowingFilters), this, DeparturesActivity.this), false);
                }
                break;
            case MODE_SUGGESTIONS:
                search(Intent.ACTION_SEARCH, searchView.getQuery().toString());
                break;
            case MODE_LINE_SEARCH:
                search(NextOsloApp.SEARCH_LINE, searchView.getQuery().toString());
                break;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case NextOsloApp.REQUEST_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateData(false);
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.request_location_info)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton(R.string.set_permission, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestLocationPermission();
                                    dialogInterface.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
    }

    private void updateData(boolean force) {
        mode = MODE_STOP_VISITS;
        if (!haveAccessTo(ACCESS_FINE_LOCATION)) {
            requestLocationPermission();
            return;
        }

        if (force) {
            mLastUpdate = null;
            mLastLocation = null;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mLastLocation == null || secondsSince(mLastLocation.getTime()) > 60) {
            mLastLocation = new Location("GPS");
            mLastLocation.setLongitude(10.750337d);
            mLastLocation.setLatitude(59.927333d);
            onLocationChanged(mLastLocation);

//            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//            mLocationRequest.setNumUpdates(1);
//
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            onLocationChanged(mLastLocation);
        }
    }

    private boolean haveAccessTo(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION},
                NextOsloApp.REQUEST_PERMISSION_LOCATION);
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
            mAllDeparturesTask = new GetAllDeparturesNearLocationTask(this);
            mAllDeparturesTask.execute(location);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Connection failed");
        new AlertDialog.Builder(this).setMessage(R.string.error_connection_failed)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGoogleApiClient.reconnect();
                    }
                }).create()
                .show();
    }

    @Override
    public void onRefresh() {
        if (searchView.isIconified()) {
            updateData(true);
        } else {
            switch (mode) {
                case MODE_LINE_SEARCH:
                    search(NextOsloApp.SEARCH_LINE, mLastQuery);
                    break;
                case MODE_SUGGESTIONS:
                    search(Intent.ACTION_SEARCH, searchView.getQuery().toString());
                    break;


            }
        }
    }

    @Override
    public void onItemClick(Object item) {
        if (item instanceof StopVisitListItem) {
            Intent details = new Intent(this, DetailsActivity.class);
            details.putExtra(StopVisitListItem.class.getSimpleName(), (StopVisitListItem) item);
            startActivity(details);
        } else if (item instanceof Stop) {
            Intent stop = new Intent(this, StopActivity.class);
            stop.putExtra(Stop.class.getSimpleName(), (Stop) item);
            startActivity(stop);
        } else if (item instanceof SearchSuggestion) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + ((SearchSuggestion) item).query + "&dirflg=r"));
            startActivity(mapIntent);
        } else if(item instanceof DepartureViewItem) {
            Intent details = new Intent(this, DetailsActivity.class);
            details.putExtra(DepartureViewItem.class.getSimpleName(), (DepartureViewItem) item);
            startActivity(details);
        }
        else if (item instanceof Line) {
            new SearchClosestStopForLineTask().execute((Line) item);
        }
    }

    @Override
    public void onFilterUpdate(Transporttype transporttype, boolean isChecked) {
        NextOsloApp.SHOW_TRANSPORT_TYPE.put(transporttype, isChecked);
        getSharedPreferences(NextOsloApp.USER_PREFERENCES, MODE_PRIVATE).edit().putBoolean(transporttype.name(), isChecked).apply();

        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastNearbyResults, mIsShowingFilters), this, this), false);
    }

    @Override
    public void addToFavourite(StopVisitListItem item) {
        mFavouriteService.addFavourite(item);
        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastNearbyResults, mIsShowingFilters), this, this), false);
    }

    @Override
    public void removeFromFavourite(StopVisitListItem item) {
        mFavouriteService.removeFavourite(item);
        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastNearbyResults, mIsShowingFilters), this, this), false);
    }

    @Override
    public void showInMap(StopVisitListItem item) {

    }

    @Override
    public void suggestionClicked(SearchSuggestion searchSuggestion) {
        Log.d(LOG_TAG, "Search for " + searchSuggestion);

        if (searchSuggestion.type == SearchSuggestion.LINE_SUGGESTION) {
            search(searchSuggestion.intent, searchSuggestion.query);
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.setQuery("", false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onStateChanged() {
        mRecyclerView.swapAdapter(new DeparturesAdapter(mStore, DeparturesActivity.this, DeparturesActivity.this), false);
    }

    public void onGetAllDeparturesNearLocationPreExecute() {
        mSwipeView.setRefreshing(true);
        mStore.setDepartures(Collections.emptyList());
    }

    public void onGetAllDeparturesNearLocationResult(List<StopVisit> departures) {
        mode = MODE_STOP_VISITS;
        mLastUpdate = System.currentTimeMillis();
        mStore.setDepartures(departures);
        mSwipeView.setRefreshing(false);
    }

    private class SearchClosestStopForLineTask extends AsyncTask<Line, Void, List<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeView.setRefreshing(true);
        }

        @Override
        protected List<Object> doInBackground(Line... lines) {
            Line line = lines[0];
            Stop closestStop = StopFilter.getClosestStop(Requests.getAllStopsForLine(String.valueOf(line.getID()), mLastLocation));

            StopVisitsResult result = new StopVisitsResult(new Date());

            result.stopVisits.addAll(Requests.getAllDepartures(closestStop, line.getName()));
            return convertToListData(result, mIsShowingFilters);
        }

        @Override
        protected void onCancelled(List<Object> objects) {
            super.onCancelled(objects);
            mSwipeView.setRefreshing(false);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, getBaseContext(), DeparturesActivity.this), false);
            mSwipeView.setRefreshing(false);
        }
    }

    private class SearchSuggestionsTask extends AsyncTask<String, Void, List<Object>> {

        private final StopVisitsResult result;

        public SearchSuggestionsTask() {
            result = new StopVisitsResult(new Date());
        }

        @Override
        protected List<Object> doInBackground(String... queries) {
            final String query = queries[0];

            ExecutorService es = Executors.newFixedThreadPool(4);

            es.execute(() -> searchLinesNearby(query));
            es.execute(() -> searchLines(query));
            es.execute(() -> searchStops(query));
            es.execute(() -> searchSuggestions(query));

            es.shutdown();
            try {
                es.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return convertToListData(result, mIsShowingFilters);
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(result, mIsShowingFilters), DeparturesActivity.this, DeparturesActivity.this), false);

        }

        @Override
        protected void onPostExecute(List<Object> objects) {
            super.onPostExecute(objects);
            mode = MODE_SUGGESTIONS;
            mRecyclerView.swapAdapter(new DeparturesAdapter(objects, DeparturesActivity.this, DeparturesActivity.this), false);
            mSwipeView.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeView.setRefreshing(true);
        }

        private void searchStops(String query) {
            int numberOfStops = 0;
            for (Stop stop : NextOsloApp.ALL_STOPS) {
                if (stop.getName().toLowerCase().startsWith(query.toLowerCase())) {
                    result.stops.add(stop);
                    if (++numberOfStops > 8) {
                        break;
                    }
                }
            }
            if (numberOfStops < 8) {
                for (Stop stop : NextOsloApp.ALL_STOPS) {
                    if (!result.stops.contains(stop) && stop.getName().toLowerCase().contains(query.toLowerCase())) {
                        result.stops.add(stop);
                        if (++numberOfStops > 8) {
                            break;
                        }
                    }
                }
            }

            publishProgress();
        }

        private void searchSuggestions(String query) {
            Log.d(LOG_TAG, "Getting line search suggestions");
            Cursor suggestions = new SearchSuggestionProvider().getSuggestions(query);

            List<Object> searchSuggestions = new ArrayList<>();

            while ((suggestions.moveToNext())) {
                long id = suggestions.getLong(suggestions.getColumnIndex("_id"));
                int iconRes = suggestions.getInt(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1));
                String text = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                String text2 = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));
                String suggestionQuery = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY));
                String intent = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_ACTION));

                result.suggestions.add(new SearchSuggestion(id, iconRes, text, text2, suggestionQuery, intent));

            }
            suggestions.close();
            Log.d(NextOsloApp.LOG_TAG, "Suggestions: " + searchSuggestions.toString());
            publishProgress();
        }

        private void searchLinesNearby(final String query) {
            if (mLastNearbyResults != null && !mLastNearbyResults.stopVisits.isEmpty()) {
                for (StopVisit stopvisit : mLastNearbyResults.stopVisits) {
                    if (stopvisit.getLineRef().equals(query)) {
                        result.linesNearby.add(stopvisit);
                    }
                }
            }

            publishProgress();
        }

        private void searchLines(final String query) {
            result.lines.addAll(Requests.getLinesSuggestion(query));


            publishProgress();
        }
    }

    private class SearchLineTask extends AsyncTask<String, Void, List<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeView.setRefreshing(true);
        }

        @Override
        protected List<Object> doInBackground(String... strings) {
            final String query = strings[0];
            final StopVisitsResult result = new StopVisitsResult(new Date());

            Log.d(LOG_TAG, "SearchLineTask: " + strings[0]);


            final ExecutorService es = Executors.newCachedThreadPool();
            final Stop closestStop = StopFilter.getClosestStop(Requests.getAllStopsForLine(query, mLastLocation));
//            for (final Stop stop : allStopsForLine) {
            es.execute(() -> {
                List<StopVisit> allDeparturesForStop = Requests.getAllDepartures(closestStop, query);
                synchronized (result) {
                    result.stopVisits.addAll(allDeparturesForStop);

                }
            });
//            }
            es.shutdown();
            try {
                es.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e("GetDepartures", e.getMessage(), e);
            }
            Log.d(LOG_TAG, "result2 " + result.toString());
            return convertToListData(result, mIsShowingFilters);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);
            mode = MODE_LINE_SEARCH;
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, DeparturesActivity.this, DeparturesActivity.this), false);
            mSwipeView.setRefreshing(false);
        }
    }

    private static List<Object> convertToListData(StopVisitsResult result, boolean showFilters) {
        List<Object> data = new ArrayList<>();

        if (showFilters) {
            data.add(new FilterView.FilterType());
        }

        if (result == null) {
            return data;
        }

        if (result.getTimeOfSearch() != null) {
            data.add(result.getTimeOfSearch());
        }

        if (!result.linesNearby.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_LINES_NEARBY);
            data.addAll(convertToListItems(result.linesNearby));
        }

        if (!result.lines.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_LINES);
            data.addAll(result.lines);
        }

        if (!result.stops.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_STOPS);
            data.addAll(result.stops);
        }

        if (!result.suggestions.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_ADDRESSES);
            data.addAll(result.suggestions);
        }

        if (!result.stopVisits.isEmpty()) {
            List<StopVisitListItem> favourites = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(onlyFavorites(removeTransportTypes(result.stopVisits)))));
            List<StopVisitListItem> others = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(withoutFavourites(removeTransportTypes(result.stopVisits)))));

            List<StopVisitListItem> allStopVisitList = convertToListItems(result.stopVisits);
            for (StopVisitListItem favourite : favourites) {
                StopVisitFilters.getOtherStopsForStopVisitListItem(favourite, allStopVisitList);
            }
            for (StopVisitListItem other : others) {
                StopVisitFilters.getOtherStopsForStopVisitListItem(other, allStopVisitList);
            }

            if (result.stopVisits.isEmpty()) {
                data.add(NextOsloApp.DEPARTURES_HEADER_EMPTY);
            }

            if (favourites.isEmpty()) {
                data.add(NextOsloApp.DEPARTURES_HEADER_NO_FAVOURITES);
            } else {
                data.add(NextOsloApp.DEPARTURES_HEADER_FAVOURITES);
                data.addAll(favourites);
                data.add(new SpaceItem());
            }

            data.add(NextOsloApp.DEPARTURES_HEADER_OTHERS);
            data.addAll(others);

        }


        return data;
    }
}
