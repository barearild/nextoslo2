package com.barearild.next.v2.reisrest;

import android.location.Location;
import android.util.Log;

import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
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
import java.util.List;
import java.util.Scanner;

public class Requests {

    private static final String RUTER_API = "http://reisapi.ruter.no/";
    private static final String GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES = "Place/GetClosestStops/?coordinates=(x=%d,y=%d)"
            + "&proposals=%d&walkingDistance=%d";
    private static final String GET_ALL_DEPARTURES = "StopVisit/GetDepartures/%d";

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

            Log.d("nextnext", "walking distance to " + stop.getName() + " is " + stop.getWalkingDistance());
        }
    }

    public static List<StopVisit> getAllDepartures(Stop stop) {
        String requestString = String.format(GET_ALL_DEPARTURES, stop.getID());

        JSONArray data = doRequest(requestString);

        Type listType = new TypeToken<List<StopVisit>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateJodaDeserializer())
                .registerTypeAdapter(VehicleMode.class, new VehicleModeDeserializer())
                .registerTypeAdapter(Transporttype.class, new TransporttypeDeserializer())
                ;

        List<StopVisit> stopVisits = gsonBuilder.create().fromJson(data.toString(), listType);

        for (StopVisit stopVisit : stopVisits) {
            stopVisit.setStop(stop);
        }

        return stopVisits;
    }

    private static JSONArray doRequest(String request) {
        Log.d("nextnext", "doRequest[" + RUTER_API+request + "]");

        HttpURLConnection urlConnection = null;

        try {
            URL requestUrl = new URL(RUTER_API + request);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(10000);

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                //handle
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            return new JSONArray(getResponseText(in));
        } catch (java.io.IOException e) {
            Log.e("nextnext", e.getMessage(), e.getCause());
        } catch (JSONException e) {
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
