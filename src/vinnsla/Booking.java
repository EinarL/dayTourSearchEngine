package vinnsla;

/**
 * Klasi fyrir Booking
 */

public class Booking {

    //private Participant participant;
    private String user;
    //private DayTour dayTour;
    private int dayTourID;
    private int numPeople;
    //private int price;

    public Booking(String user, int dayTourID, int numPeople){
        //this.participant = participant;
        this.user = user;
        //this.dayTour = dayTour;
        this.dayTourID = dayTourID;
        this.numPeople = numPeople;
        //this.price = price;
    }

    //public Participant getParticipant() { return participant; }

    public String getUser() { return user; }

    //public DayTour getDayTour() { return dayTour; }

    public int getDayTourID() { return dayTourID; }

    public int getNumPeople() { return numPeople; }

    //public int getPrice() { return price; }

}
