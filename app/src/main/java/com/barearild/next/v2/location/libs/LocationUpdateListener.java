package com.barearild.next.v2.location.libs;

import android.location.Location;

public interface LocationUpdateListener {

    public void startLocationUpdates();

    public void locationUpdatesStarted();

    public void locationUpdatesStopped();

    public void onLocationUpdate(Location updatedLocation);
}
