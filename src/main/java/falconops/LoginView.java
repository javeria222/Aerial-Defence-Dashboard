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

        // Main container (Black Background)
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #000000;");

        // Header text (Dark Green #135A0E)
        Text headerText = new Text("");
        headerText.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        headerText.setFill(Color.web("#135A0E"));

        // Form container (GridPane)
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));

        // Labels (Dark Green #135A0E)
        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-text-fill: #135A0E; -fx-font-weight: bold;");
        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-text-fill: #135A0E; -fx-font-weight: bold;");

        // Text Fields (Black with Dark Green Border, Red Input Text)
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

        // Error Label (Red)
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // Login Button (Hover Effect: Green BG + Black Text)
        Button loginButton = new Button("Log In");
        loginButton.setStyle(
                "-fx-background-color: black; " +
                        "-fx-text-fill: #135A0E; " +
                        "-fx-border-color: #135A0E; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-weight: bold;"
        );

        // Hover Effect
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                    "-fx-background-color: #135A0E; " + // Green BG on hover
                            "-fx-text-fill: black; " +          // Black text on hover
                            "-fx-border-color: #135A0E; " +
                            "-fx-border-width: 2px; " +
                            "-fx-font-weight: bold;"
            );
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
                    "-fx-background-color: black; " +  // Default: Black BG
                            "-fx-text-fill: #135A0E; " +      // Default: Green text
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

        // Add components to form
        formGrid.add(userLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);
        formGrid.add(loginButton, 1, 2);
        formGrid.add(errorLabel, 0, 3, 2, 1); // Span 2 columns

        // Add header and form to root
        root.getChildren().addAll(headerText, formGrid);
    }

    public VBox getView() {
        return root;
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }
}