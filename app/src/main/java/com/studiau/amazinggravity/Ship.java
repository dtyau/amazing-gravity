package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Author: Daniel Au.
 */

public class Ship {

    public Ship(Context context) {

        radius = BASE_RADIUS;

        locationX = BASE_LOCATIONX;

        locationY = BASE_LOCATIONY;

        speedX = BASE_SPEEDX;

        desiredRotation = 0;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);

        matrix = new Matrix();

        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);

        matrix.postTranslate(locationX, locationY);

    }

    public void update() {

        if ( desiredRotation > rotation ) {

            rotation += ROTATION_SPEED;

        } else if ( desiredRotation < rotation ) {

            rotation -= ROTATION_SPEED;

        }

        matrix.reset();

        matrix.postTranslate( -bitmap.getWidth() / 2, -bitmap.getHeight() / 2 );

        matrix.postRotate( rotation );

        matrix.postTranslate( locationX, locationY );

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawBitmap(bitmap, matrix, paint);

    }

    public void handleActionDownAndMove(float touchEventX) {

        float distanceX = Math.abs( locationX - touchEventX );

        float newSpeedX = BASE_SPEEDX + ( MAX_ADDITIONAL_SPEEDX * ( distanceX / GameView.getCanvasWidth() ) );

        if ( touchEventX < locationX ) {

            speedX = newSpeedX;

            desiredRotation = MAX_ROTATION * (distanceX / GameView.getCanvasWidth() );

        } else if ( touchEventX > locationX) {

            speedX = -newSpeedX;

            desiredRotation = -MAX_ROTATION * (distanceX / GameView.getCanvasWidth() );

        }

    }

    public void handleActionUp() {

        speedX = BASE_SPEEDX;

        desiredRotation = 0;

    }

    public float getBaseSpeedY() {

        return BASE_SPEEDY;

    }

    public float getLocationX() {

        return locationX;

    }

    public float getLocationY() {

        return locationY;

    }

    public float getSpeedX() {

        return speedX;

    }

    private Bitmap bitmap;

    private Matrix matrix;

    private float radius, locationX, locationY, speedX, desiredRotation, rotation;

    private static final float BASE_RADIUS = 24;

    private static final float BASE_SPEEDX = 0f;

    private static final float MAX_ADDITIONAL_SPEEDX = 0.008f;

    private static final float BASE_SPEEDY = 0.001f;

    private static final float BASE_LOCATIONX = ( GameView.getCanvasWidth() * 0.5f );

    private static final float BASE_LOCATIONY = ( GameView.getCanvasHeight() * 0.8f );

    private static final float MAX_ROTATION = 120;

    private static final float ROTATION_SPEED = 5;

}
