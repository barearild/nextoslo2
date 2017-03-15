package com.barearild.next.v2.favourites;


import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.delete.StopVisitListItem;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Arild on 16.11.2014.
 */
public class Favourite {

    public static final String ALL = "ALL";


    final int id;
    final Set<Integer> stopsWhereFavourite = new HashSet<Integer>();
    boolean allIsFavourite = false;

    public Favourite(String favourite) {
        String[] idAndStopsSplit = favourite.split(":::");
        id = idAndStopsSplit[0].hashCode();

        String[] stops = idAndStopsSplit[1].split(";");

        if(stops[0].equals(ALL)) {
            allIsFavourite = true;
            stopsWhereFavourite.clear();
        } else {
            for (String stop : stops) {
                stopsWhereFavourite.add(Integer.valueOf(stop));
            }
            allIsFavourite=false;
        }
    }

    public Favourite(StopVisitListItem stopvisit) {
        id = stopvisit.getId().hashCode();
        addStop(stopvisit.getStop());
    }

    public int getId() {
        return id;
    }

    public void addStop(Stop stop) {
        stopsWhereFavourite.add(stop.getID());
    }


    @Override
    public String toString() {
        return id + ":::" + stopsToString();
    }

    private String stopsToString() {
        if(allIsFavourite) {
            return ALL;
        }
        String stops = "";
        for (Integer stopId : stopsWhereFavourite) {
            stops += stopId + ";";
        }
        return stops;
    }
}
