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
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vinnsla.DayTourRepository;
import vinnsla.DayTour;
import vinnsla.User;

import java.io.IOException;
import java.util.Objects;

public class DayTourOverviewController {
    @FXML private VBox bookedVbox; // vbox fyrir "your booked daytours"
    @FXML private VBox yourVbox; // vbox fyrir "your daytours"
    @FXML private ImageView goBackImg;
    @FXML private Label goBackLabel;
    @FXML private StackPane stackPane;
    private Scene scene;

    public void init(Scene scene){
        this.scene = scene;
        // center content
        stackPane.translateXProperty()
                .bind(scene.widthProperty().subtract(stackPane.widthProperty())
                        .divide(2));

        showBookedDayTours();
        showYourDayTours();
    }

    public void showBookedDayTours(){

        DayTour[] dayTours = null;
        try {
            dayTours = DayTourRepository.getDayToursByUserBooked(User.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dayTours == null) return;

        bookedVbox.getChildren().clear();
        for(DayTour dt : dayTours){
            int[] booking = DayTourRepository.getBooking(dt.getId()); // [numParticipants, price]
            if(booking == null) continue;

            DayTourListing dtListing = new DayTourListing(dt.getTourTitle(), dt.getDesc(), booking[1], dt.getSpotsLeft(), dt.getFrontImage(), dt.getDate(), dt.getLocation(), dt.getRating());
            dtListing.setSpotsLeft(booking[0]);

            bookedVbox.getChildren().add(dtListing);
            // bætum við cancel takka á hverja dagsferð
            Button cancelButton = new Button();
            cancelButton.setText("Cancel Booking");
            cancelButton.setPrefWidth(170);
            cancelButton.setPrefHeight(50);
            cancelButton.setFont(Font.font ("System", 18));
            cancelButton.setLayoutX(527);
            cancelButton.setLayoutY(236);
            cancelButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../styles/style.css")).toExternalForm());
            cancelButton.setOnAction(e -> {
                DayTourRepository.removeBooking(User.getUsername(), dt.getId(), ((Label) dtListing.lookup("#title")).getText());
                showBookedDayTours();
            });
            dtListing.getChildren().add(cancelButton);
        }
    }

    public void showYourDayTours(){
        DayTour[] dayTours = null;
        try {
            dayTours = DayTourRepository.getDayToursByOwner(User.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dayTours == null) return;

        yourVbox.getChildren().clear();
        for(DayTour dt : dayTours){
            DayTourListing dtListing = new DayTourListing(dt.getTourTitle(), dt.getDesc(), dt.getPrice(), dt.getSpotsLeft(), dt.getFrontImage(), dt.getDate(), dt.getLocation(), dt.getRating());
            yourVbox.getChildren().add(dtListing);
            // bætum við edit takka á hverja dagsferð
            Button editButton = new Button();
            editButton.setText("Edit Day Tour");
            editButton.setId("edit-button");
            editButton.setPrefWidth(170);
            editButton.setPrefHeight(50);
            editButton.setFont(Font.font ("System", 18));
            editButton.setLayoutX(507);
            editButton.setLayoutY(236);
            editButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../styles/style.css")).toExternalForm());
            editButton.setOnAction(e -> {
                // keyrist þegar notandi ýtir á edit takkann
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/addTourDialog.fxml"));
                Parent parent = null;
                try {
                    parent = fxmlLoader.load();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Stage dialogStage = new Stage();
                Scene dialogScene = new Scene(parent, 800,790);
                dialogStage.initModality(Modality.APPLICATION_MODAL); // notandi má ekki nota aðal gluggann fyrr en hann er búinn að loka dialognum
                dialogStage.setScene(dialogScene);
                dialogStage.setResizable(false);
                AddTourDialog cont = fxmlLoader.getController();
                cont.show(true);
                try {
                    cont.addTourInfo(dt.getTourTitle());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dialogStage.showAndWait();
                // uppfærum yourDayTours eftir að notandinn lokar dialognum.
                showYourDayTours();
            });
            dtListing.getChildren().add(editButton);

        }
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

    /**
     * keyrist þegar notandi ýtir á "Add a Daytour" takkann.
     */
    public void addDayTour() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/addTourDialog.fxml"));
        Parent parent = fxmlLoader.load();
        Stage dialogStage = new Stage();
        Scene dialogScene = new Scene(parent, 800,790);
        dialogStage.initModality(Modality.APPLICATION_MODAL); // notandi má ekki nota aðal gluggann fyrr en hann er búinn að loka dialognum
        dialogStage.setScene(dialogScene);
        dialogStage.setResizable(false);
        AddTourDialog cont = fxmlLoader.getController();
        cont.show(false);

        dialogStage.showAndWait();
        // uppfærum yourDayTours eftir að notandinn lokar dialognum.
        showYourDayTours();
    }

    public void goBack() throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../resources/dayTours.fxml")));
        Scene scene = bookedVbox.getScene();
        scene.setRoot(newRoot);
    }

}
