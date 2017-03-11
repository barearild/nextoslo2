package com.barearild.next.v2.views.departures.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public interface ViewItem<T extends RecyclerView.ViewHolder, K extends ViewItem> {

    void onBindViewHolder(Context context, T viewHolder, K data, int position);
}
