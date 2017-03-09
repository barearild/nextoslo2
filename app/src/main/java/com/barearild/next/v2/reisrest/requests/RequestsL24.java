package com.barearild.next.v2.reisrest.requests;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.StopVisit.DeviationDetails;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.barearild.next.v2.reisrest.StopVisit.StopVisit.Builder.fromStopVisit;
import static com.barearild.next.v2.reisrest.place.Stop.Builder.fromStop;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_ALL_DEPARTURES;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_ALL_DEPARTURES_FOR_LINE_AT_STOP;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_DEVIATION_DETAILS;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_STOPS_FOR_LINE;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_DEVIATION_DETAILS_TYPE;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_TYPE;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_VISIT_TYPE;
import static com.barearild.next.v2.reisrest.requests.Requests.doRequest;

@TargetApi(Build.VERSION_CODES.N)
class RequestsL24 {

    static List<Stop> getAllStopsForLineL24(String lineId, Location location) {
        String request = String.format(GET_STOPS_FOR_LINE, lineId);

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

        return Optional.ofNullable(deviationDetails)
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .orElse(new DeviationDetails());
    }

    static List<Line> getLinesSuggestion(String searchString) {
        if (NextOsloApp.mAllLines == null) {
            NextOsloApp.mAllLines = Requests.getAllLines();
        }

        return NextOsloApp.mAllLines.stream()
                .filter(line -> line.getName().toLowerCase().matches("[A-Z,a-z]*?(" + searchString.toLowerCase() + ")[A-Z,a-z]*"))
                .collect(Collectors.toList());
    }


    private static List<Stop> calculateWalkingDistanceToStops(List<Stop> stops, Location location) {
        return stops.stream()
                .map(stop -> fromStop(stop).withWalkingDistanceTo(location))
                .map(Stop.Builder::build)
                .collect(Collectors.toList());
    }

    private static List<StopVisit> stopVisitWithStop(List<StopVisit> stopVisits, Stop stop) {
        return stopVisits.stream()
                .map(StopVisit.Builder::fromStopVisit)
                .map(builder -> builder.withStop(stop))
                .map(StopVisit.Builder::build)
                .collect(Collectors.toList());
    }
}
