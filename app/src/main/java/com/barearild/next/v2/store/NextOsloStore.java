package com.barearild.next.v2.store;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.views.departures.DeparturesAdapter;
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

public class NextOsloStore implements DeparturesAdapter.DepartureAdapterStore {

    public interface StateListener {
        void onStateChanged();
    }

    private boolean showFilters = false;
    private boolean showAll = false;
    private boolean haveMore = false;

    private Date lastUpdate = null;

    private List<StopVisit> departures = Collections.emptyList();

    private List<DepartureViewItem> favourites = Collections.emptyList();
    private List<DepartureViewItem> other = Collections.emptyList();
    private List<DepartureViewItem> more = Collections.emptyList();

    private List<NextOsloStore.StateListener> listeners = new ArrayList<>();

    public List<StopVisit> getDepartures() {
        return departures;
    }

    public List<DepartureViewItem> getFavourites() {
        return favourites;
    }

    public List<DepartureViewItem> getOther() {
        return other;
    }

    public List<DepartureViewItem> getMore() {
        return more;
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

    public boolean shouldShowAll() {
        return this.showAll;
    }

    public void setShowFilters(boolean showFilters) {
        this.showFilters = showFilters;
        notifyListeners();
    }

    public void setDepartures(List<StopVisit> departures) {
        this.departures = new ArrayList<>(departures);
        Collections.sort(departures);

        this.favourites = getFavourites(departures);
        this.other = getOther(departures);
        this.more = getMore(departures);

        this.lastUpdate = departures.isEmpty() ? null : new Date();
        this.showAll = false;
        notifyListeners();
    }

    private List<DepartureViewItem> getFavourites(List<StopVisit> departures) {
        return Collections.emptyList();
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

    public boolean haveMore() {
        return haveMore;
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
        for (NextOsloStore.StateListener listener : listeners) {
            listener.onStateChanged();
        }
    }

    public void addListener(NextOsloStore.StateListener listener) {
        listeners.add(listener);
    }

    private List<DepartureViewItem> getMore(List<StopVisit> departures) {
        List<DepartureViewItem> items = stopVisitsToDepartureListItems(departures);

        if(items.isEmpty() || items.size() <= 10) {
            return Collections.emptyList();
        }

        return items.subList(10, items.size()-1);
    }

    private List<DepartureViewItem> getOther(List<StopVisit> departures) {
        List<DepartureViewItem> departureViewItems = stopVisitsToDepartureListItems(departures);

        if(departureViewItems.size() > 10) {
            return departureViewItems.subList(0, 9);
        }
//
//        haveMore = false;
        return departureViewItems;
    }

    @NonNull
    private List<DepartureViewItem> stopVisitsToDepartureListItems(List<StopVisit> departures) {
        Map<String, List<StopVisit>> stopVisitMap = new LinkedHashMap<>();

        for (StopVisit departure : departures) {
            if (!stopVisitMap.containsKey(departure.getId())) {
                stopVisitMap.put(departure.getId(), new ArrayList<>());
            }
            stopVisitMap.get(departure.getId()).add(departure);
        }

        for (String key : stopVisitMap.keySet()) {
            stopVisitMap.put(key, orderByWalkingDistance(stopVisitMap.get(key)));
        }


        List<DepartureViewItem> departureViewItems = new ArrayList<>();
        for (List<StopVisit> stopVisits : stopVisitMap.values()) {
            departureViewItems.add(new DepartureViewItem(stopVisits));
        }

        Collections.sort(departureViewItems, byFirstDepartureDepartureList());
        return departureViewItems;
    }

    public List<ViewItem> getData() {
        if(departures.isEmpty()) {
            return Collections.emptyList();
        }

        List<ViewItem> data = new ArrayList<>();
//        if (showFilters) {
//            data.add(new FilterView.FilterType());
//        }
//
        if (lastUpdate != null) {
            data.add(new TimestampViewItem(lastUpdate));
        }

        if(!other.isEmpty()) {
            data.add(DEPARTURES_HEADER_DEPARTURES);
            data.addAll(other);
        }

        if(showAll && !more.isEmpty()) {
            data.addAll(more);
        } else if(!showAll && !more.isEmpty()) {
            data.add(new ShowMoreViewItem());
        }

        return data;
    }

    private void addOtherDepartures(List<Object> data) {
        data.add(DEPARTURES_HEADER_OTHERS);
        data.addAll(getOther(departures));
        if(haveMore) {
        }

    }

}
