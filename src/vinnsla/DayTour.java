package vinnsla;


import javafx.scene.image.Image;

import java.util.Date;

/**
 * Klasi fyrir day tour
 */

public class DayTour{

    private int ID;
    private String tourTitle;
    private String desc;
    private Image[] images;
    private Date date;
    private int price;
    private int maxSpots;
    private int spotsLeft;
    private String location;
    private int duration;
    private float rating;

    public DayTour(int ID, String tourTitle, String desc, Image[] images, Date date, int price,
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

    public String getTourTitle(){
        return tourTitle;
     }

     public String getDesc(){
        return desc;
     }

     public int getId(){
        return ID;
    }

    public Date getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public int getMaxSpots(){ return maxSpots; }

    public int getSpotsLeft() {
        return spotsLeft;
    }

    public String getLocation() {
        return location;
    }

    public float getRating() {
        return rating;
    }

    public Image[] getImages() {
        return images;
    }

    public Image getFrontImage(){
        if (images.length > 0)
            return images[0];
        return null;
    }

    public boolean isFull(){
        return spotsLeft == 0;
    }


}
