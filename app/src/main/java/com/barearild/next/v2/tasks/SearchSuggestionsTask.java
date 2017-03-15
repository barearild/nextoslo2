package com.barearild.next.v2.tasks;

public class SearchSuggestionsTask /*extends AsyncTask<String, Object, List<Object>> */{

/*    private final StopVisitsResult result;

    public SearchSuggestionsTask() {
        result = new StopVisitsResult(new Date());
    }

    @Override
    protected List<Object> doInBackground(String... queries) {
        final String query = queries[0];

        ExecutorService es = Executors.newFixedThreadPool(4);

        es.execute(new Runnable() {
            @Override
            public void run() {
                searchLinesNearby(query);
            }
        });
        es.execute(new Runnable() {
            @Override
            public void run() {
                searchLines(query);
            }
        });
        es.execute(new Runnable() {
            @Override
            public void run() {
                searchStops(query);
            }
        });
        es.execute(new Runnable() {
            @Override
            public void run() {
                searchSuggestions(query);
            }
        });

        es.shutdown();
        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return convertToListData(result, mIsShowingFilters);
    }


    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);

        mRecyclerView.swapAdapter(new DeparturesAdapter(convertToListData(result, mIsShowingFilters), DeparturesActivity.this, DeparturesActivity.this), false);

    }

    @Override
    protected void onPostExecute(List<Object> objects) {
        super.onPostExecute(objects);
        mode = MODE_SUGGESTIONS;
        mRecyclerView.swapAdapter(new DeparturesAdapter(objects, DeparturesActivity.this, DeparturesActivity.this), false);
        mSwipeView.setRefreshing(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSwipeView.setRefreshing(true);
    }

    private void searchStops(String query) {
        int numberOfStops = 0;
        for (Stop stop : NextOsloApp.ALL_STOPS) {
            if (stop.getName().toLowerCase().startsWith(query.toLowerCase())) {
                result.stops.add(stop);
                if (++numberOfStops > 8) {
                    break;
                }
            }
        }
        if (numberOfStops < 8) {
            for (Stop stop : NextOsloApp.ALL_STOPS) {
                if (!result.stops.contains(stop) && stop.getName().toLowerCase().contains(query.toLowerCase())) {
                    result.stops.add(stop);
                    if (++numberOfStops > 8) {
                        break;
                    }
                }
            }
        }

        publishProgress();
    }

    private void searchSuggestions(String query) {
        Log.d(LOG_TAG, "Getting line search suggestions");
        Cursor suggestions = new SearchSuggestionProvider().getSuggestions(query);

        List<Object> searchSuggestions = new ArrayList<>();

        while ((suggestions.moveToNext())) {
            long id = suggestions.getLong(suggestions.getColumnIndex("_id"));
            int iconRes = suggestions.getInt(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1));
            String text = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
            String text2 = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));
            String suggestionQuery = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY));
            String intent = suggestions.getString(suggestions.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_ACTION));

            result.suggestions.add(new SearchSuggestion(id, iconRes, text, text2, suggestionQuery, intent));

        }
        suggestions.close();
        Log.d(NextOsloApp.LOG_TAG, "Suggestions: " + searchSuggestions.toString());
        publishProgress();
    }

    private void searchLinesNearby(final String query) {
        if (mLastNearbyResults != null && !mLastNearbyResults.stopVisits.isEmpty()) {
            for (StopVisit stopvisit : mLastNearbyResults.stopVisits) {
                if (stopvisit.getMonitoredVehicleJourney().getLineRef().equals(query)) {
                    result.linesNearby.add(stopvisit);
                }
            }
        }

        publishProgress();
    }

    private void searchLines(final String query) {
        result.lines.addAll(Requests.getLinesSuggestion(query));


        publishProgress();
    }

    private static List<Object> convertToListData(StopVisitsResult result, boolean showFilters) {
        List<Object> data = new ArrayList<>();

        if (showFilters) {
            data.add(new FilterView.FilterType());
        }

        if (result == null) {
            return data;
        }

        if (result.getTimeOfSearch() != null) {
            data.add(result.getTimeOfSearch());
        }

        if (!result.linesNearby.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_LINES_NEARBY);
            data.addAll(convertToListItems(result.linesNearby));
        }

        if (!result.lines.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_LINES);
            data.addAll(result.lines);
        }

        if (!result.stops.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_STOPS);
            data.addAll(result.stops);
        }

        if (!result.suggestions.isEmpty()) {
            data.add(NextOsloApp.DEPARTURES_HEADER_ADDRESSES);
            data.addAll(result.suggestions);
        }

        if (!result.stopVisits.isEmpty()) {
            List<StopVisitListItem> favourites = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(onlyFavorites(removeTransportTypes(result.stopVisits)))));
            List<StopVisitListItem> others = orderedByFirstDeparture(convertToListItems(orderByWalkingDistance(withoutFavourites(removeTransportTypes(result.stopVisits)))));

            List<StopVisitListItem> allStopVisitList = convertToListItems(result.stopVisits);
            for (StopVisitListItem favourite : favourites) {
                StopVisitFilters.getOtherStopsForStopVisitListItem(favourite, allStopVisitList);
            }
            for (StopVisitListItem other : others) {
                StopVisitFilters.getOtherStopsForStopVisitListItem(other, allStopVisitList);
            }

            if (result.stopVisits.isEmpty()) {
                data.add(NextOsloApp.DEPARTURES_HEADER_EMPTY);
            }

            if (favourites.isEmpty()) {
                data.add(NextOsloApp.DEPARTURES_HEADER_NO_FAVOURITES);
            } else {
                data.add(NextOsloApp.DEPARTURES_HEADER_FAVOURITES);
                data.addAll(favourites);
                data.add(new SpaceViewItem());
            }

            data.add(NextOsloApp.DEPARTURES_HEADER_OTHERS);
            data.addAll(others);

        }


        return data;
    }*/
}
