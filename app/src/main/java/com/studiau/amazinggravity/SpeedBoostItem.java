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

        if (((locationX + (bitmap.getWidth() / 2)) > (ship.getLocationX() - (ship.getWidth() / 2))) &&
                ((locationX - (bitmap.getWidth() / 2)) < (ship.getLocationX() + (ship.getWidth() / 2))) &&
                ((locationY + (bitmap.getHeight() / 2)) > (ship.getLocationY() - (ship.getHeight() / 2))) &&
                ((locationY - (bitmap.getHeight() / 2)) < (ship.getLocationY() + (ship.getHeight() / 2)))) {

            ship.incrementSpeedBoostCounter();

            collided = true;

            while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

                for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                    explosionParticles.add(new ExplosionParticle(colour, SPEED_BOOST_ITEM_EXPLOSION_RADIUS, locationX, locationY, canvasWidth, canvasHeight));

                }

            }

        }

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