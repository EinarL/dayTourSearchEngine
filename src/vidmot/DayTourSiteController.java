package vidmot;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import vinnsla.Comment;


public class DayTourSiteController extends AnchorPane {

    @FXML private Label price;
    @FXML private Label title;
    @FXML private Label date;
    @FXML private Label spotsLeft;
    @FXML private Label desc;
    @FXML private Label duration;
    @FXML private Label location;
    @FXML private ImageView starlmg;
    @FXML private ImageView dtImages;

    private Comment[] comments;
    private String user;


}
