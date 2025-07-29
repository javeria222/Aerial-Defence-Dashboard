package falconops;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RadarSimulation {
    private static Timeline radarSweep;
    public static double currentSweepAngle = 0;
    public static final double RADAR = 300, CENTER_X = 450, CENTER_Y = 300;
    public final Stage stage;
    Pane root;
    private Polygon sweepTriangle;


    public RadarSimulation(Pane root, Stage stage){
        this.root = root;
        this.stage = stage;
    }

    //      ===============RADAR STATIC COMPONENTS===============
    public void Radar() {
        int numberOfCircles = 3;

        Circle outerCircle = new Circle(CENTER_X, CENTER_Y, RADAR, Color.TRANSPARENT);
        outerCircle.setStroke(Color.LIMEGREEN);
        outerCircle.setStrokeWidth(3);
        root.getChildren().add(outerCircle);

        for (int i = 1; i <= numberOfCircles; i++) {
            int currentRadius = (int) (RADAR - (i * (RADAR /numberOfCircles)));
            Circle innerCircle = new Circle(CENTER_X, CENTER_Y, currentRadius, Color.TRANSPARENT);
            innerCircle.setStroke(Color.LIMEGREEN);
            innerCircle.setStrokeWidth(1);
            innerCircle.setOpacity(0.5);
            root.getChildren().add(innerCircle);

        }
        Line lineX = new Line(CENTER_X - RADAR, CENTER_Y, CENTER_X + RADAR, CENTER_Y);
        lineX.setStroke(Color.LIMEGREEN);
        lineX.setStrokeWidth(1);
        lineX.setOpacity(0.5);
        root.getChildren().add(lineX);

        Line lineY = new Line(CENTER_X, CENTER_Y - RADAR, CENTER_X, CENTER_Y + RADAR);
        lineY.setStroke(Color.LIMEGREEN);
        lineY.setStrokeWidth(1);
        lineY.setOpacity(0.5);
        root.getChildren().add(lineY);
    }

    //      ===============RADAR MOVING SWEEP LINE===============
    public void radarSweepLine() {

        if (radarSweep != null && radarSweep.getStatus() == Animation.Status.RUNNING) {
            return; // already running
        }


        sweepTriangle = new Polygon();
        sweepTriangle.getPoints().addAll(
                450.0, 300.0,
                750.0, 265.0,
                750.0, 350.0
        );
        Stop[] stops = new Stop[] {new Stop(0,Color.TRANSPARENT), new Stop(1,Color.LIMEGREEN)};
        LinearGradient linear = new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,stops);
        sweepTriangle.setEffect(new javafx.scene.effect.GaussianBlur(5));
        sweepTriangle.setFill(linear);
        root.getChildren().add(sweepTriangle);

        Rotate rotate = new Rotate(0, 450, 300);
        sweepTriangle.getTransforms().add(rotate);


        radarSweep = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    currentSweepAngle = 0;
                }, new KeyValue(rotate.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(5), e -> {
                    currentSweepAngle = 360;
                }, new KeyValue(rotate.angleProperty(), 360))
        );

        rotate.angleProperty().addListener((obs, oldVal, newVal) -> {
            currentSweepAngle = newVal.doubleValue();
        });

        radarSweep.setCycleCount(Timeline.INDEFINITE);
        radarSweep.play();
    }


}