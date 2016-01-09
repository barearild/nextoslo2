package com.barearild.next.v2.reisrest;


import android.graphics.drawable.Drawable;

/**
 * NOTE: The order of the elements are important as order is used in deserialization from JSON
 */
public enum VehicleMode {
    Bus(Transporttype.Bus),
    Boat(Transporttype.Boat),
    Train(Transporttype.Train),
    Tram(Transporttype.Tram),
    Metro(Transporttype.Metro),;

    private final Transporttype transporttype;

    private VehicleMode(Transporttype transporttype) {
        this.transporttype = transporttype;
    }

    public Transporttype transporttype() {
        return transporttype;
    }

    public Drawable getProgressDrawable() {
        return null;
    }


}
