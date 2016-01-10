package com.barearild.next.v2.reisrest;


import com.barearild.next.v2.reisrest.StopVisit.StopVisit;

import java.util.EnumSet;

import v2.next.barearild.com.R;

/**
 * NOTE: The order of the elements are important as order is used in deserialization from JSON
 */
public enum Transporttype {
    Walking(R.color.defaultColorPrimary, R.string.transport_walking),
    AirportBus(R.color.defaultColorPrimary, R.string.transport_airportbus),
    Bus(R.color.busColorPrimary, R.string.transport_bus),
    Dummy(R.color.defaultColorPrimary, R.string.transport_dummy),
    AirportTrain(R.color.trainColorPrimary, R.string.transport_airporttrain),
    Boat(R.color.boatColorPrimary, R.string.transport_boat),
    Train(R.color.trainColorPrimary, R.string.transport_train),
    Tram(R.color.tramColorPrimary, R.string.transport_tram),
    Metro(R.color.metroColorPrimary, R.string.transport_metro),
    RegionalBus(R.color.regionalBusColorPrimary, R.string.transport_regional_bus);

    public static final EnumSet<Transporttype> onlyRealTimeTransporttypes = EnumSet.of(Bus, Boat, Train, Tram, Metro, RegionalBus);

    private final int colorId;
    private final int textId;

    private Transporttype(int colorId, int textId) {
        this.colorId = colorId;
        this.textId = textId;
    }

    public static Transporttype valueOf(int index) {
        return Transporttype.values()[index];
    }

    public static boolean isRegionalBus(StopVisit stopVisit) {
        Transporttype transporttype = stopVisit.getMonitoredVehicleJourney().getVehicleMode().transporttype();
        String lineRef = stopVisit.getMonitoredVehicleJourney().getLineRef();
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

    @Override
    public String toString() {
        return name();
    }

}
