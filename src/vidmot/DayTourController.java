package vidmot;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DayTourController implements Initializable {

    @FXML private ComboBox<String> areaDropdown;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] areas = {"Vesturland","Norðurland","Suðurland","Austurland"};

        for (String area : areas) {
            areaDropdown.getItems().add(area);
        }
        areaDropdown.setValue(areas[0]);
    }
}
