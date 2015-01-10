package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * @Author: Daniel Au
 */

public class Obstacle {

    public Obstacle(Context context, float gameViewCanvasWidth, float gameViewCanvasHeight, Ship ship) {

        random = new Random();

        this.gameViewCanvasWidth = gameViewCanvasWidth;

        this.gameViewCanvasHeight = gameViewCanvasHeight;

        RADIUS_TO_MASS_RATIO = gameViewCanvasWidth / 100;

        MAX_RADIUS = (BASE_MASS + MAX_ADDITIONAL_MASS) * RADIUS_TO_MASS_RATIO;

        shipLocationX = ship.getLocationX();

        shipLocationY = ship.getLocationY();

        shipWidth = ship.getWidth();

        shipHeight = ship.getHeight();

        colour = setObstacleColour();

        speedBoostItem = new SpeedBoostItem(context, colour, random.nextBoolean(), radius, locationX, locationY,
                gameViewCanvasWidth, gameViewCanvasHeight);

        reset(ship);

    }

    public void reset(Ship ship) {

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        float randomFloat = random.nextFloat();

        boolean randomBoolean = random.nextBoolean();

        if (randomBoolean) {

            locationX = ((randomFloat * 0.3f) + 0.1f) * gameViewCanvasWidth;

        } else {

            locationX = ((randomFloat * 0.3f) + 0.6f) * gameViewCanvasWidth;

        }

        locationX += (ship.getAngle() / ship.getMaxRotation()) *
                MAX_HORIZONTAL_LOCATION_SHIFT * gameViewCanvasWidth;

        locationY = -(gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO) / (NUMBER_OF_OBSTACLES - 1);

        speedX = BASE_SPEEDX;

        speedY = BASE_SPEEDY;

        oldSpeedX = 0;

        colour = setObstacleColour();

        scored = false;

        if(ScoreManager.getScore() > 1) {

            if (randomFloat < CHANCE_FOR_SPEED_BOOST_ITEM) {

                linkedWithSpeedBoost = true;

                speedBoostItem.reset(colour, randomBoolean, radius, locationX, locationY);

            } else {

                linkedWithSpeedBoost = false;

            }

        }

    }

    public void update(Ship ship, float collectiveSpeedX) {

        if ((locationY - radius) > (shipLocationY + ship.getHeight())) {

            if (!scored) {

                ScoreManager.incrementScore();

            }

            scored = true;

        }

        if ((locationY) > (gameViewCanvasHeight * OBSTACLE_KILL_HEIGHT_RATIO)) {

            reset(ship);

        }

        updateLocationX(ship, collectiveSpeedX);

        updateLocationY();

        if(((locationY + radius) > (shipLocationY - shipHeight)) &&
                ((locationY - radius) < (shipLocationY + shipHeight))) {

            if(((locationX + radius) > (shipLocationX - shipWidth)) &&
                    ((locationX - radius) < (shipLocationX + shipWidth))) {

                if (checkCollision(ship)) {

                    GameView.setGameState(GameView.GameState.GAMEOVER);

                }

            }

            if (linkedWithSpeedBoost && !speedBoostItem.isCollided()) {

                speedBoostItem.checkCollision(ship);

            }

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        if(((locationX + radius) > 0) && ((locationX - radius) < gameViewCanvasWidth) &&
                ((locationY + radius) > 0) && ((locationY - radius) < gameViewCanvasHeight)) {

            paint.setColor(Color.parseColor(colour));

            canvas.drawCircle(locationX, locationY, radius, paint);

        }

        if(linkedWithSpeedBoost) {

            speedBoostItem.draw(canvas, paint);

        }

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

        return (distance < radius);

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

            if(linkedWithSpeedBoost) {

                speedBoostItem.updateLocationX((speedX - ship.getSpeedX()) * gameViewCanvasWidth);

            }

        }

    }

    private void updateLocationY() {

        locationY += speedY * gameViewCanvasHeight;

        if(linkedWithSpeedBoost) {

            speedBoostItem.updateLocationY(speedY * gameViewCanvasHeight);

        }

    }

    private String setObstacleColour() {

        int scoreTier = (ScoreManager.getScore() + 2 ) / COLOUR_CHANGE_SCORE_INCREMENT;

        if (scoreTier > NUMBER_OF_COLOURS) {

            scoreTier %= (NUMBER_OF_COLOURS + 1);

        }

        switch (scoreTier) {

            default:

                return "#FFF176"; // default is yellow

            case 0:

                return "#FFF176"; // yellow

            case 1:

                return "#81C784"; // green

            case 2:

                return "#4FC3F7"; // light blue

            case 3:

                return "#F06292"; // pink

            case 4:

                return "#4DB6AC"; // teal

            case 5:

                return "#9575CD"; // deep purple

        }

    }

    public void setBottomEdgeToLocationYWithMaxRadius(float desiredLocationY) {

        locationY = desiredLocationY - MAX_RADIUS;

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

    private SpeedBoostItem speedBoostItem;

    private float gameViewCanvasWidth, gameViewCanvasHeight,
            shipLocationX, shipLocationY, shipWidth, shipHeight,
            mass, radius, locationX, locationY, speedX, speedY, oldSpeedX;

    private boolean scored, linkedWithSpeedBoost;

    private String colour;

    private final int COLOUR_CHANGE_SCORE_INCREMENT = 10;

    private final int NUMBER_OF_COLOURS = 5;

    private final int BASE_MASS = 10;

    private final int MAX_ADDITIONAL_MASS = 5;

    private final float RADIUS_TO_MASS_RATIO;

    private final float MAX_RADIUS;

    private final float BASE_SPEEDX = 0f;

    private final float BASE_SPEEDY = 0.006f;

    private final float MAX_HORIZONTAL_LOCATION_SHIFT = 0.3f;

    private final float CHANCE_FOR_SPEED_BOOST_ITEM = 0.2f;

    private final int NUMBER_OF_OBSTACLES = ObstacleManager.NUMBER_OF_OBSTACLES;

    private final float OBSTACLE_KILL_HEIGHT_RATIO = ObstacleManager.OBSTACLE_KILL_HEIGHT_RATIO;

}