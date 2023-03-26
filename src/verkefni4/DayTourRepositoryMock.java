package verkefni4;

import vinnsla.Booking;
import vinnsla.DayTour;
import vinnsla.Participant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DayTourRepositoryMock implements DayTourRepo {

    private static DayTour[] dt;
    private Booking[] bookings;

    public DayTourRepositoryMock() {
        DayTour einn = new DayTour(1,"ABC ferð","Þetta er fyrsta ferðin",new String[0],new Date(),1234,10,10,"Austurland",12,0);
        DayTour tveir = new DayTour(1,"Peninga ferð","Þetta er dýrasta ferðin",new String[0],new Date(),100000,15,15,"Vesturland",8,0);
        DayTour þrir = new DayTour(1,"Stjörnu ferð","Þetta er besta ferðin",new String[0],new Date(),5000,20,10,"Suðurland",8,5);
        DayTour fjorir = new DayTour(1,"Norður ferð","Þetta er Norðurlands ferðin",new String[0],new Date(),4000,10,10,"Norðurland",2,3);
        DayTour fimm = new DayTour(1,"Uppbókuð ferð","Þetta er uppbókuð ferð",new String[0],new Date(),1500,10,0,"Vesturland",3,0);
        dt = new DayTour[]{einn, tveir, þrir, fjorir, fimm};
        Booking fyrsta = new Booking("Kalli", 1, 5);
        Booking onnur = new Booking("Sigga", 2, 6);;
        Booking thridja = new Booking("Friðþjófur", 3, 7);;
        bookings = new Booking[]{fyrsta, onnur, thridja};
    }

    public DayTour[] getDayTours(String area, String search, String order) {
        DayTour[] filter = new DayTour[5];
        int fIndex = 0;
        for (int i = 0; i < 5; i++) {
            if ((dt[i].getLocation().equals(area) || area.equals("Allt land")) && dt[i].getTourTitle().contains(search)) {
                filter[fIndex] = dt[i];
                fIndex++;
            }
        }
        DayTour[] result = new DayTour[fIndex];
        System.arraycopy(filter, 0, result, 0, fIndex);

        if(order.equals("Verð")) {
            for(int i = 0; i < fIndex-1; i++) {
                int min_idx = i;
                for (int j = i + 1; j < fIndex; j++)
                    if (result[j].getPrice() < result[min_idx].getPrice())
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                DayTour temp = result[min_idx];
                result[min_idx] = result[i];
                result[i] = temp;
            }
        }

        if(order.equals("Einkunn")) {
            for(int i = 0; i < fIndex-1; i++) {
                int min_idx = i;
                for (int j = i + 1; j < fIndex; j++)
                    if (result[j].getRating() > result[min_idx].getRating())
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                DayTour temp = result[min_idx];
                result[min_idx] = result[i];
                result[i] = temp;
            }
        }

        if(order.equals("Stafrófsröð")) {
            for(int i = 0; i < fIndex-1; i++) {
                int min_idx = i;
                for (int j = i + 1; j < fIndex; j++)
                    if (result[j].getTourTitle().compareTo(result[min_idx].getTourTitle()) < 0)
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                DayTour temp = result[min_idx];
                result[min_idx] = result[i];
                result[i] = temp;
            }
        }
        return result;
    }

    public Booking getBooking(String user, int tourID) {
        for (Booking b : bookings) {
            if (b.getUser().equals(user) && b.getDayTourID() == tourID) return b;
        }
        return null;
    }

    public void addBooking(Booking booking) {
        List<Booking> bookingsList = new ArrayList<>(Arrays.asList(bookings));
        bookingsList.add(booking);
        bookings = bookingsList.toArray(new Booking[0]);
    }

    public void removeBooking(Booking booking) {
        List<Booking> bookingsList = new ArrayList<>(Arrays.asList(bookings));
        for (Booking b : bookingsList) {
            if (booking.getUser().equals(b.getUser()) && booking.getDayTourID() == b.getDayTourID()) {
                    bookingsList.remove(b);
                    break;
                }
            }
        bookings = bookingsList.toArray(new Booking[0]);
    }
}
