package com.barearild.next.v2.reisrest.requests;

import android.annotation.TargetApi;
import android.location.Location;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

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
    static final Type LIST_STOP_VISIT_TYPE = new TypeToken<List<StopVisit>>() {
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
        final String requestString = String.format(Locale.getDefault(), GET_ALL_DEPARTURES_FOR_LINE_AT_STOP, stop.getID(), line);

        final List<StopVisit> allStopVisits = stopVisitWithStop(doRequest(requestString, LIST_STOP_VISIT_TYPE), stop);

        Log.d(NextOsloApp.LOG_TAG, allStopVisits.toString());

        return allStopVisits;
    }

    public static List<StopVisit> getAllDepartures(Stop stop) {
        final String requestString = String.format(Locale.getDefault(), GET_ALL_DEPARTURES, stop.getID());

        final List<StopVisit> stopVisits = stopVisitWithStop(doRequest(requestString, LIST_STOP_VISIT_TYPE), stop);

        Log.d(NextOsloApp.LOG_TAG, stopVisits.toString());

        return stopVisits;
    }

    public static DeviationDetails getDeviationDetails(int deviationId) {
        final String requestString = String.format(Locale.getDefault(), GET_DEVIATION_DETAILS, deviationId);

        final List<DeviationDetails> deviationDetails = doRequest(requestString, LIST_DEVIATION_DETAILS_TYPE);

        return deviationDetails.get(0);
    }

    private static List<Line> getAllLines() {
        return doRequest(GET_ALL_LINES, LIST_LINE_TYPE);
    }

    public static List<Stop> getAllStops() {
        return doRequest(GET_ALL_STOPS, LIST_STOP_TYPE);
    }

    public static List<Line> getLinesSuggestion(String searchString) {
        ArrayList<Line> lineSuggestions = new ArrayList<>();
        if (NextOsloApp.mAllLines == null) {
            NextOsloApp.mAllLines = Requests.getAllLines();
            Log.d(NextOsloApp.LOG_TAG, "All lines: " + NextOsloApp.mAllLines);
        }

        if (NextOsloApp.mAllLines != null) {
            for (Line line : NextOsloApp.mAllLines) {
                if (isANumber(searchString) && line.getName().toLowerCase().matches("[A-Z,a-z]*?(" + searchString + ")[A-Z,a-z]*")) {
                    lineSuggestions.add(line);
                    Log.d(NextOsloApp.LOG_TAG, "Adding line to suggestions " + line);
                } else if (line.getName().equalsIgnoreCase(searchString)) {
                    lineSuggestions.add(line);
                    Log.d(NextOsloApp.LOG_TAG, "Adding line to suggestions " + line);
                }
            }
        }

        return lineSuggestions;
    }

    private static List<StopVisit> stopVisitWithStop(List<StopVisit> stopVisits, Stop stop) {
        List<StopVisit> stopVisitsWithStop = new ArrayList<>(stopVisits.size());

        for (StopVisit stopVisit : stopVisits) {
            stopVisitsWithStop.add(StopVisit.Builder.fromStopVisit(stopVisit).withStop(stop).build());
        }

        return stopVisitsWithStop;
    }



    private static List<Stop> calculateWalkingDistanceToStops(List<Stop> stops, Location location) {
        final List<Stop> stopsWithWalkingDistance = new ArrayList<>();
        for (Stop stop : stops) {
            stopsWithWalkingDistance.add(
                    fromStop(stop)
                            .withWalkingDistanceTo(location)
                            .build()
            );
        }

        return stopsWithWalkingDistance;
    }

    private static boolean isANumber(String searchString) {
        return searchString.matches("[0-9]+");
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

    private static boolean erPreApiL24() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }

}
