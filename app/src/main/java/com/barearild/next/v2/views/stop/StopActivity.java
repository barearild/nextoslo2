package com.barearild.next.v2.views.stop;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.StopVisitFilters;
import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.reisrest.requests.Requests;
import com.barearild.next.v2.reisrest.StopVisit.StopVisitsResult;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.departures.DeparturesAdapter;
import com.barearild.next.v2.views.departures.DeparturesRecyclerView;
import com.barearild.next.v2.views.departures.DeparturesSwipeRefreshLayout;
import com.barearild.next.v2.views.departures.FilterView;
import com.barearild.next.v2.views.departures.items.SpaceItem;
import com.barearild.next.v2.delete.StopVisitListItem;
import com.barearild.next.v2.views.details.DetailsActivity;
import com.barearild.next.v2.views.map.MapsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import v2.next.barearild.com.R;

import static com.barearild.next.v2.StopVisitFilters.convertToListItems;
import static com.barearild.next.v2.StopVisitFilters.onlyFavorites;
import static com.barearild.next.v2.StopVisitFilters.orderByWalkingDistance;
import static com.barearild.next.v2.StopVisitFilters.orderedByFirstDeparture;
import static com.barearild.next.v2.StopVisitFilters.removeTransportTypes;
import static com.barearild.next.v2.StopVisitFilters.withoutFavourites;

public class StopActivity extends AppCompatActivity implements DeparturesAdapter.OnDepartureItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_LAST_RESULT = "lastResult";

    private StopVisitsResult mLastResult;
    private long mLastUpdate;
    private DeparturesSwipeRefreshLayout mSwipeView;
    private DeparturesRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutParams;
    private Stop mStop;
    private FavouritesService mFavouriteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStop = getIntent().getParcelableExtra(Stop.class.getSimpleName());

        getSupportActionBar().setTitle(mStop.getName());
        getSupportActionBar().setSubtitle(mStop.getDistrict());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInMap(mStop);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeView = (DeparturesSwipeRefreshLayout) findViewById(R.id.departure_list_swipe);
        mSwipeView.setOnRefreshListener(this);

        mLayoutParams = new LinearLayoutManager(this);
        mLayoutParams.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (DeparturesRecyclerView) findViewById(R.id.departure_list);

        mFavouriteService = new FavouritesService(getApplicationContext());

    }

    private void showInMap(Stop stop) {
        Intent mapIntent = new Intent(StopActivity.this, MapsActivity.class);
        double[] latLon = CoordinateConversion.utm2LatLon(stop.getX(), stop.getY());
        mapIntent.putExtra("latLng", latLon);
        mapIntent.putExtra("stopName", stop.getName());

        startActivity(mapIntent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateDepartures();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_LAST_RESULT, mLastResult);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastResult = savedInstanceState.getParcelable(STATE_LAST_RESULT);

        if (mLastResult != null) {
            mLastUpdate = mLastResult.getTimeOfSearch().getTime();
            mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, false), this, this), true);
            mSwipeView.setRefreshing(false);
        }
    }

    @Override
    public void onItemClick(Object item) {

        if(item instanceof StopVisitListItem) {
            Intent details = new Intent(this, DetailsActivity.class);
            details.putExtra(StopVisitListItem.class.getSimpleName(), (StopVisitListItem)item);
            startActivity(details);
        }
    }

    @Override
    public void onFilterUpdate(Transporttype transporttype, boolean isChecked) {

    }

    @Override
    public void addToFavourite(StopVisitListItem item) {
        mFavouriteService.addFavourite(item);
        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, false), this, this), false);
    }

    @Override
    public void removeFromFavourite(StopVisitListItem item) {
        mFavouriteService.removeFavourite(item);
        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(mLastResult, false), this, this), false);
    }

    @Override
    public void showInMap(StopVisitListItem item) {
        showInMap(item.getStop());
    }

    @Override
    public void onRefresh() {
        updateDepartures();
    }

    private void updateDepartures() {
        new GetAllDeparturesTask().execute(mStop);
    }

    private class GetAllDeparturesTask extends AsyncTask<Stop, Void, List<Object>> {

        @Override
        protected List<Object> doInBackground(Stop... stops) {

            mLastResult = new StopVisitsResult(new Date());

            mLastResult.stopVisits.addAll(Requests.getAllDepartures(stops[0]));

            return convertToListData(mLastResult, false);
        }

        @Override
        protected void onPreExecute() {
            mSwipeView.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, StopActivity.this, StopActivity.this), false);
            mSwipeView.setRefreshing(false);
        }
    }

    private static List<Object> convertToListData(StopVisitsResult result, boolean showFilters) {
        List<Object> data = new ArrayList<>();

        if (showFilters) {
            data.add(new FilterView.FilterType());
        }
        data.add(result.getTimeOfSearch());

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
