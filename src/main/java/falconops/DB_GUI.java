package falconops;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.*;

public class DB_GUI {

    private TableView<Aircraft> aircraftTable;
    private TableView<Pilot> pilotTable;
    private VBox contentArea;
    private VBox detectedIdsBox;

    private VBox radarPanel; // the persistent radar panel
    private RadarSimulation activeRadar;
    private UserGenerate activeUserGen;
    public Stage primaryStage;

    public void programStarted(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        getConnection();

        // Create sidebar with dark theme
        VBox sidebar = createSidebar();
        mainPane.setLeft(sidebar);

        // Create content area with dark theme and green border
        contentArea = new VBox(10);
        contentArea.setPadding(new Insets(15));
        contentArea.setStyle("-fx-background-color: #223b18;");
        mainPane.setCenter(contentArea);

        primaryStage.setOnCloseRequest(e -> {
            if (activeRadar != null) activeRadar.radarSweepLine();
            if (activeUserGen != null) activeUserGen.stopUserGeneration();
        });

        this.primaryStage = primaryStage;

        //Radar Panel and to show it by default when main screen is called
        radarPanel = createRadarPanel();
        showPanel(radarPanel);

        Scene scene = new Scene(mainPane, 1200, 750);
        primaryStage.setTitle("FalconOps Control Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #223b18; -fx-border-color: #135A0E -fx-border-width: 2px;");

        Label title = new Label("FalconOps");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button radarBtn = createNavButton("View Radar");
        Button aircraftBtn = createNavButton("Aircraft Entry");
        Button pilotBtn = createNavButton("Pilot Entry");
        Button exitBtn = createNavButton("Exit");

      //  loginController = new LoginController();

        radarBtn.setOnAction(e -> showPanel(radarPanel));
        aircraftBtn.setOnAction(e -> showPanel(createAircraftPanel()));
        pilotBtn.setOnAction(e -> showPanel(createPilotPanel()));
        exitBtn.setOnAction(e -> primaryStage.close());

        sidebar.getChildren().addAll(title, radarBtn, aircraftBtn, pilotBtn, exitBtn);
        return sidebar;
    }

    Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 10px; -fx-border-color: #00ff00; -fx-border-width: 1px;");

        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black; -fx-font-size: 14px; "
                + "-fx-padding: 10px; -fx-border-color: #00ff00; -fx-border-width: 1px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 10px; -fx-border-color: #00ff00; -fx-border-width: 1px;"));

        return btn;
    }

    void showPanel(VBox panel) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    static Connection getConnection() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/falconops";
            String username = "falcon_admin";
            String password = "1234";

            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, " Connection Error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private VBox createRadarPanel() {
        if (radarPanel != null) {
            return radarPanel; // already initialized, reuse it
        }

        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: #333333; -fx-border-color: #00ff00; -fx-border-width: 2px;");

        // Title label
        Label title = new Label("Radar View");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        // Radar display
        try {
            Pane radarDisplay = new Pane();
            radarDisplay.setPrefSize(800, 600);
            radarDisplay.setStyle("-fx-background-color: black; -fx-border-color: #00ff00; -fx-border-width: 1px;");

            activeRadar = new RadarSimulation(radarDisplay, primaryStage);
            activeRadar.Radar();
            activeRadar.radarSweepLine();

            activeUserGen = new UserGenerate(radarDisplay, primaryStage, this);
            activeUserGen.userAnimation();

            // VBox to show detected IDs
            VBox detectedIdsBox = new VBox(5);
            detectedIdsBox.setPadding(new Insets(10));
            detectedIdsBox.setStyle("-fx-background-color: #222222; -fx-border-color: #00ff00; -fx-border-width: 1px;");
            detectedIdsBox.setPrefWidth(200);

            Label detectedTitle = new Label("Detected IDs");
            detectedTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

            detectedIdsBox.getChildren().add(detectedTitle);

            // Add both radar and detectedIdsBox in an HBox to place them side by side
            HBox radarAndIds = new HBox(10, radarDisplay, detectedIdsBox);

            container.getChildren().addAll(title, radarAndIds);

            // Keep a reference to update it later
            this.detectedIdsBox = detectedIdsBox;

        } catch (Exception e) {
            System.out.println("Error creating radar panel: " + e.getMessage());
        }

        radarPanel = container; // cache the radar panel
        return radarPanel;
    }

    private VBox createAircraftPanel() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Create form for aircraft entry
        VBox form = new VBox(10);

        // Create labels and fields
        Label idLabel = new Label("Aircraft ID:");
        idLabel.setStyle("-fx-text-fill: white;");
        TextField idField = new TextField();
        idField.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        Label nameLabel = new Label("Aircraft Name:");
        nameLabel.setStyle("-fx-text-fill: white;");
        TextField nameField = new TextField();
        nameField.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-text-fill: white;");
        TextField statusField = new TextField();
        statusField.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        // Create button panel with buttons side by side
        HBox buttonPanel = new HBox(10);
        Button insertBtn = new Button("Insert Aircraft");
        Button deleteBtn = new Button("Delete Aircraft");
        styleFormButton(insertBtn);
        styleFormButton(deleteBtn);
        buttonPanel.getChildren().addAll(insertBtn, deleteBtn);

        // Add all components to the form
        form.getChildren().addAll(
                idLabel, idField,
                nameLabel, nameField,
                statusLabel, statusField,
                buttonPanel
        );

        // Create the table with custom styling
        aircraftTable = new TableView<>();
        aircraftTable.setStyle("-fx-background-color:#444444; -fx-border-color: #00ff00; -fx-border-width: 1px;");

        // Set row factory for hover effects
        aircraftTable.setRowFactory(tv -> {
            TableRow<Aircraft> row = new TableRow<>();
            row.setStyle("-fx-background-color: #444444 -fx-text-fill: white;");

            row.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                if (isNowHovered) {
                    row.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black;");
                } else {
                    row.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
                }
            });
            return row;
        });

        // Create columns with styling



        aircraftTable = new TableView<>();
        TableColumn<Aircraft, String> idCol = new TableColumn<>("Aircraft_ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Aircraft, String> nameCol = new TableColumn<>("Aircraft_Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Aircraft, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        aircraftTable.getColumns().addAll(idCol, nameCol, statusCol);

        // Button actions (unchanged)
        insertBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String status = statusField.getText().trim();
            if(id.isEmpty() || name.isEmpty() || status.isEmpty()) {
                SoundPlayer.play("Sounds/All_fields_required.wav", false);
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required!");
                return;
            }
            insertAircraft(id, name, status);
            loadAircraftTable();
        });

        deleteBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                SoundPlayer.play("Sounds/Please_enter_AircrafID.wav", false);
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter Aircraft ID to delete.");
                return;
            }
            deleteAircraft(id);
            loadAircraftTable();
        });

        loadAircraftTable();

        root.getChildren().addAll(form, aircraftTable);
        return root;
    }
    private VBox createPilotPanel() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Create form for aircraft entry
        VBox form = new VBox(10);

        // Create labels and fields
        Label idLabel = new Label("Pilot ID:");
        idLabel.setStyle("-fx-text-fill: white;");
        TextField idField = new TextField();
        idField.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        Label nameLabel = new Label("Pilot Name:");
        nameLabel.setStyle("-fx-text-fill: white;");
        TextField nameField = new TextField();
        nameField.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-text-fill: white;");
        TextField statusField = new TextField();
        statusField.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        // Create button panel with buttons side by side
        HBox buttonPanel = new HBox(10);
        Button insertBtn = new Button("Insert Pilot");
        Button deleteBtn = new Button("Delete Pilot");
        styleFormButton(insertBtn);
        styleFormButton(deleteBtn);
        buttonPanel.getChildren().addAll(insertBtn, deleteBtn);

        // Add all components to the form
        form.getChildren().addAll(
                idLabel, idField,
                nameLabel, nameField,
                statusLabel, statusField,
                buttonPanel
        );

        pilotTable = new TableView<>();
        TableColumn<Pilot, String> idCol = new TableColumn<>("Pilot_ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Pilot, String> nameCol = new TableColumn<>("Pilot_Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Pilot, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        pilotTable.getColumns().addAll(idCol, nameCol, statusCol);

        insertBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String status = statusField.getText().trim();
            if(id.isEmpty() || name.isEmpty() || status.isEmpty()) {
                SoundPlayer.play("Sounds/All_fields_required.wav", false);
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required!");
                return;
            }
            insertPilot(id, name, status);
            loadPilotTable();

        });

        deleteBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                SoundPlayer.play("Sounds/Please_enter_PilotID.wav", false);
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter Pilot ID to delete.");
                return;
            }
            deletePilot(id);
            loadPilotTable();

        });

        loadPilotTable();

        root.getChildren().addAll(form, pilotTable);
        return root;
    }

    private void styleFormButton(Button btn) {
        btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 8px; -fx-border-color: #00ff00; -fx-border-width: 1px;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #00ff00; -fx-text-fill: black; -fx-font-size: 14px; "
                + "-fx-padding: 8px; -fx-border-color: #00ff00; -fx-border-width: 1px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 8px; -fx-border-color: #00ff00; -fx-border-width: 1px;"));
    }

    public void addDetectedID(String id) {
        if (detectedIdsBox == null) return;
        Platform.runLater(() -> {
            Label idLabel = new Label(id);
            idLabel.setStyle("-fx-text-fill: lime; -fx-font-size: 14px;");
            detectedIdsBox.getChildren().add(idLabel);
        });
    }

    // Rest of the methods remain unchanged...
    private void insertAircraft(String id, String name, String status) {
        String sql = "INSERT INTO Aircraft (Aircraft_ID, Aircraft_Name, Status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            ps.setString(2, name.trim());
            ps.setString(3, status.trim());
            int result = ps.executeUpdate();
            if (result == 1) {
                SoundPlayer.play("Sounds/Aircraft_added_succe.wav", false);
                showAlert(Alert.AlertType.INFORMATION, "System Update", "Aircraft added successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not add aircraft: " + e.getMessage());
        }
    }

    private void deleteAircraft(String id) {
        String sql = "DELETE FROM Aircraft WHERE TRIM(Aircraft_ID) = ?"; //added trim here to remove spaces from the database ID (if added through SQL workbench)
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.trim()); //added trim here to remove spaces from the input
            int result = ps.executeUpdate();
            if (result == 1) {
                SoundPlayer.play("Sounds/Aircraft_deleted_suc.wav", false);
                showAlert(Alert.AlertType.INFORMATION, "System Update", "Aircraft deleted successfully.");
            } else {
                SoundPlayer.play("Sounds/No_aircraft_found.wav", false);
                showAlert(Alert.AlertType.WARNING, "Delete Failed", "No aircraft found with ID: " + id);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not delete aircraft: " + e.getMessage());
        }
    }

    private void insertPilot(String id, String name, String status) {
        String sql = "INSERT INTO Pilot (Pilot_ID, Pilot_Name, Status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.trim());
            ps.setString(2, name.trim());
            ps.setString(3, status.trim());
            int result = ps.executeUpdate();
            if (result == 1) {
                SoundPlayer.play("Sounds/Pilot_added_success.wav", false);
                showAlert(Alert.AlertType.INFORMATION, "System Update", "Pilot added successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not add pilot: " + e.getMessage());
        }
    }

    private void deletePilot(String id) {
        String sql = "DELETE FROM Pilot WHERE TRIM(Pilot_ID) = ?"; //added trim here to remove spaces from the database ID (if added through SQL workbench)
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.trim()); //added trim here to remove spaces from the input
            int result = ps.executeUpdate();
            if (result == 1) {
                SoundPlayer.play("Sounds/Pilot_deleted_succes.wav", false);
                showAlert(Alert.AlertType.INFORMATION, "System Update", "Pilot deleted successfully.");
            } else {
                SoundPlayer.play("Sounds/No_pilot_found.wav", false);
                showAlert(Alert.AlertType.WARNING, "Delete Failed", "No pilot found with ID: " + id);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not delete pilot: " + e.getMessage());
        }
    }

    private void loadAircraftTable() {
        aircraftTable.getItems().clear();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Aircraft")) {

            while (rs.next()) {
                String id = rs.getString("Aircraft_ID");
                String name = rs.getString("Aircraft_Name");
                String status = rs.getString("Status");
                aircraftTable.getItems().add(new Aircraft(id, name, status));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load aircraft data: " + e.getMessage());
        }
    }

    private void loadPilotTable() {
        pilotTable.getItems().clear();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Pilot")) {

            while (rs.next()) {
                String id = rs.getString("Pilot_ID");
                String name = rs.getString("Pilot_Name");
                String status = rs.getString("Status");
                pilotTable.getItems().add(new Pilot(id, name, status));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load pilot data: " + e.getMessage());
        }
    }

    static void showAlert(Alert.AlertType type, String title, String message) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static class Aircraft {
        private final String id;
        private final String name;
        private final String status;

        public Aircraft(String id, String name, String status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getStatus() { return status; }
    }

    public static class Pilot {
        private final String id;
        private final String name;
        private final String status;

        public Pilot(String id, String name, String status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getStatus() { return status; }
    }
}
