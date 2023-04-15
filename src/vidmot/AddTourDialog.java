package vidmot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import vinnsla.DayTour;
import vinnsla.DayTourRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
    @FXML private Label addTourText;
    @FXML private Button confirmButton;
    private boolean isEditing; // ef þetta er false þá er notandinn að búa til nýja dagsferð en ekki að edita dagsferð
    private DayTour dt = null; // dagsferðin sem notandi er að edita

    public void show(boolean isEditing){
        ObservableList<String> areas = FXCollections.observableArrayList("Vesturland","Norðurland","Suðurland","Austurland");

        this.isEditing = isEditing;

        areaDropdown.setItems(areas);
        if(!isEditing){
            areaDropdown.setValue(areas.get(0));
            date.setValue(LocalDate.now());
        }else{
            addTourText.setText("Edit Day Tour");
            confirmButton.setText("Edit Day Tour");
        }
    }

    /**
     * Þetta er fall sem keyrist aðeins ef notandi er að edita dagsferð
     * Fallið fyllir inn í boxin með upplýsingum af dagsferðinni sem notandinn er að edita.
     * @param dayTourTitle nafnið á dagsferðinni sem notandinn er að breyta.
     */
    public void addTourInfo(String dayTourTitle) throws Exception {
            dt = DayTourRepository.getDayTourByTitle(dayTourTitle);

            title.setText(dt.getTourTitle());
            desc.setText(dt.getDesc());
            images.setText(dt.getImagesString());
            date.setValue(dt.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            areaDropdown.setValue(dt.getLocation());
            duration.setText(String.valueOf(dt.getDuration()));
            price.setText(String.valueOf(dt.getPrice()));
            availableSeats.setText(String.valueOf(dt.getMaxSpots()));
    }

    /**
     * Keyrist þegar það er ýtt á Add Daytour takkann.
     * @throws Exception
     */
    public void confirmAdd() throws Exception {
        if(!checkInput()) return;
        String d = date.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if(!isEditing){
            DayTourRepository.addDayTour(title.getText(), desc.getText(), images.getText(), d,
                    Integer.parseInt(price.getText()), Integer.parseInt(availableSeats.getText()),
                    areaDropdown.getValue(), Integer.parseInt(duration.getText()));
        }else{ // if isEditing
            DayTourRepository.editDayTour(dt.getTourTitle(), title.getText(), desc.getText(), images.getText(), d,
                    Integer.parseInt(price.getText()), Integer.parseInt(availableSeats.getText()),
                    dt.getMaxSpots() - dt.getSpotsLeft(), areaDropdown.getValue(), Integer.parseInt(duration.getText()));
        }

        close();
    }

    /**
     * private fall til þess að athuga að allt sem notandi setti í textaboxin sé löglegt.
     * Title má ekki vera tómt, eða hafa sama title og einhver önnur dagsferð
     * Duration, Price og availableSeats verða að vera heiltölur >= 0.
     * if isEditing, þá má availableSeats ekki vera minni en fjöldi manns sem er bókaður fyrir þessa dagsferð.
     * links á images verða að virka
     * @return skilar true ef notandi má búa til dagsferð, annars false
     */
    private boolean checkInput(){
        if(title.getText().equals("")){
            errorText.setText("You need to give your day tour a title!");
            return false;
        }

        if(DayTourRepository.doesDayTourExist(title.getText())){
            if(!(isEditing && title.getText().equals(dt.getTourTitle()))){
                errorText.setText("The title cannot be the same as another day tour!");
                return false;
            }
        }
        try{
            Integer.parseInt(duration.getText());
            Integer.parseInt(price.getText());
            Integer.parseInt(availableSeats.getText());
        } catch(NumberFormatException e){
            errorText.setText("Duration, price per person, and amount of available seats must all be integers!");
            return false;
        }

        if(Integer.parseInt(duration.getText()) <= 0 || Integer.parseInt(price.getText()) < 0 || Integer.parseInt(availableSeats.getText()) < 0){
            errorText.setText("Duration, price per person, and amount of available seats must be positive integers!");
            return false;
        }

        if(isEditing){
            // ef fjöldi bókaða manns í dagsferðina er meiri en leyfilegur fjöldi
            if(dt.getMaxSpots() - dt.getSpotsLeft() > Integer.parseInt(availableSeats.getText())){
                errorText.setText("There are " + (dt.getMaxSpots() - dt.getSpotsLeft()) + " people " +
                        "booked for this day tour \n the amount of available seats cannot be less than that!");
                return false;
            }
        }

        if(!images.getText().equals("")){
            String[] imgs = images.getText().split(",");
            int i = 1;
            for(String img : imgs){
                try{
                    new Image(img);
                }catch (IllegalArgumentException e){
                    errorText.setText("Image number " + i + " is not a valid image link!");
                    return false;
                }
                i++;
            }
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
