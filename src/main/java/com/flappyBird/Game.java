package com.flappyBird;

import com.geneticAlgorithm.GeneticAlgorithm;
import com.neuralNetwork.NetworkException;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

import java.util.*;

public class Game {
    private double screenWidth;
    private double screenHeight;
    private double gameAreaHeight;
    private double controllerAreaHeight;

    private List<Bird> birds;
    private List<Bird> birdsToRemove;
    private List<Bird> clients;
    private double gravity;
    private double flapImpulse;

    private List<Pipe> pipePool;
    private int pipeIdx;
    private List<Pipe> pipesAhead;
    private List<Pipe> pipesBehind;
    private List<Pipe> pipesToRender;
    private List<Pipe> pipesToRemove;
    private double pipeWidth;
    private double pipeVX;
    private double pipeSpawnTime;
    private double pipeSpawnInterval;

    private Label scoreText;
    private int score = 0;
    private int generalScore = 0;
    private int nextTarget = 10;

    private Slider speedSlider;
    private Label speedText;

    Button stopButton;
    boolean gameInProgress = true;

    private Pane gameArea;
    private Pane controllerArea;

    private Pane root;

    private AnimationTimer gameLoop;
    private final double FIXED_MS_STEP = 16;
    private double accumulator = 0;
    private long lastTime;

    private int gen = 0;

    public Game(double screenWidth, double screenHeight, int birdsCount, double birdRadius, double gravity, double flapImpulse, double pipeWidth, double pipeVX, double pipeSpawnInterval) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        gameAreaHeight = 0.8 * screenHeight;
        controllerAreaHeight = 0.2 * screenHeight;

        birds = new ArrayList<>();
        birdsToRemove = new ArrayList<>();
        clients = new ArrayList<>();
        this.gravity = gravity;
        this.flapImpulse = flapImpulse;

        for (int i = 0; i < birdsCount; i++) {
            Bird bird = new Bird(birdRadius, 0.2 * screenWidth, gameAreaHeight / 2);
            birds.add(bird);
            clients.add(bird);
        }

        pipePool = new ArrayList<>();
        int pipes = (int) (Math.ceil((screenWidth + pipeWidth) / (-pipeVX * pipeSpawnInterval)));
        for(int i = 0; i < pipes; i++){
            pipePool.add(new Pipe(pipeWidth));
        }

        pipesAhead = new ArrayList<>();
        pipesBehind = new ArrayList<>();
        pipesToRender = new ArrayList<>();
        pipesToRemove = new ArrayList<>();
        this.pipeWidth = pipeWidth;
        this.pipeVX = pipeVX;
        this.pipeSpawnInterval = pipeSpawnInterval;

        scoreText = new Label("Score: 0");
        scoreText.setLayoutX(0.05 * screenWidth);
        scoreText.setLayoutY(0.05 * gameAreaHeight);
        scoreText.setStyle("-fx-font-size: 24;");

        gameArea = new Pane(scoreText);
        gameArea.setLayoutX(0);
        gameArea.setLayoutY(0);
        gameArea.setPrefWidth(screenWidth);
        gameArea.setPrefHeight(gameAreaHeight);
        gameArea.setStyle("-fx-background-color: lightblue;");

        speedSlider = new Slider(1, 100, 1);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(10);
        speedSlider.setBlockIncrement(1);
        speedSlider.setLayoutX(0.2 * screenWidth);
        speedSlider.setLayoutY(0);
        speedSlider.setPrefWidth(0.8 * screenWidth);
        speedSlider.setPrefHeight(0.1 * controllerAreaHeight);

        speedText = new Label("Speed multiplier: 1");
        speedText.setLayoutX(0);
        speedText.setLayoutY(0);
        speedText.setPrefWidth(0.2 * screenWidth);
        speedText.setPrefHeight(0.1 * controllerAreaHeight);
        speedText.setStyle("-fx-font-size: 14;");
        speedText.textProperty().bind(Bindings.format("Speed: %.1fx", speedSlider.valueProperty()));

        stopButton = new Button("Stop/Play");
        stopButton.setLayoutX(0.4 * screenWidth);
        stopButton.setLayoutY(0.11 * gameAreaHeight);
        stopButton.setPrefWidth(0.2 * screenWidth);
        stopButton.setPrefHeight(0.08 * gameAreaHeight);
        stopButton.setOnAction(actionEvent -> {
            if (gameInProgress) {
                gameLoop.stop();
                lastTime = 0;
            } else {
                gameLoop.start();
            }

            gameInProgress = !gameInProgress;
        });

        controllerArea = new Pane(speedText, speedSlider, stopButton);
        controllerArea.setLayoutX(0);
        controllerArea.setLayoutY(gameAreaHeight);
        controllerArea.setPrefWidth(screenWidth);
        controllerArea.setPrefHeight(controllerAreaHeight);

        root = new Pane(gameArea, controllerArea);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if(lastTime == 0){
                    lastTime = l;
                    return;
                }

                double realDt = (double) (l - lastTime) / 1000000;
                lastTime = l;

                double simDt = realDt * speedSlider.getValue();
                accumulator += simDt;

                while(accumulator >= FIXED_MS_STEP) {
                    try {
                        updatePhysics();
                    } catch (NetworkException e) {
                        throw new RuntimeException(e);
                    }

                    accumulator -= FIXED_MS_STEP;
                }

                updateView();
            }
        };
    }

    private void updatePhysics() throws NetworkException {
        pipeSpawnTime += FIXED_MS_STEP;
        if (pipeSpawnTime >= pipeSpawnInterval) {
            spawnPipe(screenWidth);
            pipeSpawnTime = 0;
        }

        for (Pipe pipe : pipesAhead) {
            pipe.updatePhysics(FIXED_MS_STEP, pipeVX);
        }

        Iterator<Pipe> pipesBehindIterator = pipesBehind.iterator();
        while (pipesBehindIterator.hasNext()) {
            Pipe pipe = pipesBehindIterator.next();
            pipe.updatePhysics(FIXED_MS_STEP, pipeVX);

            if (pipe.isOffScreen(pipeWidth)) {
                if(!pipesToRender.remove(pipe)) pipesToRemove.add(pipe);

                pipesBehindIterator.remove();
            }
        }

        Iterator<Bird> birdIterator = birds.iterator();
        while (birdIterator.hasNext()) {
            Bird bird = birdIterator.next();

            boolean died = false;

            if (bird.getNetwork().calculate(new double[] {bird.getVY(), bird.getY(), pipesAhead.get(0).getX() - 0.2 * screenWidth, pipesAhead.get(0).getTopPipe().getHeight(), pipesAhead.get(0).getBottomPipe().getHeight()})[0] >= 0.5) {
                bird.flap(flapImpulse);
            }

            bird.updatePhysics(FIXED_MS_STEP, gravity);

            if (bird.getY() < 0 || bird.getY() >= gameAreaHeight) {
                died = true;
                birdsToRemove.add(bird);
                birdIterator.remove();
            }

            if (!died && ((!pipesBehind.isEmpty() && checkCollision(bird, pipesBehind.get(pipesBehind.size() - 1))) || checkCollision(bird, pipesAhead.get(0)))) {
                birdsToRemove.add(bird);
                birdIterator.remove();
            }
        }

        if (!birds.isEmpty() && !pipesAhead.isEmpty() && checkThrough(pipesAhead.get(0))) {
            scoreText.setText("Score: " + ++score);
            generalScore += birds.size();

            for(Bird bird : birds) bird.updateScore();

            pipesBehind.add(pipesAhead.remove(0));
        }

        if (birds.isEmpty()) {
            System.out.println("Gen: " + gen++ + ", Avg score: " + (double) generalScore / clients.size() + ", Max score: " + score);

            while (score >= nextTarget) {
                nextTarget *= 1.5;
                GeneticAlgorithm.setMutationRate(GeneticAlgorithm.getMutationRate() * 0.9);
                GeneticAlgorithm.setMutationStrength(GeneticAlgorithm.getMutationStrength() * 0.9);
            }

            GeneticAlgorithm.evolve(clients);
            birds.addAll(clients);
            reset();
            start();
        }
    }

    private void updateView() {
        for(Bird bird : birds){
            bird.updateView();
        }

        for(Pipe pipe : pipesAhead){
            pipe.updateView();
        }

        for(Pipe pipe : pipesBehind){
            pipe.updateView();
        }

        for(Bird bird : birdsToRemove){
            gameArea.getChildren().remove(bird.getView());
        }
        birdsToRemove.clear();

        for(Pipe pipe : pipesToRemove){
            gameArea.getChildren().remove(pipe.getView());
        }
        pipesToRemove.clear();

        for(Pipe pipe : pipesToRender){
            gameArea.getChildren().add(pipe.getView());
        }
        pipesToRender.clear();
    }

    private void reset() {
        gameLoop.stop();

        gameArea.getChildren().clear();

        for (Bird bird : clients) {
            bird.reset(gameAreaHeight / 2);
        }

        pipesBehind.clear();
        pipesAhead.clear();
        pipesToRender.clear();
        pipesToRemove.clear();

        score = 0;
        generalScore = 0;
        scoreText.setText("Score: 0");
        gameArea.getChildren().add(scoreText);

        lastTime = 0;

        pipeIdx = 0;
    }

    private boolean checkThrough(Pipe pipe) {
        return 0.2 * screenWidth >= pipe.getX();
    }

    private boolean checkCollision(Bird bird, Pipe pipe){
        double bx = 0.2 * screenWidth;
        double by = bird.getY();
        double r = bird.getView().getRadius();

        double px = pipe.getX();
        double py1 = 0;
        double py2 = pipe.getBottomPipe().getY();
        double ph1 = pipe.getTopPipe().getHeight();
        double ph2 = pipe.getBottomPipe().getHeight();

        return circleIntersectsRectangle(bx, by, r, px, py1, pipeWidth, ph1) || circleIntersectsRectangle(bx, by, r, px, py2, pipeWidth, ph2);
    }

    private boolean circleIntersectsRectangle(double cx, double cy, double r, double rx, double ry, double rw, double rh) {
        double closestX = cx < rx ? rx : (Math.min(cx, rx + rw));
        double closestY = cy < ry ? ry : (Math.min(cy, ry + rh));

        double dx = cx - closestX;
        double dy = cy - closestY;

        return dx * dx + dy * dy <= r * r;
    }

    private void spawnPipe(double x) {
        Pipe pipe = pipePool.get(pipeIdx++);
        if(pipeIdx == pipePool.size()) pipeIdx = 0;

        pipe.reset(x, gameAreaHeight);

        pipesAhead.add(pipe);
        pipesToRender.add(pipe);

        scoreText.toFront();
    }

    public void start(){
        for (Bird bird : birds) {
            gameArea.getChildren().add(bird.getView());
        }

        double pipeGap = -pipeVX * pipeSpawnInterval;
        double firstPipeGap = (pipeWidth + pipeGap) / 2;
        double pipeSpawnArea = 0.8 * screenWidth - firstPipeGap;
        while (pipeSpawnArea >= 0) {
            spawnPipe(screenWidth - pipeSpawnArea);
            pipeSpawnArea -= pipeGap;
        }
        pipeSpawnTime = ((pipeGap + pipeSpawnArea) / pipeGap) * pipeSpawnInterval;

        for(Pipe pipe : pipesToRender){
            gameArea.getChildren().add(pipe.getView());
        }
        pipesToRender.clear();

        gameLoop.start();
    }

    public Pane getRoot(){
        return root;
    }
}
