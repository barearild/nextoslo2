package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class MonitoredCall implements Parcelable {

    private DateTime ExpectedDepartureTime;

    public MonitoredCall() {
    }

    private MonitoredCall(Parcel in) {
        this.ExpectedDepartureTime = new DateTime(in.readLong());
    }

    public DateTime getExpectedDepartureTime() {
        return ExpectedDepartureTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ExpectedDepartureTime.getMillis());
    }

    @Override
    public String toString() {
        return "{" +
                "ExpectedDepartureTime=" + ExpectedDepartureTime +
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
