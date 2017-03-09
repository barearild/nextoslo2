package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class MonitoredCall implements Parcelable {

    @SerializedName("ExpectedDepartureTime")
    @Expose
    private DateTime expectedDepartureTime;

    public MonitoredCall() {
    }

    private MonitoredCall(Parcel in) {
        this.expectedDepartureTime = new DateTime(in.readLong());
    }

    public DateTime getExpectedDepartureTime() {
        return expectedDepartureTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.expectedDepartureTime.getMillis());
    }

    @Override
    public String toString() {
        return "{" +
                "expectedDepartureTime=" + expectedDepartureTime +
                '}';
    }

    public static final Creator<MonitoredCall> CREATOR = new Creator<MonitoredCall>() {
        public MonitoredCall createFromParcel(Parcel source) {
            return new MonitoredCall(source);
        }

        public MonitoredCall[] newArray(int size) {
            return new MonitoredCall[size];
        }
    };
}
