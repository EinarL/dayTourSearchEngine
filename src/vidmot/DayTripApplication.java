package vidmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DayTripApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DayTripApplication.class.getResource("../resources/dayTrip.fxml"));
        primaryStage.setTitle("Day Trip");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 900));
        primaryStage.show();
        //primaryStage.setResizable(false);
        scene = primaryStage.getScene();
    }

    public static Scene getScene(){
        return scene;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
