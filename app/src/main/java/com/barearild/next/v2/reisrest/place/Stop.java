package com.barearild.next.v2.reisrest.place;

import android.os.Parcel;
import android.os.Parcelable;

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
//    @Expose
//    private Transporttype Type;
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
//    @Expose
//    private List<Deviation> Deviations = new ArrayList<Deviation>();
//    @Expose
//    private List<Line> Lines = new ArrayList<Line>();
    @Expose
    private boolean RealTimeStop;
//    @Expose
//    private List<StopPoint> StopPoints = new ArrayList<StopPoint>();
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
//        this.Type = Transporttype.valueOf(in.readInt());
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
//        this.Deviations = new ArrayList<Deviation>();
//        in.readList(this.Deviations, Deviation.class.getClassLoader());
//        this.Lines = new ArrayList<Line>();
//        in.readList(this.Lines, Line.class.getClassLoader());
        this.RealTimeStop = in.readByte() != 0;
//        this.StopPoints = new ArrayList<StopPoint>();
//        in.readList(this.StopPoints, StopPoint.class.getClassLoader());
        this.WalkingDistance = in.readInt();
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String District) {
        this.District = District;
    }

    public Stop withDistrict(String District) {
        this.District = District;
        return this;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Stop withID(int ID) {
        this.ID = ID;
        return this;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Stop withName(String Name) {
        this.Name = Name;
        return this;
    }

    public int getRank() {
        return Rank;
    }

    public void setRank(int Rank) {
        this.Rank = Rank;
    }

    public Stop withRank(int Rank) {
        this.Rank = Rank;
        return this;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    public Stop withShortName(String ShortName) {
        this.ShortName = ShortName;
        return this;
    }

    public List<Stop> getStops() {
        return Stops;
    }

    public void setStops(List<Stop> Stops) {
        this.Stops = Stops;
    }

//    public Transporttype getType() {
//        return Type;
//    }

//    public void setType(Transporttype Type) {
//        this.Type = Type;
//    }

//    public Stop withType(int typeIndex) {
//        this.Type = Transporttype.valueOf(typeIndex);
//        return this;
//    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public Stop withX(int X) {
        this.X = X;
        return this;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public Stop withY(int Y) {
        this.Y = Y;
        return this;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String Zone) {
        this.Zone = Zone;
    }

    public Stop withZone(String Zone) {
        this.Zone = Zone;
        return this;
    }

    public boolean isAlightingAllowed() {
        return AlightingAllowed;
    }

    public void setAlightingAllowed(boolean AlightingAllowed) {
        this.AlightingAllowed = AlightingAllowed;
    }

    public Stop withAlightingAllowed(boolean AlightingAllowed) {
        this.AlightingAllowed = AlightingAllowed;
        return this;
    }

    public Calendar getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(Calendar ArrivalTime) {
        this.ArrivalTime = ArrivalTime;
    }

//    public Stop withArrivalTime(String ArrivalTime) {
//        this.ArrivalTime = DateDeserializer.deserializer(ArrivalTime);
//        return this;
//    }

    public boolean isBoardingAllowed() {
        return BoardingAllowed;
    }

    public void setBoardingAllowed(boolean BoardingAllowed) {
        this.BoardingAllowed = BoardingAllowed;
    }

    public Stop withBoardingAllowed(boolean BoardingAllowed) {
        this.BoardingAllowed = BoardingAllowed;
        return this;
    }

    public Calendar getDepartureTime() {
        return DepartureTime;
    }

    public void setDepartureTime(Calendar DepartureTime) {
        this.DepartureTime = DepartureTime;
    }

//    public Stop withDepartureTime(String DepartureTime) {
//        this.DepartureTime = DateDeserializer.deserializer(DepartureTime);
//        return this;
//    }

//    public List<Deviation> getDeviations() {
//        return Deviations;
//    }

//    public void setDeviations(List<Deviation> Deviations) {
//        this.Deviations = Deviations;
//    }

//    public Stop withDeviations(List<Deviation> Deviations) {
//        this.Deviations = Deviations;
//        return this;
//    }

//    public List<Line> getLines() {
//        return Lines;
//    }

//    public void setLines(List<Line> Lines) {
//        this.Lines = Lines;
//    }

//    public Stop withLines(List<Line> Lines) {
//        this.Lines = Lines;
//        return this;
//    }

    public boolean isRealTimeStop() {
        return RealTimeStop;
    }

    public void setRealTimeStop(boolean RealTimeStop) {
        this.RealTimeStop = RealTimeStop;
    }

    public Stop withRealTimeStop(boolean RealTimeStop) {
        this.RealTimeStop = RealTimeStop;
        return this;
    }

//    public List<StopPoint> getStopPoints() {
//        return StopPoints;
//    }

//    public void setStopPoints(List<StopPoint> StopPoints) {
//        this.StopPoints = StopPoints;
//    }

//    public Stop withStopPoint(StopPoint StopPoint) {
//        this.StopPoints.add(StopPoint);
//        return this;
//    }

    public int getWalkingDistance() {
        return WalkingDistance;
    }

    public void setWalkingDistance(int WalkingDistance) {
        this.WalkingDistance = WalkingDistance;
    }

    public Stop withWalkingDistance(int WalkingDistance) {
        this.WalkingDistance = WalkingDistance;
        return this;
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
//        dest.writeInt(this.Type.ordinal());
        dest.writeInt(this.X);
        dest.writeInt(this.Y);
        dest.writeString(this.Zone);
        dest.writeByte(AlightingAllowed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.ArrivalTime != null ? this.ArrivalTime.getTimeInMillis() : -1);
        dest.writeByte(BoardingAllowed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.DepartureTime != null ? this.DepartureTime.getTimeInMillis() : -1);
//        dest.writeList(this.Deviations);
//        dest.writeList(this.Lines);
        dest.writeByte(RealTimeStop ? (byte) 1 : (byte) 0);
//        dest.writeList(this.StopPoints);
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
}
