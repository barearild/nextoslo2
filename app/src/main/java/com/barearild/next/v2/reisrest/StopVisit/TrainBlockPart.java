package com.barearild.next.v2.reisrest.StopVisit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrainBlockPart {

    @SerializedName("NumberOfBlockParts")
    @Expose
    private int numberOfBlockParts;

    public int getNumberOfBlockParts() {
        return numberOfBlockParts;
    }

    public void setNumberOfBlockParts(int numberOfBlockParts) {
        this.numberOfBlockParts = numberOfBlockParts;
    }
}
