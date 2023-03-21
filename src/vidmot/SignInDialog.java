package vidmot;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vinnsla.Database;
import vinnsla.User;

import java.io.IOException;

public class SignInDialog {

    @FXML private Label signInText;
    @FXML private Label questionText;
    @FXML private Label errorText;
    @FXML private Hyperlink hyperlinkText;
    @FXML private Button confirmButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private boolean isSigningIn = true; // true ef notandi er að signa inn, false ef notandi er að signa upp.

    public void show(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/signInDialog.fxml"));
            Stage dialogStage = new Stage();
            Scene dialogScene = new Scene(fxmlLoader.load(), 600, 400);
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(false);

            // fjarlægja border frá hyperlink
            hyperlinkText = (Hyperlink) dialogScene.lookup("#hyperlinkText");
            hyperlinkText.setBorder(Border.EMPTY);
            hyperlinkText.setPadding(new Insets(4, 0, 4, 4));

            dialogStage.showAndWait();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Keyrist þegar það er ýtt á "Sign In instead" eða "Sign Up instead"
     * Breytir sign in option yfir í sign up (eða öfugt).
     */
    public void signUpInstead(){
        if(isSigningIn){
            signInText.setText("Sign Up");
            questionText.setText("Already have an account?");
            questionText.setLayoutX(241);
            hyperlinkText.setText("Sign In instead");
            confirmButton.setText("Sign Up");

            isSigningIn = false;
        }else{
            signInText.setText("Sign In");
            questionText.setText("Don't have an account?");
            questionText.setLayoutX(254);
            hyperlinkText.setText("Sign Up instead");
            confirmButton.setText("Sign In");

            isSigningIn = true;
        }
    }

    /**
     * Keyrist þegar notandi ýtir á "Sign In" eða "Sign Up" takkann.
     * Fyrir sign in:
     *      leyfir notanda að signa inn ef username og password er rétt
     * Fyrir sign up:
     *      Leyfir notanda að signa upp ef:
     *          username er ekki núþegar til
     *          username og password dálkur er ekki tómur
     */
    public void signIn() throws Exception {
        if(isSigningIn){
            if(Database.doesUserExist(usernameField.getText(), passwordField.getText())){
                // geymum usernameið í User klasanum svo við vitum hver er signaður inn.
                User.setUsername(usernameField.getText());

                showMainWindow();
            }else{ // ef notandi er ekki til í databaseinu
                errorText.setText("username and/or password is incorrect!");
                errorText.setVisible(true);

            }
        }else{ // ef notandi er að signa up
            if(usernameField.getText().length() == 0 || passwordField.getText().length() == 0){
                errorText.setText("username and password fields cannot be empty!");
                errorText.setVisible(true);
                return;
            }
            if(Database.doesUserExist(usernameField.getText())){
                errorText.setText("username already exists!");
                errorText.setVisible(true);
            }else{ // ef usernameið er laust
                // geymum usernameið í User klasanum svo við vitum hver er signaður inn.
                User.setUsername(usernameField.getText());

                // bætum við notendanum í databaseinn
                Database.addUser(usernameField.getText(), passwordField.getText());

                showMainWindow();
            }

        }
    }

    /**
     * birtir dayTours.fxml og lokar þessum dialog
     * @throws IOException
     */
    private void showMainWindow() throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DayToursApplication.class.getResource("../resources/dayTours.fxml"));
        primaryStage.setTitle("Day Trip Search");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 1200, 800));
        primaryStage.setMinWidth(1100);
        primaryStage.show();

        // lokar glugganum
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    public void removeErrorText(){
        errorText.setVisible(false);
    }
}
