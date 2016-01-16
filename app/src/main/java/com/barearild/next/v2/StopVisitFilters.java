package com.barearild.next.v2;

import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.StopVisit.StopVisitsResult;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.VehicleMode;
import com.barearild.next.v2.views.departures.StopVisitListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopVisitFilters {

    public static List<StopVisitListItem> convertToListItemsByStop(List<StopVisit> stopVisits) {
        List<StopVisitListItem> convertedListItems = new ArrayList<>();
        Map<Integer, StopVisitListItem> departureMap = new HashMap<>();
        for (StopVisit departure : stopVisits) {
            StopVisitListItem existingDeparture = departureMap.get(departure.getStop().getID());

            if (existingDeparture == null) {
                departureMap.put(departure.getStop().getID(), new StopVisitListItem(departure));
            } else if (existingDeparture.getStop().equals(departure.getStop())) {
                existingDeparture.addStopVisit(departure);
            }
        }

        for (StopVisitListItem departureListItem : departureMap.values()) {
            convertedListItems.add(departureListItem);
        }

        return convertedListItems;
    }

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

    public static void getOtherStopsForStopVisitListItem(StopVisitListItem stopVisitListItem, List<StopVisitListItem> allStopVisitList) {
        for (StopVisitListItem item : allStopVisitList) {
            if(item.getId().equals(stopVisitListItem.getId()) && item.getStop().getID() != stopVisitListItem.getStop().getID()) {
                stopVisitListItem.addOtherStopForLine(item);
            }
        }
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

    public static List<StopVisit> onlyFavorites(List<StopVisit> unfilteredDepartures) {
        List<StopVisit> filteredDepartures = new ArrayList<StopVisit>();
        for (StopVisit departure : unfilteredDepartures) {
            if (FavouritesService.isFavourite(departure)) {
                filteredDepartures.add(departure);
            }
        }

        return filteredDepartures;
    }

    public static List<StopVisit> withoutFavourites(List<StopVisit> unfilteredDepartures) {
        List<StopVisit> filteredDepartures = new ArrayList<StopVisit>();
        for (StopVisit departure : unfilteredDepartures) {
            if (!FavouritesService.isFavourite(departure)) {
                filteredDepartures.add(departure);
            }
        }

        return filteredDepartures;
    }

    public static List<StopVisit> removeTransportTypes(List<StopVisit> unfilteredDepartures) {
        List<StopVisit> filteredDepartures = new ArrayList<StopVisit>();
        for (StopVisit departure : unfilteredDepartures) {
            if (showTransportType(departure)) {
                filteredDepartures.add(departure);
            }
        }

        return filteredDepartures;
    }

    private static boolean showTransportType(StopVisit departure) {
        if (NextOsloApp.SHOW_TRANSPORT_TYPE.isEmpty()) {
            return true;
        }


        VehicleMode vehicleMode = departure.getMonitoredVehicleJourney().getVehicleMode();
        Transporttype transporttype = vehicleMode.transporttype();
        if (Transporttype.isRegionalBus(departure)) {
            transporttype = Transporttype.RegionalBus;
        }

        return NextOsloApp.SHOW_TRANSPORT_TYPE.get(transporttype);
    }

    public static StopVisitsResult filterLineRef(String query, StopVisitsResult stopVisitsResult) {
        StopVisitsResult filteredResult = new StopVisitsResult(stopVisitsResult.getTimeOfSearch());
        for (StopVisit stopVisit : stopVisitsResult) {
            if(stopVisit.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredResult.add(stopVisit);
            }
        }

        return filteredResult;
    }
}
