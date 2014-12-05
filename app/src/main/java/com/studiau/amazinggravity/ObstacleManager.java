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

    public ObstacleManager(float gameViewCanvasWidth, float gameViewCanvasHeight,
                           float shipLocationX, float shipLocationY,
                           float shipWidth, float shipHeight) {

        addObstacleCounter = OBSTACLE_INCREMENT;

        blurMaskFilter = new BlurMaskFilter(42, BlurMaskFilter.Blur.OUTER);

        this.gameViewCanvasWidth = gameViewCanvasWidth;

        this.gameViewCanvasHeight = gameViewCanvasHeight;

        this.shipLocationX = shipLocationX;

        this.shipLocationY = shipLocationY;

        this.shipWidth = shipWidth;

        this.shipHeight = shipHeight;

        reset();

    }

    public void update(Ship ship) {

        checkScore();

        float collectiveSpeedX = getCollectiveSpeedX();

        for (Obstacle obstacle : obstacles) {

            while (checkLocation(obstacle)) {

                obstacle.reset();

            }

            obstacle.update(ship, collectiveSpeedX);

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(blurMaskFilter);

        for (Obstacle obstacle : obstacles) {

            obstacle.draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    public void reset() {

        obstacles = null;

        obstacles = new ArrayList<Obstacle>();

        addObstacleCounter = OBSTACLE_INCREMENT;

        addObstacle();

    }

    private void addObstacle() {

        obstacles.add(new Obstacle(gameViewCanvasWidth, gameViewCanvasHeight,
                shipLocationX, shipLocationY, shipWidth, shipHeight));

    }

    private void checkScore() {

        if (ScoreManager.getScore() >= addObstacleCounter) {

            addObstacle();

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

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

    private float gameViewCanvasWidth, gameViewCanvasHeight, shipLocationX, shipLocationY, shipWidth, shipHeight;

    private int addObstacleCounter;

    private final int OBSTACLE_INCREMENT = 3;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}
