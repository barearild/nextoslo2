package com.barearild.next.v2.views.departures;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.reisrest.Transporttype;

import org.joda.time.DateTime;

import java.util.ArrayList;
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
    private static final int TYPE_FILTER = 5;

    private final List<Object> data;
    private final java.text.DateFormat dateFormat;
    private final java.text.DateFormat timeFormat;
    private final Context context;

    private final OnDepartureItemClickListener onDepartureItemClickListener;
    private LayoutInflater inflater;

    public DeparturesAdapter(Context context) {
        this(new ArrayList<>(), context, null);
    }

    public DeparturesAdapter(List<Object> data, Context context, OnDepartureItemClickListener onDepartureItemClickListener) {
        super();
        this.data = data;
        this.context = context;
        this.onDepartureItemClickListener = onDepartureItemClickListener;

        setHasStableIds(true);

        dateFormat = DateFormat.getDateFormat(context);
        timeFormat = DateFormat.getTimeFormat(context);

        inflater = LayoutInflater.from(context);
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
            case TYPE_FILTER:
                return TYPE_FILTER;
        }

        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.departure_list_header, parent, false));
            case TYPE_DEPARTURE:
                View departureView = inflater.inflate(R.layout.departure_item, parent, false);
                return new DepartureListItemHolder(departureView);
            case TYPE_SPACE:
//                return new SpaceViewHolder(inflater.inflate(R.layout.departure_list_item_space, parent, false));
            case TYPE_TIMESTAMP:
                return new TimestampViewHolder(inflater.inflate(R.layout.departure_list_timestamp, parent, false));
            case TYPE_FILTER:
                return new FilterViewHolder(inflater.inflate(R.layout.departure_filter, parent, false));
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
                break;
            case TYPE_HEADER:
                onBindHeaderViewHolder((HeaderViewHolder)viewHolder, position);
                break;
            case TYPE_FILTER:
                onBindFilterViewHolder((FilterViewHolder)viewHolder, position);
                break;

        }
    }

    private void onBindFilterViewHolder(FilterViewHolder viewHolder, int position) {

    }

    private void onBindDepartureListViewHolder(DepartureListItemHolder viewHolder, int position) {
        final StopVisitListItem stopVisit = (StopVisitListItem) data.get(position);

        viewHolder.firstDeparture.setText(timeAsString(StopVisitListItem.getExpectedDepartureTime(stopVisit.firstDeparture())));
        viewHolder.secondDeparture.setText(timeAsString(StopVisitListItem.getExpectedDepartureTime(stopVisit.secondDeparture())));

        viewHolder.destinationName.setText(stopVisit.getDestinationName());
        viewHolder.lineRef.setText(stopVisit.getLinePublishedName());
        viewHolder.stopName.setText(stopVisit.getStop().getName());

        viewHolder.setColor(stopVisit.getTransporttype());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDepartureItemClickListener.onItemClick(stopVisit);
            }
        });

        setupPopupMenu(viewHolder, stopVisit, position);
    }

    private void onBindTimestampViewHolder(TimestampViewHolder viewHolder, int position) {
        Date timeOfLastUpdate = ((Date) data.get(position));

        viewHolder.timestamp.setText(dateFormat.format(timeOfLastUpdate) + " " + timeFormat.format(timeOfLastUpdate));
    }

    private void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        DeparturesHeader header = (DeparturesHeader) data.get(position);

        viewHolder.headerText.setText(header.text);
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
        } else if (item instanceof DeparturesHeader) {
            return TYPE_HEADER;
        } else if(item instanceof FilterView.FilterType){
            return TYPE_FILTER;
        }
        else {
            return TYPE_HEADER;
        }
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    private String timeAsString(DateTime time) {
        if(time == null) {
            return null;
        }
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

    private void setupPopupMenu(DepartureListItemHolder viewHolder, final StopVisitListItem item, final int position) {
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.menu_departure_popup);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_add_to_favourites:
                                Log.d("MenuItem clicked", "Add to favourites");
                                onDepartureItemClickListener.addToFavourite(item);
                                break;
                            case R.id.action_delete_from_favourites:
                                Log.d("MenuItem clicked", "Remove from favourites");
                                onDepartureItemClickListener.removeFromFavourite(item);
                                break;
                            case R.id.action_show_in_map:
                                onDepartureItemClickListener.showInMap(item);
                                break;
                        }
                        return true;
                    }
                });
                MenuItem addToFavourites = popupMenu.getMenu().findItem(R.id.action_add_to_favourites);
                if (FavouritesService.isFavourite(item)) {
                    addToFavourites.setVisible(false);
                    popupMenu.getMenu().findItem(R.id.action_delete_from_favourites).setVisible(true);
                } else {
                    addToFavourites.setVisible(true);
                    popupMenu.getMenu().findItem(R.id.action_delete_from_favourites).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }



    private class TimestampViewHolder extends RecyclerView.ViewHolder {

        TextView timestamp;

        public TimestampViewHolder(View itemView) {
            super(itemView);
            timestamp = (TextView) itemView.findViewById(R.id.departure_list_timestamp);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView headerText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.departure_list_item_header);
        }
    }

    public interface OnDepartureItemClickListener {
        void onItemClick(StopVisitListItem stopVisitListItem);
        void onFilterUpdate(Transporttype transporttype, boolean isChecked);
        void addToFavourite(StopVisitListItem item);
        void removeFromFavourite(StopVisitListItem item);
        void showInMap(StopVisitListItem item);
    }

    private class FilterViewHolder extends RecyclerView.ViewHolder {

        FilterView filterView;

        public FilterViewHolder(View itemView) {
            super(itemView);

            filterView = (FilterView) itemView.findViewById(R.id.departure_filter_view);
            filterView.setOnDepartureItemClickListener(onDepartureItemClickListener);
        }
    }
}
