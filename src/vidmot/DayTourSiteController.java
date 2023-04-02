package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private DayTour dt;
    private Image[] images;
    private int imagePointer = 0;

    public void setTourInfo(String title) throws Exception {
        dt = DayTourRepository.getDayTourByTitle(title);

        this.title.setText(title);
        this.desc.setText(dt.getDesc());
        this.price.setText(dt.getPrice() + "kr.");
        this.spotsLeft.setText(dt.getSpotsLeft() + "/" + dt.getMaxSpots() + " seats left");
        this.dtImages.setImage(dt.getFrontImage());
        this.date.setText("Date: " + (new SimpleDateFormat("dd/MM/yyyy").format(dt.getDate())));
        this.area.setText("Location: " + dt.getLocation());

        this.images = dt.getImages();


        // ef notandi hefur bókað ferðina, þá getur hann ekki bókað hana aftur
        if(DayTourRepository.hasUserBookedDayTour(dt.getId())){
            bookButton.setDisable(true);
        }

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
}
