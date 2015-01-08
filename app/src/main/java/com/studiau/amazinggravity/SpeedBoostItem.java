package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import java.util.Random;

/**
 * @Author: Daniel Au.
 */

public class SpeedBoostItem {

    public SpeedBoostItem(Context context, String colour, float radius, float locationX, float locationY) {

        lightingColorFilter = new LightingColorFilter(Color.BLACK, Color.parseColor(colour));

        random = new Random();

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_boost);

        if(random.nextBoolean()) {

            this.locationX = locationX + (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() / 2);

        } else {

            this.locationX = locationX - (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() / 2);

        }

        this.locationY = locationY - (bitmap.getHeight() / 2);

        collided = false;

    }

    public void updateLocationX(float speedX) {

        if(!collided) {

            locationX += speedX;

        }

    }

    public void updateLocationY(float speedY) {

        if(!collided) {

            locationY += speedY;

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        if(!collided) {

            paint.setMaskFilter(null);

            paint.setColorFilter(lightingColorFilter);

            canvas.drawBitmap(bitmap, locationX, locationY, paint);

            paint.setColorFilter(null);

        }

    }

    public void checkCollision(Ship ship) {

        if (((locationX + (bitmap.getWidth() / 2)) > (ship.getLocationX() - (ship.getWidth() / 2))) &&
                ((locationX - (bitmap.getWidth() / 2)) < (ship.getLocationX() + (ship.getWidth() / 2))) &&
                ((locationY + (bitmap.getHeight() / 2)) > (ship.getLocationY() - (ship.getHeight() / 2))) &&
                ((locationY - (bitmap.getHeight() / 2)) < (ship.getLocationY() + (ship.getHeight() / 2)))) {

            ship.incrementSpeedBoostCounter();

            collided = true;

        }

    }

    public Boolean isCollided() {

        return collided;

    }

    private Bitmap bitmap;

    private LightingColorFilter lightingColorFilter;

    private Random random;

    private boolean collided;

    private float locationX, locationY;

    private final int DISTANCE_FROM_OBSTACLE_MULTIPLIER = 2;

}