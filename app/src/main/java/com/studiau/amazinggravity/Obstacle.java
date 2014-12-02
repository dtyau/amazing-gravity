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

        oldSpeedX = 0;

    }

    public void update(Ship ship, float collectiveSpeedX) {

        if ((locationY - radius) > (GameView.getCanvasHeight() * OBSTACLE_KILL_HEIGHT_RATIO)) {

            reset();

        }

        updateLocationX(ship, collectiveSpeedX);

        updateSpeedAndLocationY(ship);

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private void updateLocationX(Ship ship, float collectiveSpeedX) {

        if ((locationY + radius) > 0) {

            float verticalDampening = (float) Math.pow(
                    ((Math.abs(locationY - ship.getLocationY()) / ship.getLocationY()) - 1), 2);

            speedX += (collectiveSpeedX * verticalDampening);

            locationX += (speedX - ship.getSpeedX()) * GameView.getCanvasWidth();

        }

    }

    private void updateSpeedAndLocationY(Ship ship) {

        float distanceY = Math.abs(locationY - ship.getLocationY()) /
                ship.getLocationY();

        float verticalDampening = (float) Math.pow(
                ((Math.abs(locationY - ship.getLocationY()) / ship.getLocationY()) - 1), 4);

        if (((locationY + radius) > 0) &&
                ((locationY - radius) < GameView.getCanvasHeight())) {

            float newSpeedY = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) *
                    ((-Math.pow(distanceY, 4) + 1) / 500)) *
                    verticalDampening;

            speedY += newSpeedY;

        }

        locationY += speedY * GameView.getCanvasHeight();

    }

    public float getNewSpeedX(Ship ship) {

        if (((locationY + radius) > 0) &&
                ((locationY - radius) < GameView.getCanvasHeight()) &&
                ((locationX + radius) > 0) &&
                ((locationX - radius) < GameView.getCanvasWidth())) {

            float distanceX = Math.abs(locationX - ship.getLocationX()) /
                    GameView.getCanvasWidth();

            float newSpeedX = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) *
                    (((-1 * (Math.pow(distanceX, 4))) + 1) / 1000));

            if (locationX < ship.getLocationX()) {

                oldSpeedX = newSpeedX;

                return newSpeedX;

            } else {

                oldSpeedX = (-1 * newSpeedX);

                return (-1 * newSpeedX);

            }

        } else {

            return oldSpeedX;

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

    private float mass, radius, locationX, locationY, speedX, speedY, oldSpeedX;

    private final static int BASE_MASS = 8;

    private final static int MAX_ADDITIONAL_MASS = 10;

    private final static float RADIUS_TO_MASS_RATIO = 18;

    private final static float BASE_SPEEDX = 0f;

    private final static float BASE_SPEEDY = 0.006f;

    private final static float OBSTACLE_KILL_HEIGHT_RATIO = 2f;

    private final static float OBSTACLE_OFFSCREEN_RATIOX = 1f;


}