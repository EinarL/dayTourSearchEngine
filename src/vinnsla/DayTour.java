package vinnsla;


import java.util.Date;

/**
 * Klasi fyrir day tour
 */

public class DayTour{

    private int ID;
    private String tourTitle;
    private String desc;
    private String[] images;
    private Date date;
    private int price;
    private int maxSpots;
    private int spotsLeft;
    private String location;
    private int duration;
    private float rating;

    public DayTour(int ID, String tourTitle, String desc, String[] images, Date date, int price,
                   int maxSpots, int spotsLeft, String location, int duration, float rating){
        this.ID = ID;
        this.tourTitle = tourTitle;
        this.desc = desc;
        this.images = images;
        this.date = date;
        this.price = price;
        this.maxSpots = maxSpots;
        this.spotsLeft = spotsLeft;
        this.location = location;
        this.duration = duration;
        this.rating = rating;
    }

}
