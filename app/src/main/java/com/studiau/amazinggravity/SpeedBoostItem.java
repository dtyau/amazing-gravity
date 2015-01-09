package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * @Author: Daniel Au.
 */

public class SpeedBoostItem {

    public SpeedBoostItem(Context context, String colour, boolean rightSide, float radius, float locationX, float locationY, float canvasWidth, float canvasHeight) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_boost);

        explosionParticles = new ArrayList<>();

        while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.add(new ExplosionParticle(colour, SPEED_BOOST_ITEM_EXPLOSION_RADIUS, locationX, locationY, canvasWidth, canvasHeight));

            }

        }

        reset(colour, rightSide, radius, locationX, locationY);

    }

    public void reset(String colour, boolean rightSide, float radius, float locationX, float locationY) {

        lightingColorFilter = new LightingColorFilter(Color.BLACK, Color.parseColor(colour));

        if(rightSide) {

            this.locationX = locationX + (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() / 2);

        } else {

            this.locationX = locationX - (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() / 2);

        }

        this.locationY = locationY - (bitmap.getHeight() / 2);

        for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

            explosionParticles.get(i).reset(colour, SPEED_BOOST_ITEM_EXPLOSION_RADIUS);

        }

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

        float shipLocationX = ship.getLocationX();

        float shipLocationY = ship.getLocationY();

        float shipWidth = ship.getWidth();

        float shipHeight = ship.getHeight();

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

        if (distance < bitmap.getWidth()) {

            ship.incrementSpeedBoostCounter();

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.get(i).setLocation(locationX, locationY);

            }

            collided = true;

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

    private boolean collided;

    private float locationX, locationY;

    private final int DISTANCE_FROM_OBSTACLE_MULTIPLIER = 2;

    private final int AMOUNT_OF_EXPLOSION = 30;

    private final int SPEED_BOOST_ITEM_EXPLOSION_RADIUS = 4;

}