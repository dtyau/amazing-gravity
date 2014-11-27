package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Planet {

    public Planet() {

        random = new Random();

        reset();

    }

    private void reset() {

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        locationX = random.nextFloat() * GameView.getCanvasWidth();

        locationY = -2 * radius;

        speedX = BASE_SPEEDX;

        speedY = BASE_SPEEDY;

    }

    public void update(Ship ship) {

        if ( locationY > GameView.getCanvasHeight() + ( 2 * radius ) ) {

            reset();

        }

        float distanceX = Math.abs(locationX - ship.getLocationX()) /
                GameView.getCanvasWidth();

        float newSpeedX = (float) ( ( mass / ( BASE_MASS + MAX_ADDITIONAL_MASS ) ) /
                ( Math.pow( ( distanceX + 1.4), 18 ) ) );

        if ( locationX < ship.getLocationX() ) {

            speedX += newSpeedX;

        } else if ( locationX > ship.getLocationX() ) {

            speedX -= newSpeedX;

        }

        locationX += ( speedX - ship.getSpeedX() ) * GameView.getCanvasWidth();

        float distanceY = Math.abs( locationY - ship.getLocationY() ) /
                GameView.getCanvasHeight();

        float newSpeedY = (float) ( ( mass / ( BASE_MASS + MAX_ADDITIONAL_MASS ) ) /
                ( Math.pow( ( distanceY + 1.4), 18 ) ) );

        speedY += newSpeedY;

        locationY += speedY * GameView.getCanvasHeight();

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.WHITE);

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private Random random;

    private float mass, radius, locationX, locationY, speedX, speedY;

    private final static int BASE_MASS = 6;

    private final static int MAX_ADDITIONAL_MASS = 10;

    private final static float RADIUS_TO_MASS_RATIO = 12;

    private final static float BASE_SPEEDX = 0f;

    private final static float BASE_SPEEDY = 0.005f;

    private final static String TAG = Planet.class.getSimpleName();

}