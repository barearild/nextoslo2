package com.barearild.next.v2.location.libs;

import android.location.Location;

public class UtmLocation {

    Integer easting;
    Integer northing;
    private String longZone;
    private String latZone;

    public UtmLocation() {
    }

    public UtmLocation(Location location) {
        this(location.getLatitude(), location.getLongitude());
    }

    private UtmLocation(Double latitude, Double longitude) {
        CoordinateConversion coordinateConversion = new CoordinateConversion();
        UtmLocation utmLocation = coordinateConversion.latLon2UTM(latitude, longitude);
        longZone = utmLocation.getLongZone();
        latZone = utmLocation.getLatZone();
        easting = utmLocation.getEasting();
        northing = utmLocation.getNorthing();
    }

    String getLongZone() {
        return longZone;
    }

    public void setLongZone(String longZone) {
        this.longZone = longZone;
    }

    String getLatZone() {
        return latZone;
    }

    public void setLatZone(String latZone) {
        this.latZone = latZone;
    }

    public Integer getEasting() {
        return easting;
    }

    public void setEasting(Double easting) {
        this.easting = easting.intValue();
    }

    public void setEasting(Integer easting) {
        this.easting = easting;
    }

    public Integer getNorthing() {
        return northing;
    }

    public void setNorthing(Double northing) {
        this.northing = northing.intValue();
    }

    public void setNorthing(Integer northing) {
        this.northing = northing;
    }

    @Override
    public String toString() {
        return String.format("(X=%d,Y=%d)", easting, northing);
    }
}
