package com.barearild.next.v2.views.departures.items;

import android.content.Context;
import android.text.Html;

import com.barearild.next.v2.views.departures.DeparturesAdapter;
import com.barearild.next.v2.views.departures.items.ViewItem;

public class HeaderViewItem implements ViewItem<DeparturesAdapter.HeaderViewHolder> {

    public String text;

    public HeaderViewItem() {
        this.text = "";
    }

    public HeaderViewItem(String text) {
        this.text = text;
    }

    @Override
    public void onBindViewHolder(Context context, DeparturesAdapter.HeaderViewHolder viewHolder, int position) {
        viewHolder.headerText.setText(Html.fromHtml(text));
    }
}
