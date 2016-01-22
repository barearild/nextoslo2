package com.barearild.next.v2.search;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.reisrest.place.Stop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import v2.next.barearild.com.R;

public class SearchSuggestionProvider extends ContentProvider {

    public static final String GOOGLE_API_KEY = "AIzaSyC0XTqTESWXZSYmjset6oXOsY9BmGeTrso";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    public static final String[] COLUMNS = {
            "_id", // must include this column
            SearchManager.SUGGEST_COLUMN_ICON_1,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_QUERY,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID

    };

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selectionClause, String[] selectionArgs, String sortOrder) {

        String searchString = uri.getLastPathSegment();

        return getSuggestions(searchString);
    }

    @NonNull
    public Cursor getSuggestions(String searchString) {
        MatrixCursor matrixCursor = new MatrixCursor(COLUMNS);

        JSONArray addressSuggestions = getAddressSuggestions(searchString);
        if (addressSuggestions != null) {
            try {
                for (int i = 0; i < addressSuggestions.length(); i++) {
                    matrixCursor.addRow(createAddressSuggestionRow(i, addressSuggestions.getJSONObject(i).getString("description")));
                }
            } catch (JSONException e) {
                Log.e(NextOsloApp.LOG_TAG, e.getMessage(), e);
            }
        }

        return matrixCursor;
    }


    private JSONArray getAddressSuggestions(String searchString) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            URL url = new URL(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?sensor=false&key=" + GOOGLE_API_KEY + "&components=country:no" + "&input=" + URLEncoder.encode(searchString, "utf8"));

            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (IOException e) {
            Log.e(NextOsloApp.LOG_TAG, e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
            return jsonObject.getJSONArray("predictions");

        } catch (JSONException e) {
            Log.e(NextOsloApp.LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    private Object[] createStopSuggestion(Stop stop) {
        return new Object[]{
                stop.getID(),
                R.drawable.ic_action_place,
                stop.getName(),
                stop.getDistrict(),
                stop.getID(),
                NextOsloApp.SEARCH_STOP,
                SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT
        };
    }

    private Object[] createLineSuggestionRow(long lineId, String lineName, int iconRes) {
        return new Object[]{
                lineId,
                iconRes,
                lineName,
                "",
                lineId,
                NextOsloApp.SEARCH_LINE,
                SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT
        };
    }

    private Object[] createAddressSuggestionRow(long id, String text1) {
        return new Object[]{
                id,
                R.drawable.ic_search_white_36dp,
                text1,
                "",
                text1,
                NextOsloApp.SEARCH_ADDRESS,
                SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT
        };
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
