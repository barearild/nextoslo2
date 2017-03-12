package com.barearild.next.v2.views;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.views.departures.items.DepartureListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.barearild.next.v2.StopVisitFilters.byFirstDepartureDepartureList;
import static com.barearild.next.v2.StopVisitFilters.orderByWalkingDistance;

public class NextOsloStore {

    public interface StateListener {
        void onStateChanged();
    }

    private boolean showFilters = false;
    private boolean showAll = false;

    private Date lastUpdate = null;

    private List<StopVisit> departures = Collections.emptyList();

    private List<StateListener> listeners = new ArrayList<>();

    public List<StopVisit> getDepartures() {
        return departures;
    }

    public void setLastUpdate(Date date) {
        this.lastUpdate = date;
        notifyListeners();
    }

    public void toggleShowFilter() {
        this.showFilters = !this.showFilters;
        notifyListeners();
    }

    public void showAll() {
        this.showAll = true;
        notifyListeners();
    }

    public void setShowFilters(boolean showFilters) {
        this.showFilters = showFilters;
        notifyListeners();
    }

    public void setDepartures(List<StopVisit> departures) {
        this.departures = new ArrayList<>(departures);
        this.lastUpdate = departures.isEmpty() ? null : new Date();
        notifyListeners();
    }


    public void addDepartures(List<StopVisit> departures) {
        if(this.departures == null) {
            this.departures = new ArrayList<>();
        }

        this.departures.addAll(departures);
//        if (NextOsloApp.erPreApiL24()) {
//            departures = addDeparturePreL24(departures, item);
//
//        } else {
//            departures = addDepartureL24(departures, item);
//        }
        notifyListeners();
    }

    public void addDeparture(StopVisit item) {
        if (NextOsloApp.erPreApiL24()) {
            departures = addDeparturePreL24(departures, item);

        } else {
            departures = addDepartureL24(departures, item);
        }
        notifyListeners();
    }


    private List<StopVisit> addDeparturePreL24(List<StopVisit> departures, StopVisit item) {
        List<StopVisit> newList = new ArrayList<>(departures);
        newList.add(item);
        return newList;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private List<StopVisit> addDepartureL24(List<StopVisit> departures, StopVisit item) {
        return Stream.concat(departures.stream(), Stream.of(item)).collect(Collectors.toList());
    }

    private void notifyListeners() {
        for (StateListener listener : listeners) {
            listener.onStateChanged();
        }
    }

    public void addListener(StateListener listener) {
        listeners.add(listener);
    }

    public List<DepartureListItem> getOther() {
        Map<Long, List<StopVisit>> stopVisitMap = new LinkedHashMap<>();

        for (StopVisit departure : departures) {
            if (!stopVisitMap.containsKey(departure.getHash())) {
                stopVisitMap.put(departure.getHash(), new ArrayList<>());
            }
            stopVisitMap.get(departure.getHash()).add(departure);
        }

        for (Long key : stopVisitMap.keySet()) {
            stopVisitMap.put(key, orderByWalkingDistance(stopVisitMap.get(key)));
        }


        List<DepartureListItem> departureListItems = new ArrayList<>();
        for (List<StopVisit> stopVisits : stopVisitMap.values()) {
            departureListItems.add(new DepartureListItem(stopVisits));
        }

        Collections.sort(departureListItems, byFirstDepartureDepartureList());
        return departureListItems;
    }

    public List<Object> getData() {
        List<Object> data = new ArrayList<>();

//        if (showFilters) {
//            data.add(new FilterView.FilterType());
//        }
//
//        if (lastUpdate != null) {
//            data.add(lastUpdate);
//        }

        if (!departures.isEmpty()) {

            Log.d("departures", departures.toString());

            Map<Long, List<StopVisit>> stopVisitMap = new LinkedHashMap<>();

            for (StopVisit departure : departures) {
                if (!stopVisitMap.containsKey(departure.getHash())) {
                    stopVisitMap.put(departure.getHash(), new ArrayList<>());
                }
                stopVisitMap.get(departure.getHash()).add(departure);
            }

            for (Long key : stopVisitMap.keySet()) {
                stopVisitMap.put(key, orderByWalkingDistance(stopVisitMap.get(key)));
            }


            List<DepartureListItem> departureListItems = new ArrayList<>();
            for (List<StopVisit> stopVisits : stopVisitMap.values()) {
                departureListItems.add(new DepartureListItem(stopVisits));
            }

            Collections.sort(departureListItems, byFirstDepartureDepartureList());
            data.addAll(departureListItems);

//            List<StopVisitListItem> favourites = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(onlyFavorites(removeTransportTypes(departures)))));
//            List<StopVisitListItem> others = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(withoutFavourites(removeTransportTypes(departures)))));
//
//            List<StopVisitListItem> allStopVisitList = convertToListItems(departures);
//            for (StopVisitListItem favourite : favourites) {
//                StopVisitFilters.getOtherStopsForStopVisitListItem(favourite, allStopVisitList);
//            }
//            for (StopVisitListItem other : others) {
//                StopVisitFilters.getOtherStopsForStopVisitListItem(other, allStopVisitList);
//            }
//
//            if (departures.isEmpty()) {
//                data.add(NextOsloApp.DEPARTURES_HEADER_EMPTY);
//            }
//
//            if (favourites.isEmpty()) {
//                data.add(NextOsloApp.DEPARTURES_HEADER_NO_FAVOURITES);
//            } else {
//                data.add(NextOsloApp.DEPARTURES_HEADER_FAVOURITES);
//                data.addAll(favourites);
//                data.add(new SpaceItem());
//            }
//
//            data.add(NextOsloApp.DEPARTURES_HEADER_OTHERS);
//            data.addAll(others);

        }

        return data;
    }
}

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