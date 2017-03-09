package com.barearild.next.v2.reisrest.StopVisit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FramedVehicleJourneyRef {
    @SerializedName("DataFrameRef")
    @Expose
    private String dataFrameRef;

    @SerializedName("DatedVehicleJourneyRef")
    @Expose
    private String datedVehicleJourneyRef;


    public String getDataFrameRef() {
        return dataFrameRef;
    }

    public String getDatedVehicleJourneyRef() {
        return datedVehicleJourneyRef;
    }
}
