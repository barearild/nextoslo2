package com.barearild.next.v2.views.departures.items;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.views.departures.holders.DepartureListItemHolder;
import com.barearild.next.v2.delete.StopVisitListItem;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import v2.next.barearild.com.R;

public class DepartureViewItem implements ViewItem<DepartureListItemHolder>, Parcelable {

    private static final long FORTY_FIVE_SECONDS = 45000;
    private static final long ONE_MINUTE = 60000L;
    private String lineRef;
    private String destinationName;
    private Stop stop;
    private List<StopVisit> departures;
    private Transporttype transporttype;

    private long hashcode;

    public DepartureViewItem(List<StopVisit> stopVisits) {
        final StopVisit stopVisit = stopVisits.get(0);
        this.lineRef = stopVisit.getLineRef();
        this.destinationName = stopVisit.getDestinationName();
        this.stop = stopVisit.getStop();
        this.transporttype = stopVisit.getTransportType();
        this.departures = new ArrayList<>(stopVisits);

        hashcode = stopVisit.getId().hashCode() + stopVisit.getStop().getID();
    }

    static DepartureViewItem from(Parcel source) {
        ArrayList<StopVisit> list = new ArrayList<>();
        source.readTypedList(list, StopVisit.CREATOR);
        return new DepartureViewItem(list);
    }

    public String getLineRef() {
        return lineRef;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public StopVisit getFirstDeparture() {
        return departures.get(0);
    }

    public StopVisit getSecondDeparture() {
        return departures.size() >= 2 ? departures.get(1) : null;
    }

    public Stop getStop() {
        return stop;
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

    public List<StopVisit> getDepartures() {
        return departures;
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
        return "DepartureViewItem{" +
                "lineRef='" + getLineRef() + '\'' +
                ", destinationName='" + getDestinationName() + '\'' +
                ", firstDeparture=" + getFirstDeparture() +
                ", secondDeparture=" + getSecondDeparture() +
                ", stopName='" + getStopName() + '\'' +
                ", transporttype=" + transporttype +
                '}';
    }


    public static final Creator<DepartureViewItem> CREATOR = new Creator<DepartureViewItem>() {
        @Override
        public DepartureViewItem createFromParcel(Parcel source) {
            return from(source);
        }

        @Override
        public DepartureViewItem[] newArray(int size) {
            return new DepartureViewItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(departures);

        /*
        *         this.lineRef = source.readString();
        this.destinationName = source.readString();
        this.stop = source.readParcelable(Stop.class.getClassLoader());
        this.departures = new ArrayList<>();
        source.readTypedList(this.departures, StopVisit.CREATOR);
        hashcode = getLineName().hashCode() + stop.getID();*/

    }

    public String getId() {
        return getFirstDeparture().getId();
    }

    public static String departureTimeString(DateTime expectedDepartureTime, Context context) {
        if (expectedDepartureTime == null) {
            return null;
        }
        final java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        DateTime currentTime = DateTime.now(expectedDepartureTime.getZone());

        int timeDiffInMinutes = timediffInMinutes(expectedDepartureTime.getMillis() - currentTime.getMillis());

        if (timeDiffInMinutes == 0) {
            return context.getResources().getString(R.string.departure_now);
        } else if (timeDiffInMinutes < 10) {
            return String.format(context.getResources().getString(R.string.departure_minutes), timeDiffInMinutes);
        } else {
            return timeFormat.format(expectedDepartureTime.toDate());
        }
    }

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
}
