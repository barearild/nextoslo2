package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class StopVisitsResult extends ArrayList<StopVisit> implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timeOfSearch != null ? timeOfSearch.getTime() : -1);
        dest.writeArray(this.toArray());
    }

    protected StopVisitsResult(Parcel in) {
        long tmpTimeOfSearch = in.readLong();
        this.timeOfSearch = tmpTimeOfSearch == -1 ? null : new Date(tmpTimeOfSearch);
        this.addAll(in.readArrayList(StopVisit.class.getClassLoader()));
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
