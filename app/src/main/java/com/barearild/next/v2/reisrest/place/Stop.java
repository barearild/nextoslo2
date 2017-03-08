package com.barearild.next.v2.reisrest.place;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.barearild.next.v2.location.libs.CoordinateConversion;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Stop implements Parcelable {

    public static Creator<Stop> CREATOR = new Creator<Stop>() {
        public Stop createFromParcel(Parcel source) {
            return new Stop(source);
        }

        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };
    @Expose
    private String District;
    @Expose
    private int ID;
    @Expose
    private String Name;
    @Expose
    private int Rank;
    @Expose
    private String ShortName;
    @Expose
    private List<Stop> Stops = new ArrayList<Stop>();
    @Expose
    private int X;
    @Expose
    private int Y;
    @Expose
    private String Zone;
    @Expose
    private boolean AlightingAllowed;
    @Expose
    private Calendar ArrivalTime;
    @Expose
    private boolean BoardingAllowed;
    @Expose
    private Calendar DepartureTime;
    @Expose
    private boolean RealTimeStop;
    @Expose
    private int WalkingDistance;

    private Stop() {
    }

    private Stop(Parcel in) {
        this.District = in.readString();
        this.ID = in.readInt();
        this.Name = in.readString();
        this.Rank = in.readInt();
        this.ShortName = in.readString();
        this.Stops = new ArrayList<Stop>();
        in.readList(this.Stops, Stop.class.getClassLoader());
        this.X = in.readInt();
        this.Y = in.readInt();
        this.Zone = in.readString();
        this.AlightingAllowed = in.readByte() != 0;

        long tmpArrivalTime = in.readLong();
        if (tmpArrivalTime > -1) {
            this.ArrivalTime = Calendar.getInstance();
            this.ArrivalTime.setTimeInMillis(tmpArrivalTime);
        }
        this.BoardingAllowed = in.readByte() != 0;
        long tmpDepartureTime = in.readLong();
        if (tmpDepartureTime > -1) {
            this.DepartureTime = Calendar.getInstance();
            this.DepartureTime.setTimeInMillis(in.readLong());
        }
        this.RealTimeStop = in.readByte() != 0;
        this.WalkingDistance = in.readInt();
    }

    public Stop(Builder builder) {
        this.District = builder.District;
        this.ID = builder.ID;
        this.Name = builder.Name;
        this.Rank = builder.Rank;
        this.ShortName = builder.ShortName;
        this.Stops = builder.Stops;
        this.X = builder.X;
        this.Y = builder.Y;
        this.Zone = builder.Zone;
        this.AlightingAllowed = builder.AlightingAllowed;
        this.ArrivalTime = builder.ArrivalTime;
        this.BoardingAllowed = builder.BoardingAllowed;
        this.DepartureTime = builder.DepartureTime;
        this.RealTimeStop = builder.RealTimeStop;
        this.WalkingDistance = builder.WalkingDistance;
    }

    public String getDistrict() {
        return District;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getRank() {
        return Rank;
    }

    public String getShortName() {
        return ShortName;
    }

    public List<Stop> getStops() {
        return Stops;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public String getZone() {
        return Zone;
    }

    public boolean isAlightingAllowed() {
        return AlightingAllowed;
    }

    public Calendar getArrivalTime() {
        return ArrivalTime;
    }

    public boolean isBoardingAllowed() {
        return BoardingAllowed;
    }

    public Calendar getDepartureTime() {
        return DepartureTime;
    }

    public boolean isRealTimeStop() {
        return RealTimeStop;
    }

    public int getWalkingDistance() {
        return WalkingDistance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.District);
        dest.writeInt(this.ID);
        dest.writeString(this.Name);
        dest.writeInt(this.Rank);
        dest.writeString(this.ShortName);
        dest.writeList(this.Stops);
        dest.writeInt(this.X);
        dest.writeInt(this.Y);
        dest.writeString(this.Zone);
        dest.writeByte(AlightingAllowed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.ArrivalTime != null ? this.ArrivalTime.getTimeInMillis() : -1);
        dest.writeByte(BoardingAllowed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.DepartureTime != null ? this.DepartureTime.getTimeInMillis() : -1);
        dest.writeByte(RealTimeStop ? (byte) 1 : (byte) 0);
        dest.writeInt(this.WalkingDistance);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Stop) {
            return getID() == ((Stop) o).getID();
        }
        return false;
    }

    public static class Builder {
        private String District;
        private int ID;
        private String Name;
        private int Rank;
        private String ShortName;
        private List<Stop> Stops = new ArrayList<Stop>();
        private int X;
        private int Y;
        private String Zone;
        private boolean AlightingAllowed;
        private Calendar ArrivalTime;
        private boolean BoardingAllowed;
        private Calendar DepartureTime;
        private boolean RealTimeStop;
        private int WalkingDistance;

        public static Builder stop() {
            return new Builder();
        }

        public static Builder fromStop(Stop stop) {
            Builder builder = new Builder();
            builder.District = stop.District;
            builder.ID = stop.ID;
            builder.Name = stop.Name;
            builder.Rank = stop.Rank;
            builder.ShortName = stop.ShortName;
            builder.Stops = stop.Stops;
            builder.X = stop.X;
            builder.Y = stop.Y;
            builder.Zone = stop.Zone;
            builder.AlightingAllowed = stop.AlightingAllowed;
            builder.ArrivalTime = stop.ArrivalTime;
            builder.BoardingAllowed = stop.BoardingAllowed;
            builder.DepartureTime = stop.DepartureTime;
            builder.RealTimeStop = stop.RealTimeStop;
            builder.WalkingDistance = stop.WalkingDistance;

            return builder;
        }

        public Stop build() {
            return new Stop(this);
        }

        public Builder withDistrict(String district) {
            District = district;
            return this;
        }

        public Builder withID(int ID) {
            this.ID = ID;
            return this;
        }

        public Builder withName(String name) {
            Name = name;
            return this;
        }

        public Builder withRank(int rank) {
            Rank = rank;
            return this;
        }

        public Builder withShortName(String shortName) {
            ShortName = shortName;
            return this;
        }

        public Builder withStops(List<Stop> stops) {
            Stops = stops;
            return this;
        }

        public Builder withX(int x) {
            X = x;
            return this;
        }

        public Builder withY(int y) {
            Y = y;
            return this;
        }

        public Builder withZone(String zone) {
            Zone = zone;
            return this;
        }

        public Builder withAlightingAllowed(boolean alightingAllowed) {
            AlightingAllowed = alightingAllowed;
            return this;
        }

        public Builder withArrivalTime(Calendar arrivalTime) {
            ArrivalTime = arrivalTime;
            return this;
        }

        public Builder withBoardingAllowed(boolean boardingAllowed) {
            BoardingAllowed = boardingAllowed;
            return this;
        }

        public Builder withDepartureTime(Calendar departureTime) {
            DepartureTime = departureTime;
            return this;
        }

        public Builder withRealTimeStop(boolean realTimeStop) {
            RealTimeStop = realTimeStop;
            return this;
        }

        public Builder withWalkingDistance(int walkingDistance) {
            WalkingDistance = walkingDistance;
            return this;
        }

        public Builder withWalkingDistanceTo(Location location) {
            double[] latLonForStop = CoordinateConversion.utm2LatLon(X, Y);
            float[] result = new float[3];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), latLonForStop[0], latLonForStop[1], result);

            return withWalkingDistance((int) result[0]);
        }
    }
}
