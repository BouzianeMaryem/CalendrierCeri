package com.example.calendrier_ceri_ines_maryem;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root1 = FXMLLoader.load(getClass().getResource("login/fxml/login1.fxml"));
        Parent root2 = FXMLLoader.load(getClass().getResource("login/fxml/login2.fxml"));

        StackPane stackPane = new StackPane();

        stackPane.getChildren().addAll(root2, root1);

        Scene scene = new Scene(stackPane);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), root1);
        scaleTransition.setToX(0);
        scaleTransition.setToY(0);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), root1);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.6));

        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);

        parallelTransition.setOnFinished(e -> stackPane.getChildren().remove(root1));

        SequentialTransition sequentialTransition = new SequentialTransition(pause, parallelTransition);
        Image icon = new Image(getClass().getResourceAsStream("login/img/logo.png"));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();

        sequentialTransition.play();
    }

    public static void main(String[] args) {
        launch();
    }
}