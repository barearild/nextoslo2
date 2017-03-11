package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OccupancyData implements Parcelable {

    private boolean OccupancyAvailable;
    private int OccupancyPercentage;

    public OccupancyData() {
    }

    private OccupancyData(Parcel in) {
        this.OccupancyAvailable = in.readByte() != 0;
        this.OccupancyPercentage = in.readInt();
    }

    public boolean isOccupancyAvailable() {
        return OccupancyAvailable;
    }

    public int getOccupancyPercentage() {
        return OccupancyPercentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(OccupancyAvailable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.OccupancyPercentage);
    }

    public static final Creator<OccupancyData> CREATOR = new Creator<OccupancyData>() {
        public OccupancyData createFromParcel(Parcel source) {
            return new OccupancyData(source);
        }

        public OccupancyData[] newArray(int size) {
            return new OccupancyData[size];
        }
    };
}
