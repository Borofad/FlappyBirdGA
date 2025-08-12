package com.flappyBird;

import com.flappyBird.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FlappyBirdGA extends Application {
    @Override
    public void start(Stage stage) {
        double screenWidth = 600;
        double screenHeight = 500;
        Game game = new Game(screenWidth, screenHeight, 500, 5, 0.0003, -0.15, 30, -0.1, 3000);
        game.start();

        Scene scene = new Scene(game.getRoot(), screenWidth, screenHeight, Color.SKYBLUE);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Flappy Bird");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}