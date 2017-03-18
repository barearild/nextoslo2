package com.barearild.next.v2.store;

import com.barearild.next.v2.views.departures.DeparturesAdapter;
import com.barearild.next.v2.views.departures.items.ViewItem;

import java.util.Collections;
import java.util.List;

public class StopStore implements DeparturesAdapter.DepartureAdapterStore {

    @Override
    public void showAll() {

    }

    public List<ViewItem> getData() {
        return Collections.emptyList();
    }
}
