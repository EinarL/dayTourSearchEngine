package vinnsla;

/**
 * Klasi fyrir review
 */

public class Comment {

    private String tourName;
    private DayTour dayTour;
    private String review;
    private float stars;

    public Comment(String tourName, DayTour dayTour, String review, float stars){
        this.tourName = tourName;
        this.dayTour = dayTour;
        this.review = review;
        this.stars = stars;
    }

    public String getTourName() { return tourName; }

    public DayTour getDayTour() { return dayTour; }

    public String getReview() { return review; }

    public float getStars() { return stars; }

    public void updateStars(float updatedStars){
        stars = updatedStars;
    }




}
