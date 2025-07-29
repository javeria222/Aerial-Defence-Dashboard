package falconops;

import javafx.animation.*;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class UserGenerate {
    private final Pane root;
    private final Random random = new Random();
    private Timeline blipTimeline;
    private int blipCounter = 0;
    private final Stage stage;
    private final DB_GUI dbInstance;

    public UserGenerate(Pane root, Stage stage, DB_GUI dbInstance) {
        this.root = root;
        this.stage = stage;
        this.dbInstance = dbInstance;
    }

    public void userAnimation() {

        if (blipTimeline != null && blipTimeline.getStatus() == Animation.Status.RUNNING) {
            return; // already running
        }

        //User goes to center
        blipTimeline = new Timeline();
        Duration delay = Duration.millis(2000);

        for (int i = 0; i<= 9; i++) {
            Duration startTime = delay;
            delay = delay.add(Duration.millis(5000));

            KeyFrame frame = new KeyFrame(startTime, e -> {
                generateUser();
            });

            blipTimeline.getKeyFrames().add(frame);
        }


        blipTimeline.play();
    }

    public void stopUserGeneration() {
        if (blipTimeline != null) {
            blipTimeline.stop();
        }
    }

    private String pickRandomAuthorizedID() {
        List<String> idsFromDB = new ArrayList<>();
        Random random = new Random();

        String query = "SELECT Aircraft_ID FROM Aircraft";  // Adjust table name if needed

        try (Connection conn = DB_GUI.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                idsFromDB.add(rs.getString("Aircraft_ID"));
            }

            if (idsFromDB.isEmpty()) {
                DB_GUI.showAlert(Alert.AlertType.ERROR, "DB Error", "⚠️ No authorized IDs found in the database!");
                return null;
            }

            return idsFromDB.get(random.nextInt(idsFromDB.size()));

        } catch (SQLException e) {
            DB_GUI.showAlert(Alert.AlertType.ERROR, "DB Error", "❌ Failed to fetch authorized IDs: " + e.getMessage());
            return null;
        }
    }


    private void generateUser() {
        blipCounter++;
        boolean unauthorized = false;
        String aircraftID;

        if (blipCounter < 10) {
            aircraftID = pickRandomAuthorizedID();
        }
        else {
            // Load authorized IDs into a set for quick lookup
            Set<String> authorizedSet = new HashSet<>();
            try (Connection conn = DB_GUI.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT Aircraft_ID FROM Aircraft")) {
                while (rs.next()) {
                    authorizedSet.add(rs.getString("Aircraft_ID"));
                }
            } catch (SQLException e) {
                DB_GUI.showAlert(Alert.AlertType.ERROR, "DB Error", "Error fetching authorized IDs: " + e.getMessage());
            }

            // Generate random unauthorized ID
            Random random = new Random();
            do {
                // Generate random ID e.g. single uppercase letter
                char randomChar = (char) ('A' + random.nextInt(26));
                aircraftID = String.valueOf(randomChar);
            } while (authorizedSet.contains(aircraftID)); // ensure it's not in authorized


        }

        double angle = random.nextDouble() * 360; // Random angle
        double distance = RadarSimulation.RADAR + random.nextDouble() * 200; // Random distance outside radar range
        double startX = RadarSimulation.CENTER_X + distance * Math.cos(Math.toRadians(angle));
        double startY = RadarSimulation.CENTER_Y + distance * Math.sin(Math.toRadians(angle));

        Circle blip = new Circle(startX, startY, 6, Color.ORANGE);

        List<Object> userData = new ArrayList<>();
        userData.add(angle);
        userData.add(aircraftID);
        userData.add(unauthorized);
        blip.setUserData(userData);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.ORANGE);
        glow.setRadius(20);
        blip.setEffect(glow);

        root.getChildren().add(blip);

        Timeline moveToCenter = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blip.translateXProperty(), 0),
                        new KeyValue(blip.translateYProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(10),
                        new KeyValue(blip.translateXProperty(), RadarSimulation.CENTER_X - startX),
                        new KeyValue(blip.translateYProperty(), RadarSimulation.CENTER_Y - startY)
                )
        );

        AirCraftDetection detection = new AirCraftDetection(root, dbInstance);
        AnimationTimer collisionChecker = detection.collisionChecker(blip, moveToCenter, glow);

        collisionChecker.start();

        moveToCenter.setOnFinished(e -> {
            root.getChildren().remove(blip);
            collisionChecker.stop();
        });

        moveToCenter.play();
    }
}