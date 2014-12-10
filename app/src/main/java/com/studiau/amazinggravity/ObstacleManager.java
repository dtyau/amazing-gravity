package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * Author: Daniel Au.
 */

public class ObstacleManager {

    public ObstacleManager(float gameViewCanvasWidth, float gameViewCanvasHeight, Ship ship) {

        blurMaskFilter = new BlurMaskFilter(42, BlurMaskFilter.Blur.OUTER);

        this.gameViewCanvasWidth = gameViewCanvasWidth;

        this.gameViewCanvasHeight = gameViewCanvasHeight;

        reset(ship);

    }

    public void update(Ship ship) {

        float collectiveSpeedX = getCollectiveSpeedX();

        for (Obstacle obstacle : obstacles) {

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

    public void reset(Ship ship) {

        obstacles = null;

        obstacles = new ArrayList<>();

        float locationY = 0;

        for (int i = 0; i < NUMBER_OF_OBSTACLES; i++) {

            Log.d(TAG, "locationY: " + locationY);

            addObstacle(ship);

            obstacles.get(i).setBottomEdgeToLocationYWithMaxRadius(locationY);

            locationY -= ((gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO) / (NUMBER_OF_OBSTACLES - 1));

        }

    }

    private void addObstacle(Ship ship) {

        obstacles.add(new Obstacle(gameViewCanvasWidth, gameViewCanvasHeight, ship));

    }

    private float getCollectiveSpeedX() {

        float collectiveSpeedX = 0f;

        for (Obstacle obstacle : obstacles) {

            collectiveSpeedX += obstacle.getNewSpeedX();

        }

        return collectiveSpeedX;

    }

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

    private float gameViewCanvasWidth, gameViewCanvasHeight;

    public static final int NUMBER_OF_OBSTACLES = 3;

    public static final float OBSTACLE_KILL_HEIGHT_RATIO = 1.6f;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}