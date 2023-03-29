package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vinnsla.DayTour;
import vinnsla.DayTourRepository;
import vinnsla.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class DayTourSiteController {
    @FXML private Label title;
    @FXML private Label desc;
    @FXML private Label price;
    @FXML private Label date;
    @FXML private Label spotsLeft;
    @FXML private Label area;
    @FXML private Label duration;
    @FXML private ImageView dtImages;
    @FXML private ImageView starImg;
    private String[] images;

    public void setTourInfo(String title) throws Exception {
        DayTour dt = DayTourRepository.getDayTourByTitle(title);

        this.title.setText(title);
        this.desc.setText(dt.getDesc());
        this.price.setText(dt.getPrice() + "kr.");
        this.spotsLeft.setText(dt.getSpotsLeft() + "/" + dt.getMaxSpots() + " seats left");
        this.dtImages.setImage(new Image(dt.getFrontImage()));
        this.date.setText("Date: " + (new SimpleDateFormat("dd/MM/yyyy").format(dt.getDate())));
        this.area.setText("Location: " + dt.getLocation());

        this.images = dt.getImages();

        String ratingStr;
        float rating = dt.getRating();
        if (rating % 1 == 0){
            int ratingInt = Math.round(rating);
            ratingStr = Integer.toString(ratingInt);
        }else{
            ratingStr = Float.toString(rating);
        }

        String ratingNoDots = ratingStr.replace(".","");
        this.starImg.setImage(new Image("./images/stars/" + ratingNoDots + "rating.png"));
    }

    public void goBack() throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../resources/dayTours.fxml")));
        Scene scene = title.getScene();
        scene.setRoot(newRoot);
    }

    public void nextImage(){

    }
    public void previousImage(){

    }
}
