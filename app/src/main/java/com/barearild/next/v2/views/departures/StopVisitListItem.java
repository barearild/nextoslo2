package com.barearild.next.v2.views.departures;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class StopVisitListItem implements Comparable<StopVisitListItem>, Parcelable {

    public static final Creator<StopVisitListItem> CREATOR = new Creator<StopVisitListItem>() {
        @Override
        public StopVisitListItem createFromParcel(Parcel source) {
            return new StopVisitListItem(source);
        }

        @Override
        public StopVisitListItem[] newArray(int size) {
            return new StopVisitListItem[size];
        }
    };
    private final String id;
    private final String lineRef;
    private final String linePublishedName;
    private final String destinationName;
    private final Stop stop;
    private final Transporttype transporttype;
    private final List<StopVisit> stopVisits;

    private StopVisitListItem(String id, String lineRef, String linePublishedName, String destinationName, Stop stop, Transporttype transporttype) {
        this.id = id;
        this.lineRef = lineRef;
        this.linePublishedName = linePublishedName;
        this.destinationName = destinationName;
        this.stop = stop;
        this.transporttype = transporttype;
        this.stopVisits = new ArrayList<>();
    }

    public StopVisitListItem(StopVisit stopVisit) {
        this(stopVisit.getId(),
                stopVisit.getMonitoredVehicleJourney().getLineRef(),
                stopVisit.getMonitoredVehicleJourney().getPublishedLineName(),
                stopVisit.getMonitoredVehicleJourney().getDestinationName(),
                stopVisit.getStop(),
                stopVisit.getMonitoredVehicleJourney().getVehicleMode().transporttype()
        );
        addStopVisit(stopVisit);
    }

    @SuppressWarnings("unchecked")
    private StopVisitListItem(Parcel in) {
        id = in.readString();
        lineRef = in.readString();
        linePublishedName = in.readString();
        destinationName = in.readString();
        stop = in.readParcelable(Stop.class.getClassLoader());
        transporttype = Transporttype.valueOf(in.readInt());
        stopVisits = new ArrayList<StopVisit>();
        in.readTypedList(stopVisits, StopVisit.CREATOR);
    }

    public void clearStopVisits() {
        this.stopVisits.clear();
    }

    public void addStopVisit(StopVisit departure) {
        this.stopVisits.add(departure);
    }

    public String getLineRef() {
        return lineRef;
    }

    public String getLinePublishedName() {
        return linePublishedName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getLineName() {
        return linePublishedName + " " + destinationName;
    }

    public Stop getStop() {
        return stop;
    }

    public String getId() {
        return id;
    }

    public Transporttype getTransporttype() {
        if (Transporttype.isRegionalBus(lineRef, transporttype)) {
            return Transporttype.RegionalBus;
        }
        return transporttype;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StopVisitListItem) {
            return getId().equals(((StopVisitListItem) other).getId());
        }

        return false;
    }

    @Override
    public int compareTo(@NonNull() StopVisitListItem another) {
        return firstDeparture().compareTo(another.firstDeparture());
    }

    public StopVisit firstDeparture() {
        if (stopVisits.size() > 0) {
            return stopVisits.get(0);
        }
        return null;
    }

    public StopVisit secondDeparture() {
        if (stopVisits.size() > 1) {
            return stopVisits.get(1);
        }
        return null;
    }

    public StopVisit thirdDeparture() {
        if (stopVisits.size() > 2) {
            return stopVisits.get(2);
        }
        return null;
    }

    public StopVisit fourthDeparture() {
        if (stopVisits.size() > 3) {
            return stopVisits.get(3);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(lineRef);
        dest.writeString(linePublishedName);
        dest.writeString(destinationName);
        dest.writeParcelable(stop, flags);
        dest.writeInt(transporttype.ordinal());
        dest.writeTypedList(stopVisits);
    }

    public boolean isDepartureInCongestion(StopVisit departure) {
        return departure != null && departure.isInCongestion();
    }

    public boolean isAlmostFull(StopVisit stopVisit) {
        return stopVisit != null && stopVisit.isAlmostFull();
    }

    public boolean isStopVisitsEmpty() {
        return stopVisits.isEmpty();
    }


    public static DateTime getExpectedDepartureTime(StopVisit stopVisit) {
        if(stopVisit != null && stopVisit.getMonitoredVehicleJourney() != null &&
                stopVisit.getMonitoredVehicleJourney().getMonitoredCall() != null
                ) {
            return stopVisit.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime();
        }

        return null;
    }
}
