package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightFilter {
    List<Flight> flights;

    public FlightFilter(List<Flight> flights) {
        this.flights = flights;
    }

    public List<Flight> getCurrentDepartures() {
        List<Flight> currentDepartures = new ArrayList<>();
        for (Flight flight : flights) {
            int count = 0;
            for (Segment segment : flight.getSegments()) {
                if (LocalDateTime.now().equals(segment.getDepartureDate()) || LocalDateTime.now().isBefore(segment.getDepartureDate())) {
                    count++;
                }
            }
            if (count == flight.getSegments().size()) {
                currentDepartures.add(flight);
            }
        }
        return currentDepartures;
    }

    public List<Flight> getRightDeparture() {
        List<Flight> rightDeparture = new ArrayList<>();
        for (Flight flight : flights) {
            int count = 0;
            for (Segment segment : flight.getSegments()) {
                if (segment.getDepartureDate().isBefore(segment.getArrivalDate())) {
                    count++;
                }
            }
            if (count == flight.getSegments().size()) {
                rightDeparture.add(flight);
            }
        }
        return rightDeparture;
    }

    public List<Flight> getDepartureWithBigInterval() {
        List<Flight> bigIntervalFlight = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.getSegments().size() > 1) {
                if (isNeedFlight(flight)) {
                    bigIntervalFlight.add(flight);
                }
            } else {
                bigIntervalFlight.add(flight);
            }
        }
        return bigIntervalFlight;
    }

    private boolean isNeedFlight(Flight flight) {
        int count = 0;
            for (int i = 1 ; i < flight.getSegments().size(); i++) {
                LocalDateTime dateOne = flight.getSegments().get(i-1).getArrivalDate();
                LocalDateTime dateTwo = flight.getSegments().get(i).getDepartureDate();
                int dateCompare = dateOne.plusHours(3).compareTo(dateTwo);
                if (dateCompare <= 0) {
                    count++;
                }
            }
        return count == flight.getSegments().size() - 1;
    }
}
