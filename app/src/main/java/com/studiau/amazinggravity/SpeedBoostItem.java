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

    public SpeedBoostItem(Context context, String colour, boolean rightSide, float radius,
                          float locationX, float locationY) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_boost);

        explosionParticles = new ArrayList<>();

        while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.add(new ExplosionParticle(colour, SPEED_BOOST_ITEM_EXPLOSION_RADIUS,
                        locationX, locationY));

            }

        }

        reset(colour, rightSide, radius, locationX, locationY);

    }

    public void reset(String colour, boolean rightSide, float radius, float locationX, float locationY) {

        lightingColorFilter = new LightingColorFilter(Color.BLACK, Color.parseColor(colour));

        if (rightSide) {

            this.locationX = locationX + (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() * 0.5f);

        } else {

            this.locationX = locationX - (DISTANCE_FROM_OBSTACLE_MULTIPLIER * radius) - (bitmap.getWidth() * 0.5f);

        }

        this.locationY = locationY - (bitmap.getHeight() * 0.5f);

        for (ExplosionParticle explosionParticle : explosionParticles) {

            explosionParticle.reset(colour, SPEED_BOOST_ITEM_EXPLOSION_RADIUS);

        }

        collided = false;

    }

    public void updateLocationX(float speedX) {

        if (!collided) {

            locationX += speedX;

        } else {

            for (ExplosionParticle explosionParticle : explosionParticles) {

                explosionParticle.update();

            }

        }

    }

    public void updateLocationY(float speedY) {

        if (!collided) {

            locationY += speedY;

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        if (((locationX + (bitmap.getWidth() * 0.5f)) > 0) &&
                ((locationX - (bitmap.getWidth() * 0.5f)) < GameView.canvasWidth) &&
                ((locationY + (bitmap.getHeight() * 0.5f)) > 0) &&
                ((locationY - (bitmap.getHeight() * 0.5f)) < GameView.canvasHeight)) {

            paint.setMaskFilter(null);

            if (!collided) {

                paint.setColorFilter(lightingColorFilter);

                canvas.drawBitmap(bitmap, locationX, locationY, paint);

                paint.setColorFilter(null);

            } else {

                for (ExplosionParticle explosionParticle : explosionParticles) {

                    explosionParticle.draw(canvas, paint);

                }

            }

        }

    }

    public void checkCollision(Ship ship) {

        if (((locationX + bitmap.getWidth()) > (Ship.locationX - Ship.width)) &&
                ((locationX - bitmap.getWidth()) < (Ship.locationX + Ship.width))) {

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

            if (distance < bitmap.getWidth()) {

                ship.incrementSpeedBoostCounter();

                for (ExplosionParticle explosionParticle : explosionParticles) {

                    explosionParticle.setLocation(locationX, locationY);

                }

                collided = true;

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

    private volatile boolean collided;

    private volatile float locationX, locationY;

    private static final int DISTANCE_FROM_OBSTACLE_MULTIPLIER = 2;

    private static final int AMOUNT_OF_EXPLOSION = 30;

    private static final int SPEED_BOOST_ITEM_EXPLOSION_RADIUS = 4;

}