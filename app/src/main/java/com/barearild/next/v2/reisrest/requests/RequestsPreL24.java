package com.barearild.next.v2.reisrest.requests;

import android.location.Location;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.StopVisit.DeviationDetails;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.barearild.next.v2.reisrest.place.Stop.Builder.fromStop;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_ALL_DEPARTURES;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_ALL_DEPARTURES_FOR_LINE_AT_STOP;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_DEVIATION_DETAILS;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_DEVIATION_DETAILS_TYPE;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_TYPE;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_VISIT_TYPE;
import static com.barearild.next.v2.reisrest.requests.Requests.doRequest;

class RequestsPreL24 {


    static List<Stop> getAllStopsForLine(String lineId, Location location) {
        String request = String.format(Requests.GET_STOPS_FOR_LINE, lineId);

        return calculateWalkingDistanceToStops(doRequest(request, LIST_STOP_TYPE), location);
    }

    static List<Stop> getClosestStopsToLocation(Location location, int numberOfStops, int walkingDistance) {
        CoordinateConversion coordinateConversion = new CoordinateConversion();
        UtmLocation utmLocation = coordinateConversion.location2Utm(location);

        String requestString = String.format(Locale.getDefault(), GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES,
                utmLocation.getEasting(), utmLocation.getNorthing(), numberOfStops, walkingDistance);

        return calculateWalkingDistanceToStops(doRequest(requestString, LIST_STOP_TYPE), location);
    }


    static List<StopVisit> getAllDepartures(Stop stop, String line) {
        final String requestString = String.format(Locale.getDefault(), GET_ALL_DEPARTURES_FOR_LINE_AT_STOP, stop.getID(), line);

        final List<StopVisit> allStopVisits = stopVisitWithStop(doRequest(requestString, LIST_STOP_VISIT_TYPE), stop);

        Log.d(NextOsloApp.LOG_TAG, allStopVisits.toString());

        return allStopVisits;
    }

    static List<StopVisit> getAllDepartures(Stop stop) {
        final String requestString = String.format(Locale.getDefault(), GET_ALL_DEPARTURES, stop.getID());

        final List<StopVisit> stopVisits = stopVisitWithStop(doRequest(requestString, LIST_STOP_VISIT_TYPE), stop);

        Log.d(NextOsloApp.LOG_TAG, stopVisits.toString());

        return stopVisits;
    }

    static DeviationDetails getDeviationDetails(int deviationId) {
        final String requestString = String.format(Locale.getDefault(), GET_DEVIATION_DETAILS, deviationId);

        final List<DeviationDetails> deviationDetails = doRequest(requestString, LIST_DEVIATION_DETAILS_TYPE);

        return deviationDetails.get(0);
    }

    static List<Line> getLinesSuggestion(String searchString) {
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

    private static List<StopVisit> stopVisitWithStop(List<StopVisit> stopVisits, Stop stop) {
        List<StopVisit> stopVisitsWithStop = new ArrayList<>(stopVisits.size());

        for (StopVisit stopVisit : stopVisits) {
            stopVisitsWithStop.add(StopVisit.Builder.fromStopVisit(stopVisit).withStop(stop).build());
        }

        return stopVisitsWithStop;
    }

}
