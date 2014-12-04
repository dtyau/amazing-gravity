package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Obstacle {

    public Obstacle(float gameViewCanvasWidth, float gameViewCanvasHeight,
                    float shipLocationX, float shipLocationY) {

        random = new Random();

        this.gameViewCanvasWidth = gameViewCanvasWidth;

        this.gameViewCanvasHeight = gameViewCanvasHeight;

        this.shipLocationX = shipLocationX;

        this.shipLocationY = shipLocationY;

        reset();

    }

    public void reset() {

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        locationX = random.nextFloat() * gameViewCanvasWidth;

        while (locationX / gameViewCanvasWidth < 0.1 ||
                locationX / gameViewCanvasWidth > 0.4 &&
                        locationX / gameViewCanvasWidth < 0.6 ||
                locationX / gameViewCanvasWidth > 0.9) {

            locationX = random.nextFloat() * gameViewCanvasWidth;

        }

        locationY = (-2 * radius) + (random.nextFloat() * -gameViewCanvasHeight);

        speedX = BASE_SPEEDX;

        speedY = BASE_SPEEDY * (1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS))));

        oldSpeedX = 0;

    }

    public void update(Ship ship, float collectiveSpeedX) {

        if ((locationY - radius) > (gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO)) {

            reset();

            ScoreManager.incrementScore();

        }

        updateLocationX(ship, collectiveSpeedX);

        updateSpeedAndLocationY();

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private void updateLocationX(Ship ship, float collectiveSpeedX) {

        if ((locationY + radius) > 0) {

            float verticalDampening = (float) ((Math.pow(
                    ((Math.abs(locationY - shipLocationY) / shipLocationY) - 1), 2) *
                    0.9) + 0.1);

            speedX += (collectiveSpeedX * verticalDampening);

            locationX += (speedX - ship.getSpeedX()) * gameViewCanvasWidth;

        }

    }

    private void updateSpeedAndLocationY() {

        float distanceY = Math.abs(locationY - shipLocationY) /
                shipLocationY;

        float verticalDampening = (float) Math.pow((distanceY - 1), 4);

        if (((locationY) > 0) &&
                ((locationY) < gameViewCanvasHeight)) {

            float newSpeedY = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) *
                    (((-1 * (Math.pow(distanceY, 4))) + 1) / 1000))
                    * verticalDampening;

            speedY += newSpeedY;

        }

        locationY += speedY * gameViewCanvasHeight;

    }

    public float getNewSpeedX() {

        if (((locationY + radius) > 0) &&
                ((locationY - radius) < gameViewCanvasHeight) &&
                ((locationX + radius) > 0) &&
                ((locationX - radius) < gameViewCanvasWidth)) {

            float distanceX = Math.abs(locationX - shipLocationX) /
                    shipLocationX;

            float newSpeedX = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) *
                    (((-1 * (Math.pow(distanceX, 4))) + 1) / 2000));

            if (locationX < shipLocationX) {

                oldSpeedX = newSpeedX;

                return newSpeedX;

            } else {

                oldSpeedX = (-1 * newSpeedX);

                return (-1 * newSpeedX);

            }

        } else {

            float distanceY = (Math.abs(locationY - shipLocationY) /
                    ((gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO) - shipLocationY));

            if (distanceY <= 1) {

                float verticalDampening = (float) Math.pow((distanceY - 1), 2);

                oldSpeedX -= oldSpeedX * verticalDampening;

                return oldSpeedX;

            } else {

                return oldSpeedX;

            }

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

    private float gameViewCanvasWidth, gameViewCanvasHeight, shipLocationX, shipLocationY,
            mass, radius, locationX, locationY, speedX, speedY, oldSpeedX;

    private final static int BASE_MASS = 6;  // was 8

    private final static int MAX_ADDITIONAL_MASS = 8;

    private final static float RADIUS_TO_MASS_RATIO = 18;

    private final static float BASE_SPEEDX = 0f;

    private final static float BASE_SPEEDY = 0.006f;

    private final static float OBSTACLE_KILL_HEIGHT_RATIO = 1.6f;

    private final static float OBSTACLE_OFFSCREEN_RATIOX = 1f;


}