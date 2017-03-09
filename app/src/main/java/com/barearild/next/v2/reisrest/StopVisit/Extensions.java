package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Extensions implements Parcelable {

    @SerializedName("OccupancyData")
    @Expose
    private OccupancyData occupancyData;
    @SerializedName("Deviations")
    @Expose
    private List<Deviation> deviations = new ArrayList<Deviation>();
    @SerializedName("LineColour")
    @Expose
    private String lineColour;

    public Extensions() {
    }

    private Extensions(Parcel in) {
        this.occupancyData = in.readParcelable(OccupancyData.class.getClassLoader());
        in.readTypedList(deviations, Deviation.CREATOR);
        this.lineColour = in.readString();
    }

    OccupancyData getOccupancyData() {
        return occupancyData;
    }

    public List<Deviation> getDeviations() {
        return deviations;
    }

    String getLineColour() {
        return "#" + lineColour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.occupancyData, flags);
        dest.writeTypedList(deviations);
        dest.writeString(this.lineColour);
    }

    public static final Creator<Extensions> CREATOR = new Creator<Extensions>() {
        public Extensions createFromParcel(Parcel source) {
            return new Extensions(source);
        }

        public Extensions[] newArray(int size) {
            return new Extensions[size];
        }
    };
}
