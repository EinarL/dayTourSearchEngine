package vidmot;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import vinnsla.DayTour;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


/**
 * Þessi klasi er controller fyrir dayTourListing.fxml.
 */
public class DayTourListing extends AnchorPane {

    @FXML private Label title;
    @FXML private Label desc;
    @FXML private Label price;
    @FXML private Label spotsLeft;
    @FXML private ImageView image; // ekki fylki af myndum því hér viljum við bara sýna eina mynd
    @FXML private Label date;
    @FXML private Label area; // landssvæði
    @FXML private ImageView starImg;

    public DayTourListing(String title, String desc, int price, int spotsLeft, Image image, Date date, String area, float rating) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/dayTourListing.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.title.setText(title);
        this.desc.setText(desc);
        this.price.setText(price + "kr.");
        this.spotsLeft.setText(spotsLeft + " pláss eftir");
        this.image.setImage(image);
        this.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        this.area.setText(area);

        // rounded corners on image:
        final Rectangle clip = new Rectangle();
        clip.arcWidthProperty().set(25);
        clip.arcHeightProperty().set(25);
        clip.setWidth(this.image.getLayoutBounds().getWidth());
        clip.setHeight(this.image.getLayoutBounds().getHeight());
        this.image.setClip(clip);


        //edita hér: ef rating er -1, þá ekki sýna stjörnur, heldur sýna bara "Unrated" texta eða ekki neitt visible(false) t.d.
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
        }else{
            this.starImg.setVisible(false);
        }

    }

    public void bookTour() throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("../resources/dayTourSite.fxml")));
        Parent newRoot = loader.load();

        Scene scene = title.getScene();

        DayTourSiteController cont = loader.getController();
        scene.setRoot(newRoot);
        cont.setTourInfo(title.getText(), scene);
    }

}
