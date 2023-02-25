package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    public DayTourListing(String title, String desc, int price, int spotsLeft, String image, Date date, String area, float rating) {
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
        this.image.setImage(new Image(image));
        this.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        this.area.setText(area);


    }
}
