package vidmot;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import vinnsla.Database;
import vinnsla.DayTour;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DayTourSearchController implements Initializable {

    @FXML private ComboBox<String> areaDropdown;
    @FXML private ComboBox<String> sortDropdown;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dayTourWindow;
    @FXML private TextField searchBar;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> areas = FXCollections.observableArrayList("Allt land","Vesturland","Norðurland","Suðurland","Austurland");

        areaDropdown.setItems(areas);
        areaDropdown.setValue(areas.get(0));

        ObservableList<String> differentSorts = FXCollections.observableArrayList("Einkunn","Stafrófsröð");

        sortDropdown.setItems(differentSorts);
        sortDropdown.setValue(differentSorts.get(0));

        // þetta er til þess að laga villu þar sem dagsferðir fóru fyrir utan scrollPane:
        Rectangle clip = new Rectangle(scrollPane.getWidth(), scrollPane.getHeight()-5);
        clip.heightProperty().bind(scrollPane.heightProperty());
        clip.widthProperty().bind(scrollPane.widthProperty());
        scrollPane.setClip(clip);

        try {
            showDayTours();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Þessi aðferð birtir dagsferðirnar á "dayTourSearch.fxml" gluggann.
     */
    public void showDayTours() throws IOException {
        DayTour[] dayTours = null;
        try {
            dayTours = Database.getDayTours(areaDropdown.getValue(), searchBar.getText(), sortDropdown.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dayTours == null) return;

        dayTourWindow.getChildren().clear(); // fjarlægjum allar dagsferðir áður en við birtum þær
        for(DayTour dt : dayTours){
            DayTourListing dtListing = new DayTourListing(dt.getTourTitle(), dt.getDesc(), dt.getPrice(), dt.getSpotsLeft(), dt.getFrontImage(), dt.getDate(), dt.getLocation(), dt.getRating());
            dayTourWindow.getChildren().add(dtListing);
        }


    }
}
