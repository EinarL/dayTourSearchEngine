package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vinnsla.Database;
import vinnsla.DayTour;
import vinnsla.User;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLOutput;

public class DayTourOverviewController {
    @FXML private VBox vbox;
    @FXML private TextField tourName;
    @FXML private Button goBackButton;
    private static String user = User.getUsername();



    public void showBookedDayTours(){
        DayTour[] dayTours = null;
        try {
            dayTours = Database.getDayToursByUserBooked(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dayTours == null) return;

        vbox.getChildren().clear();
        for(DayTour dt : dayTours){
            DayTourListing dtListing = new DayTourListing(dt.getTourTitle(), dt.getDesc(), dt.getPrice(), dt.getSpotsLeft(), dt.getFrontImage(), dt.getDate(), dt.getLocation(), dt.getRating());
            vbox.getChildren().add(dtListing);
        }
    }

    public void removeBooking() throws IOException {
        Database.removeBooking(user, tourName.getText());
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DayToursApplication.class.getResource("../resources/dayTours.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 800));
        primaryStage.setMinWidth(1100);
        primaryStage.show();

        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.close();

    }


    public void goBack() throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DayToursApplication.class.getResource("../resources/dayTours.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 800));
        primaryStage.setMinWidth(1100);
        primaryStage.show();

        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.close();
    }



}
