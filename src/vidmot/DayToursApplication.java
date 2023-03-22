package vidmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DayToursApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DayToursApplication.class.getResource("../resources/signInDialog.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 600, 400));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
