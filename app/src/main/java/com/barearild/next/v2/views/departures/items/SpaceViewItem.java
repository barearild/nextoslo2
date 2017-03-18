package com.barearild.next.v2.views.departures.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class SpaceViewItem implements ViewItem<RecyclerView.ViewHolder>{
    private long id = 1L;

    public long getId() {
        return id;
    }

    @Override
    public void onBindViewHolder(Context context, RecyclerView.ViewHolder viewHolder, int position) {

    }
}
