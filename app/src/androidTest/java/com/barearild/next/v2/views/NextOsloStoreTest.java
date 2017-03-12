package com.barearild.next.v2.views;

import android.os.Build;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.place.Stop;
import com.barearild.next.v2.reisrest.requests.Requests;
import com.barearild.next.v2.views.departures.items.DepartureListItem;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.barearild.next.v2.reisrest.place.Stop.Builder.stop;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NextOsloStoreTest {

    public NextOsloStoreTest() {
        super();
    }

    @Test
    public void shouldGiveDataInSortedOrder() throws Exception {

        Stop groenland = stop()
                .withID(3010610)
                .withName("Grønland [T-bane]")
                .withX(598430)
                .withY(6643010)
                .withDistrict("Oslo")
                .build();

        Stop osloBussterminal = stop()
                .withID(3010619)
                .withName("Oslo Bussterminal")
                .withX(598368)
                .withY(6642882)
                .withDistrict("Oslo")
                .build();

        List<StopVisit> allDepartures = Requests.getAllDepartures(groenland);
        allDepartures.addAll(Requests.getAllDepartures(osloBussterminal));

        NextOsloStore store = new NextOsloStore();
        store.setDepartures(allDepartures);

        List<Object> data = store.getData();

        List<DepartureListItem> departures = getDepartureListItemsFrom(data);

        assertThat(departures.size()).isGreaterThan(1);

        DateTime last = null;
        for (DepartureListItem item : departures) {
            if (last != null) {
                assertThat(item.getFirstDeparture().isAfter(last)).isTrue();
            }
            last = item.getFirstDeparture();
        }

    }

    private List<DepartureListItem> getDepartureListItemsFrom(List<Object> objects) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return objects.stream().filter(o -> o instanceof DepartureListItem).map(o -> (DepartureListItem) o).collect(Collectors.toList());
        } else {
            List<DepartureListItem> departureListItems = new ArrayList<>();
            for (Object object : objects) {
                if(object instanceof DepartureListItem) {
                    departureListItems.add((DepartureListItem) object);
                }
            }

            return departureListItems;
        }
    }


}