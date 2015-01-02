package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Author: Daniel Au
 */

public class Ship {

    public Ship(Context context, float canvasWidth, float canvasHeight, boolean controlInverted) {

        blurMaskFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);

        bitmap_speedBoost = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed_boost);

        bitmap_speedBoostLocationX = 0.03f * canvasWidth;

        matrix = new Matrix();

        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);

        matrix.postTranslate(locationX, locationY);

        locationX = canvasWidth * BASE_LOCATIONX;

        locationY = canvasHeight * BASE_LOCATIONY;

        speedBoostTrigger = canvasHeight * SPEED_BOOST_TRIGGER;

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

        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);

        matrix.postRotate(rotation);

        matrix.postTranslate(locationX, locationY);

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(blurMaskFilter);

        canvas.drawBitmap(bitmap, matrix, paint);

        paint.setMaskFilter(null);

    }

    public void handleActionDownAndMove(float touchEventX, float touchEventY) {

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

                desiredRotation = -MAX_ROTATION * (modifierX);

            } else {

                desiredRotation = MAX_ROTATION * (modifierX);

            }

        } else if (touchEventX > locationX) {

            speedX = -newSpeedX;

            if(controlInverted) {

                desiredRotation = MAX_ROTATION * (modifierX);

            } else {

                desiredRotation = -MAX_ROTATION * (modifierX);

            }

        }

        float distanceY = locationY - touchEventY;

        if (distanceY > speedBoostTrigger) {

            activateSpeedBoost();

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

    public float getBaseSpeedY() {

        return BASE_SPEEDY;

    }

    public float getExhaustLocationX() {

        return locationX - ((rotation / MAX_ROTATION) * (bitmap.getWidth() / 2));

    }

    public float getExhaustLocationY() {

        return locationY +
                ((1 - Math.abs(rotation / MAX_ROTATION)) * (bitmap.getHeight() / 2));

    }

    public float getLocationX() {

        return locationX;

    }

    public float getLocationY() {

        return locationY;

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

    public float getWidth() {

        return bitmap.getWidth();

    }

    public float getHeight() {

        return bitmap.getHeight();

    }

    private BlurMaskFilter blurMaskFilter;

    private Bitmap bitmap, bitmap_speedBoost;

    private Matrix matrix;

    private boolean controlInverted, boosting;

    private int speedBoostCounter;

    private float locationX, locationY, speedX, desiredRotation, rotation, speedBoost,
            speedBoostTrigger, bitmap_speedBoostLocationX, bitmap_speedBoostDefaultLocationY;

    private final float BITMAP_SPEED_BOOST_LOCATION_Y_INCREMENT = 0.5f;

    private final float SPEED_BOOST_TRIGGER = 0.10f;

    private final float INITIAL_SPEED_BOOST = 3;

    private final float SPEED_BOOST_DECREMENT = 0.1f;

    private final float BASE_SPEEDX = 0f;

    private final float MAX_ADDITIONAL_SPEEDX = 0.0044f;

    private final float BASE_SPEEDY = 0.0001f;

    private final float BASE_LOCATIONX = 0.5f;

    private final float BASE_LOCATIONY = 0.75f;

    private final float MAX_ROTATION = 60;

    private final float ROTATION_SPEED = 10;

    private final float MAX_HORIZONTAL_TOUCH = 0.6f;

    private final static String TAG = Ship.class.getSimpleName();

}
