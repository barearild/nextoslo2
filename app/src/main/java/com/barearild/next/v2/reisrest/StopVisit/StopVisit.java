package com.barearild.next.v2.reisrest.StopVisit;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.place.Stop;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import v2.next.barearild.com.R;


public class StopVisit implements Comparable<StopVisit>, Parcelable {

    public static final Creator<StopVisit> CREATOR = new Creator<StopVisit>() {
        public StopVisit createFromParcel(Parcel source) {
            return new StopVisit(source);
        }

        public StopVisit[] newArray(int size) {
            return new StopVisit[size];
        }
    };
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

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public MonitoredVehicleJourney getMonitoredVehicleJourney() {
        return monitoredVehicleJourney;
    }

    public void setMonitoredVehicleJourney(MonitoredVehicleJourney monitoredVehicleJourney) {
        this.monitoredVehicleJourney = monitoredVehicleJourney;
    }

    public Extensions getExtensions() {
        return extensions;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public String getId() {
        return monitoredVehicleJourney.getPublishedLineName() + monitoredVehicleJourney.getDestinationName();
    }

    @Override
    public int compareTo(StopVisit other) {
        return this.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime()
                .compareTo(other.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime());
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

        switch (getMonitoredVehicleJourney().getVehicleMode()) {
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

    public boolean isAlmostFull() {
        return getOccupancyPercentage() >= OCCUPANCY_WARNING_LIMIT;
    }

    public boolean isInCongestion() {
        return getMonitoredVehicleJourney().isInCongestion();
    }

//    @Override
//    public String toString() {
//        return "{"+getId()+"}";
//    }


    @Override
    public String toString() {
        return "StopVisit{" +
                "monitoredVehicleJourney=" + monitoredVehicleJourney +
                ", inCongestion=" + inCongestion +
                '}';
    }
}
