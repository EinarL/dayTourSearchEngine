package vinnsla;

/**
 * Klasi fyrir Booking
 */

public class Booking {

    private Participant participant;
    private DayTour dayTour;
    private int numPeople;
    private int price;

    public Booking(Participant participant, DayTour dayTour, int numPeople, int price){
        this.participant = participant;
        this.dayTour = dayTour;
        this.numPeople = numPeople;
        this.price = price;
    }

    public Participant getParticipant() { return participant; }

    public DayTour getDayTour() { return dayTour; }

    public int getNumPeople() { return numPeople; }

    public int getPrice() { return price; }

}
