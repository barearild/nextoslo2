package com.barearild.next.v2.tasks;

import android.os.AsyncTask;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.reisrest.requests.Requests;

import java.util.List;

public class GetAllDeparturesFromStopTask extends AsyncTask<Stop, Object, List<StopVisit>> {
    private final GetAllDeparturesFromStopTaskListener callback;

    public GetAllDeparturesFromStopTask(GetAllDeparturesFromStopTaskListener callback) {
        this.callback = callback;
    }

    @Override
    protected List<StopVisit> doInBackground(Stop... stops) {
        return Requests.getAllDepartures(stops[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onGetAllDeparturesFromStopPreExecute();
    }

    @Override
    protected void onPostExecute(List<StopVisit> result) {
        super.onPostExecute(result);
        callback.onGetAllDeparturesFromStopPostExecute(result);
    }

    public interface GetAllDeparturesFromStopTaskListener {
        void onGetAllDeparturesFromStopPreExecute();
        void onGetAllDeparturesFromStopPostExecute(List<StopVisit> result);
    }
}
