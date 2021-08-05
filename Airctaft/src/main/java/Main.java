import com.gridnine.testing.FlightBuilder;
import com.gridnine.testing.FlightFilter;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {
        FlightFilter flightFilter = new FlightFilter(FlightBuilder.createFlights());
        out.println("Отфильтрованны вылет до текущего момента времени: ");
        flightFilter.getCurrentDepartures().forEach(out::println);
        out.println("Отфильтрованны вылеты где имеются сегменты с датой прилёта раньше даты вылета: ");
        flightFilter.getRightDeparture().forEach(out::println);
        out.println("Отфильтрованы вылеты, у которых общее время, проведённое на земле превышает два часа (время на земле" +
                " — это интервал между прилётом одного сегмента и вылетом следующего за ним): ");
        flightFilter.getDepartureWithBigInterval().forEach(out::println);
    }
}
