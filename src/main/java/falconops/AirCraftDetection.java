package falconops;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.List;

public class AirCraftDetection {
    Pane root;
    private static Clip clip;
    static List<String> authorizedIDs = new ArrayList<>();
    private DB_GUI dbInstance;

    public AirCraftDetection(Pane root, DB_GUI dbInstance) {
        this.root = root;
        this.dbInstance = dbInstance;
    }

    //      ====DETECTED  BY  LINE  SWEEP=========
    public AnimationTimer collisionChecker(Circle blip, Timeline moveToCenter, DropShadow glow) {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                Object data = blip.getUserData();

                if (data instanceof List) {
                    List<?> list = (List<?>) data;
                    double blipAngle = (double) list.get(0);
                    String aircraftID = ((String) list.get(1));

                    double diff = Math.abs(RadarSimulation.currentSweepAngle - blipAngle);
                    if (diff > 180) diff = 360 - diff;

                    double x = blip.getCenterX() + blip.getTranslateX();
                    double y = blip.getCenterY() + blip.getTranslateY();
                    double dx = x - 450;
                    double dy = y - 300;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if ((diff < 2 || diff > 358) && distance <= 300) {
                        try {
                            CompareDB.comparision(aircraftID, authorizedIDs);

                            if (authorizedIDs.contains(aircraftID)) {
                                dbInstance.addDetectedID("✅ Aircraft ID " + aircraftID + " is AUTHORIZED.");
                                blip.setFill(Color.GREEN);
                                glow.setColor(Color.GREEN);
                            } else {
                                dbInstance.addDetectedID("⛔ Aircraft ID " + aircraftID + " is UNAUTHORIZED.");
                                blip.setFill(Color.RED);
                                glow.setColor(Color.RED);
                               // playBuzzerSound();
                                SoundPlayer.play("Sounds/Buzz.wav", true);

                                this.stop(); // Stop AnimationTimer before dialog

                                Platform.runLater(() -> {
                                    // 1. Show alert synchronously (will block here)
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                //    SoundPlayer.play("Sounds/Unauthorized_Approach.wav", false);
                                    alert.setTitle("Unauthorized approach");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Time to prepare the Fantastic Tea… or missiles.");
                                    alert.showAndWait();  // Safe here because we're outside AnimationTimer
                                    SoundPlayer.stopLoopingSound();

                                    LoginController.deployment();

                                });
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        this.stop();
                    }
                }
            }
        };
    }
}