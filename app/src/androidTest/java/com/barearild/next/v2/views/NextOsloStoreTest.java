package com.barearild.next.v2.views;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.place.Stop;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.barearild.next.v2.reisrest.StopVisit.StopVisit.Builder.stopVisit;
import static com.barearild.next.v2.reisrest.place.Stop.Builder.stop;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NextOsloStoreTest {

    @Test
    public void shouldGiveDataInSortedOrder() throws Exception {

        Stop groenland = stop().withID(1).withName("Grønland [T-Bane]").build();
        Stop brugata = stop().withID(2).withName("Brugata").build();
        Stop jernbanetorget = stop().withID(3).withName("Jernbanetorget").build();

//        List<StopVisit> stopVisits = new ArrayList<>(Arrays.asList(
//                stopVisit().withStop(groenland).withMonitoredVehicleJourney()
//        ));
    }
}