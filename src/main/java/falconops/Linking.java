package falconops;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Linking extends DB_GUI {
    static ComboBox<String> aircraftCombo;
    static ComboBox<String> pilotCombo;

    public VBox createLinkPanel(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #121212;");

        Label aircraftLabel = new Label("Select Aircraft:");
        aircraftLabel.setStyle("-fx-text-fill: #00ff00;");
        aircraftCombo = new ComboBox<>();
        aircraftCombo.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                }
            }
        });
        aircraftCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                }
            }
        });

        refreshAircraftComboBox();

        Label pilotLabel = new Label("Select Pilot:");
        pilotLabel.setStyle("-fx-text-fill: #00ff00;");
        pilotCombo = new ComboBox<>();
        pilotCombo.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                }
            }
        });
        pilotCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;");
                }
            }
        });

        refreshPilotComboBox();

        Button linkButton = new Button("Link Pilot to Aircraft");
        linkButton.setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white; -fx-font-weight: bold;"); // Changed to white text
        linkButton.setOnMouseEntered(e -> linkButton.setStyle("-fx-background-color: #00ff00; -fx-text-fill: white;"));
        linkButton.setOnMouseExited(e -> linkButton.setStyle("-fx-background-color: #1a2e1a; -fx-text-fill: white;"));

        linkButton.setOnAction(e -> {
            String selectedAircraft = aircraftCombo.getValue();
            String selectedPilot = pilotCombo.getValue();
            if (selectedAircraft == null || selectedPilot == null) {
                SoundPlayer.play("Sounds/Please_select_both.wav", false);
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select both an aircraft and a pilot.");
                return;
            }
            linkPilotToAircraft(selectedAircraft, selectedPilot, stage);
        });

        root.getChildren().addAll(aircraftLabel, aircraftCombo, pilotLabel, pilotCombo, linkButton);
        return root;
    }


    void linkPilotToAircraft(String aircraftName, String pilotName, Stage stage) {
        String sql = "UPDATE Aircraft SET assigned_pilot = (SELECT Pilot_ID FROM Pilot WHERE Pilot_Name = ?) WHERE Aircraft_Name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pilotName);
            ps.setString(2, aircraftName);
            SoundPlayer.play("Sounds/Match_made_in_sky.wav", false);
            showAlert(Alert.AlertType.INFORMATION, "Match made in the skies!", "Linked " + pilotName + " to " + aircraftName);
            LoginController.gameInvaders();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error linking pilot and aircraft: " + e.getMessage());
        }
    }

    void refreshAircraftComboBox() {
        aircraftCombo.getItems().clear();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Aircraft_Name FROM Aircraft")) {
            while (rs.next()) {
                aircraftCombo.getItems().add(rs.getString("Aircraft_Name"));

            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error refreshing aircraft list: " + e.getMessage());
        }
    }

    void refreshPilotComboBox() {
        pilotCombo.getItems().clear();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Pilot_Name FROM Pilot WHERE Status != 'Shaheed'")) {
            while (rs.next()) {
                pilotCombo.getItems().add(rs.getString("Pilot_Name"));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error refreshing pilot list: " + e.getMessage());
        }
    }
}
