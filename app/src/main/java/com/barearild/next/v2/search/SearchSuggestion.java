package com.barearild.next.v2.search;

import com.barearild.next.v2.NextOsloApp;

/**
 * Created by Arild on 17.01.2016.
 */
public class SearchSuggestion {

    public static final int LINE_SUGGESTION = 0;
    public static final int ADDRESS_SUGGESTION = 1;


    public final long id;
    public final int iconRes;
    public final String text;
    public final String text2;
    public final String intent;
    public final String query;
    public final int type;

    public SearchSuggestion(long id, int iconRes, String text, String query, String intent) {
        this(id, iconRes, text, null, query, intent);
    }

    public SearchSuggestion(long id, int iconRes, String text, String text2, String query, String intent) {
        this.id = id;
        this.iconRes = iconRes;
        this.text = text;
        this.text2 = text2 != null && !text2.isEmpty() ? text2 : null;
        this.query = query;
        this.intent = intent;

        if(intent.equals(NextOsloApp.SEARCH_LINE)) {
            type = LINE_SUGGESTION;
        } else {
            type = ADDRESS_SUGGESTION;
        }
    }

    @Override
    public String toString() {
        return "SearchSuggestion{" +
                "id=" + id +
                ", iconRes=" + iconRes +
                ", text='" + text + '\'' +
                ", intent='" + intent + '\'' +
                ", query='" + query + '\'' +
                ", type=" + type +
                '}';
    }
}
