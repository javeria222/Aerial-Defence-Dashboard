package falconops;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginView {

    private final VBox root;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Label errorLabel;
    private final LoginController controller;

    public LoginView(LoginController controller) {
        this.controller = controller;

        // Main container 
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle(
                "-fx-background-color: #000000; " +
                        "-fx-background-image: url('falconOpsIconN.jpg'); " +
                        "-fx-background-size: contain; " +
                        "-fx-background-position: center; " +
                        "-fx-background-repeat: no-repeat;"
        );

        // Circular container for login form
        VBox circleContainer = new VBox(20);
        circleContainer.setAlignment(Pos.CENTER);
        circleContainer.setPadding(new Insets(30));
        circleContainer.setMaxSize(350, 350); 

        // Circular styling with black background and border
        circleContainer.setStyle(
                "-fx-background-color: #000000; " +
                        "-fx-background-radius: 175; " +
                        "-fx-border-color: #135A0E; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 175; "
        );

        
        Text headerText = new Text("");
        headerText.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        headerText.setFill(Color.web("#135A0E"));

        // Form container 
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-text-fill: #135A0E; -fx-font-weight: bold;");
        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-text-fill: #135A0E; -fx-font-weight: bold;");

        usernameField = new TextField();
        usernameField.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: red; " +
                        "-fx-border-color: #135A0E; " +
                        "-fx-border-width: 2px; " +
                        "-fx-prompt-text-fill: #555555;"
        );

        passwordField = new PasswordField();
        passwordField.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: red; " +
                        "-fx-border-color: #135A0E; " +
                        "-fx-border-width: 2px; " +
                        "-fx-prompt-text-fill: #555555;"
        );

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Button loginButton = new Button("Log In");
        loginButton.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #135A0E; " +
                        "-fx-border-color: #135A0E; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-weight: bold;"
        );

        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                    "-fx-background-color: #135A0E; " + 
                            "-fx-text-fill: black; " +   
                            "-fx-border-color: #135A0E; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-weight: bold;"
            );
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
                    "-fx-background-color: black; " + 
                            "-fx-text-fill: #135A0E; " +   
                            "-fx-border-color: #135A0E; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-weight: bold;"
            );
        });

        loginButton.setOnAction(e -> {
            errorLabel.setText("");
            try {
                controller.tryLogin((Stage) root.getScene().getWindow(),
                        usernameField.getText(), passwordField.getText());
            } catch (SQLException | ClassNotFoundException ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        formGrid.add(userLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);
        formGrid.add(loginButton, 1, 2);
        formGrid.add(errorLabel, 0, 3, 2, 1);

        root.getChildren().addAll(headerText, formGrid);
    }

    public VBox getView() {
        return root;
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }
}
