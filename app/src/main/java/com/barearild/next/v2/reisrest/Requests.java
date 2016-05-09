package com.barearild.next.v2.reisrest;

import android.location.Location;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.StopVisit.DeviationDetails;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;
import com.google.gson.Gson;
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
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Requests {

    private static final String RUTER_API = "http://reisapi.ruter.no/";
    private static final String GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES = "http://reisapi.ruter.no/Place/GetClosestStops/?coordinates=(x=%d,y=%d)"
            + "&proposals=%d&walkingDistance=%d";
    private static final String GET_ALL_DEPARTURES = "http://reisapi.ruter.no/StopVisit/GetDepartures/%d";
    private static final String GET_ALL_DEPARTURES_FOR_LINE_AT_STOP = "http://reisapi.ruter.no/StopVisit/GetDepartures/%d?linenames=%s";
    private static final String GET_DEVIATION_DETAILS = "http://devi.ruter.no/devirest.svc/json/deviationids/%d";
    private static final String GET_STOPS_FOR_LINE = "http://reisapi.ruter.no/Line/GetStopsByLineId/%s";
    private static final String GET_ALL_LINES = "http://reisapi.ruter.no/Line/GetLinesRuter";
    private static final String GET_ALL_STOPS = "http://reisapi.ruter.no/Place/GetStopsRuter";

    public static List<Stop> getAllStopsForLine(String lineId, Location location) {
        JSONArray data = doRequest(String.format(GET_STOPS_FOR_LINE, lineId));

        Type listType = new TypeToken<List<Stop>>() {
        }.getType();

        List<Stop> stops = new Gson().fromJson(data.toString(), listType);

        if (location != null) {
            calculateWalkingDistanceToStops(stops, location);
        }

        return stops;
    }

    public static List<Stop> getClosestStopsToLocation(Location location, int numberOfStops, int walkingDistance) {
        CoordinateConversion coordinateConversion = new CoordinateConversion();
        UtmLocation utmLocation = coordinateConversion.location2Utm(location);

        String requestString = String.format(GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES,
                utmLocation.getEasting(), utmLocation.getNorthing(), numberOfStops, walkingDistance);

        JSONArray data = doRequest(requestString);

        Type listType = new TypeToken<List<Stop>>() {
        }.getType();
        List<Stop> stops = new Gson().fromJson(data.toString(), listType);

        calculateWalkingDistanceToStops(stops, location);

        return stops;
    }

    private static void calculateWalkingDistanceToStops(List<Stop> stops, Location location) {
        for (Stop stop : stops) {
            double[] latLonForStop = CoordinateConversion.utm2LatLon(stop.getX(), stop.getY());
            float[] result = new float[3];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), latLonForStop[0], latLonForStop[1], result);

            stop.setWalkingDistance((int) result[0]);
        }
    }

    public static List<StopVisit> getAllDepartures(Stop stop, String line) {
        String requestString = String.format(GET_ALL_DEPARTURES_FOR_LINE_AT_STOP, stop.getID(), line);

        JSONArray data = doRequest(requestString);

        Log.d(NextOsloApp.LOG_TAG, data.toString());

        Type listType = new TypeToken<List<StopVisit>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateJodaDeserializer())
                .registerTypeAdapter(VehicleMode.class, new VehicleModeDeserializer())
                .registerTypeAdapter(Transporttype.class, new TransporttypeDeserializer());

        List<StopVisit> allStopVisits = gsonBuilder.create().fromJson(data.toString(), listType);

        List<StopVisit> stopVisitsForStop = new ArrayList<>(allStopVisits);

//        for (StopVisit stopVisit : allStopVisits) {
//            if(stopVisit.getMonitoredVehicleJourney().getLineRef().equals(line)) {
//                stopVisitsForStop.add(stopVisit);
//            }
//        }

        for (StopVisit stopVisit : stopVisitsForStop) {
            stopVisit.setStop(stop);
        }

        Log.d(NextOsloApp.LOG_TAG, stopVisitsForStop.toString());

        return stopVisitsForStop;
    }

    public static List<StopVisit> getAllDepartures(Stop stop) {
        String requestString = String.format(GET_ALL_DEPARTURES, stop.getID());

        JSONArray data = doRequest(requestString);

        Log.d(NextOsloApp.LOG_TAG, data.toString());

        Type listType = new TypeToken<List<StopVisit>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateJodaDeserializer())
                .registerTypeAdapter(VehicleMode.class, new VehicleModeDeserializer())
                .registerTypeAdapter(Transporttype.class, new TransporttypeDeserializer());

        List<StopVisit> stopVisits = gsonBuilder.create().fromJson(data.toString(), listType);

        for (StopVisit stopVisit : stopVisits) {
            stopVisit.setStop(stop);
        }

        Log.d(NextOsloApp.LOG_TAG, stopVisits.toString());

        return stopVisits;
    }

    public static DeviationDetails getDeviationDetails(int deviationId) {
        String requestString = String.format(GET_DEVIATION_DETAILS, deviationId);

        final JSONArray data = doRequest(requestString);

        Type listType = new TypeToken<List<DeviationDetails>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateJodaDeserializer());

        List<DeviationDetails> deviationDetails = gsonBuilder.create().fromJson(data.toString(), listType);

        return deviationDetails.get(0);
    }

    public static List<Line> getAllLines() {

        final JSONArray data = doRequest(GET_ALL_LINES);

        Type listType = new TypeToken<List<Line>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Transporttype.class, new TransporttypeDeserializer());

        return gsonBuilder.create().fromJson(data.toString(), listType);
    }

    public static List<Stop> getAllStops() {
        final JSONArray data = doRequest(GET_ALL_STOPS);

        Type listType = new TypeToken<List<Stop>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();

        return gsonBuilder.create().fromJson(data.toString(), listType);
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

    private static boolean isANumber(String searchString) {
        return searchString.matches("[0-9]+");
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
