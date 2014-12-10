package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Obstacle {

    public Obstacle(float gameViewCanvasWidth, float gameViewCanvasHeight, Ship ship) {

        random = new Random();

        this.gameViewCanvasWidth = gameViewCanvasWidth;

        this.gameViewCanvasHeight = gameViewCanvasHeight;

        shipLocationX = ship.getLocationX();

        shipLocationY = ship.getLocationY();

        shipWidth = ship.getWidth();

        shipHeight = ship.getHeight();

        reset(ship);

    }

    public void reset(Ship ship) {

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        locationX = random.nextFloat() * gameViewCanvasWidth;

        while (locationX / gameViewCanvasWidth < 0.1 ||
                locationX / gameViewCanvasWidth > 0.4 &&
                        locationX / gameViewCanvasWidth < 0.6 ||
                locationX / gameViewCanvasWidth > 0.9) {

            locationX = random.nextFloat() * gameViewCanvasWidth;

        }

        locationX += (ship.getAngle() / ship.getMaxRotation()) * 0.5 * gameViewCanvasWidth;

        locationY = (-2 * radius) + (random.nextFloat() * -gameViewCanvasHeight);

        speedX = BASE_SPEEDX;

        //speedY = BASE_SPEEDY * (1 - (MASS_TO_SPEEDY_MODIFIER * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS))));
        speedY = BASE_SPEEDY;

        oldSpeedX = 0;

    }

    public void update(Ship ship, float collectiveSpeedX) {

        if ((locationY - radius) > (gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO)) {

            reset(ship);

            ScoreManager.incrementScore();

        }

        updateLocationX(ship, collectiveSpeedX);

        updateSpeedAndLocationY();

        if (checkCollision(ship)) {

            GameView.setGameState(GameView.GameState.GAMEOVER);

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private boolean checkCollision(Ship ship) {

        float shipAngle = (float) Math.toRadians(ship.getAngle());

        // Rotate the circle's center points back (change point of reference)
        float unrotatedLocationX = (float) (Math.cos(shipAngle) * (locationX - shipLocationX) -
                Math.sin(shipAngle) * (locationY - shipLocationY) + shipLocationX);

        float unrotatedLocationY = (float) (Math.sin(shipAngle) * (locationX - shipLocationX) +
                Math.cos(shipAngle) * (locationY - shipLocationY) + shipLocationY);

        // Closest point in the rectangle to the center of circle rotated backwards (unrotated)
        float closestX, closestY;

        // Find the unrotated closest X point from center of circle rotated backwards (unrotated)
        if (unrotatedLocationX < (shipLocationX - (shipWidth / 2))) {

            closestX = shipLocationX - (shipWidth / 2);

        } else if (unrotatedLocationX > (shipLocationX + (shipWidth / 2))) {

            closestX = shipLocationX + (shipWidth / 2);

        } else {

            closestX = unrotatedLocationX;

        }

        // Find the unrotated closest Y point from center of circle rotated backwards (unrotated)
        if (unrotatedLocationY < (shipLocationY - (shipHeight / 2))) {

            closestY = shipLocationY - (shipHeight / 2);

        } else if (unrotatedLocationY > (shipLocationY + (shipHeight / 2))) {

            closestY = shipLocationY + (shipHeight / 2);

        } else {

            closestY = unrotatedLocationY;

        }

        float distance = pythagorizeDistance(unrotatedLocationX, unrotatedLocationY, closestX, closestY);

        if (distance < radius) {

            return true;

        } else {

            return false;

        }

    }

    private float pythagorizeDistance(float fromX, float fromY,
                                      float toX, float toY) {

        float a = Math.abs(fromX - toX);

        float b = Math.abs(fromY - toY);

        return (float) Math.sqrt((a * a) + (b * b));

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

        /*float distanceY = Math.abs(locationY - shipLocationY) /
                shipLocationY;

        float verticalDampening = (float) Math.pow((distanceY - 1), 4);

        if (((locationY) > 0) &&
                ((locationY) < gameViewCanvasHeight)) {

            float newSpeedY = (float) ((1 - (MASS_TO_SPEEDY_MODIFIER * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) *
                    (((-1 * (Math.pow(distanceY, 4))) + 1) / 1000))
                    * verticalDampening;

            speedY += newSpeedY;

        }*/

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

                float verticalDampening = (float) ( 1 - Math.pow((distanceY - 1), 2) );

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

    private float gameViewCanvasWidth, gameViewCanvasHeight,
            shipLocationX, shipLocationY, shipWidth, shipHeight,
            mass, radius, locationX, locationY, speedX, speedY, oldSpeedX;

    private final static int BASE_MASS = 8;

    private final static int MAX_ADDITIONAL_MASS = 8;

    private final static float RADIUS_TO_MASS_RATIO = 14;

    private final static float BASE_SPEEDX = 0f;

    private final static float BASE_SPEEDY = 0.01f;

    private final static float OBSTACLE_KILL_HEIGHT_RATIO = 1.6f;

    private final static float MASS_TO_SPEEDY_MODIFIER = 0.8f;


}