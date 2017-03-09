package com.barearild.next.v2.reisrest.requests;

import android.location.Location;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.barearild.next.v2.reisrest.StopVisit.DeviationDetails;
import com.barearild.next.v2.reisrest.StopVisit.StopVisit;
import com.barearild.next.v2.reisrest.line.Line;
import com.barearild.next.v2.reisrest.place.Stop;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.barearild.next.v2.LocationTestdata.ALEXANDER_KIELLANDS_PLASS;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class RequestsTest {


    @Test
    public void retrieveStopsForLineAtLocation() throws Exception {
        List<Stop> allStopsForLine = Requests.getAllStopsForLine("54", ALEXANDER_KIELLANDS_PLASS);

        assertThat(allStopsForLine).isNotEmpty();

        for (Stop stop : allStopsForLine) {
            assertThat(stop.getWalkingDistance()).isGreaterThan(0);
        }
    }

    @Test
    public void retrieveAllDeparturesForStopAndLine() throws Exception {
        Stop groenlandTBane = new Stop.Builder()
                .withID(3010610)
                .build();

        List<StopVisit> allDepartures = Requests.getAllDepartures(groenlandTBane, "5");

        assertThat(allDepartures).isNotEmpty();

        for (StopVisit stopVisit : allDepartures) {
            assertThat(stopVisit.getStop().getID()).isEqualTo(groenlandTBane.getID());
        }
    }

    @Test
    public void retrieveClosestStopsToLocation() throws Exception {
        int maxNumberOfStops = 5;
        int maxWalkingDistance = 1500;

        List<Stop> closestStopsToLocation = Requests.getClosestStopsToLocation(ALEXANDER_KIELLANDS_PLASS, maxNumberOfStops, maxWalkingDistance);

        assertThat(closestStopsToLocation.size()).isGreaterThan(0).isLessThanOrEqualTo(5);

        for (Stop stop : closestStopsToLocation) {
            assertThat(stop.getWalkingDistance()).isGreaterThan(0);
        }
    }

    @Test
    public void retrieveAllDeparturesForStop() throws Exception {
        Stop groenlandTBane = new Stop.Builder()
                .withID(3010610)
                .build();

        List<StopVisit> allDepartures = Requests.getAllDepartures(groenlandTBane);

        assertThat(allDepartures).isNotEmpty();

        for (StopVisit stopVisit : allDepartures) {
            assertThat(stopVisit.getStop().getID()).isEqualTo(groenlandTBane.getID());
        }
    }

    @Test
    public void retrieveDeviationDetails() throws Exception {
        int deviationId = 43071;

        DeviationDetails details = Requests.getDeviationDetails(deviationId);

        assertThat(details).isNotNull();
        assertThat(details.getId()).isEqualTo(deviationId);
        assertThat(details.getLead()).isEqualTo("Buss 235 stopper ikke på Stoppesteder som ikke benyttes er Fossumbanen, Fossum terrasse, og Listuveien, inntil videre. Dette skyldes glatt føre.");

    }

    @Test
    public void retrieveLineSuggestionsForLineNumber() throws Exception {

        List<Line> linesSuggestion = Requests.getLinesSuggestion("54");

        assertThat(linesSuggestion).hasSize(2);
    }

    @Test
    public void retrieveLineSuggestionsForLineName() throws Exception {

        List<Line> linesSuggestion = Requests.getLinesSuggestion("R20X");

        assertThat(linesSuggestion).hasSize(1);
    }
}