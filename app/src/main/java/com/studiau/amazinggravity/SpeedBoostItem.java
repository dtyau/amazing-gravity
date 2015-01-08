package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * @Author: Daniel Au.
 */

public class SpeedBoostItem {

    public SpeedBoostItem(Context context, String colour, float radius, float locationX, float locationY, float canvasWidth, float canvasHeight) {

        this.colour = colour;

        lightingColorFilter = new LightingColorFilter(Color.BLACK, Color.parseColor(this.colour));

        this.canvasWidth = canvasWidth;

        this.canvasHeight = canvasHeight;

        random = new Random();

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_boost);

        if(random.nextBoolean()) {

            this.locationX = locationX + (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() / 2);

        } else {

            this.locationX = locationX - (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() / 2);

        }

        this.locationY = locationY - (bitmap.getHeight() / 2);

        explosionParticles = new ArrayList<>();

        collided = false;

    }

    public void updateLocationX(float speedX) {

        if(!collided) {

            locationX += speedX;

        } else {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.get(i).update();

            }

        }

    }

    public void updateLocationY(float speedY) {

        if(!collided) {

            locationY += speedY;

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(null);

        if(!collided) {

            paint.setColorFilter(lightingColorFilter);

            canvas.drawBitmap(bitmap, locationX, locationY, paint);

            paint.setColorFilter(null);

        } else {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) { // For explosion

                explosionParticles.get(i).draw(canvas, paint);

            }

        }

    }

    public void checkCollision(Ship ship) {

        float shipAngle = (float) Math.toRadians(ship.getAngle());

        // Rotate the circle's center points back (change point of reference)
        float unrotatedLocationX = (float) (Math.cos(shipAngle) * (locationX - ship.getLocationX()) -
                Math.sin(shipAngle) * (locationY - ship.getLocationY()) + ship.getLocationX());

        float unrotatedLocationY = (float) (Math.sin(shipAngle) * (locationX - ship.getLocationX()) +
                Math.cos(shipAngle) * (locationY - ship.getLocationY()) + ship.getLocationY());

        // Closest point in the rectangle to the center of circle rotated backwards (unrotated)
        float closestX, closestY;

        // Find the unrotated closest X point from center of circle rotated backwards (unrotated)
        if (unrotatedLocationX < (ship.getLocationX() - (ship.getWidth() / 2))) {

            closestX = ship.getLocationX() - (ship.getWidth() / 2);

        } else if (unrotatedLocationX > (ship.getLocationX() + (ship.getWidth() / 2))) {

            closestX = ship.getLocationX() + (ship.getWidth() / 2);

        } else {

            closestX = unrotatedLocationX;

        }

        // Find the unrotated closest Y point from center of circle rotated backwards (unrotated)
        if (unrotatedLocationY < (ship.getLocationY() - (ship.getHeight() / 2))) {

            closestY = ship.getLocationY() - (ship.getHeight() / 2);

        } else if (unrotatedLocationY > (ship.getLocationY() + (ship.getHeight() / 2))) {

            closestY = ship.getLocationY() + (ship.getHeight() / 2);

        } else {

            closestY = unrotatedLocationY;

        }

        float distance = pythagorizeDistance(unrotatedLocationX, unrotatedLocationY, closestX, closestY);

        if (distance < bitmap.getWidth()) {

            ship.incrementSpeedBoostCounter();

            collided = true;

            while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

                for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                    explosionParticles.add(new ExplosionParticle(colour, SPEED_BOOST_ITEM_EXPLOSION_RADIUS, locationX, locationY, canvasWidth, canvasHeight));

                }

            }

        }

    }

    private float pythagorizeDistance(float fromX, float fromY, float toX, float toY) {

        float a = Math.abs(fromX - toX);

        float b = Math.abs(fromY - toY);

        return (float) Math.sqrt((a * a) + (b * b));

    }

    public Boolean isCollided() {

        return collided;

    }

    private ArrayList<ExplosionParticle> explosionParticles;

    private Bitmap bitmap;

    private LightingColorFilter lightingColorFilter;

    private Random random;

    private boolean collided;

    private String colour;

    private float locationX, locationY, canvasWidth, canvasHeight;

    private final int DISTANCE_FROM_OBSTACLE_MULTIPLIER = 2;

    private final int AMOUNT_OF_EXPLOSION = 30;

    private final int SPEED_BOOST_ITEM_EXPLOSION_RADIUS = 4;

}