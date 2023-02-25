package vidmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DayTourSearchApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DayTourSearchApplication.class.getResource("../resources/dayTourSearch.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 900));
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
