package com.barearild.next.v2.views.departures;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.barearild.next.v2.favourites.FavouritesService;
import com.barearild.next.v2.reisrest.Transporttype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<Object> getData() {
        return data;
    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case TYPE_DEPARTURE:
                StopVisitListItem stopVisitListItem = (StopVisitListItem) getItem(position);
                return (stopVisitListItem.getId().hashCode() + stopVisitListItem.getStop().getID());
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
                return new SpaceViewHolder(inflater.inflate(R.layout.departure_item_space, parent, false));
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
        }
    }

    private void onBindDepartureListViewHolder(DepartureListItemHolder viewHolder, int position) {
        final StopVisitListItem stopVisit = (StopVisitListItem) data.get(position);

        viewHolder.firstDeparture.setText(StopVisitListItem.departureTimeString(stopVisit.firstDeparture(), context));
        viewHolder.secondDeparture.setText(StopVisitListItem.departureTimeString(stopVisit.secondDeparture(), context));

        viewHolder.destinationName.setText(stopVisit.getDestinationName());
        viewHolder.lineRef.setText(stopVisit.getLinePublishedName());
        viewHolder.stopName.setText(stopVisit.getStop().getName());

        viewHolder.setColor(stopVisit.firstDeparture());

        viewHolder.warning.setVisibility(stopVisit.shouldShowWarningInList() ? View.VISIBLE : View.GONE);

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

        viewHolder.headerText.setText(Html.fromHtml(header.text));
//        viewHolder.headerText.setMovementMethod(LinkMovementMethod.getInstance());
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
        } else if(item instanceof SpaceItem) {
            return TYPE_SPACE;
        }
        else {
            return TYPE_HEADER;
        }
    }

    public Object getItem(int position) {
        return data.get(position);
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
            headerText.setMovementMethod(LinkMovementMethod.getInstance());
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

    private class SpaceViewHolder extends RecyclerView.ViewHolder {
        public SpaceViewHolder(View itemView) {
            super(itemView);
        }
    }
}
