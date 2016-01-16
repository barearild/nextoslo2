package com.barearild.next.v2.reisrest.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Arild on 13.01.2016.
 */
public class StopFilter {

    public static Stop getClosestStop(List<Stop> stops) {
        if(stops == null || stops.isEmpty()) {
            return null;
        }
        ArrayList<Stop> sortedList = new ArrayList<>(stops);
        Collections.sort(sortedList, byWalkingDistance());
        return sortedList.get(0);
    }

    public static Comparator<Stop> byWalkingDistance() {
        return new Comparator<Stop>() {
            @Override
            public int compare(Stop lhs, Stop rhs) {
                return lhs.getWalkingDistance() - rhs.getWalkingDistance();
            }
        };
    }
}
