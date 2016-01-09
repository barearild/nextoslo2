package com.barearild.next.v2;

import android.content.Context;

import com.barearild.next.v2.reisrest.Transporttype;

import v2.next.barearild.com.R;

public class LineColorService {

    public static int lineColor(Context context, Transporttype transporttype) {
        return context.getResources().getColor(getResourceId(transporttype));
    }

    private static int getResourceId(Transporttype transporttype) {
        switch (transporttype) {
            case Boat:
                return R.color.boatColorPrimary;
            case Bus:
                return R.color.busColorPrimary;
            case RegionalBus:
                return R.color.regionalBusColorPrimary;
            case Metro:
                return R.color.metroColorPrimary;
            case Train:
                return R.color.trainColorPrimary;
            case Tram:
                return R.color.tramColorPrimary;
            default:
                return R.color.defaultColorPrimary;
        }
    }
}
