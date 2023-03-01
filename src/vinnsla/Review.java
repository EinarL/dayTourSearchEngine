package vinnsla;

/**
 * Klasi fyrir review
 */

public class Review {

    private String tourName;
    private DayTour dayTour;
    private String review;
    private double stars;

    public Review(String tourName, DayTour dayTour, String review, double stars){
        this.tourName = tourName;
        this.dayTour = dayTour;
        this.review = review;
        this.stars = stars;
    }

    public String getTourName() { return tourName; }

    public DayTour getDayTour() { return dayTour; }

    public String getReview() { return review; }

    public double getStars() { return stars; }

    public void updateStars(Double updatedStars){
        stars = updatedStars;
    }




}
