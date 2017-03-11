package com.barearild.next.v2.reisrest.StopVisit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StopVisitComparator {

    public static Comparator<StopVisit> byWalkingDistance() {
        return (lhs, rhs) -> lhs.getStop().getWalkingDistance() - rhs.getStop().getWalkingDistance();
    }

    public static List<StopVisit> orderByWalkingDistance(List<StopVisit> stopVisits) {
        Collections.sort(stopVisits, byWalkingDistance());
        return stopVisits;
    }
}
