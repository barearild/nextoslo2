package com.barearild.next.v2.reisrest;


import com.barearild.next.v2.reisrest.StopVisit.StopVisit;

import java.util.EnumSet;

import v2.next.barearild.com.R;

/**
 * NOTE: The order of the elements are important as order is used in deserialization from JSON
 */
public enum Transporttype {
    Walking(R.color.defaultColorPrimary, R.string.transport_walking, R.drawable.ic_directions_walk_white_36dp),
    AirportBus(R.color.defaultColorPrimary, R.string.transport_airportbus, R.drawable.ic_directions_bus_white_36dp),
    Bus(R.color.busColorPrimary, R.string.transport_bus, R.drawable.ic_directions_bus_white_36dp),
    Dummy(R.color.defaultColorPrimary, R.string.transport_dummy, R.drawable.ic_directions_subway_white_36dp),
    AirportTrain(R.color.trainColorPrimary, R.string.transport_airporttrain, R.drawable.ic_directions_railway_white_36dp),
    Boat(R.color.boatColorPrimary, R.string.transport_boat, R.drawable.ic_directions_boat_white_36dp),
    Train(R.color.trainColorPrimary, R.string.transport_train, R.drawable.ic_directions_railway_white_36dp),
    Tram(R.color.tramColorPrimary, R.string.transport_tram, R.drawable.ic_directions_transit_white_36dp),
    Metro(R.color.metroColorPrimary, R.string.transport_metro, R.drawable.ic_directions_subway_white_36dp),
    RegionalBus(R.color.regionalBusColorPrimary, R.string.transport_regional_bus, R.drawable.ic_directions_bus_white_36dp);

    public static final EnumSet<Transporttype> onlyRealTimeTransporttypes = EnumSet.of(Bus, Boat, Train, Tram, Metro, RegionalBus);

    private final int colorId;
    private final int textId;
    private int imageResId;

    private Transporttype(int colorId, int textId, int imageResId) {
        this.colorId = colorId;
        this.textId = textId;
        this.imageResId = imageResId;
    }

    public static Transporttype valueOf(int index) {
        return Transporttype.values()[index];
    }

    public static boolean isRegionalBus(StopVisit stopVisit) {
        Transporttype transporttype = stopVisit.getTransportType();
        String lineRef = stopVisit.getLineRef();
        return isRegionalBus(lineRef, transporttype);
    }

    public static boolean isRegionalBus(String lineRef, Transporttype transporttype) {
        if (Transporttype.Bus.equals(transporttype)) {
            try {
                Integer lineRefInt = Integer.parseInt(lineRef);
                if (lineRefInt == 67 || (lineRefInt >= 120 && lineRefInt < 1000)) {
                    return true;
                }
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        return false;
    }

    public int getColorId() {
        return colorId;
    }

    public int getTextId() {
        return textId;
    }

    public int getImageResId() {
        return imageResId;
    }

    @Override
    public String toString() {
        return name();
    }


}
