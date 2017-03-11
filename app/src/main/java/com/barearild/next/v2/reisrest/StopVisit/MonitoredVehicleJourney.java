package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.barearild.next.v2.reisrest.VehicleMode;

public class MonitoredVehicleJourney implements Parcelable {

    private String LineRef;

    private String PublishedLineName;

    private String DestinationName;

    private boolean InCongestion;

    private VehicleMode VehicleMode;

    private MonitoredCall MonitoredCall;


    public MonitoredVehicleJourney() {
    }

    private MonitoredVehicleJourney(Parcel in) {
        this.LineRef = in.readString();
        this.PublishedLineName = in.readString();
        this.DestinationName = in.readString();
        this.InCongestion = in.readByte() != 0;
        int tmpVehicleMode = in.readInt();
        this.VehicleMode = tmpVehicleMode == -1 ? null : VehicleMode.values()[tmpVehicleMode];
        this.MonitoredCall = in.readParcelable(MonitoredCall.class.getClassLoader());
    }

    public String getLineRef() {
        return LineRef;
    }

    public String getPublishedLineName() {
        return PublishedLineName;
    }

    public String getDestinationName() {
        return DestinationName;
    }

    public boolean isInCongestion() {
        return InCongestion;
    }

    public VehicleMode getVehicleMode() {
        return VehicleMode;
    }

    public MonitoredCall getMonitoredCall() {
        return MonitoredCall;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.LineRef);
        dest.writeString(this.PublishedLineName);
        dest.writeString(this.DestinationName);
        dest.writeByte(InCongestion ? (byte) 1 : (byte) 0);
        dest.writeInt(this.VehicleMode == null ? -1 : this.VehicleMode.ordinal());
        dest.writeParcelable(this.MonitoredCall, flags);
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
                "PublishedLineName='" + PublishedLineName + '\'' +
                ", DestinationName='" + DestinationName + '\'' +
                ", InCongestion=" + InCongestion +
                ", MonitoredCall=" + MonitoredCall +
                '}';
    }
}
