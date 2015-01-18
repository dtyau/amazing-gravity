package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * @Author: Daniel Au
 */

public class Ship {

    public Ship(Context context, boolean controlInverted) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);

        bitmap_speedBoost = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_boost);

        bitmap_speedBoostLocationX = (int) (0.03f * GameView.canvasWidth);

        bitmap_speedBoostDefaultLocationY = (int) (BITMAP_SPEED_BOOST_DEFAULT_LOCATION_Y * GameView.canvasHeight);

        bitmap_speedBoostLocationYIncrement = (int) (BITMAP_SPEED_BOOST_LOCATION_Y_INCREMENT * GameView.canvasHeight);

        matrix = new Matrix();

        matrix.postTranslate(-bitmap.getWidth() * 0.5f, -bitmap.getHeight() * 0.5f);

        matrix.postTranslate(locationX, locationY);

        locationX = (int) (GameView.canvasWidth * BASE_LOCATIONX);

        locationY = (int) (GameView.canvasHeight * BASE_LOCATIONY);

        width = bitmap.getWidth();

        height = bitmap.getHeight();

        leftEdge = locationX - (int) (width * 0.5f);

        rightEdge = locationX + (int) (width * 0.5f);

        topEdge = locationY - (int) (height * 0.5f);

        bottomEdge = locationY + (int) (height * 0.5f);

        this.controlInverted = controlInverted;

        reset();

    }

    public void update() {

        if (speedBoost > 1) {

            speedBoost -= SPEED_BOOST_DECREMENT;

            if(speedBoost < 1) {

                speedBoost = 1;

                boosting = false;

            }

        }

        if (rotation < desiredRotation &&
                (rotation + ROTATION_SPEED) <= desiredRotation) {

            rotation += ROTATION_SPEED;

        } else if (rotation > desiredRotation &&
                (rotation - ROTATION_SPEED) >= desiredRotation) {

            rotation -= ROTATION_SPEED;

        }

        matrix.reset();

        matrix.postTranslate(-bitmap.getWidth() * 0.5f, -bitmap.getHeight() * 0.5f);

        matrix.postRotate(rotation);

        matrix.postTranslate(locationX, locationY);

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(null);

        canvas.drawBitmap(bitmap, matrix, paint);

        int drawSpeedBoostCounter = speedBoostCounter;

        while (drawSpeedBoostCounter > 0) {

            float speedBoostLocationY = bitmap_speedBoostDefaultLocationY +
                    (bitmap_speedBoostLocationYIncrement * drawSpeedBoostCounter);

            canvas.drawBitmap(bitmap_speedBoost, bitmap_speedBoostLocationX, speedBoostLocationY, paint);

            drawSpeedBoostCounter--;

        }

    }

    public void handleActionDownAndMove(float touchEventX) {

        float distanceX = Math.abs(locationX - touchEventX) /
                locationX;

        if (distanceX > MAX_HORIZONTAL_TOUCH) {

            distanceX = MAX_HORIZONTAL_TOUCH;

        }

        float modifierX = (1 / MAX_HORIZONTAL_TOUCH) * distanceX;

        float newSpeedX = BASE_SPEEDX + (MAX_ADDITIONAL_SPEEDX * modifierX);

        if (touchEventX < locationX) {

            speedX = newSpeedX;

            if(controlInverted) {

                desiredRotation = (int) (-MAX_ROTATION * (modifierX));

            } else {

                desiredRotation = (int) (MAX_ROTATION * (modifierX));

            }

        } else if (touchEventX > locationX) {

            speedX = -newSpeedX;

            if(controlInverted) {

                desiredRotation = (int) (MAX_ROTATION * (modifierX));

            } else {

                desiredRotation = (int) (-MAX_ROTATION * (modifierX));

            }

        }

    }

    public void handleActionUp() {

        speedX = BASE_SPEEDX;

        desiredRotation = 0;

    }

    public void reset() {

        speedX = BASE_SPEEDX;

        rotation = 0;

        desiredRotation = 0;

        speedBoost = 1;

        boosting = false;

    }

    public void activateSpeedBoost() {

        if(!boosting && (speedBoostCounter >= 1)) {

            speedBoost = INITIAL_SPEED_BOOST;

            boosting = true;

            speedBoostCounter--;

        }

    }

    public void setSpeedBoostCounter(int speedBoostCounter) {

        this.speedBoostCounter = speedBoostCounter;

    }

    public void incrementSpeedBoostCounter() {

        speedBoostCounter++;

    }

    public boolean isBoosting() {

        return boosting;

    }

    public float getExhaustLocationX() {

        return locationX - ((rotation / MAX_ROTATION) * (bitmap.getWidth() * 0.25f));

    }

    public float getExhaustLocationY() {

        return locationY +
                ((1 - Math.abs(rotation / MAX_ROTATION)) * (bitmap.getHeight() * 0.5f));

    }

    public float getSpeedX() {

        if(controlInverted) {

            return -(speedX * speedBoost);

        }

        return (speedX * speedBoost);

    }

    public float getAngle() {

        return rotation;

    }

    public float getMaxRotation() {

        return MAX_ROTATION;

    }

    private Bitmap bitmap, bitmap_speedBoost;

    private Matrix matrix;

    private volatile boolean controlInverted, boosting;

    private volatile int speedBoostCounter, desiredRotation, rotation, bitmap_speedBoostLocationX,
            bitmap_speedBoostDefaultLocationY, bitmap_speedBoostLocationYIncrement;

    private volatile float speedX, speedBoost;

    public volatile static int locationX, locationY, leftEdge, rightEdge, topEdge, bottomEdge, width, height;

    private static final float BITMAP_SPEED_BOOST_DEFAULT_LOCATION_Y = 0.01f;

    private static final float BITMAP_SPEED_BOOST_LOCATION_Y_INCREMENT = 0.05f;

    private static final int INITIAL_SPEED_BOOST = 3;

    private static final float SPEED_BOOST_DECREMENT = 0.1f;

    private static final float BASE_SPEEDX = 0f;

    private static final float MAX_ADDITIONAL_SPEEDX = 0.0044f;

    public static final float BASE_SPEEDY = 0.0001f;

    private static final float BASE_LOCATIONX = 0.5f;

    private static final float BASE_LOCATIONY = 0.75f;

    private static final float MAX_ROTATION = 60;

    private static final int ROTATION_SPEED = 10;

    private static final float MAX_HORIZONTAL_TOUCH = 0.6f;

    private final static String TAG = Ship.class.getSimpleName();

}
