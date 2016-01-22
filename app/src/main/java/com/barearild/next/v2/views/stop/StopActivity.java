package com.barearild.next.v2.views.stop;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.StopVisitFilters;
import com.barearild.next.v2.reisrest.Requests;
import com.barearild.next.v2.reisrest.StopVisit.StopVisitsResult;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.departures.DeparturesAdapter;
import com.barearild.next.v2.views.departures.DeparturesRecyclerView;
import com.barearild.next.v2.views.departures.DeparturesSwipeRefreshLayout;
import com.barearild.next.v2.views.departures.FilterView;
import com.barearild.next.v2.views.departures.SpaceItem;
import com.barearild.next.v2.views.departures.StopVisitListItem;

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

    private StopVisitsResult mLastResult;
    private long mLastUpdate;
    private DeparturesSwipeRefreshLayout mSwipeView;
    private DeparturesRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutParams;
    private Stop mStop;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeView = (DeparturesSwipeRefreshLayout) findViewById(R.id.departure_list_swipe);
        mSwipeView.setOnRefreshListener(this);

        mLayoutParams = new LinearLayoutManager(this);
        mLayoutParams.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (DeparturesRecyclerView) findViewById(R.id.departure_list);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateDepartures();
    }

    @Override
    public void onItemClick(Object item) {

    }

    @Override
    public void onFilterUpdate(Transporttype transporttype, boolean isChecked) {

    }

    @Override
    public void addToFavourite(StopVisitListItem item) {

    }

    @Override
    public void removeFromFavourite(StopVisitListItem item) {

    }

    @Override
    public void showInMap(StopVisitListItem item) {

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
            super.onPreExecute();
            mSwipeView.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(List<Object> result) {
            super.onPostExecute(result);
            Log.d("nextnext", "OnPostExecute " + result.toString());
            mLastUpdate = System.currentTimeMillis();
            mRecyclerView.swapAdapter(new DeparturesAdapter(result, getBaseContext(), StopActivity.this), false);
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
