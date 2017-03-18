package com.barearild.next.v2.views;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.views.departures.items.DepartureViewItem;
import com.barearild.next.v2.views.departures.items.ShowMoreViewItem;
import com.barearild.next.v2.views.departures.items.TimestampViewItem;
import com.barearild.next.v2.views.departures.items.ViewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.barearild.next.v2.NextOsloApp.DEPARTURES_HEADER_DEPARTURES;
import static com.barearild.next.v2.NextOsloApp.DEPARTURES_HEADER_OTHERS;
import static com.barearild.next.v2.StopVisitFilters.byFirstDepartureDepartureList;
import static com.barearild.next.v2.StopVisitFilters.orderByWalkingDistance;

//    public static List<StopVisitListItem> convertToListItems(List<StopVisit> stopVisits) {
//        List<StopVisitListItem> convertedListItems = new ArrayList<>();
//        Map<String, StopVisitListItem> departureMap = new HashMap<>();
//        for (StopVisit departure : stopVisits) {
//            StopVisitListItem existingDeparture = departureMap.get(departure.getId());
//
//            if (existingDeparture == null) {
//                departureMap.put(departure.getId()    , new StopVisitListItem(departure));
//            } else if (existingDeparture.getStop().equals(departure.getStop())) {
//                existingDeparture.addStopVisit(departure);
//            }
//        }
//
//        for (StopVisitListItem departureListItem : departureMap.values()) {
//            convertedListItems.add(departureListItem);
//        }
//
//        return convertedListItems;
//    }

enum NextOsloActions {
    ADD_DEPARTURE;
}
