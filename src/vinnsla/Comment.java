package vinnsla;

import java.util.Date;

/**
 * Klasi fyrir comment
 */

public class Comment {
    private int commentID;
    private String userCommented;
    private int dayTourID;
    private String commentText;
    private int likes;
    private Date date;
    private float rating; // rating er það sem userCommented gefur dagsferðinni í einkunn // rating = -1 ef það er ekki rating

    public Comment(int commentID, String userCommented, int dayTourID, String commentText, int likes, Date date, float rating){
        this.commentID = commentID;
        this.userCommented = userCommented;
        this.dayTourID = dayTourID;
        this.commentText = commentText;
        this.likes = likes;
        this.date = date;
        this.rating = rating;
    }

    public int getCommentID() { return commentID; }

    public String getUserCommented() { return userCommented; }

    public int getDayTourID() { return dayTourID; }

    public String getCommentText() { return commentText; }

    public int getLikes() { return likes; }

    public float getRating() { return rating; }

    public void addLike(){
        likes++;
    }

    public Date getDate(){
        return date;
    }




}
