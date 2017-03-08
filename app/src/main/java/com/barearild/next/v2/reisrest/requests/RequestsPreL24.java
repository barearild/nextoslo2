package com.barearild.next.v2.reisrest.requests;

import android.location.Location;

import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.barearild.next.v2.location.libs.UtmLocation;
import com.barearild.next.v2.reisrest.place.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.barearild.next.v2.reisrest.place.Stop.Builder.fromStop;
import static com.barearild.next.v2.reisrest.requests.Requests.GET_CLOSEST_STOPS_ADVANCED_BY_COORDINATES;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_TYPE;
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

    static List<Stop> calculateWalkingDistanceToStops(List<Stop> stops, Location location) {
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

}
