package vidmot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;
import vinnsla.Database;

import java.io.IOException;
import java.net.Inet4Address;

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
    private static int spotsl;


    public void setTourInfo(String title, String date, String spotsLeft, String area, String description, ImageView imageView, ImageView starslmg, int spotsl){
        this.title.setText(title);
        this.date.setText(date);
        this.spotsLeft.setText(spotsLeft);
        this.area.setText(area);
        this.description.setText(description);
        this.imageView.setImage(imageView.getImage());
        this.starlmg.setImage(starslmg.getImage());
        this.spotsl = spotsl;
        bookTourButton.setDisable(true);
        message.setText("");
    }

    public void bookTour() throws InterruptedException, IOException {
        int num = Integer.valueOf(numPeople.getText());
        Database.updateSpotsLeft(title.getText(), num);
        message.setText("Bókun staðfest, góða skemmtun!");

        sleep();
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DayToursApplication.class.getResource("../resources/dayTours.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 800));
        primaryStage.setMinWidth(1100);
        primaryStage.show();

        Stage stage = (Stage) bookTourButton.getScene().getWindow();
        stage.close();
    }


    public void sleep() throws InterruptedException {
        Thread.sleep(3000);
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

    public void confirm(){

        if(!name.getText().isEmpty() && !email.getText().isEmpty() && !phonen.getText().isEmpty() && !numPeople.getText().isEmpty()){
            int spotsWanted = Integer.valueOf(numPeople.getText());
            if(spotsl - spotsWanted > 0){
                bookTourButton.setDisable(false);
                message.setText("Vinsamlega kláraðu bókun með því að smella á 'Bóka ferð' takkan fyrir neðan");
            }
            else{
                message.setText("Ekki nægur fjöldi plássa laus");
            }
        }
        else{
            message.setText("Vinsamlega kláraðu að fylla út upplýsingar");
        }


    }
}
