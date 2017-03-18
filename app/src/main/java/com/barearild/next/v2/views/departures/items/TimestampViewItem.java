package com.barearild.next.v2.views.departures.items;

import android.content.Context;

import com.barearild.next.v2.NextOsloApp;
import com.barearild.next.v2.views.departures.holders.TimestampViewHolder;

import java.util.Date;

import static com.barearild.next.v2.NextOsloApp.DATE_FORMAT;
import static com.barearild.next.v2.NextOsloApp.TIME_FORMAT;

public class TimestampViewItem implements ViewItem<TimestampViewHolder>{

    String timestamp;

    public TimestampViewItem(String timestamp) {
        this.timestamp = timestamp;
    }

    public TimestampViewItem(Date timestamp) {
        this(DATE_FORMAT.format(timestamp) + " " + TIME_FORMAT.format(timestamp));
    }

    @Override
    public void onBindViewHolder(Context context, TimestampViewHolder viewHolder, int position) {
        viewHolder.timestamp.setText(timestamp);
    }
}
