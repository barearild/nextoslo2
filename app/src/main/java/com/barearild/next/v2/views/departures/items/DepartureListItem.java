package com.barearild.next.v2.views.departures.items;

import android.content.Context;
import android.os.Build;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.departures.DepartureListItemHolder;
import com.barearild.next.v2.views.departures.StopVisitListItem;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DepartureListItem implements ViewItem<DepartureListItemHolder> {

    private String lineRef;
    private String destinationName;
    private Stop stop;
    private List<StopVisit> departures;
    private Transporttype transporttype;

    private long hashcode;

    public DepartureListItem(List<StopVisit> stopVisits) {
        final StopVisit stopVisit = stopVisits.get(0);
        this.lineRef = stopVisit.getLineRef();
        this.destinationName = stopVisit.getDestinationName();
        this.stop = stopVisit.getStop();
        this.transporttype = stopVisit.getTransportType();
        this.departures = new ArrayList<>(stopVisits);

        hashcode = stopVisit.getId().hashCode() + stopVisit.getStop().getID();
    }

    public String getLineRef() {
        return lineRef;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public DateTime getFirstDeparture() {
        return departures.get(0).getExpectedDepartureTime();
    }

    public DateTime getSecondDeparture() {
        return departures.size() >= 2 ? departures.get(1).getExpectedDepartureTime() : null;
    }

    public String getStopName() {
        return stop.getName();
    }

    public Transporttype getTransporttype() {
        return transporttype;
    }

    public int getX() {
        return stop.getX();
    }

    public int getY() {
        return stop.getY();
    }

    public String getLineName() {
        return lineRef + " " + destinationName;
    }

    public String getLineAndStopName() {
        return lineRef + " " + destinationName + " " + stop.getName();
    }

    @Override
    public void onBindViewHolder(Context context, DepartureListItemHolder viewHolder, int position) {
        viewHolder.firstDeparture.setText(StopVisitListItem.departureTimeString(getFirstDeparture(), context));
        viewHolder.secondDeparture.setText(StopVisitListItem.departureTimeString(getSecondDeparture(), context));

        viewHolder.destinationName.setText(getDestinationName());
        viewHolder.lineRef.setText(getLineRef());
        viewHolder.stopName.setText(getStopName());

        viewHolder.setColor(getTransporttype());

        //        viewHolder.warning.setVisibility(stopVisit.shouldShowWarningInList() ? View.VISIBLE : View.GONE);

    }

    @Override
    public String toString() {
        return "DepartureListItem{" +
                "lineRef='" + getLineRef() + '\'' +
                ", destinationName='" + getDestinationName() + '\'' +
                ", firstDeparture=" + getFirstDeparture() +
                ", secondDeparture=" + getSecondDeparture() +
                ", stopName='" + getStopName() + '\'' +
                ", transporttype=" + transporttype +
                '}';
    }
}
