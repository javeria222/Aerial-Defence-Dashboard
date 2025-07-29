package falconops;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

import static falconops.DB_GUI.showAlert;

public class SpaceInvaders {

    private Pane root = new Pane();
    private AnimationTimer gameLoop;
    private double t = 0;

    private Sprite player = new Sprite(300, 750, 40, 40, "player", Color.BLUE);

    Sprite s = new Sprite(100, 150, 30, 30, "enemy", Color.RED);

    //      ===============GAME BASIC COMPONENTS===============
    private Parent createContent() {
        root.setPrefSize(600, 800);

        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        root.getChildren().add(player);
        root.getChildren().add(s);

        TranslateTransition translate = new TranslateTransition(Duration.millis(3000));
        translate.setToX(500);
        translate.setNode(s);
        translate.setCycleCount(Animation.INDEFINITE);
        translate.setAutoReverse(true);
        translate.setInterpolator(Interpolator.LINEAR);
        translate.play();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        gameLoop.start();

        return root;
    }


    private List<Sprite> sprites() {
        return root.getChildren().stream().map(n -> (Sprite)n).collect(Collectors.toList());
    }


    private void update() {
        t += 0.016;

        sprites().forEach(s -> {
            switch (s.type) {

                case "enemybullet":
                    s.moveDown();

                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        player.dead = true;
                        s.dead = true;
                    }
                    break;

                case "playerbullet":
                    s.moveUp();

                    if (s.getBoundsInParent().intersects(this.s.getBoundsInParent())) {
                        this.s.dead = true;
                        s.dead = true;
                    }
                    break;

                case "enemy":

                    if (t > 1) {
                        if (Math.random() < 0.5) {
                            shoot(s);
                        }
                    }

                    break;
            }
        });

        root.getChildren().removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.dead;
        });

        if(player.dead){
            gameLoop.stop(); // stop the animation loop
            Platform.runLater(() -> {
                SoundPlayer.play("Sounds/Mission_Failed.wav", false);
                showAlert(Alert.AlertType.INFORMATION, "Mission Failed", "Looks like the enemy had better aim today!");
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });
            return;
        }
        if(s.dead){
            gameLoop.stop();
            Platform.runLater(() -> {
                SoundPlayer.play("Sounds/YOU_WON.wav", false);
                showAlert(Alert.AlertType.INFORMATION, "YOU WON", "Looks like you have a guest to offer Fantastic Tea!");
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });
            return;
        }

        if (t > 1) {
            t = 0;
        }
    }

    private void shoot(Sprite who) {
        Sprite s = new Sprite((int) who.getTranslateX() + 20, (int) who.getTranslateY(), 5, 20, who.type + "bullet", Color.WHITE);

        root.getChildren().add(s);
    }

    public void startGame(Stage stage){
        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    player.moveLeft();
                    break;
                case RIGHT:
                    player.moveRight();
                    break;
                case UP:
                    player.moveUp();
                    break;
                case DOWN:
                    player.moveDown();
                    break;
                case SPACE:
                    shoot(player);
                    break;
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    private static class Sprite extends Rectangle {
        boolean dead = false;
        final String type;

        Sprite(int x, int y, int w, int h, String type, Color color) {
            super(w, h, color);

            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
        }

        void moveLeft() {
            setTranslateX(getTranslateX() - 10);
        }

        void moveRight() {
            setTranslateX(getTranslateX() + 10);
        }

        void moveUp() {
            setTranslateY(getTranslateY() - 10);
        }

        void moveDown() {
            setTranslateY(getTranslateY() + 10);
        }
    }
}