package com.barearild.next.v2.views.departures.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.views.departures.DepartureListItemHolder;
import com.barearild.next.v2.views.departures.DeparturesRecyclerView;
import com.barearild.next.v2.views.departures.StopVisitListItem;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class DepartureListItem implements ViewItem<DepartureListItemHolder, DepartureListItem> {

    private String lineRef;
    private String destinationName;
    private DateTime firstDeparture;
    private DateTime secondDeparture;
    private String stopName;
    private Transporttype transporttype;

    private long hashcode;

    public DepartureListItem(List<StopVisit> stopVisits) {
        final StopVisit stopVisit = stopVisits.get(0);
        this.lineRef = getLineRef(stopVisit);
        this.destinationName = getDestinationName(stopVisit);
        this.stopName = getStopName(stopVisit);
        this.transporttype = getTransportType(stopVisit);
        this.firstDeparture = getFirstExectedDepartureTime(stopVisits);
        this.secondDeparture = getSecondExectedDepartureTime(stopVisits);

        hashcode = stopVisit.getId().hashCode() + stopVisit.getStop().getID();
    }

    public String getLineRef() {
        return lineRef;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public DateTime getFirstDeparture() {
        return firstDeparture;
    }

    public DateTime getSecondDeparture() {
        return secondDeparture;
    }

    public String getStopName() {
        return stopName;
    }

    public Transporttype getTransporttype() {
        return transporttype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DepartureListItem that = (DepartureListItem) o;

        if (hashcode != that.hashcode) return false;
        if (!lineRef.equals(that.lineRef)) return false;
        if (!destinationName.equals(that.destinationName)) return false;
        if (firstDeparture != null ? !firstDeparture.equals(that.firstDeparture) : that.firstDeparture != null)
            return false;
        if (secondDeparture != null ? !secondDeparture.equals(that.secondDeparture) : that.secondDeparture != null)
            return false;
        if (!stopName.equals(that.stopName)) return false;
        return transporttype == that.transporttype;

    }

    @Override
    public int hashCode() {
        int result = lineRef.hashCode();
        result = 31 * result + destinationName.hashCode();
        result = 31 * result + (firstDeparture != null ? firstDeparture.hashCode() : 0);
        result = 31 * result + (secondDeparture != null ? secondDeparture.hashCode() : 0);
        result = 31 * result + stopName.hashCode();
        result = 31 * result + transporttype.hashCode();
        result = 31 * result + (int) (hashcode ^ (hashcode >>> 32));
        return result;
    }

    private static String getDestinationName(StopVisit stopVisit) {
        if (haveMonitoredVehicleJourney(stopVisit)) {
            return stopVisit.getMonitoredVehicleJourney().getDestinationName();
        }

        return null;
    }

    private static String getStopName(StopVisit stopVisit) {
        if (stopVisit != null && stopVisit.getStop() != null) {
            return stopVisit.getStop().getName();
        }

        return null;
    }

    private static Transporttype getTransportType(StopVisit stopVisit) {
        if (haveMonitoredVehicleJourney(stopVisit) && stopVisit.getMonitoredVehicleJourney().getVehicleMode() != null) {
            return stopVisit.getMonitoredVehicleJourney().getVehicleMode().transporttype();
        }

        return null;
    }

    private static String getLineRef(StopVisit stopVisit) {
        if (haveMonitoredVehicleJourney(stopVisit)) {
            return stopVisit.getMonitoredVehicleJourney().getPublishedLineName();
        }
        return null;
    }

    private static DateTime getFirstExectedDepartureTime(List<StopVisit> stopVisits) {
        return getExpectedDepartureTime(stopVisits.get(0));
    }

    private static DateTime getSecondExectedDepartureTime(List<StopVisit> stopVisits) {
        return stopVisits.size() >= 2 ? getExpectedDepartureTime(stopVisits.get(1)) : null;
    }

    private static DateTime getExpectedDepartureTime(StopVisit stopVisit) {
        if (haveMonitoredVehicleJourney(stopVisit) && stopVisit.getMonitoredVehicleJourney().getMonitoredCall() != null
                ) {
            return stopVisit.getMonitoredVehicleJourney().getMonitoredCall().getExpectedDepartureTime();
        }

        return null;
    }

    private static boolean haveMonitoredVehicleJourney(StopVisit stopVisit) {
        return stopVisit != null && stopVisit.getMonitoredVehicleJourney() != null;
    }

    @Override
    public void onBindViewHolder(Context context, DepartureListItemHolder viewHolder, DepartureListItem item, int position) {
        viewHolder.firstDeparture.setText(StopVisitListItem.departureTimeString(item.getFirstDeparture(), context));
        viewHolder.secondDeparture.setText(StopVisitListItem.departureTimeString(item.getSecondDeparture(), context));

        viewHolder.destinationName.setText(item.getDestinationName());
        viewHolder.lineRef.setText(item.getLineRef());
        viewHolder.stopName.setText(item.getStopName());

        viewHolder.setColor(item.getTransporttype());

        //        viewHolder.warning.setVisibility(stopVisit.shouldShowWarningInList() ? View.VISIBLE : View.GONE);

    }

    @Override
    public String toString() {
        return "DepartureListItem{" +
                "lineRef='" + lineRef + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", firstDeparture=" + firstDeparture +
                ", secondDeparture=" + secondDeparture +
                ", stopName='" + stopName + '\'' +
                ", transporttype=" + transporttype +
                '}';
    }
}
