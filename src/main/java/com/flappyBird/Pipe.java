package com.flappyBird;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Pipe {
    private Rectangle topPipe;
    private Rectangle bottomPipe;
    private Group view;
    private double x;

    public Pipe(double width){
        topPipe = new Rectangle(width, 0, Color.GREEN);
        bottomPipe = new Rectangle(width, 0, Color.GREEN);

        view = new Group(topPipe, bottomPipe);
    }

    public void updatePhysics(double dt, double vx){
        x += vx * dt;
    }

    public void updateView(){
        topPipe.setX(x);
        bottomPipe.setX(x);
    }

    public Group getView() {
        return view;
    }

    public Rectangle getTopPipe(){
        return topPipe;
    }

    public Rectangle getBottomPipe(){
        return bottomPipe;
    }

    public boolean isOffScreen(double width) {
        return x + width < 0;
    }

    public double getX() {
        return x;
    }

    public void reset(double x, double gameAreaHeight) {
        this.x = x;

        double gapHeight = Math.random() * (gameAreaHeight / 3 - gameAreaHeight / 4) + gameAreaHeight / 4;
        double gapY = Math.random() * (gameAreaHeight - gapHeight);

        topPipe.setY(0);
        topPipe.setHeight(gapY);

        bottomPipe.setY(gapHeight + gapY);
        bottomPipe.setHeight(gameAreaHeight - (gapHeight + gapY));

        updateView();
    }
}
