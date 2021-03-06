package com.barearild.next.v2.reisrest.StopVisit;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

import v2.next.barearild.com.R;

import static com.barearild.next.v2.reisrest.StopVisit.MonitoredCall.Builder.monitoredCall;
import static com.barearild.next.v2.reisrest.StopVisit.MonitoredVehicleJourney.Builder.monitoredVehicleJourney;
import static com.barearild.next.v2.reisrest.StopVisit.MonitoredVehicleJourney.Builder.monitoredVehicleJourneyFrom;


public class StopVisit implements Comparable<StopVisit>, Parcelable {

    public static final int OCCUPANCY_WARNING_LIMIT = 80;
    private Stop stop;
    @SerializedName("MonitoredVehicleJourney")
    @Expose
    private MonitoredVehicleJourney monitoredVehicleJourney;
    @SerializedName("Extensions")
    @Expose
    private Extensions extensions;
    @SerializedName("InCongestion")
    @Expose
    private boolean inCongestion;

    public StopVisit() {
    }

    private StopVisit(Parcel in) {
        this.stop = in.readParcelable(Stop.class.getClassLoader());
        this.monitoredVehicleJourney = in.readParcelable(MonitoredVehicleJourney.class.getClassLoader());
        this.extensions = in.readParcelable(Extensions.class.getClassLoader());
    }

    public StopVisit(Builder builder) {
        this.stop = builder.stop;
        this.monitoredVehicleJourney = builder.monitoredVehicleJourney;
        this.extensions = builder.extensions;
        this.inCongestion = builder.inCongestion;
    }

    public Stop getStop() {
        return stop;
    }

//    public MonitoredVehicleJourney getMonitoredVehicleJourney() {
//        return monitoredVehicleJourney;
//    }

//    public Extensions getExtensions() {
//        return extensions;
//    }

    public DateTime getExpectedDepartureTime() {
        if(monitoredVehicleJourney != null && monitoredVehicleJourney.getMonitoredCall() != null) {
            return monitoredVehicleJourney.getMonitoredCall().getExpectedDepartureTime();
        }
        return null;
    }

    public String getLineRef() {
        if (monitoredVehicleJourney != null) {
            return monitoredVehicleJourney.getPublishedLineName();
        }

        return null;
    }

    public String getId() {
        return monitoredVehicleJourney.getPublishedLineName() + monitoredVehicleJourney.getDestinationName();
    }

    public long getHash() {
        return getId().hashCode() + stop.getID();
    }

    @Override
    public int compareTo(StopVisit other) {
        return this.monitoredVehicleJourney.getMonitoredCall().getExpectedDepartureTime()
                .compareTo(other.monitoredVehicleJourney.getMonitoredCall().getExpectedDepartureTime());
    }

    public int getOccupancyPercentage() {
        if (extensions == null || extensions.getOccupancyData() == null) {
            return 0;
        }
        return extensions.getOccupancyData().getOccupancyPercentage();
    }

    public int getLineColor() {
        if (extensions != null && extensions.getLineColour() != null && !extensions.getLineColour().trim().isEmpty()) {
            try {
                return Color.parseColor(extensions.getLineColour().trim());
            } catch (Exception e) {
                Log.e(NextOsloApp.LOG_TAG, "Error while parsing color from Ruter", e);
                //Just log the error and continue with the code below
            }
        }

        switch (monitoredVehicleJourney.getVehicleMode()) {
            case Boat:
                return R.color.boatColorPrimary;
            case Bus:
                return R.color.busColorPrimary;
            case Metro:
                return R.color.metroColorPrimary;
            case Train:
                return R.color.trainColorPrimary;
            case Tram:
                return R.color.tramColorPrimary;
            default:
                return R.color.defaultColorPrimary;
        }
    }

    public boolean isAlmostFull() {
        return getOccupancyPercentage() >= OCCUPANCY_WARNING_LIMIT;
    }

    public boolean isInCongestion() {
        return monitoredVehicleJourney.isInCongestion();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.stop, 0);
        dest.writeParcelable(this.monitoredVehicleJourney, 0);
        dest.writeParcelable(this.extensions, flags);
    }

    public static final Creator<StopVisit> CREATOR = new Creator<StopVisit>() {
        public StopVisit createFromParcel(Parcel source) {
            return new StopVisit(source);
        }

        public StopVisit[] newArray(int size) {
            return new StopVisit[size];
        }
    };

    @Override
    public String toString() {
        return "StopVisit{" +
                "monitoredVehicleJourney=" + monitoredVehicleJourney +
                ", inCongestion=" + inCongestion +
                '}';
    }

    public Transporttype getTransportType() {
        if (monitoredVehicleJourney != null && monitoredVehicleJourney.getVehicleMode() != null) {
            return monitoredVehicleJourney.getVehicleMode().transporttype();
        } else {
            return null;
        }
    }

    public String getDestinationName() {
        if(monitoredVehicleJourney != null) {
            return monitoredVehicleJourney.getDestinationName();
        } else {
            return null;
        }
    }

    public String getPublishedLineName() {
        if(monitoredVehicleJourney != null) {
            return monitoredVehicleJourney.getPublishedLineName();
        } else {
            return null;
        }
    }

    public List<Deviation> getDeviations() {
        if(extensions != null) {
            return extensions.getDeviations();
        } else {
            return null;
        }
    }

    public static class Builder {

        private Stop stop;
        private MonitoredVehicleJourney monitoredVehicleJourney;
        private Extensions extensions;
        private boolean inCongestion;

        public static Builder stopVisit() {
            return new Builder();
        }

        public static Builder fromStopVisit(StopVisit stopVisit) {
            return stopVisit().
                    withStop(stopVisit.stop)
                    .withExtensions(stopVisit.extensions)
                    .withInCongestion(stopVisit.inCongestion)
                    .withMonitoredVehicleJourney(stopVisit.monitoredVehicleJourney);
        }

        public StopVisit build() {
            return new StopVisit(this);
        }

        public Builder withStop(Stop stop) {
            this.stop = stop;
            return this;
        }

        public Builder withMonitoredVehicleJourney(MonitoredVehicleJourney monitoredVehicleJourney) {
            this.monitoredVehicleJourney = monitoredVehicleJourney;
            return this;
        }

        public Builder withMonitoredVehicleJourney(MonitoredVehicleJourney.Builder monitoredVehicleJourney) {
            this.monitoredVehicleJourney = monitoredVehicleJourney.build();
            return this;
        }

        public Builder withExtensions(Extensions extensions) {
            this.extensions = extensions;
            return this;
        }

        public Builder withInCongestion(boolean inCongestion) {
            this.inCongestion = inCongestion;
            return this;
        }

        public Builder withLine(String lineRef, String lineName) {
            return withMonitoredVehicleJourney(
                    getMonitoredVehicleJourneyBuilder()
                            .withLineRef(lineRef)
                            .withDestinationName(lineName)
            );
        }

        public Builder withDeparture(DateTime departureTime) {
            return withMonitoredVehicleJourney(
                    getMonitoredVehicleJourneyBuilder()
                            .withMonitoredCall(
                                    monitoredCall()
                                            .withExpectedDepartureTime(departureTime)
                            )
            );
        }

        private MonitoredVehicleJourney.Builder getMonitoredVehicleJourneyBuilder() {
            return monitoredVehicleJourney == null ? monitoredVehicleJourney() : monitoredVehicleJourneyFrom(monitoredVehicleJourney);
        }
    }
}
