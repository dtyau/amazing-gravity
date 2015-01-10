package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * @Author: Daniel Au
 */

public class ObstacleManager {

    public ObstacleManager(Context context, int gameViewCanvasWidth, int gameViewCanvasHeight, Ship ship) {

        this.context = context;

        blurMaskFilter = new BlurMaskFilter(42, BlurMaskFilter.Blur.OUTER);

        ObstacleManager.gameViewCanvasWidth = gameViewCanvasWidth;

        ObstacleManager.gameViewCanvasHeight = gameViewCanvasHeight;

        obstacles = new ArrayList<>();

        reset(ship);

    }

    public void update(Ship ship) {

        float collectiveSpeedX = getCollectiveSpeedX();

        for (Obstacle obstacle : obstacles) {

            obstacle.update(ship, collectiveSpeedX);

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        for (Obstacle obstacle : obstacles) {

            paint.setMaskFilter(blurMaskFilter);

            obstacle.draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    public void reset(Ship ship) {

        obstacles.clear();

        float locationY = 0;

        for (int i = 0; i < NUMBER_OF_OBSTACLES; i++) {

            obstacles.add(new Obstacle(context, gameViewCanvasWidth, gameViewCanvasHeight, ship));

            obstacles.get(i).setBottomEdgeToLocationYWithMaxRadius(locationY);

            locationY -= ((gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO) / (NUMBER_OF_OBSTACLES - 1));

        }

    }

    private float getCollectiveSpeedX() {

        float collectiveSpeedX = 0f;

        for (Obstacle obstacle : obstacles) {

            collectiveSpeedX += obstacle.getNewSpeedX();

        }

        return collectiveSpeedX;

    }

    // For particle interaction

    public boolean checkCollisionForParticles(Ship ship, float particleLocationX, float particleLocationY) {

        boolean collision = false;

        for (Obstacle obstacle: obstacles) {

            if ((obstacle.getLocationY() + obstacle.getRadius()) > ship.getLocationY()) {

                float distanceX = Math.abs(particleLocationX - obstacle.getLocationX());

                float distanceY = Math.abs(particleLocationY - obstacle.getLocationY());

                float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));

                if (distance < obstacle.getRadius()) {

                    collision = true;

                }

            }

        }

        return collision;

    }

    private Context context;

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

    private static int gameViewCanvasWidth, gameViewCanvasHeight;

    public static final int NUMBER_OF_OBSTACLES = 5;

    public static final float OBSTACLE_KILL_HEIGHT_RATIO = 2f;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}