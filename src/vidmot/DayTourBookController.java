package vidmot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;
import vinnsla.Database;
import vinnsla.DayTour;
import vinnsla.User;

import java.io.IOException;
import java.net.Inet4Address;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * Þessi klasi er controller fyrir bookDayTour.fmxl.
 */

public class DayTourBookController {

    @FXML private Label title;
    @FXML private Label date;
    @FXML private Label spotsLeft;
    @FXML private Label area;
    @FXML private Label description;
    @FXML private Label message;
    @FXML private ImageView imageView;
    @FXML private ImageView starlmg;
    @FXML private Button goBackButton;
    @FXML private Button bookTourButton;
    @FXML private TextField name;
    @FXML private TextField email;
    @FXML private TextField phonen;
    @FXML private TextField numPeople;
    @FXML private VBox dayTourVBox;
    @FXML private ComboBox<String> paymentCombo;
    private static int spotsl;
    private static String dtTitle;
    private static String user;


    public void setTourInfo(String title) throws SQLException, ParseException, ClassNotFoundException {

        DayTour dt = Database.getDayTourByTitle(title);
        DayTourListing dtls = new DayTourListing(dt.getTourTitle(), dt.getDesc(), dt.getPrice(), dt.getSpotsLeft(), dt.getFrontImage(), dt.getDate(), dt.getLocation(), dt.getRating());
        dayTourVBox.getChildren().clear();
        dayTourVBox.getChildren().add(dtls);

        spotsl = dt.getSpotsLeft();
        dtTitle = dt.getTourTitle();
        user = User.getUsername();

        paymentCombo.getItems().add("Millifærsla");
        paymentCombo.getItems().add("Aur");
        paymentCombo.getItems().add("VISA/DEBIT");
    }

    public void bookTour() throws InterruptedException, IOException {

        int num = Integer.parseInt(numPeople.getText());
        Database.addBooking(user, num, dtTitle);
        message.setText("Bókun staðfest, góða skemmtun!");
        sleep();
        goBack();
    }

    public void confirm(){

        if(!name.getText().isEmpty() && !email.getText().isEmpty() && !phonen.getText().isEmpty() && !numPeople.getText().isEmpty()){
            try{
                int spotsWanted = Integer.parseInt(numPeople.getText());
                if(spotsl - spotsWanted > 0){
                    bookTourButton.setDisable(false);
                    message.setText("Vinsamlega staðfestu bókun með því að smella á 'Bóka ferð' takkann");
                }
                else{
                    message.setText("Ekki er nægur fjöldi plássa laus");
                }

            } catch (NumberFormatException e) {
                message.setText("Vinsamlega skráðu inn löglegan fjölda fólks");
            }
        }
        else{
            message.setText("Vinsamlega kláraðu að fylla út upplýsingar");
        }
    }

    public void goBack() throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../resources/dayTours.fxml")));
        Scene scene = dayTourVBox.getScene();
        scene.setRoot(newRoot);
    }

    public void sleep() throws InterruptedException {
        Thread.sleep(3000);
    }
}
