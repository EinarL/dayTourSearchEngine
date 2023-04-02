package vidmot;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import vinnsla.DayTourRepository;
import vinnsla.DayTour;
import vinnsla.User;

import java.io.IOException;
import java.util.Objects;

public class DayTourOverviewController {
    @FXML private VBox vbox;
    private static final String user = User.getUsername();

    public void showBookedDayTours(){

        DayTour[] dayTours = null;
        try {
            dayTours = DayTourRepository.getDayToursByUserBooked(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dayTours == null) return;

        vbox.getChildren().clear();
        for(DayTour dt : dayTours){
            DayTourListing dtListing = new DayTourListing(dt.getTourTitle(), dt.getDesc(), dt.getPrice(), dt.getSpotsLeft(), dt.getFrontImage(), dt.getDate(), dt.getLocation(), dt.getRating());
            vbox.getChildren().add(dtListing);
            // bætum við cancel takka á hverja dagsferð
            Button cancelButton = new Button();
            cancelButton.setText("Cancel Booking");
            cancelButton.setPrefWidth(170);
            cancelButton.setPrefHeight(50);
            cancelButton.setFont(Font.font ("System", 18));
            cancelButton.setLayoutX(507);
            cancelButton.setLayoutY(236);
            cancelButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../styles/style.css")).toExternalForm());
            cancelButton.setOnAction(e -> {
                DayTourRepository.removeBooking(user, ((Label) dtListing.lookup("#title")).getText());
                showBookedDayTours();
            });
            dtListing.getChildren().add(cancelButton);

        }
    }


    public void goBack() throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../resources/dayTours.fxml")));
        Scene scene = vbox.getScene();
        scene.setRoot(newRoot);
    }

}
