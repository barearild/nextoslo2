package com.barearild.next.v2.reisrest.requests;

import android.annotation.TargetApi;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.DateJodaDeserializer;
import com.barearild.next.v2.reisrest.StopVisit.DeviationDetails;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.TransporttypeDeserializer;
import com.barearild.next.v2.reisrest.VehicleMode;
import com.barearild.next.v2.reisrest.VehicleModeDeserializer;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.barearild.next.v2.NextOsloApp.erPreApiL24;
import static com.barearild.next.v2.reisrest.place.Stop.Builder.fromStop;

public class Requests {

    static final String GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES = "http://reisapi.ruter.no/Place/GetClosestStops/?coordinates=(x=%d,y=%d)"
            + "&proposals=%d&walkingDistance=%d";
    static final String GET_ALL_DEPARTURES = "http://reisapi.ruter.no/StopVisit/GetDepartures/%d";
    static final String GET_ALL_DEPARTURES_FOR_LINE_AT_STOP = "http://reisapi.ruter.no/StopVisit/GetDepartures/%d?linenames=%s";
    static final String GET_DEVIATION_DETAILS = "http://devi.ruter.no/devirest.svc/json/deviationids/%d";
    static final String GET_STOPS_FOR_LINE = "http://reisapi.ruter.no/Line/GetStopsByLineId/%s";
    static final String GET_ALL_LINES = "http://reisapi.ruter.no/Line/GetLinesRuter";
    static final String GET_ALL_STOPS = "http://reisapi.ruter.no/Place/GetStopsRuter";

    private static final GsonBuilder GSON = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateJodaDeserializer())
            .registerTypeAdapter(VehicleMode.class, new VehicleModeDeserializer())
            .registerTypeAdapter(Transporttype.class, new TransporttypeDeserializer());

    static final Type LIST_STOP_TYPE = new TypeToken<List<Stop>>() {
    }.getType();
    public static final Type LIST_STOP_VISIT_TYPE = new TypeToken<List<StopVisit>>() {
    }.getType();
    static final Type LIST_DEVIATION_DETAILS_TYPE = new TypeToken<List<DeviationDetails>>() {
    }.getType();
    static final Type LIST_LINE_TYPE = new TypeToken<List<Line>>() {
    }.getType();

    public static List<Stop> getAllStopsForLine(String lineId, Location location) {
        if(erPreApiL24()) {
            return RequestsPreL24.getAllStopsForLine(lineId, location);
        } else {
            return RequestsL24.getAllStopsForLineL24(lineId, location);
        }
    }

    public static List<Stop> getClosestStopsToLocation(Location location, int numberOfStops, int walkingDistance) {
        if(erPreApiL24()) {
            return RequestsPreL24.getClosestStopsToLocation(location, numberOfStops, walkingDistance);
        } else {
            return RequestsL24.getClosestStopsToLocation(location, numberOfStops, walkingDistance);
        }
    }

    public static List<StopVisit> getAllDepartures(Stop stop, String line) {
        if(erPreApiL24()) {
            return RequestsPreL24.getAllDepartures(stop, line);
        } else {
            return RequestsL24.getAllDepartures(stop, line);
        }
    }

    public static List<StopVisit> getAllDepartures(Stop stop) {
        if(erPreApiL24()) {
            return RequestsPreL24.getAllDepartures(stop);
        } else {
            return RequestsL24.getAllDepartures(stop);
        }
    }

    public static DeviationDetails getDeviationDetails(int deviationId) {
        if(erPreApiL24()) {
            return RequestsPreL24.getDeviationDetails(deviationId);
        } else {
            return RequestsL24.getDeviationDetails(deviationId);
        }
    }

    public static List<Stop> getAllStops() {
        return doRequest(GET_ALL_STOPS, LIST_STOP_TYPE);
    }

    public static List<Line> getLinesSuggestion(String searchString) {
        if(erPreApiL24()) {
            return RequestsPreL24.getLinesSuggestion(searchString);
        } else {
            return RequestsL24.getLinesSuggestion(searchString);
        }
    }

    static List<Line> getAllLines() {
        return doRequest(GET_ALL_LINES, LIST_LINE_TYPE);
    }


    static <K> K doRequest(String requestString, Type listType) {
        return GSON.create().fromJson(doRequest(requestString).toString(), listType);
    }

    private static JSONArray doRequest(String request) {
        Log.d("nextnext", "doRequest[" + request + "]");

        HttpURLConnection urlConnection = null;

        try {
            URL requestUrl = new URL(request);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(20000);

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                //handle
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            JSONArray data = new JSONArray(getResponseText(in));

            in.close();

            return data;
        } catch (java.io.IOException | JSONException | NoSuchElementException e) {
            Log.e("nextnext", e.getMessage(), e.getCause());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new JSONArray();
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

}
