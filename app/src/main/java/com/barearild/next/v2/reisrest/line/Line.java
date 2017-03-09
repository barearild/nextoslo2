package com.barearild.next.v2.reisrest.line;

import com.barearild.next.v2.reisrest.Transporttype;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Line {

    @SerializedName("ID")
    @Expose
    private Long ID;
    @SerializedName("Name")
    @Expose
    private String Name;
    @SerializedName("Transportation")
    @Expose
    private Transporttype Transportation;
    @SerializedName("LineColour")
    @Expose
    private String LineColour;

    public Long getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public Transporttype getTransportation() {
        return Transportation;
    }

    public String getLineColour() {
        return LineColour;
    }

    @Override
    public String toString() {
        return "Line{" +
                "ID=" + ID +
                ", Name='" + Name + '\'' +
                ", Transportation=" + Transportation +
                ", LineColour='" + LineColour + '\'' +
                '}';
    }
}
