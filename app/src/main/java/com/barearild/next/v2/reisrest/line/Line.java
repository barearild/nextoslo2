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

    /**
     *
     * @return
     * The ID
     */
    public Long getID() {
        return ID;
    }

    /**
     *
     * @param ID
     * The ID
     */
    public void setID(Long ID) {
        this.ID = ID;
    }

    /**
     *
     * @return
     * The Name
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param Name
     * The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     *
     * @return
     * The Transportation
     */
    public Transporttype getTransportation() {
        return Transportation;
    }

    /**
     *
     * @param Transportation
     * The Transportation
     */
    public void setTransportation(Transporttype Transportation) {
        this.Transportation = Transportation;
    }

    /**
     *
     * @return
     * The LineColour
     */
    public String getLineColour() {
        return LineColour;
    }

    /**
     *
     * @param LineColour
     * The LineColour
     */
    public void setLineColour(String LineColour) {
        this.LineColour = LineColour;
    }

    @Override
    public String toString() {
        return "Line{" +
                "Name='" + Name + '\'' +
                ", ID=" + ID +
                '}';
    }
}
