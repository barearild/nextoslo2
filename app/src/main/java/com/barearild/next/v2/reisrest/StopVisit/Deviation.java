
package com.barearild.next.v2.reisrest.StopVisit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Deviation implements Parcelable {


    public static Creator<Deviation> CREATOR = new Creator<Deviation>() {
        public Deviation createFromParcel(Parcel source) {
            return new Deviation(source);
        }

        public Deviation[] newArray(int size) {
            return new Deviation[size];
        }
    };


    @Expose
    private String Header;
    @Expose
    private int ID;

    public Deviation() {
    }

    public Deviation(String header) {
        this.Header = header;
        this.ID = -1;
    }

    private Deviation(Parcel in) {
        this.Header = in.readString();
        this.ID = in.readInt();
    }

    public String getHeader() {
        return Header;
    }

    public int getID() {
        return ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Header);
        dest.writeInt(this.ID);
    }
}
