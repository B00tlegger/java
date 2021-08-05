import com.gridnine.testing.Flight;
import com.gridnine.testing.FlightFilter;
import com.gridnine.testing.Segment;
import junit.framework.TestCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RightDepartureTest extends TestCase {
    List<Segment> segments = new ArrayList<>();
    List<Flight> flights = new ArrayList<>();

    @Override
    protected void setUp() throws Exception {
        segments.add(new Segment(LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusHours(2)));
        segments.add(new Segment(LocalDateTime.now(), LocalDateTime.now().minusHours(1)));
        segments.add(new Segment(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4)));
        segments.add(new Segment(LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(3)));
    }

    public void testRightDepartureAllRight() {
        List<Segment> newSegments = new ArrayList<>();
        newSegments.add(segments.get(0));
        flights.add(new Flight(newSegments));
        List<Segment> newSegments2 = new ArrayList<>();
        newSegments2.add(segments.get(0));
        newSegments2.add(segments.get(2));
        flights.add(new Flight(newSegments2));

        double actual = new FlightFilter(flights).getRightDeparture().size();
        double expected = 2;
        assertEquals(expected, actual);
    }

    public void testRightDepartureOneWrong() {
        List<Segment> newSegments = new ArrayList<>();
        newSegments.add(segments.get(1));
        flights.add(new Flight(newSegments));

        List<Segment> newSegments2 = new ArrayList<>();
        newSegments2.add(segments.get(0));
        newSegments2.add(segments.get(newSegments2.size() - 1));
        flights.add(new Flight(newSegments2));

        double actual = new FlightFilter(flights).getRightDeparture().size();
        double expected = 1;
        assertEquals(expected, actual);
    }

    public void testRightDepartureAllWrong() {
        List<Segment> newSegments = new ArrayList<>();
        newSegments.add(segments.get(1));
        flights.add(new Flight(newSegments));

        List<Segment> newSegments2 = new ArrayList<>();
        newSegments2.add(segments.get(1));
        newSegments2.add(segments.get(2));
        newSegments2.add(segments.get(3));
        flights.add(new Flight(newSegments2));

        double actual = new FlightFilter(flights).getRightDeparture().size();
        double expected = 0;
        assertEquals(expected, actual);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
