package com.flappyBird;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Pipe {
    private Rectangle topPipe;
    private Rectangle bottomPipe;
    private Group view;
    private double x;
    private double topY;
    private double bottomY;
    private double topH;
    private double bottomH;
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

        topPipe.setY(0);
        topPipe.setHeight(topH);

        bottomPipe.setY(bottomY);
        bottomPipe.setHeight(bottomH);

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

        topY = 0;
        topH = gapY;

        bottomY = gapHeight + gapY;
        bottomH = gameAreaHeight - (gapHeight + gapY);
    }

    public void setX(double x){
        this.x = x;
    }

    public double getTopY() {
        return topY;
    }

    public double getBottomY() {
        return bottomY;
    }

    public double getTopH() {
        return topH;
    }

    public double getBottomH() {
        return bottomH;
    }
}
