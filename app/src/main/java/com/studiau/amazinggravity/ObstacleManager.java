package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

/**
 * Author: Daniel Au
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

        float collectiveNewSpeedX = 0;

        for (Obstacle obstacle: obstacles) {

            if ((obstacle.getLocationY() > (PARTICLE_AFFECTED_Y * gameViewCanvasHeight)) &&
                    (obstacle.getLocationY() < gameViewCanvasHeight)) {

                float distanceX = Math.abs(particle_LocationX - obstacle.getLocationX()) / gameViewCanvasWidth;

                if (distanceX > 1) {

                    distanceX = 1;

                }

                float newSpeedX = (float) ((((-1 * (Math.pow(distanceX, 2))) + 1)) / 400);

                if ((particle_LocationX - obstacle.getLocationX()) > 0) {

                    collectiveNewSpeedX = -newSpeedX;

                } else {

                    collectiveNewSpeedX = newSpeedX;

                }

            }

        }

        return collectiveNewSpeedX;

    }

    public float giveSpeedYForParticles(Ship ship, float particle_LocationY) {

        float collectiveNewSpeedY = 0;

        for (Obstacle obstacle: obstacles) {

            if ((obstacle.getLocationY() > (PARTICLE_AFFECTED_Y * gameViewCanvasHeight)) &&
            (obstacle.getLocationY() < gameViewCanvasHeight)) {

                float distanceY = (particle_LocationY - obstacle.getLocationY()) / gameViewCanvasHeight;

                float newSpeedY = (float) ((((-1 * (Math.pow(Math.abs(distanceY), 2))) + 1)) / 640);

                if (distanceY > 0) {

                    collectiveNewSpeedY = -newSpeedY;

                } else {

                    collectiveNewSpeedY = newSpeedY;

                }

            }

        }

        return collectiveNewSpeedY;

    }

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

    private float gameViewCanvasWidth, gameViewCanvasHeight;

    private final float PARTICLE_AFFECTED_Y = 0.5f;

    public static final int NUMBER_OF_OBSTACLES = 5;

    public static final float OBSTACLE_KILL_HEIGHT_RATIO = 2f;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}