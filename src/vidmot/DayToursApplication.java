package vidmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DayToursApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DayToursApplication.class.getResource("../resources/dayTours.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 800));
        primaryStage.setMinWidth(1100);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
