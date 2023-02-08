package vidmot;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DayTourController implements Initializable {

    @FXML private ComboBox<String> areaDropdown;
    @FXML private ComboBox<String> sortDropdown;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> areas = FXCollections.observableArrayList("Vesturland","Norðurland","Suðurland","Austurland");

        areaDropdown.setItems(areas);
        areaDropdown.setValue(areas.get(0));

        ObservableList<String> differentSorts = FXCollections.observableArrayList("Einkunn","Stafrófsröð");

        sortDropdown.setItems(differentSorts);
        sortDropdown.setValue(differentSorts.get(0));
    }
}
