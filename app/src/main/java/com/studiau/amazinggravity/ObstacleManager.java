package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * @Author: Daniel Au
 */

public class ObstacleManager {

    public ObstacleManager(Context context, Ship ship) {

        this.context = context;

        calendar = Calendar.getInstance();

        seed = (long) calendar.get(Calendar.DAY_OF_YEAR);

        seededRandom = new Random(seed);

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

            obstacle.draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    public void reset(Ship ship) {

        seededRandom = new Random(seed);

        obstacles.clear();

        float locationY = 0;

        for (int i = 0; i < NUMBER_OF_OBSTACLES; i++) {

            obstacles.add(new Obstacle(context, ship));

            obstacles.get(i).setBottomEdgeToLocationYWithMaxRadius(locationY);

            locationY -= ((GameView.canvasHeight * OBSTACLE_KILL_HEIGHT_RATIO) / (NUMBER_OF_OBSTACLES - 1));

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

    public boolean checkCollisionForParticles(float particleLocationX, float particleLocationY) {

        for (Obstacle obstacle: obstacles) {

            float radius = obstacle.getRadius();

            float locationX = obstacle.getLocationX();

            float locationY = obstacle.getLocationY();

            if (((locationY + radius) > Ship.locationY) &&
                    ((locationX + radius) > 0) && ((locationX - radius) < GameView.canvasWidth)) {

                float distanceX = Math.abs(particleLocationX - locationX);

                float distanceY = Math.abs(particleLocationY - locationY);

                float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));

                if (distance < radius) {

                    return true;

                }

            }

        }

        return false;

    }

    private Context context;

    private ArrayList<Obstacle> obstacles;

    private final Calendar calendar;

    private long seed;

    public static Random seededRandom;

    public static final int NUMBER_OF_OBSTACLES = 5;

    public static final float OBSTACLE_KILL_HEIGHT_RATIO = 2f;

    private final static String TAG = ObstacleManager.class.getSimpleName();

}