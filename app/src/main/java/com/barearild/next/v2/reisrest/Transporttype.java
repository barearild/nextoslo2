package com.barearild.next.v2.reisrest;


import com.barearild.next.v2.reisrest.StopVisit.StopVisit;

import java.util.EnumSet;

import v2.next.barearild.com.R;

/**
 * NOTE: The order of the elements are important as order is used in deserialization from JSON
 */
public enum Transporttype {
    Walking(R.color.defaultColorPrimary),
    AirportBus(R.color.defaultColorPrimary),
    Bus(R.color.busColorPrimary),
    Dummy(R.color.defaultColorPrimary),
    AirportTrain(R.color.trainColorPrimary),
    Boat(R.color.boatColorPrimary),
    Train(R.color.trainColorPrimary),
    Tram(R.color.tramColorPrimary),
    Metro(R.color.metroColorPrimary),
    RegionalBus(R.color.regionalBusColorPrimary);
    public static final EnumSet<Transporttype> onlyRealTimeTransporttypes = EnumSet.of(Bus, Boat, Train, Tram, Metro, RegionalBus);
    private final int colorId;

    private Transporttype(int colorId) {
        this.colorId = colorId;
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

    @Override
    public String toString() {
        return name();
    }
}
