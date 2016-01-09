package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OccupancyData implements Parcelable {

    public static final Creator<OccupancyData> CREATOR = new Creator<OccupancyData>() {
        public OccupancyData createFromParcel(Parcel source) {
            return new OccupancyData(source);
        }

        public OccupancyData[] newArray(int size) {
            return new OccupancyData[size];
        }
    };
    @SerializedName("OccupancyAvailable")
    @Expose
    private boolean occupancyAvailable;
    @SerializedName("OccupancyPercentage")
    @Expose
    private int occupancyPercentage;

    public OccupancyData() {
    }

    private OccupancyData(Parcel in) {
        this.occupancyAvailable = in.readByte() != 0;
        this.occupancyPercentage = in.readInt();
    }

    public boolean isOccupancyAvailable() {
        return occupancyAvailable;
    }

    public void setOccupancyAvailable(boolean occupancyAvailable) {
        this.occupancyAvailable = occupancyAvailable;
    }

    public int getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(int occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(occupancyAvailable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.occupancyPercentage);
    }
}
