package vidmot;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import vinnsla.Database;
import vinnsla.DayTour;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DayTourSearchController implements Initializable {

    @FXML private ComboBox<String> areaDropdown;
    @FXML private ComboBox<String> sortDropdown;

    private DayTour[] dayTours;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> areas = FXCollections.observableArrayList("Allt land","Vesturland","Norðurland","Suðurland","Austurland");

        areaDropdown.setItems(areas);
        areaDropdown.setValue(areas.get(0));

        ObservableList<String> differentSorts = FXCollections.observableArrayList("Einkunn","Stafrófsröð");

        sortDropdown.setItems(differentSorts);
        sortDropdown.setValue(differentSorts.get(0));

        try {
            dayTours = Database.getDayToursByArea(areas.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(dayTours));
    }
}
