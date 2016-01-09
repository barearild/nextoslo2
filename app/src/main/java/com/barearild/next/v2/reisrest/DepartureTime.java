package com.barearild.next.v2.reisrest;

import android.content.Context;
import android.text.format.DateFormat;

import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;

import java.util.concurrent.TimeUnit;

import v2.next.barearild.com.R;

public class DepartureTime extends BaseDateTime {

    private static final long FORTY_FIVE_SECONDS = 45000;
    private static final long ONE_MINUTE = 60000L;

    public DepartureTime(DateTime dateTime) {
        super(dateTime.getMillis(), dateTime.getZone());
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

    public String toString(Context context) {
        final java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        DateTime currentTime = DateTime.now(getZone());

        int timeDiffInMinutes = timediffInMinutes(getMillis() - currentTime.getMillis());

        if (timeDiffInMinutes == 0) {
            return context.getResources().getString(R.string.departure_now);
        } else if (timeDiffInMinutes < 10) {
            return String.format(context.getResources().getString(R.string.departure_minutes), timeDiffInMinutes);
        } else {
            return timeFormat.format(toDate());
        }
    }
}
