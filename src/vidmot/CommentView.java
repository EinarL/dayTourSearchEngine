package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import vinnsla.Comment;
import vinnsla.DayTourRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentView extends AnchorPane {

    @FXML private Label userCommented;
    @FXML private Label date;
    @FXML private Label commentText;
    @FXML private Label likes;
    @FXML private ImageView likeImage;
    @FXML private ImageView starImg;
    private boolean hasLiked = false;
    private final int commentID;

    public CommentView(Comment cmt){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/comment.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.commentID = cmt.getCommentID();
        this.userCommented.setText(cmt.getUserCommented());
        this.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(cmt.getDate()));
        this.commentText.setText(cmt.getCommentText());
        this.likes.setText(Integer.toString(cmt.getLikes()));

        String ratingStr;
        float rating = cmt.getRating();
        if(rating != -1){
            if (rating % 1 == 0){
                int ratingInt = Math.round(rating);
                ratingStr = Integer.toString(ratingInt);
            }else{
                ratingStr = Float.toString(rating);
            }
            String ratingNoDots = ratingStr.replace(".","");
            this.starImg.setImage(new Image("./images/stars/" + ratingNoDots + "rating.png"));
        }else{
            this.starImg.setVisible(false);
        }




        if(DayTourRepository.hasUserLikedComment(commentID)){
            hasLiked = true;
            likeImage.setImage(new Image("./images/colored_like.png"));
        }
    }

    public void hoveringOverLikeButton(){
        likeImage.setImage(new Image("./images/colored_like.png"));
    }

    public void StoppedHoveringOverLikeButton(){
        if(!hasLiked){
            likeImage.setImage(new Image("./images/gray_like.png"));
        }
    }

    public void like() throws Exception {
        if(!hasLiked){
            hasLiked = true;
            // call database to add like
            DayTourRepository.addLike(commentID);
            likes.setText(Integer.toString(Integer.parseInt(likes.getText()) + 1));
        }
    }
}
