package vidmot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import vinnsla.Comment;
import vinnsla.DayTour;
import vinnsla.DayTourRepository;
import vinnsla.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Controller fyrir dayTourSite.fxml
 * þessi birta er fyrir hvert instance af dayTour, það er hægt að sjá ítarlegri upplýsingar
 * um dagsferðina á þessari síðu.
 * Þessi síða birtist þegar það er ýtt á einhverja dagsferð.
 */
public class DayTourSiteController {
    @FXML private Label title;
    @FXML private Label desc;
    @FXML private Button bookButton;
    @FXML private Label price;
    @FXML private Label date;
    @FXML private Label spotsLeft;
    @FXML private Label area;
    @FXML private Label duration;
    @FXML private ImageView dtImages;
    @FXML private ImageView starImg;
    @FXML private VBox commentPane;
    @FXML private TextField ratingField;
    @FXML private CheckBox giveRatingCheckbox;
    @FXML private Label giveRatingText;
    @FXML private Button incButton;
    @FXML private Button decButton;
    @FXML private TextArea commentText;
    @FXML private StackPane stackPane;
    @FXML private AnchorPane anchorPane;
    @FXML private ImageView goBackImg;
    @FXML private Label goBackLabel;
    @FXML private ComboBox<String> commentOrder;
    private DayTour dt;
    private Image[] images;
    private int imagePointer = 0;

    public void setTourInfo(String title, Scene scene) throws Exception {
        dt = DayTourRepository.getDayTourByTitle(title);

        this.title.setText(title);
        this.desc.setText(dt.getDesc());
        this.price.setText(dt.getPrice() + "kr.");
        this.spotsLeft.setText(dt.getSpotsLeft() + "/" + dt.getMaxSpots() + " seats left");
        this.dtImages.setImage(dt.getFrontImage());
        this.date.setText("Date: " + (new SimpleDateFormat("dd/MM/yyyy").format(dt.getDate())));
        this.area.setText("Location: " + dt.getLocation());

        this.images = dt.getImages();

        ObservableList<String> order = FXCollections.observableArrayList("Aldri", "Likes");

        commentOrder.setItems(order);
        commentOrder.setValue(order.get(0));
        commentOrder.setOnAction(event -> {
            try {
                showComments();
            } catch (Exception e) {

            }
        });


        // ef notandi hefur bókað ferðina, þá getur hann ekki bókað hana aftur
        if(DayTourRepository.hasUserBookedDayTour(dt.getId())){
            bookButton.setDisable(true);
        }

        updateRating();

        // þetta er til þess að laga villu þar sem comments fóru fyrir utan VBox:
        Rectangle clip = new Rectangle(commentPane.getWidth(), commentPane.getHeight());
        clip.heightProperty().bind(commentPane.heightProperty());
        clip.widthProperty().bind(commentPane.widthProperty());
        commentPane.setClip(clip);

        // ef notandi hefur gefið þessari dagsferð einkunn áður, þá má hann það ekki aftur
        if(DayTourRepository.hasUserRatedDayTour(dt.getId())){
            userCannotRate();
        }

        // center content
        stackPane.translateXProperty()
                .bind(scene.widthProperty().subtract(stackPane.widthProperty())
                        .divide(2));

        showComments();
    }

    private void updateRating() throws Exception {
        dt = DayTourRepository.getDayTourByTitle(dt.getTourTitle());
        float rating = dt.getRating();
        if(rating != -1){ // ef dagsferðin er með einkunn
            String ratingStr;

            if (rating % 1 == 0){
                int ratingInt = Math.round(rating);
                ratingStr = Integer.toString(ratingInt);
            }else{
                ratingStr = Float.toString(rating);
            }

            String ratingNoDots = ratingStr.replace(".","");
            this.starImg.setImage(new Image("./images/stars/" + ratingNoDots + "rating.png"));
            this.starImg.setVisible(true);
        }else{
            this.starImg.setVisible(false);
        }
    }

    private void showComments() throws Exception{
        Comment[] comments = DayTourRepository.getCommentsByTour(dt.getId(), commentOrder.getValue());

        commentPane.getChildren().clear(); // fjarlægjum öll comments áður en við birtum þær
        for(Comment cmt : comments){
            CommentView cmtView = new CommentView(cmt);
            commentPane.getChildren().add(cmtView);
        }
    }

    public void goBack() throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../resources/dayTours.fxml")));
        Scene scene = title.getScene();
        scene.setRoot(newRoot);
    }

    /**
     * Þegar notandi ýtir á Book Trip takkann, þá birtir þetta fall dialog gluggann.
     */
    public void bookTour() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/bookTourDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Stage dialogStage = new Stage();
        Scene dialogScene = new Scene(parent, 600,370);
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        BookTourDialog cont = fxmlLoader.getController();
        cont.show(dt);

        dialogStage.showAndWait();

        // uppfærum upplýsingar eftir að notandinn lokar dialognum.
        if(DayTourRepository.hasUserBookedDayTour(dt.getId())){
            bookButton.setDisable(true);
        }
        dt = DayTourRepository.getDayTourByTitle(dt.getTourTitle());
        spotsLeft.setText(dt.getSpotsLeft() + "/" + dt.getMaxSpots() + " seats left");
    }

    public void isHoveringBackButton(){
        goBackLabel.setTextFill(Color.color(0.5294,0.8078,0.9216)); // skyblue
        goBackImg.setFitWidth(43);
        goBackImg.setFitHeight(41);

    }

    public void notHoveringBackButton(){
        goBackLabel.setTextFill(Color.color(0,0,0)); // black
        goBackImg.setFitWidth(42);
        goBackImg.setFitHeight(40);
    }

    public void nextImage(){
        if(imagePointer < images.length - 1){
            imagePointer++;
            dtImages.setImage(images[imagePointer]);
        }
    }
    public void previousImage(){
        if(imagePointer > 0){
            imagePointer--;
            dtImages.setImage(images[imagePointer]);
        }
    }

    public void incrementRating(){
        if(Float.parseFloat(ratingField.getText()) < 5){
            ratingField.setText(String.valueOf(Float.parseFloat(ratingField.getText()) + 0.5));
        }
    }

    public void decrementRating(){
        if(Float.parseFloat(ratingField.getText()) > 0){
            ratingField.setText(String.valueOf(Float.parseFloat(ratingField.getText()) - 0.5));
        }
    }

    /**
     * Keyrist þegar notandi ýtir á give rating checkbox-ið
     */
    public void doGiveRating(){
        if(giveRatingCheckbox.isSelected()){
            disableRating(false);
        }else{
            disableRating(true);
        }
    }

    /**
     * private aðferð til þess að gera rating fyrir comments óvirka
     * @param b true ef maður vill disable-a, annars false
     */
    private void disableRating(boolean b){
        giveRatingText.setDisable(b);
        incButton.setDisable(b);
        decButton.setDisable(b);
        ratingField.setDisable(b);
    }

    /**
     * keyrum þessa aðferð ef notandi er búinn að gefa dagsferðinni rating áður
     * þá má hann það ekki aftur.
     */
    private void userCannotRate(){
        giveRatingCheckbox.setSelected(false);
        giveRatingCheckbox.setDisable(true);
        disableRating(true);
    }

    /**
     * Þegar notandi ýtir á "Comment" takkann
     */
    public void makeComment() throws Exception {
        if(giveRatingCheckbox.isSelected()){ // gefa rating
            DayTourRepository.addComment(dt.getId(), commentText.getText(), Float.parseFloat(ratingField.getText()));
            userCannotRate();
            updateRating();
        }else{ // ekki gefa rating
            DayTourRepository.addComment(dt.getId(), commentText.getText(), -1);
            dt = DayTourRepository.getDayTourByTitle(dt.getTourTitle());
        }
        commentText.setText("");
        showComments(); // refresh comments
    }
}
