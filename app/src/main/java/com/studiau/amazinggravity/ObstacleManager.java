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

    public ObstacleManager(Context context, float gameViewCanvasWidth, float gameViewCanvasHeight, Ship ship) {

        this.context = context;

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

        for (Obstacle obstacle : obstacles) {

            paint.setMaskFilter(blurMaskFilter);

            obstacle.draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    public void reset(Ship ship) {

        obstacles = null;

        obstacles = new ArrayList<>();

        float locationY = 0;

        for (int i = 0; i < NUMBER_OF_OBSTACLES; i++) {

            addObstacle(ship);

            obstacles.get(i).setBottomEdgeToLocationYWithMaxRadius(locationY);

            locationY -= ((gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO) / (NUMBER_OF_OBSTACLES - 1));

        }

    }

    private void addObstacle(Ship ship) {

        obstacles.add(new Obstacle(context, gameViewCanvasWidth, gameViewCanvasHeight, ship));

    }

    private float getCollectiveSpeedX() {

        float collectiveSpeedX = 0f;

        for (Obstacle obstacle : obstacles) {

            collectiveSpeedX += obstacle.getNewSpeedX();

        }

        return collectiveSpeedX;

    }

    // For particle interaction

    public boolean checkCollisionForParticles(Ship ship, float particle_LocationX, float particle_LocationY) {

        boolean collision = false;

        for (Obstacle obstacle: obstacles) {

            if ((obstacle.getLocationY() + obstacle.getRadius()) > ship.getLocationY()) {

                float distanceX = Math.abs(particle_LocationX - obstacle.getLocationX());

                float distanceY = Math.abs(particle_LocationY - obstacle.getLocationY());

                float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceY* distanceY));

                if (distance < obstacle.getRadius()) {

                    collision = true;

                }

            }

        }

        return collision;

    }

    public float giveSpeedXForParticles(Ship ship, float particle_LocationX) {

        float newSpeedX = 0;

        for (Obstacle obstacle: obstacles) {

            if ((obstacle.getLocationY() > (PARTICLE_AFFECTED_Y * gameViewCanvasHeight)) &&
                    (obstacle.getLocationY() < gameViewCanvasHeight)) {

                float distanceX = Math.abs(particle_LocationX - obstacle.getLocationX()) / gameViewCanvasWidth;

                if (distanceX > 1) {

                    distanceX = 1;

                }

                newSpeedX = (float) ((((-1 * (Math.pow(distanceX, 2))) + 1)) / 200);

                if ((particle_LocationX - obstacle.getLocationX()) > 0) {

                    return -newSpeedX;

                } else {

                    return newSpeedX;

                }

            }

        }

        return newSpeedX;

    }

    private Context context;

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

    private float gameViewCanvasWidth, gameViewCanvasHeight;

    private final float PARTICLE_AFFECTED_Y = 0.5f;

    public static final int NUMBER_OF_OBSTACLES = 5;

    public static final float OBSTACLE_KILL_HEIGHT_RATIO = 2f;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}