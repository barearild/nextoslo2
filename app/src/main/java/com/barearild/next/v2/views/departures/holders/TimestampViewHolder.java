package com.barearild.next.v2.views.departures.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import v2.next.barearild.com.R;

public class TimestampViewHolder extends RecyclerView.ViewHolder {

    public final TextView timestamp;

    public TimestampViewHolder(View itemView) {
        super(itemView);
        timestamp = (TextView) itemView.findViewById(R.id.departure_list_timestamp);
    }
}
