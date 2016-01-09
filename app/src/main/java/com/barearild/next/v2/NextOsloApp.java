package com.barearild.next.v2;

import android.app.Application;
import android.content.SharedPreferences;

import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.views.departures.DeparturesHeader;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.HashMap;

import v2.next.barearild.com.R;

public class NextOsloApp extends Application {

    public static final DeparturesHeader DEPARTURES_HEADER_NO_FAVOURITES = new DeparturesHeader();
    public static final DeparturesHeader DEPARTURES_HEADER_OTHERS = new DeparturesHeader();

    public static final String PACKAGE_NAME = "com.barearild.next.v2";
    public static final String USER_PREFERENCES = "user_preferences";

    public static final String PREFS_CONVERTED = "converted";
    public static final String FAVOURITES_CONVERTED_V2 = "favourites_converted_v2";

    public static final String FAVOURITES = "favourites";
    public static final String DEPARTURES = "departures";

    public static final HashMap<Transporttype, Boolean> SHOW_TRANSPORT_TYPE = new HashMap<Transporttype, Boolean>();

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        DEPARTURES_HEADER_NO_FAVOURITES.text = getText(R.string.no_favourites).toString();
        DEPARTURES_HEADER_OTHERS.text = getText(R.string.all_others).toString();

        setActiveTransportTypes();

        JodaTimeAndroid.init(this);
    }

    private void setActiveTransportTypes() {
        for (Transporttype transporttype : Transporttype.onlyRealTimeTransporttypes) {
            SHOW_TRANSPORT_TYPE.put(transporttype, prefs.getBoolean(transporttype.name(), true));
        }
    }
}
