package com.barearild.next.v2.favourites;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.views.departures.StopVisitListItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FavouritesService {

    public static final int ALL_STOPS = -1;

    private static final Map<String, Set<Integer>> favourites = new HashMap<String, Set<Integer>>();
    private SharedPreferences prefs;

    public FavouritesService(Context context) {
        prefs = context.getSharedPreferences(NextOsloApp.USER_PREFERENCES, Context.MODE_PRIVATE);

        if (!prefs.getBoolean(NextOsloApp.FAVOURITES_CONVERTED_V2, false)) {
            convertFavourites();
            prefs.edit().putBoolean(NextOsloApp.FAVOURITES_CONVERTED_V2, true).apply();
        }

        loadFavouritesFromPreference();
    }

    private void convertFavourites() {
        Set<String> oldFavourites = prefs.getStringSet(NextOsloApp.FAVOURITES, new HashSet<String>());
        Map<String, Set<Integer>> newFavourites = new HashMap<String, Set<Integer>>(oldFavourites.size());
        for (String oldFavourite : oldFavourites) {
            if (oldFavourite.contains(":::")) {
                continue;
            }
            newFavourites.put(oldFavourite, new HashSet<Integer>());
            newFavourites.get(oldFavourite).add(ALL_STOPS);
        }

        saveFavouritesToPreferences(newFavourites);
    }

    private void saveFavouritesToPreferences(Map<String, Set<Integer>> newFavourites) {
        prefs.edit().putStringSet(NextOsloApp.FAVOURITES, toStringSet(newFavourites)).apply();
        loadFavouritesFromPreference();
        BackupManager.dataChanged(NextOsloApp.PACKAGE_NAME);
    }

    public void loadFavouritesFromPreference() {
        Set<String> favouritesPrefs = prefs.getStringSet(NextOsloApp.FAVOURITES, new HashSet<String>());
        favourites.clear();
        Set<String> favouritesPrefsWithoutOld = new HashSet<String>();
        for (String favouritesPref : favouritesPrefs) {
            if (favouritesPref.contains(":::")) {
                favouritesPrefsWithoutOld.add(favouritesPref);
            }
        }

        for (String favouriteString : favouritesPrefsWithoutOld) {
            if (!favouriteString.contains(":::")) {
                continue;
            }
            favourites.put(getIdFromString(favouriteString), getStopIdsFromString(favouriteString));
        }
        Log.d("nextnext", "Favourites are: " + favourites);
    }

    private Set<Integer> getStopIdsFromString(String favouriteString) {
        String stopsString = favouriteString.substring(favouriteString.lastIndexOf(":::") + 3);

        String[] splittedStops = stopsString.split(";");
        Set<Integer> stopIds = new HashSet<Integer>();
        for (String stop : splittedStops) {
            stopIds.add(Integer.valueOf(stop));
        }

        return stopIds;
    }

    public void addFavourite(StopVisitListItem stopvisit) {
        Set<Integer> stopIds = favourites.get(stopvisit.getId());
        if (stopIds == null) {
            stopIds = new HashSet<Integer>();
        }
        if (stopIds.iterator().hasNext() && stopIds.iterator().next() == ALL_STOPS) {
            stopIds.clear();
        }
        stopIds.add(stopvisit.getStop().getID());
        favourites.put(stopvisit.getId(), stopIds);
        saveFavouritesToPreferences(favourites);
    }

    public void removeFavourite(StopVisitListItem stopvisit) {
        Set<Integer> stopIds = favourites.get(stopvisit.getId());
        if (stopIds == null) {
            return;
        }

        if (stopIds.iterator().hasNext() && stopIds.iterator().next() == ALL_STOPS) {
            favourites.remove(stopvisit.getId());
        } else {
            stopIds.remove(stopvisit.getStop().getID());
            if (stopIds.isEmpty()) {
                favourites.remove(stopvisit.getId());
            } else {
                favourites.put(stopvisit.getId(), stopIds);
            }
        }
        saveFavouritesToPreferences(favourites);
    }

    private Set<String> toStringSet(Map<String, Set<Integer>> favourites) {
        Set<String> stringFavourites = new HashSet<String>(favourites.size());

        for (String key : favourites.keySet()) {
            Set<Integer> stopIds = favourites.get(key);
            String favouriteString = String.valueOf(key) + ":::";
            for (Integer stopId : stopIds) {
                favouriteString += String.valueOf(stopId) + ";";
            }
            stringFavourites.add(favouriteString);
        }

        return stringFavourites;
    }

    private String getIdFromString(String favouriteString) {
        String[] idAndStopsSplit = favouriteString.split(":::");
        return idAndStopsSplit[0];
    }

    public static boolean isFavourite(StopVisitListItem stopvisit) {
        Set<Integer> stopIds = favourites.get(stopvisit.getId());
        return stopIds != null && (stopIds.contains(ALL_STOPS) || stopIds.contains(stopvisit.getStop().getID()));
    }

    public static boolean isFavourite(StopVisit stopVisit) {
        Set<Integer> stopIds = favourites.get(stopVisit.getId());
        return stopIds != null && (stopIds.contains(ALL_STOPS) || stopIds.contains(stopVisit.getStop().getID()));
    }

    public static boolean isFavouriteEverywhere(StopVisitListItem stopVisit) {
        Set<Integer> stopIds = favourites.get(stopVisit.getId());
        return stopIds != null && stopIds.contains(ALL_STOPS);
    }

    public void addAsFavouriteEverywhere(StopVisitListItem stopvisit) {
        favourites.get(stopvisit.getId()).add(ALL_STOPS);
        saveFavouritesToPreferences(favourites);
    }

    public void removeAsFavouriteEverywhere(StopVisitListItem stopvisit) {
        favourites.get(stopvisit.getId()).remove(ALL_STOPS);
        favourites.get(stopvisit.getId()).add(stopvisit.getStop().getID());
        saveFavouritesToPreferences(favourites);
    }
}
