package com.barearild.next.v2.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import v2.next.barearild.com.R;

public class DeparturesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DEPARTURE = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_SPACE = 2;
    private static final int TYPE_TIMESTAMP = 3;
    private static final int TYPE_EMPTY = 4;

    private final List<Object> data;
    private final java.text.DateFormat dateFormat;
    private final java.text.DateFormat timeFormat;
    private final Context context;

    public DeparturesAdapter(List<Object> data, Context context) {
        super();
        this.data = data;
        this.context = context;

        setHasStableIds(true);

        dateFormat = DateFormat.getDateFormat(context);
        timeFormat = DateFormat.getTimeFormat(context);
    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case TYPE_DEPARTURE:
                return ((StopVisitListItem) getItem(position)).getId().hashCode();
            case TYPE_EMPTY:
                return TYPE_EMPTY;
            case TYPE_HEADER:
                return getItem(position).hashCode();
            case TYPE_SPACE:
                return TYPE_SPACE;
            case TYPE_TIMESTAMP:
                return TYPE_TIMESTAMP;
        }

        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
//                return new HeaderViewHolder(inflater.inflate(R.layout.departure_list_item_header, parent, false));
            case TYPE_DEPARTURE:
                View departureView = inflater.inflate(R.layout.departure_item, parent, false);
                return new DepartureListItemHolder(departureView);
            case TYPE_SPACE:
//                return new SpaceViewHolder(inflater.inflate(R.layout.departure_list_item_space, parent, false));
            case TYPE_TIMESTAMP:
                return new TimestampViewHolder(inflater.inflate(R.layout.departure_list_timestamp, parent, false));
            case TYPE_EMPTY:
//                return new EmptyViewHolder(inflater.inflate(R.layout.departure_list_empty, parent, false));
        }

        throw new IllegalArgumentException("ViewType " + viewType + " is not supportedd");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (getItemViewType(position)) {
            case TYPE_DEPARTURE:
                onBindDepartureListViewHolder((DepartureListItemHolder) viewHolder, position);
                break;
            case TYPE_TIMESTAMP:
                onBindTimestampViewHolder((TimestampViewHolder) viewHolder, position);

        }
    }

    private String timeAsString(DateTime time) {
        final java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        DateTime currentTime = DateTime.now(time.getZone());

        int timeDiffInMinutes = timediffInMinutes(time.getMillis() - currentTime.getMillis());

        if (timeDiffInMinutes == 0) {
            return context.getResources().getString(R.string.departure_now);
        } else if (timeDiffInMinutes < 10) {
            return String.format(context.getResources().getString(R.string.departure_minutes), timeDiffInMinutes);
        } else {
            return timeFormat.format(time.toDate());
        }
    }

    private static final long FORTY_FIVE_SECONDS = 45000;
    private static final long ONE_MINUTE = 60000L;

    static int timediffInMinutes(long milliseconds) {
        if (milliseconds < 0) {
            return (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        }

        if (milliseconds < FORTY_FIVE_SECONDS) {
            return 0;
        }

        if (milliseconds <= ONE_MINUTE) {
            return 1;
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes);

        if (seconds <= 30) {
            return (int) minutes;
        } else {
            return (int) (minutes + 1);
        }
    }

    private void onBindDepartureListViewHolder(DepartureListItemHolder viewHolder, int position) {
        StopVisitListItem stopVisit = (StopVisitListItem) data.get(position);

        viewHolder.firstDeparture.setText(timeAsString(stopVisit.firstDeparture().getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime()));
        viewHolder.secondDeparture.setText(timeAsString(stopVisit.secondDeparture().getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime()));

        viewHolder.destinationName.setText(stopVisit.getDestinationName());
        viewHolder.lineRef.setText(stopVisit.getLinePublishedName());
        viewHolder.stopName.setText(stopVisit.getStop().getName());

        viewHolder.setColor(stopVisit.getTransporttype());
    }

    private void onBindTimestampViewHolder(TimestampViewHolder viewHolder, int position) {
        Date timeOfLastUpdate = ((Date) data.get(position));

        viewHolder.timestamp.setText(dateFormat.format(timeOfLastUpdate) + " " + timeFormat.format(timeOfLastUpdate));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = data.get(position);
        if (item instanceof StopVisitListItem) {
            return TYPE_DEPARTURE;
        } else if (item instanceof Date) {
            return TYPE_TIMESTAMP;
        } else {
            return TYPE_HEADER;
        }
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    private class TimestampViewHolder extends RecyclerView.ViewHolder {

        TextView timestamp;

        public TimestampViewHolder(View itemView) {
            super(itemView);
            timestamp = (TextView) itemView.findViewById(R.id.departure_list_timestamp);
        }
    }
}