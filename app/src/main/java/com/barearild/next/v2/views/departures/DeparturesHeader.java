package com.barearild.next.v2.views.departures;

import android.content.Context;
import android.text.Html;

import com.barearild.next.v2.views.departures.items.ViewItem;

public class DeparturesHeader implements ViewItem<DeparturesAdapter.HeaderViewHolder> {

    public String text;

    public DeparturesHeader() {
        this.text = "";
    }

    public DeparturesHeader(String text) {
        this.text = text;
    }

    @Override
    public void onBindViewHolder(Context context, DeparturesAdapter.HeaderViewHolder viewHolder, int position) {
        viewHolder.headerText.setText(Html.fromHtml(text));
    }
}
