package com.barearild.next.v2;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import com.barearild.next.v2.reisrest.requests.Requests;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.departures.items.HeaderViewItem;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import v2.next.barearild.com.R;

public class NextOsloApp extends Application {

    public static final String LOG_TAG = "nextnext";

    public static final String SEARCH_ADDRESS = "com.barearild.next.v2.SEARCH_ADDRESS";
    public static final String SEARCH_LINE = "com.barearild.next.v2.SEARCH_LINES";
    public static final String SEARCH_STOP = "com.barearild.next.v2.SEARCH_STOP";

    public static final String GOOGLE_API_KEY = "AIzaSyC0XTqTESWXZSYmjset6oXOsY9BmGeTrso";
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    public static final HeaderViewItem DEPARTURES_HEADER_LINES_NEARBY = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_LINES = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_STOPS = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_ADDRESSES = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_EMPTY = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_NO_FAVOURITES = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_OTHERS = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_DEPARTURES = new HeaderViewItem();
    public static final HeaderViewItem DEPARTURES_HEADER_FAVOURITES = new HeaderViewItem();

    public static final String PACKAGE_NAME = "com.barearild.next.v2";
    public static final String USER_PREFERENCES = "user_preferences";

    public static final String PREFS_CONVERTED = "converted";
    public static final String FAVOURITES_CONVERTED_V2 = "favourites_converted_v2";

    public static final String FAVOURITES = "favourites";
    public static final String DEPARTURES = "departures";

    public static final String SHOW_PULL_DOWN_INFO = "prefs_first_time_update_button";

    public static final HashMap<Transporttype, Boolean> SHOW_TRANSPORT_TYPE = new HashMap<Transporttype, Boolean>();
    public static final int REQUEST_PERMISSION_LOCATION = 0;

    public static List<Line> mAllLines;

    public static final List<Stop> ALL_STOPS = new ArrayList<>();

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        DEPARTURES_HEADER_STOPS.text = getString(R.string.stops);
        DEPARTURES_HEADER_LINES_NEARBY.text = getString(R.string.lines_nearby);
        DEPARTURES_HEADER_LINES.text = getString(R.string.lines);
        DEPARTURES_HEADER_ADDRESSES.text = getString(R.string.addresses);
        DEPARTURES_HEADER_NO_FAVOURITES.text = getString(R.string.no_favourites);
        DEPARTURES_HEADER_FAVOURITES.text = getString(R.string.favourites);
        DEPARTURES_HEADER_DEPARTURES.text = getString(R.string.departures);
        DEPARTURES_HEADER_OTHERS.text = getString(R.string.all_others);
        DEPARTURES_HEADER_EMPTY.text = getString(R.string.departure_list_empty);

        setActiveTransportTypes();

        JodaTimeAndroid.init(this);

        downloadAllStopsAndLines();
    }

    private void downloadAllStopsAndLines() {
        new GetAllStopsTask().execute();
    }

    private void setActiveTransportTypes() {
        for (Transporttype transporttype : Transporttype.onlyRealTimeTransporttypes) {
            SHOW_TRANSPORT_TYPE.put(transporttype, prefs.getBoolean(transporttype.name(), true));
        }
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

//    static void updateTransportTypePrefs(Transporttype transporttype, boolean checked) {
//        SHOW_TRANSPORT_TYPE.put(transporttype, checked);
//
//        prefs.edit().putBoolean(transporttype.name(), checked).apply();
//    }

    private class GetAllStopsTask extends AsyncTask<Void, Void, List<Stop>> {

        public GetAllStopsTask() {
        }

        @Override
        protected List<Stop> doInBackground(Void... voids) {
            return Requests.getAllStops();
        }

        @Override
        protected void onPostExecute(List<Stop> stops) {
            super.onPostExecute(stops);
            synchronized (ALL_STOPS) {
                ALL_STOPS.clear();
                ALL_STOPS.addAll(stops);
            }
        }
    }

    public static boolean erPreApiL24() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }


}
