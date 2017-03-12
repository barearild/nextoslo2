package com.barearild.next.v2.tasks;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.reisrest.requests.Requests;
import com.barearild.next.v2.views.departures.DeparturesActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GetAllDeparturesNearLocationTask  extends AsyncTask<Location, Object, List<StopVisit>> {
    private final Context context;

    public GetAllDeparturesNearLocationTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<StopVisit> doInBackground(Location... locations) {

        List<Stop> closestStopsToLocation = Requests.getClosestStopsToLocation(locations[0], 15, 1400);

        final List<StopVisit> result = new ArrayList<>();

        final ExecutorService es = Executors.newCachedThreadPool();
        for (final Stop stop : closestStopsToLocation) {
            es.execute(() -> {
                result.addAll(Requests.getAllDepartures(stop));
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e("GetDepartures", e.getMessage(), e);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ((DeparturesActivity) context).onGetAllDeparturesNearLocationPreExecute();
    }

    @Override
    protected void onPostExecute(List<StopVisit> result) {
        super.onPostExecute(result);
        ((DeparturesActivity) context).onGetAllDeparturesNearLocationResult(result);
    }
}
