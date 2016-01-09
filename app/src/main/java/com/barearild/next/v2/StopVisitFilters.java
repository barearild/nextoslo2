package com.barearild.next.v2;

import com.barearild.next.v2.activities.StopVisitListItem;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopVisitFilters {

    public static List<StopVisitListItem> convertToListItems(List<StopVisit> stopVisits) {
        List<StopVisitListItem> convertedListItems = new ArrayList<>();
        Map<String, StopVisitListItem> departureMap = new HashMap<>();
        for (StopVisit departure : stopVisits) {
            StopVisitListItem existingDeparture = departureMap.get(departure.getId());

            if (existingDeparture == null) {
                departureMap.put(departure.getId(), new StopVisitListItem(departure));
            } else if (existingDeparture.getStop().equals(departure.getStop())) {
                existingDeparture.addStopVisit(departure);
            }
        }

        for (StopVisitListItem departureListItem : departureMap.values()) {
            convertedListItems.add(departureListItem);
        }

        return convertedListItems;
    }

    public static Comparator<StopVisitListItem> byFirstDeparture() {
        return new Comparator<StopVisitListItem>() {
            @Override
            public int compare(StopVisitListItem lhs, StopVisitListItem rhs) {
                StopVisit firstDeparture = lhs.firstDeparture();
                StopVisit otherFirstDeparture = rhs.firstDeparture();


                return firstDeparture.compareTo(otherFirstDeparture);
            }
        };
    }

    public static List<StopVisitListItem> orderedByFirstDeparture(List<StopVisitListItem> itemsToSort) {
        Collections.sort(itemsToSort, byFirstDeparture());
        return itemsToSort;
    }

    public static Comparator<StopVisit> byWalkingDistance() {
        return new Comparator<StopVisit>() {
            @Override
            public int compare(StopVisit lhs, StopVisit rhs) {
                return lhs.getStop().getWalkingDistance() - rhs.getStop().getWalkingDistance();
            }
        };
    }

    public static List<StopVisit> orderByWalkingDistance(List<StopVisit> stopVisits) {
        Collections.sort(stopVisits, byWalkingDistance());
        return stopVisits;
    }
}
