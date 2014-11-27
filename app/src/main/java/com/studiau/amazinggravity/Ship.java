package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Ship {

    public Ship() {

        radius = BASE_RADIUS;

        locationX = BASE_LOCATIONX;

        locationY = BASE_LOCATIONY;

        speedX = BASE_SPEEDX;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.WHITE);

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    public void handleActionDownAndMove(float touchEventX) {

        float distanceX = Math.abs( locationX - touchEventX );

        float newSpeedX = BASE_SPEEDX * ( 1 + ( distanceX / GameView.getCanvasWidth() ) );

        if ( touchEventX < locationX ) {

            speedX = newSpeedX;

        } else if ( touchEventX > locationX) {

            speedX = -newSpeedX;

        }

    }

    public void handleActionUp() {

        speedX = BASE_SPEEDX;

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

    private float radius, locationX, locationY, speedX;

    private static final float BASE_RADIUS = 24;

    private static final float BASE_SPEEDX = 0.004f;

    private static final float BASE_SPEEDY = 0.001f;

    private static final float BASE_LOCATIONX = ( GameView.getCanvasWidth() * 0.5f );

    private static final float BASE_LOCATIONY = ( GameView.getCanvasHeight() * 0.8f );


}
