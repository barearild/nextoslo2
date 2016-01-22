package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.search.SearchSuggestion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopVisitsResult implements Parcelable {
    private final Date timeOfSearch;
    public final List<StopVisit> stopVisits;
    public final List<SearchSuggestion> suggestions;
    public final List<Line> lines;
    public final List<StopVisit> linesNearby;
    public final List<Stop> stops;

    public StopVisitsResult(Date timeOfSearch) {
        this.timeOfSearch = timeOfSearch;
        this.stopVisits = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.linesNearby = new ArrayList<>();
        this.stops = new ArrayList<>();
    }

    public Date getTimeOfSearch() {
        return timeOfSearch;
    }

    @Override
    public String toString() {
        return "{Timestamp: " + getTimeOfSearch() + ", stops: " + stopVisits.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timeOfSearch != null ? timeOfSearch.getTime() : -1);
        dest.writeArray(this.stopVisits.toArray());
        dest.writeArray(this.suggestions.toArray());
        dest.writeArray(this.lines.toArray());
        dest.writeArray(this.linesNearby.toArray());
        dest.writeArray(this.stops.toArray());
    }

    protected StopVisitsResult(Parcel in) {
        stopVisits = new ArrayList<>();
        suggestions = new ArrayList<>();
        lines = new ArrayList<>();
        linesNearby = new ArrayList<>();
        stops = new ArrayList<>();

        long tmpTimeOfSearch = in.readLong();
        this.timeOfSearch = tmpTimeOfSearch == -1 ? null : new Date(tmpTimeOfSearch);
        this.stopVisits.addAll(in.readArrayList(StopVisit.class.getClassLoader()));
        this.suggestions.addAll(in.readArrayList(SearchSuggestion.class.getClassLoader()));
        this.lines.addAll(in.readArrayList(Line.class.getClassLoader()));
        this.linesNearby.addAll(in.readArrayList(StopVisit.class.getClassLoader()));
        this.stops.addAll(in.readArrayList(Stop.class.getClassLoader()));
    }

    public static final Parcelable.Creator<StopVisitsResult> CREATOR = new Parcelable.Creator<StopVisitsResult>() {
        public StopVisitsResult createFromParcel(Parcel source) {
            return new StopVisitsResult(source);
        }

        public StopVisitsResult[] newArray(int size) {
            return new StopVisitsResult[size];
        }
    };
}
