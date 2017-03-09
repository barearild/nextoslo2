package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.barearild.next.v2.reisrest.VehicleMode;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MonitoredVehicleJourney implements Parcelable {

    @SerializedName("LineRef")
    @Expose
    private String lineRef;
    @SerializedName("DirectionRef")
    @Expose
    private String directionRef;
    @SerializedName("PublishedLineName")
    @Expose
    private String publishedLineName;
    @SerializedName("DirectionName")
    @Expose
    private String directionName;
    @SerializedName("DestinationRef")
    @Expose
    private int destinationRef;
    @SerializedName("DestinationName")
    @Expose
    private String destinationName;
    @SerializedName("DestinationAimedArrivalTime")
    @Expose
    private String destinationAimedArrivalTime;
    @SerializedName("Monitored")
    @Expose
    private boolean monitored;
    @SerializedName("InCongestion")
    @Expose
    private boolean inCongestion;
    @SerializedName("VehicleMode")
    @Expose
    private VehicleMode vehicleMode;
    @SerializedName("MonitoredCall")
    @Expose
    private MonitoredCall monitoredCall;

    public MonitoredVehicleJourney() {
    }

    private MonitoredVehicleJourney(Parcel in) {
        this.lineRef = in.readString();
        this.directionRef = in.readString();
        this.publishedLineName = in.readString();
        this.directionName = in.readString();
        this.destinationRef = in.readInt();
        this.destinationName = in.readString();
        this.destinationAimedArrivalTime = in.readString();
        this.monitored = in.readByte() != 0;
        this.inCongestion = in.readByte() != 0;
        int tmpVehicleMode = in.readInt();
        this.vehicleMode = tmpVehicleMode == -1 ? null : VehicleMode.values()[tmpVehicleMode];
        this.monitoredCall = in.readParcelable(MonitoredCall.class.getClassLoader());
    }

    public String getLineRef() {
        return lineRef;
    }

    public String getDirectionRef() {
        return directionRef;
    }

    public String getPublishedLineName() {
        return publishedLineName;
    }

    public String getDirectionName() {
        return directionName;
    }

    public int getDestinationRef() {
        return destinationRef;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getDestinationAimedArrivalTime() {
        return destinationAimedArrivalTime;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public boolean isInCongestion() {
        return inCongestion;
    }

    public VehicleMode getVehicleMode() {
        return vehicleMode;
    }

    public MonitoredCall getMonitoredCall() {
        return monitoredCall;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lineRef);
        dest.writeString(this.directionRef);
        dest.writeString(this.publishedLineName);
        dest.writeString(this.directionName);
        dest.writeInt(this.destinationRef);
        dest.writeString(this.destinationName);
        dest.writeString(this.destinationAimedArrivalTime);
        dest.writeByte(monitored ? (byte) 1 : (byte) 0);
        dest.writeByte(inCongestion ? (byte) 1 : (byte) 0);
        dest.writeInt(this.vehicleMode == null ? -1 : this.vehicleMode.ordinal());
        dest.writeParcelable(this.monitoredCall, flags);
    }

    public static final Creator<MonitoredVehicleJourney> CREATOR = new Creator<MonitoredVehicleJourney>() {
        public MonitoredVehicleJourney createFromParcel(Parcel source) {
            return new MonitoredVehicleJourney(source);
        }

        public MonitoredVehicleJourney[] newArray(int size) {
            return new MonitoredVehicleJourney[size];
        }
    };

    @Override
    public String toString() {
        return "{" +
                "publishedLineName='" + publishedLineName + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", inCongestion=" + inCongestion +
                ", monitoredCall=" + monitoredCall +
                '}';
    }
}
