package com.barearild.next.v2.reisrest.StopVisit;

import java.util.ArrayList;
import java.util.Date;

public class StopVisitsResult extends ArrayList<StopVisit> {
    private final Date timeOfSearch;

    public StopVisitsResult(Date timeOfSearch) {
        this.timeOfSearch = timeOfSearch;
    }

    public Date getTimeOfSearch() {
        return timeOfSearch;
    }

    @Override
    public String toString() {
        return "{Timestamp: " + getTimeOfSearch() + ", stops: " + super.toString();
    }
}
