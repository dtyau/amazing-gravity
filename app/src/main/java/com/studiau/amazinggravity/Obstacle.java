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

    public Obstacle(Context context, Ship ship) {

        random = new Random();

        RADIUS_TO_MASS_RATIO = GameView.canvasWidth * 0.01f;

        MAX_RADIUS = (BASE_MASS + MAX_ADDITIONAL_MASS) * RADIUS_TO_MASS_RATIO;

        colour = setObstacleColour();

        speedBoostItem = new SpeedBoostItem(context, colour, random.nextBoolean(), radius, locationX, locationY);

        reset(ship);

    }

    public void reset(Ship ship) {

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        float randomFloat = random.nextFloat();

        boolean randomBoolean = random.nextBoolean();

        if (randomBoolean) {

            locationX = ((randomFloat * 0.3f) + 0.1f) * GameView.canvasWidth;

        } else {

            locationX = ((randomFloat * 0.3f) + 0.6f) * GameView.canvasWidth;

        }

        locationX += (ship.getAngle() / ship.getMaxRotation()) *
                MAX_HORIZONTAL_LOCATION_SHIFT * GameView.canvasWidth;

        locationY = -(GameView.canvasHeight * ObstacleManager.OBSTACLE_KILL_HEIGHT_RATIO) /
                (ObstacleManager.NUMBER_OF_OBSTACLES - 1);

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

        if ((locationY - radius) > Ship.bottomEdge) {

            if (!scored) {

                ScoreManager.incrementScore();

            }

            scored = true;

        }

        if ((locationY) > (GameView.canvasHeight * ObstacleManager.OBSTACLE_KILL_HEIGHT_RATIO)) {

            reset(ship);

        }

        updateLocationX(ship, collectiveSpeedX);

        updateLocationY();

        if(((locationY + radius) > (Ship.locationY - Ship.height)) &&
                ((locationY - radius) < (Ship.locationY + Ship.height))) {

            if(((locationX + radius) > (Ship.locationX - Ship.width)) &&
                    ((locationX - radius) < (Ship.locationX + Ship.width))) {

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

        if(((locationX + radius) > 0) && ((locationX - radius) < GameView.canvasWidth) &&
                ((locationY + radius) > 0) && ((locationY - radius) < GameView.canvasHeight)) {

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
        float unrotatedLocationX = (float) (Math.cos(shipAngle) * (locationX - Ship.locationX) -
                Math.sin(shipAngle) * (locationY - Ship.locationY) + Ship.locationX);

        float unrotatedLocationY = (float) (Math.sin(shipAngle) * (locationX - Ship.locationX) +
                Math.cos(shipAngle) * (locationY - Ship.locationY) + Ship.locationY);

        // Closest point in the rectangle to the center of circle rotated backwards (unrotated)
        float closestX, closestY;

        // Find the unrotated closest X point from center of circle rotated backwards (unrotated)
        if (unrotatedLocationX < Ship.leftEdge) {

            closestX = Ship.leftEdge;

        } else if (unrotatedLocationX > Ship.rightEdge) {

            closestX = Ship.rightEdge;

        } else {

            closestX = unrotatedLocationX;

        }

        // Find the unrotated closest Y point from center of circle rotated backwards (unrotated)
        if (unrotatedLocationY < Ship.topEdge) {

            closestY = Ship.topEdge;

        } else if (unrotatedLocationY > Ship.bottomEdge) {

            closestY = Ship.bottomEdge;

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
                    ((Math.abs(locationY - Ship.locationY) / Ship.locationY) - 1), 2) *
                    0.9) + 0.1);

            speedX += (collectiveSpeedX * verticalDampening);

            locationX += (speedX - ship.getSpeedX()) * GameView.canvasWidth;

            if(linkedWithSpeedBoost) {

                speedBoostItem.updateLocationX((speedX - ship.getSpeedX()) * GameView.canvasWidth);

            }

        }

    }

    private void updateLocationY() {

        locationY += speedY * GameView.canvasHeight;

        if(linkedWithSpeedBoost) {

            speedBoostItem.updateLocationY(speedY * GameView.canvasHeight);

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

                return "#F06292"; // pink

            case 3:

                return "#4FC3F7"; // light blue

            case 4:

                return "#AEEA00"; // lime

            case 5:

                return "#EF5350"; // red

            case 6:

                return "#4DB6AC"; // teal

            case 7:

                return "#B39DDB"; // deep purple

        }

    }

    public void setBottomEdgeToLocationYWithMaxRadius(float desiredLocationY) {

        locationY = desiredLocationY - MAX_RADIUS;

    }

    public float getNewSpeedX() {

        if (((locationY + radius) > 0) &&
                ((locationY - radius) < GameView.canvasHeight) &&
                ((locationX + radius) > 0) &&
                ((locationX - radius) < GameView.canvasWidth)) {

            float distanceX = Math.abs(locationX - Ship.locationX) /
                    Ship.locationX;

            float newSpeedX = (float) ((1 - (0.9f * (mass / (BASE_MASS + MAX_ADDITIONAL_MASS)))) *
                    (((-1 * (Math.pow(distanceX, 4))) + 1) * 0.0005f));

            if (locationX < Ship.locationX) {

                oldSpeedX = newSpeedX;

                return newSpeedX;

            } else {

                oldSpeedX = (-1 * newSpeedX);

                return (-1 * newSpeedX);

            }

        } else {

            float distanceY = (Math.abs(locationY - Ship.locationY) /
                    ((GameView.canvasHeight * ObstacleManager.OBSTACLE_KILL_HEIGHT_RATIO) - Ship.locationY));

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

    private float mass, radius, locationX, locationY, speedX, speedY, oldSpeedX;

    private boolean scored, linkedWithSpeedBoost;

    private String colour;

    private final float RADIUS_TO_MASS_RATIO;

    private final float MAX_RADIUS;

    private static final int COLOUR_CHANGE_SCORE_INCREMENT = 10;

    private static final int NUMBER_OF_COLOURS = 7;

    private static final int BASE_MASS = 10;

    private static final int MAX_ADDITIONAL_MASS = 5;

    private static final float BASE_SPEEDX = 0f;

    private static final float BASE_SPEEDY = 0.006f;

    private static final float MAX_HORIZONTAL_LOCATION_SHIFT = 0.3f;

    private static final float CHANCE_FOR_SPEED_BOOST_ITEM = 0.2f;

}