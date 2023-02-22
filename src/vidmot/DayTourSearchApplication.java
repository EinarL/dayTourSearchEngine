package vidmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DayTourSearchApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DayTourSearchApplication.class.getResource("../resources/dayTourSearch.fxml"));
        primaryStage.setTitle("Day Trip");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 900));
        primaryStage.show();
        primaryStage.setMinWidth(600);
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
