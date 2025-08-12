package com.flappyBird;

import com.geneticAlgorithm.GeneticClient;
import com.neuralNetwork.Network;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bird implements GeneticClient {
    private Circle bird;
    private double y;
    private double vy;
    private int score = 0;
    private Network network;

    public Bird(double radius, double x, double y) {
        bird = new Circle(radius, Color.YELLOW);
        bird.setCenterX(x);
        this.y = y;
        network = new Network(5, 7, 7, 1);
    }

    public void updatePhysics(double dt, double gravity){
        y += vy * dt + gravity * dt * dt / 2;
        vy += gravity * dt;
    }

    public void updateView(){
        bird.setCenterY(y);
    }

    public void flap(double flapImpulse){
        vy = flapImpulse;
    }

    public Circle getView() {
        return bird;
    }

    public double getY() {
        return y;
    }

    public void updateScore(){
        score++;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public int getScore() {
        return score;
    }

    public void reset(double y) {
        this.y = y;
        updateView();
        vy = 0;
        score = 0;
    }

    public double getVY() {
        return vy;
    }
}
