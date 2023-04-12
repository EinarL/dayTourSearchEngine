package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import vinnsla.DayTour;
import vinnsla.DayTourRepository;
import vinnsla.User;

import java.io.IOException;

/**
 * Controller fyrir bookTourDialog.fxml
 */
public class BookTourDialog extends DialogPane {
    @FXML private Label tourName;
    @FXML private Label price;
    @FXML private TextField amount;
    @FXML private ComboBox<String> paymentCombo;
    private int oneTicketPrice;
    private int spotsLeft;
    private DayTour dt;

    public void show(DayTour dt) {
        oneTicketPrice = dt.getPrice();

        this.tourName.setText(dt.getTourTitle());
        this.price.setText("Price: " + dt.getPrice());
        this.spotsLeft = dt.getSpotsLeft();

        this.dt = dt;

        paymentCombo.getItems().add("Millifærsla");
        paymentCombo.getItems().add("Aur");
        paymentCombo.getItems().add("VISA/DEBIT");
        paymentCombo.setValue("Millifærsla");
    }

    public void close(){
        Stage stage = (Stage) tourName.getScene().getWindow();
        stage.close();
    }

    public void confirmBooking(){
        int num = Integer.parseInt(amount.getText());
        DayTourRepository.addBooking(User.getUsername(), num, tourName.getText(), dt.getId(), Integer.parseInt(amount.getText())*oneTicketPrice);
        close();
    }

    public void decrementCount(){
        if(Integer.parseInt(amount.getText()) > 1) {
            amount.setText(Integer.toString(Integer.parseInt(amount.getText()) - 1));
            calculatePrice();
        }
    }

    public void incrementCount(){
        if(Integer.parseInt(amount.getText()) < spotsLeft) {
            amount.setText(Integer.toString(Integer.parseInt(amount.getText()) + 1));
            calculatePrice();
        }
    }

    private void calculatePrice(){
        int calcPrice = Integer.parseInt(amount.getText())*oneTicketPrice;
        price.setText("Price: " + calcPrice + "kr.");
    }
}
