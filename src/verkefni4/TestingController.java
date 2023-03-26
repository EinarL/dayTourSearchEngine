package verkefni4;

import vinnsla.Booking;
import vinnsla.DayTour;

public class TestingController {
    DayTourRepo dayTourRepository;
    public TestingController(DayTourRepo DTR) {
        this.dayTourRepository = DTR;
    }

    public DayTour[] showDayTours(String area, String search, String order) {
        DayTour[] dayTours = null;
        try {
            dayTours = dayTourRepository.getDayTours(area, search, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayTours;
    }
    public void addBooking(Booking booking) {
        try {
            dayTourRepository.addBooking(booking);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removeBooking(Booking booking) {
        try {
            dayTourRepository.removeBooking(booking);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Booking getBooking(String user, int tourID) {
        return dayTourRepository.getBooking(user, tourID);
    }
}
