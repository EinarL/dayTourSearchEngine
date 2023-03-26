package verkefni4;

import vinnsla.Booking;
import vinnsla.DayTour;

public interface DayTourRepo {
    abstract DayTour[] getDayTours(String area, String search, String order) throws Exception;
    abstract void addBooking(Booking booking);
    abstract Booking getBooking(String user, int tourID);
    abstract void removeBooking(Booking booking);
}
