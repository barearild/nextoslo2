package com.barearild.next.v2.views.departures;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.barearild.next.v2.reisrest.StopVisit.Deviation;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import v2.next.barearild.com.R;

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

    public List<Deviation> getAllWarnings(Context context) {
        List<Deviation> allWarnings = new ArrayList<>();

        StopVisit firstDeparture = firstDeparture();
        StopVisit secondDeparture = secondDeparture();
        StopVisit thirdDeparture = thirdDeparture();
        StopVisit fourthDeparture = fourthDeparture();

        if (isDepartureInCongestion(firstDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.first_departure_in_congestion)));
        }
        if(isDepartureInCongestion(secondDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.second_departure_in_congestion)));
        }
        if(isDepartureInCongestion(thirdDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.third_departure_in_congestion)));
        }
        if(isDepartureInCongestion(fourthDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.fourth_departure_in_congestion)));
        }

        if (isAlmostFull(firstDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.warning_occupancy_first_departure, firstDeparture.getOccupancyPercentage())));
        }
        if(isAlmostFull(secondDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.warning_occupancy_second_departure, secondDeparture.getOccupancyPercentage())));
        }
        if(isAlmostFull(thirdDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.warning_occupancy_third_departure, thirdDeparture.getOccupancyPercentage())));
        }
        if(isAlmostFull(fourthDeparture)) {
            allWarnings.add(new Deviation(context.getString(R.string.warning_occupancy_fourth_departure, fourthDeparture.getOccupancyPercentage())));
        }

        allWarnings.addAll(getDeviations());

        return allWarnings;
    }

    public List<Deviation> getDeviations() {
        List<Deviation> allDeviations = new ArrayList<>();
        for (StopVisit stopvisit : stopVisits) {
            if (stopvisit.getExtensions() != null && stopvisit.getExtensions().getDeviations() != null)
                allDeviations.addAll(stopvisit.getExtensions().getDeviations());
        }

        return allDeviations;
    }

    public boolean shouldShowWarningInList() {
        return getDeviations().size() > 0 ||
                isDepartureInCongestion(firstDeparture()) ||
                isDepartureInCongestion(secondDeparture()) ||
                isAlmostFull(firstDeparture()) ||
                isAlmostFull(secondDeparture());
    }


    public static DateTime getExpectedDepartureTime(StopVisit stopVisit) {
        if (stopVisit != null && stopVisit.getMonitoredVehicleJourney() != null &&
                stopVisit.getMonitoredVehicleJourney().getMonitoredCall() != null
                ) {
            return stopVisit.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime();
        }

        return null;
    }

    public static String departureTimeString(StopVisit stopVisit, Context context) {
        DateTime expectedDepartureTime = getExpectedDepartureTime(stopVisit);

        if (expectedDepartureTime == null) {
            return null;
        }
        final java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        DateTime currentTime = DateTime.now(expectedDepartureTime.getZone());

        int timeDiffInMinutes = timediffInMinutes(expectedDepartureTime.getMillis() - currentTime.getMillis());

        if (timeDiffInMinutes == 0) {
            return context.getResources().getString(R.string.departure_now);
        } else if (timeDiffInMinutes < 10) {
            return String.format(context.getResources().getString(R.string.departure_minutes), timeDiffInMinutes);
        } else {
            return timeFormat.format(expectedDepartureTime.toDate());
        }
    }

    private static final long FORTY_FIVE_SECONDS = 45000;
    private static final long ONE_MINUTE = 60000L;

    static int timediffInMinutes(long milliseconds) {
        if (milliseconds < 0) {
            return (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        }

        if (milliseconds < FORTY_FIVE_SECONDS) {
            return 0;
        }

        if (milliseconds <= ONE_MINUTE) {
            return 1;
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes);

        if (seconds <= 30) {
            return (int) minutes;
        } else {
            return (int) (minutes + 1);
        }
    }

}
