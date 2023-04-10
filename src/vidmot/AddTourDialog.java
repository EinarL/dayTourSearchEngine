package vidmot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import vinnsla.DayTour;
import vinnsla.DayTourRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Controller fyrir addTourDailog.fxml
 */
public class AddTourDialog {
    @FXML private TextField title;
    @FXML private TextArea desc;
    @FXML private TextArea images;
    @FXML private DatePicker date;
    @FXML private ComboBox<String> areaDropdown;
    @FXML private TextField duration;
    @FXML private TextField price;
    @FXML private TextField availableSeats; // þetta er bæði MaxSpots og SpotsLeft
    @FXML private Label errorText;


    public void show(){
        ObservableList<String> areas = FXCollections.observableArrayList("Allt land","Vesturland","Norðurland","Suðurland","Austurland");

        areaDropdown.setItems(areas);
        areaDropdown.setValue(areas.get(0));

        date.setValue(LocalDate.now());
    }

    /**
     * Keyrist þegar það er ýtt á Add Daytour takkann.
     * @throws Exception
     */
    public void confirmAdd() throws Exception {
        if(!checkInput()) return;
        String d = date.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        DayTourRepository.addDayTour(title.getText(), desc.getText(), images.getText(), d,
                Integer.parseInt(price.getText()), Integer.parseInt(availableSeats.getText()),
                areaDropdown.getValue(), Integer.parseInt(duration.getText()));
        close();
    }

    /**
     * private fall til þess að athuga að allt sem notandi setti í textaboxin sé löglegt.
     * Title má ekki vera tómt, eða hafa sama title og einhver önnur dagsferð
     * Duration, Price og availableSeats verða að vera heiltölur.
     *
     * @return skilar true ef notandi má búa til dagsferð, annars false
     */
    private boolean checkInput(){
        if(title.getText().equals("")){
            errorText.setText("You need to give your day tour a title!");
            return false;
        }

        if(DayTourRepository.doesDayTourExist(title.getText())){
            errorText.setText("The title cannot be the same as another day tour!");
            return false;
        }
        try{
            Integer.parseInt(duration.getText());
            Integer.parseInt(price.getText());
            Integer.parseInt(availableSeats.getText());
        } catch(NumberFormatException e){
            errorText.setText("Duration, price per person, and amount of available seats must all be integers!");
            return false;
        }

        return true;
    }

    public void removeErrorText(){
        errorText.setText("");
    }

    public void close(){
        Stage stage = (Stage) areaDropdown.getScene().getWindow();
        stage.close();
    }
}
