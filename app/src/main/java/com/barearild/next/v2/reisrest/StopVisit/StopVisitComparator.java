package com.barearild.next.v2.reisrest.StopVisit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StopVisitComparator {

    public static Comparator<StopVisit> byWalkingDistance() {
        return new Comparator<StopVisit>() {
            @Override
            public int compare(StopVisit lhs, StopVisit rhs) {
                return lhs.getStop().getWalkingDistance() - rhs.getStop().getWalkingDistance();
            }
        };
    }

    public static Comparator<StopVisit> byFirstDeparture() {
        return new Comparator<StopVisit>() {
            @Override
            public int compare(StopVisit lhs, StopVisit rhs) {
                return lhs.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime()
                        .compareTo(rhs.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime());
            }
        };
    }

    public static List<StopVisit> orderByWalkingDistance(List<StopVisit> stopVisits) {
        Collections.sort(stopVisits, byWalkingDistance());
        return stopVisits;
    }
}
