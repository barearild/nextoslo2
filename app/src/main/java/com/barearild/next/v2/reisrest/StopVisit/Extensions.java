package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Extensions implements Parcelable {

    private OccupancyData OccupancyData;
    private List<Deviation> Deviations = new ArrayList<Deviation>();
    private String LineColour;

    public Extensions() {
    }

    private Extensions(Parcel in) {
        this.OccupancyData = in.readParcelable(OccupancyData.class.getClassLoader());
        in.readTypedList(Deviations, Deviation.CREATOR);
        this.LineColour = in.readString();
    }

    OccupancyData getOccupancyData() {
        return OccupancyData;
    }

    public List<Deviation> getDeviations() {
        return Deviations;
    }

    String getLineColour() {
        return "#" + LineColour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.OccupancyData, flags);
        dest.writeTypedList(Deviations);
        dest.writeString(this.LineColour);
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
