package com.barearild.next.v2.reisrest.requests;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;

import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.place.Stop;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.barearild.next.v2.reisrest.place.Stop.Builder.fromStop;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_STOPS_FOR_LINE;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_TYPE;
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


    private static List<Stop> calculateWalkingDistanceToStops(List<Stop> stops, Location location) {
        return stops.stream()
                .map(stop -> fromStop(stop).withWalkingDistanceTo(location))
                .map(Stop.Builder::build)
                .collect(Collectors.toList());
    }
}
