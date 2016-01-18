package com.barearild.next.v2.views.departures;

import android.Manifest;
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
import android.view.View;
import android.view.WindowManager;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.StopVisitFilters;
import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.reisrest.Requests;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.StopVisit.StopVisitsResult;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.reisrest.place.StopFilter;
import com.barearild.next.v2.search.SearchSuggestion;
import com.barearild.next.v2.search.SearchSuggestionProvider;
import com.barearild.next.v2.search.SearchSuggestionsAdapter;
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

import static com.barearild.next.v2.NextOsloApp.LOG_TAG;
import static com.barearild.next.v2.StopVisitFilters.convertToListItems;
import static com.barearild.next.v2.StopVisitFilters.convertToListItemsByStop;
import static com.barearild.next.v2.StopVisitFilters.onlyFavorites;
import static com.barearild.next.v2.StopVisitFilters.orderByWalkingDistance;
import static com.barearild.next.v2.StopVisitFilters.orderedByFirstDeparture;
import static com.barearild.next.v2.StopVisitFilters.removeTransportTypes;
import static com.barearild.next.v2.StopVisitFilters.withoutFavourites;

public class DeparturesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SwipeRefreshLayout.OnRefreshListener,
        DeparturesAdapter.OnDepartureItemClickListener, SearchSuggestionsAdapter.OnSuggestionClickListener {

    public static final String STATE_LAST_RESULT = "lastResult";

    private static final int MODE_STOP_VISITS = 0;
    private static final int MODE_SUGGESTIONS = 1;
    private static final int MODE_LINE_SEARCH = 2;


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
    private FavouritesService mFavouriteService;

    private boolean mIsShowingFilters = false;

    private NextOsloApp mApplication;
    private SearchView searchView;

    private int mode = MODE_STOP_VISITS;
    private String mLastQuery;
    private SearchManager mSearchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilters();
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

        mFavouriteService = new FavouritesService(getApplicationContext());
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
                    .setAction(R.string.info_do_not_show_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mApplication.getPrefs().edit().putBoolean(NextOsloApp.SHOW_PULL_DOWN_INFO, false).apply();
                        }
                    })
                    .show();
        }
    }

    private void showFilters() {
        mIsShowingFilters = !mIsShowingFilters;

        List<Object> data = new ArrayList<>();

        if (mLastResult != null) {
            data.addAll(convertToListData(mLastResult, mIsShowingFilters));
        } else {
            data.add(new FilterView.FilterType());
        }

        mRecyclerView.swapAdapter(new DeparturesAdapter(data, getBaseContext(), DeparturesActivity.this), false);
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
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, mIsShowingFilters), getBaseContext(), this), true);
            mSwipeView.setRefreshing(false);
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
                filterResult(query);
                return false;
            }
        });

        return true;
    }

    private void filterResult(String query) {

        if (query == null) {
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, mIsShowingFilters), this, this), false);
        } else {
            StopVisitsResult filtered = StopVisitFilters.filterLineRef(query, mLastResult);
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

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        switch (mode) {
            case MODE_STOP_VISITS:
                if (mLastResult != null && !mLastResult.isEmpty()) {
                    mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, mIsShowingFilters), getBaseContext(), DeparturesActivity.this), false);
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

    private void updateData(boolean force) {
        mode = MODE_STOP_VISITS;
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
            mLastLocation = null;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mLastLocation == null || secondsSince(mLastLocation.getTime()) > 60) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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

//        switch (mode) {
//            case MODE_STOP_VISITS:
//                updateData(true);
//                break;
//            case MODE_SUGGESTIONS:
//                search(Intent.ACTION_SEARCH, mLastQuery);
//                break;
//            case MODE_LINE_SEARCH:
//                search(NextOsloApp.SEARCH_LINE, mLastQuery);
//                break;
//        }
    }

    @Override
    public void onItemClick(StopVisitListItem stopVisitListItem) {
        Intent details = new Intent(this, DetailsActivity.class);
        details.putExtra(StopVisitListItem.class.getSimpleName(), stopVisitListItem);
        startActivity(details);
    }

    @Override
    public void onFilterUpdate(Transporttype transporttype, boolean isChecked) {
        NextOsloApp.SHOW_TRANSPORT_TYPE.put(transporttype, isChecked);
        getSharedPreferences(NextOsloApp.USER_PREFERENCES, MODE_PRIVATE).edit().putBoolean(transporttype.name(), isChecked).apply();

        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, mIsShowingFilters), this, this), false);
    }

    @Override
    public void addToFavourite(StopVisitListItem item) {
        mFavouriteService.addFavourite(item);
        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, mIsShowingFilters), this, this), false);
    }

    @Override
    public void removeFromFavourite(StopVisitListItem item) {
        mFavouriteService.removeFavourite(item);
        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, mIsShowingFilters), this, this), false);
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

    private class SearchSuggestionsTask extends AsyncTask<String, Void, List<Object>> {

        @Override
        protected List<Object> doInBackground(String... queries) {
            String query = queries[0];
            StopVisitsResult result = new StopVisitsResult(new Date());

            List<Line> linesSuggestion = Requests.getLinesSuggestion(query);

            if (linesSuggestion.size() == 1) {
                Log.d(LOG_TAG, "Getting line suggestions for 1 line");
                final Stop closestStop = StopFilter.getClosestStop(Requests.getAllStopsForLine(query, mLastLocation));
                List<StopVisit> allDepartures = Requests.getAllDepartures(closestStop, String.valueOf(linesSuggestion.get(0).getID()));
                result.addAll(allDepartures);
            } else {
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

                    searchSuggestions.add(new SearchSuggestion(id, iconRes, text, text2, suggestionQuery, intent));

                }
                suggestions.close();
                Log.d(NextOsloApp.LOG_TAG, "Suggestions: " + searchSuggestions.toString());

                return searchSuggestions;
            }

            return convertToListData(result, mIsShowingFilters);
        }

        @Override
        protected void onPostExecute(List<Object> objects) {
            super.onPostExecute(objects);
            mode = MODE_SUGGESTIONS;
            mRecyclerView.swapAdapter(new SearchSuggestionsAdapter(objects, getBaseContext(), DeparturesActivity.this), false);
            mSwipeView.setRefreshing(false);
        }
    }

    private static List<Object> convertToListData(List<SearchSuggestion> searchSuggestions) {
        return null;
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

//            if (mLastResult != null && !mLastResult.isEmpty()) {
//                for (StopVisit stopvisit : mLastResult) {
//                    if (stopvisit.getMonitoredVehicleJourney().getLineRef().equals(query)) {
//                        result.add(stopvisit);
//                    }
//                }
//
//                if (!result.isEmpty()) {
//                    return convertToListData(result, mIsShowingFilters);
//                }
//            }

            final ExecutorService es = Executors.newCachedThreadPool();
            final Stop closestStop = StopFilter.getClosestStop(Requests.getAllStopsForLine(query, mLastLocation));
//            for (final Stop stop : allStopsForLine) {
            es.execute(new Runnable() {
                @Override
                public void run() {
                    List<StopVisit> allDeparturesForStop = Requests.getAllDepartures(closestStop, query);
                    synchronized (result) {
                        result.addAll(allDeparturesForStop);

                    }
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
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, getBaseContext(), DeparturesActivity.this), false);
            mSwipeView.setRefreshing(false);
        }
    }

    private class GetAllDeparturesTask extends AsyncTask<Location, Void, List<Object>> {

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

            return convertToListData(mLastResult, mIsShowingFilters);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeView.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);
            mode = MODE_STOP_VISITS;
            Log.d("nextnext", "OnPostExecute " + result.toString());
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, getBaseContext(), DeparturesActivity.this), false);
            mSwipeView.setRefreshing(false);
        }
    }

    private static List<Object> convertSearchResultToListData(StopVisitsResult result, boolean showFilters) {
        List<Object> data = new ArrayList<>();

        if (showFilters) {
            data.add(new FilterView.FilterType());
        }
        data.add(result.getTimeOfSearch());

        data.addAll(convertToListItemsByStop(result));

        if (result.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_EMPTY);
        }

        return data;
    }

    private static List<Object> convertToListData(StopVisitsResult result, boolean showFilters) {
        List<Object> data = new ArrayList<>();

        if (showFilters) {
            data.add(new FilterView.FilterType());
        }
        data.add(result.getTimeOfSearch());

        List<StopVisitListItem> favourites = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(onlyFavorites(removeTransportTypes(result)))));
        List<StopVisitListItem> others = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(withoutFavourites(removeTransportTypes(result)))));

        List<StopVisitListItem> allStopVisitList = convertToListItems(result);
        for (StopVisitListItem favourite : favourites) {
            StopVisitFilters.getOtherStopsForStopVisitListItem(favourite, allStopVisitList);
        }
        for (StopVisitListItem other : others) {
            StopVisitFilters.getOtherStopsForStopVisitListItem(other, allStopVisitList);
        }

        if (result.isEmpty()) {
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

        return data;

        /*
        *
        if (filteredItems.isEmpty() && favouriteItems.isEmpty()) {
            data.add(new EmptyItem());
            return data;
        }

        if (!favouriteItems.isEmpty()) {
            data.add(favouritesHeader);
            data.addAll(favouriteItems);
            data.add(new SpaceItem());
        } else {
            data.add(noFavouritesHeader);
        }
        data.add(allOthersHeader);
        data.addAll(filteredItems);
        * */
    }
}
