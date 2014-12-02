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

    public ObstacleManager() {

        blurMaskFilter = new BlurMaskFilter(42, BlurMaskFilter.Blur.OUTER);

        obstacles = new ArrayList<Obstacle>();

        for (int i = 0; i < NUMBER_OF_OBSTACLES; i++) {

            addObstacle();

        }

    }

    private void addObstacle() {

        obstacles.add(new Obstacle());

    }

    public void update(Ship ship) {

        float collectiveSpeedX = getCollectiveSpeedX(ship);

        for (Obstacle obstacle : obstacles) {

            while (checkLocation(obstacle)) {

                obstacle.reset();

            }

            obstacle.update(ship, collectiveSpeedX);

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.parseColor("#90CAF9"));

        paint.setMaskFilter(blurMaskFilter);

        for (Obstacle obstacle : obstacles) {

            obstacle.draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    private float getCollectiveSpeedX(Ship ship) {

        float collectiveSpeedX = 0f;

        for (Obstacle obstacle : obstacles) {

            collectiveSpeedX += obstacle.getNewSpeedX(ship);

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

    private static final int NUMBER_OF_OBSTACLES = 1;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}
