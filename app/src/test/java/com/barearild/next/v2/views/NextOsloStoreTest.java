package com.barearild.next.v2.views;

import android.content.Context;

import com.barearild.next.v2.reisrest.DateJodaDeserializer;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.Transporttype;
import com.barearild.next.v2.reisrest.TransporttypeDeserializer;
import com.barearild.next.v2.reisrest.VehicleMode;
import com.barearild.next.v2.reisrest.VehicleModeDeserializer;
import com.barearild.next.v2.views.departures.items.DepartureViewItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.barearild.next.v2.reisrest.place.Stop.Builder.stop;
import static com.barearild.next.v2.reisrest.requests.Requests.LIST_STOP_VISIT_TYPE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NextOsloStoreTest {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateJodaDeserializer())
            .registerTypeAdapter(VehicleMode.class, new VehicleModeDeserializer())
            .registerTypeAdapter(Transporttype.class, new TransporttypeDeserializer())
            .create();

    @Mock
    Context context;

    @Test
    public void shouldGetData() throws Exception {

        InputStream is = getClass().getClassLoader().getResourceAsStream("NextOsloStoreTest.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }

        System.out.printf(total.toString());


        List<StopVisit> stopVisits = GSON.fromJson(total.toString(), LIST_STOP_VISIT_TYPE);

        List<StopVisit> stopVisitsWithStop = new ArrayList<>();
        for (StopVisit stopVisit : stopVisits) {
            stopVisitsWithStop.add(StopVisit.Builder.fromStopVisit(stopVisit)
                    .withStop(stop()
                            .withID(3010610)
                            .withName("Grønland [T-bane]")
                            .withX(598430)
                            .withY(6643010)
                            .withDistrict("Oslo")
                            .build())
                    .build()
            );
        }


        NextOsloStore store = new NextOsloStore();
        store.setDepartures(stopVisitsWithStop);

        List<Object> data = store.getData();

        System.out.println(data.toString());

        assertThat(data.isEmpty(), is(false));

        List<DepartureViewItem> departures = new ArrayList<>();
        for (Object o : data) {
            if (o instanceof DepartureViewItem) {
                departures.add((DepartureViewItem) o);
            }
        }

        assertDeparture(departures.get(0), "4", "Karlsrud", "2017-03-11T22:58:03.989+01:00", null);
        assertDeparture(departures.get(1), "4", "Vestli via Majorstuen", "2017-03-11T22:59:24.000+01:00", "2017-03-11T23:15:59.000+01:00");
        assertDeparture(departures.get(2), "4", "Bergkrystallen", "2017-03-11T23:01:14.000+01:00", "2017-03-11T23:16:00.000+01:00");
        assertDeparture(departures.get(3), "3", "Mortensrud", "2017-03-11T23:02:10.000+01:00", "2017-03-11T23:19:07.000+01:00");
        assertDeparture(departures.get(4), "5", "Vestli", "2017-03-11T23:03:00.000+01:00", "2017-03-11T23:19:53.000+01:00");
    }

    private void assertDeparture(DepartureViewItem item, String lineRef, String destinationName, String firstDeparture, String secondDeparture) {
        assertThat(lineRef + " " + destinationName, item.getLineRef(), is(equalTo(lineRef)));
        assertThat(lineRef + " " + destinationName, item.getDestinationName(), is(equalTo(destinationName)));
        assertThat(lineRef + " " + destinationName, item.getFirstDeparture().getMillis(), is(equalTo(new DateTime(firstDeparture).getMillis())));
        if (secondDeparture == null) {
            assertThat(lineRef + " " + destinationName, item.getSecondDeparture(), is(nullValue()));
        } else {
            assertThat(lineRef + " " + destinationName, item.getSecondDeparture().getMillis(), is(equalTo(new DateTime(secondDeparture).getMillis())));
        }
    }
}