package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * Author: Daniel Au.
 */

public class ObstacleManager {

    public ObstacleManager(float gameViewCanvasWidth, float gameViewCanvasHeight, Ship ship) {

        addObstacleCounter = OBSTACLE_INCREMENT;

        blurMaskFilter = new BlurMaskFilter(42, BlurMaskFilter.Blur.OUTER);

        this.gameViewCanvasWidth = gameViewCanvasWidth;

        this.gameViewCanvasHeight = gameViewCanvasHeight;

        reset(ship);

    }

    public void update(Ship ship) {

        checkScore(ship);

        float collectiveSpeedX = getCollectiveSpeedX();

        for (Obstacle obstacle : obstacles) {

            if (obstacle.getLocationY() - obstacle.getRadius() > gameViewCanvasHeight) {

                obstacles.remove(obstacle);

            }

            if (isCanvasEmpty()) {

                addObstacle(ship);

            }

            while (checkLocation(obstacle)) {

                obstacle.reset(ship);

            }

            obstacle.update(ship, collectiveSpeedX);

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(blurMaskFilter);

        paint.setColor(Color.parseColor(getObstacleColour()));

        for (Obstacle obstacle : obstacles) {

            obstacle.draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    public void reset(Ship ship) {

        obstacles = null;

        obstacles = new ArrayList<>();

        addObstacleCounter = OBSTACLE_INCREMENT;

        addObstacle(ship);

    }

    private void addObstacle(Ship ship) {

        obstacles.add(new Obstacle(gameViewCanvasWidth, gameViewCanvasHeight, ship));

    }

    private Boolean isCanvasEmpty() {

        for (Obstacle obstacle : obstacles) {

            if ((obstacle.getLocationY() + obstacle.getRadius()) > (gameViewCanvasHeight * 0.5)) {

                return false;

            }

        }

        return true;

    }

    private void checkScore(Ship ship) {

        if (ScoreManager.getScore() >= addObstacleCounter) {

            addObstacle(ship);

            addObstacleCounter += OBSTACLE_INCREMENT * obstacles.size();

        }

    }

    private float getCollectiveSpeedX() {

        float collectiveSpeedX = 0f;

        for (Obstacle obstacle : obstacles) {

            collectiveSpeedX += obstacle.getNewSpeedX();

        }

        return collectiveSpeedX;

    }

    private boolean checkLocation(Obstacle obstacle) {

        float radius = obstacle.getRadius();

        float locationX = obstacle.getLocationX();

        float locationY = obstacle.getLocationY();

        if ((locationY + radius) < 0) {

            for (Obstacle obstacle2 : obstacles) {

                float radius2 = obstacle2.getRadius();

                float locationX2 = obstacle2.getLocationX();

                float locationY2 = obstacle2.getLocationY();

                if (((locationX - radius) < (locationX2 + radius2) &&
                        (locationX - radius) > (locationX2 - radius2)) &&

                        ((locationY - radius) < (locationY2 + radius) &&
                                (locationY - radius) > (locationY2 - radius)) ||

                        ((locationY + radius) > (locationY2 - radius) &&
                                (locationY + radius) < (locationY2 + radius))) {

                    return true;

                }

                if (((locationX + radius) > (locationX2 - radius2) &&
                        (locationX + radius) < (locationX2 + radius2)) &&

                        ((locationY - radius) < (locationY2 + radius) &&
                                (locationY - radius) > (locationY2 - radius)) ||

                        ((locationY + radius) > (locationY2 - radius) &&
                                (locationY + radius) < (locationY2 + radius))) {

                    return true;

                }

            }

        }

        return false;

    }

    private String getObstacleColour() {

        int numberOfObstacles = obstacles.size();

        switch (numberOfObstacles) {

            default:

                return "#FFEB3B"; // default is yellow

            case 1:

                return "#FFEB3B"; // yellow

            case 2:

                return "#4CAF50"; // green

            case 3:

                return "#03A9F4"; // light blue

            case 4:

                return "#F44336"; // red

            case 5:

                return "#009688"; // teal

            case 6:

                return "#673AB7"; // deep purple

        }

    }

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

    private float gameViewCanvasWidth, gameViewCanvasHeight;

    private int addObstacleCounter;

    private final int OBSTACLE_INCREMENT = 3;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}