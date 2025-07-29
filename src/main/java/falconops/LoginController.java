package falconops;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginController extends Application {

    private LoginView loginView;
    private static Stage mainStage;
    private static Stage depStage;
    private static Image icon = new Image("falconOpsIcon.jpeg");

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        showLogin(stage);
    }

    // Method to show login window
    public void showLogin(Stage stage) {
        loginView = new LoginView(this);
        Scene scene = new Scene(loginView.getView(), 400, 300);
        stage.setScene(scene);
        stage.setTitle("FalconOps – Command Access Portal");
        stage.getIcons().add(icon);
        stage.show();
    }

    // Called by LoginView when user clicks login
    public void tryLogin(Stage stage, String username, String password) throws SQLException, ClassNotFoundException {
        if (SessionManager.authenticate(username, password)) {
            // Close login window to open main application
            stage.close();

            // Create new stage for main application
            mainStage = new Stage();
            DB_GUI mainApp = new DB_GUI();
            mainApp.programStarted(mainStage);
            mainStage.getIcons().add(icon);
        } else {
            loginView.showError("Invalid login credentials.");
        }
    }


    public static void deployment(){

        if (mainStage != null) {
            mainStage.close();  // close the stored main stage
        } else {
            DB_GUI.showAlert(Alert.AlertType.WARNING, "Main Stage is null", "⚠️ Cannot close window.");
        }

        depStage = new Stage();
        Linking depLog = new Linking();
        VBox panel = depLog.createLinkPanel(depStage);

        Scene scene = new Scene(panel, 400, 300);
        depStage.setScene(scene);
        depStage.setTitle("FalconOps – Pilot Deployment Panel");
        depStage.getIcons().add(icon);
        depStage.show();
    }

    public static void gameInvaders() throws Exception {
        if (depStage != null) {
            depStage.close();  // close the stored main(deployment) stage
        } else {
            DB_GUI.showAlert(Alert.AlertType.WARNING, "Main Stage is null", "⚠️ Cannot close window.");
        }

        Stage gameStage = new Stage();
        SpaceInvaders SI = new SpaceInvaders();
        gameStage.setTitle("FalconOps – Aerial Combat Arena");
        gameStage.getIcons().add(icon);
        SI.startGame(gameStage);

    }


}