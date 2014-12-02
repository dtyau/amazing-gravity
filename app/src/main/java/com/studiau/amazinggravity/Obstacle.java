package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Obstacle {

    public Obstacle() {

        random = new Random();

        reset();

    }

    public void reset() {

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        locationX = random.nextFloat() * GameView.getCanvasWidth();

        while (locationX / GameView.getCanvasWidth() < 0.1 ||
                locationX / GameView.getCanvasWidth() > 0.4 &&
                        locationX / GameView.getCanvasWidth() < 0.6 ||
                locationX / GameView.getCanvasWidth() > 0.9) {

            locationX = random.nextFloat() * GameView.getCanvasWidth();

        }

        locationY = (-2 * radius) + (random.nextFloat() * -GameView.getCanvasHeight());

        speedX = BASE_SPEEDX;

        speedY = BASE_SPEEDY * (1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS))));

    }

    public void update(Ship ship, float collectiveSpeedX) {

        if (locationY > (GameView.getCanvasHeight() * OBSTACLE_KILL_HEIGHT_RATIO)) {

            reset();

        }

        if (locationX < (0 - (GameView.getCanvasWidth() * OBSTACLE_OFFSCREEN_RATIOX))) {

            locationX = GameView.getCanvasWidth() * (1 + OBSTACLE_OFFSCREEN_RATIOX);

        }

        if (locationX > (GameView.getCanvasWidth() * (1 + OBSTACLE_OFFSCREEN_RATIOX))) {

            locationX = 0 - GameView.getCanvasWidth() * OBSTACLE_OFFSCREEN_RATIOX;

        }

        updateLocationX(ship, collectiveSpeedX);

        updateSpeedAndLocationY(ship);

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private void updateLocationX(Ship ship, float collectiveSpeedX) {

        if ((locationY + radius) > 0) {

            speedX += collectiveSpeedX;

            locationX += (speedX - ship.getSpeedX()) * GameView.getCanvasWidth();

        }

    }

    private void updateSpeedAndLocationY(Ship ship) {

        float distanceY = Math.abs(locationY - ship.getLocationY()) /
                GameView.getCanvasHeight();

        if (locationY > 0) {

            float newSpeedY = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) /
                    (Math.pow((distanceY + 1.4), 17)));

            speedY += newSpeedY;

        }

        locationY += speedY * GameView.getCanvasHeight();

    }

    public float getNewSpeedX(Ship ship) {

        if ((locationY + radius) > 0) {

            float distanceX = Math.abs(locationX - ship.getLocationX()) /
                    GameView.getCanvasWidth();

            float verticalDampening = (float) (1 - (Math.pow((Math.abs(locationY - ship.getLocationY()) /
                    (ship.getLocationY() + (radius))), 0.3)));

            float newSpeedX = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) /
                    (Math.pow((distanceX + 1.4), 17)) *
                    (verticalDampening));

            if (ship.getLocationX() > locationX) {

                return newSpeedX;

            } else {

                return -newSpeedX;

            }

        } else {

            return 0f;

        }

    }

    public float getRadius() {

        return radius;

    }

    public float getLocationX() {

        return locationX;

    }

    public float getLocationY() {

        return locationY;

    }

    private Random random;

    private float mass, radius, locationX, locationY, speedX, speedY;

    private final static int BASE_MASS = 8;

    private final static int MAX_ADDITIONAL_MASS = 16;

    private final static float RADIUS_TO_MASS_RATIO = 24;

    private final static float BASE_SPEEDX = 0f;

    private final static float BASE_SPEEDY = 0.004f;

    private final static float OBSTACLE_KILL_HEIGHT_RATIO = 1.3f;

    private final static float OBSTACLE_OFFSCREEN_RATIOX = 1f;


}
