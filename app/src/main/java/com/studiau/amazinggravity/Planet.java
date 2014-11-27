package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Planet {

    public Planet() {

        random = new Random();

        mass = BASE_MASS + random.nextInt(MAX_ADDITIONAL_MASS);

        radius = mass * RADIUS_TO_MASS_RATIO;

        locationX = random.nextFloat() * GameView.getCanvasWidth();

        locationY = -2 * radius;

        speedX = BASE_SPEEDX;

        speedY = BASE_SPEEDY;

    }

    public void update(Ship ship) {

        float distanceX = Math.abs( locationX - ship.getLocationX() );

        float newSpeedX = (float) ( ( mass / ( BASE_MASS + MAX_ADDITIONAL_MASS ) ) /
                ( Math.pow( ( distanceX + 1.2), 18 ) ) );

        if ( locationX < ship.getLocationX() ) {

            speedX += newSpeedX;

        } else if ( locationX > ship.getLocationX() ) {

            speedX -= newSpeedX;

        }

        locationX += speedX;

        float distanceY = Math.abs( locationY - ship.getLocationY() );

        float newSpeedY = (float) ( ( mass / ( BASE_MASS + MAX_ADDITIONAL_MASS ) ) /
                ( Math.pow( ( distanceY + 1.2), 18 ) ) );

        speedY += newSpeedY;

        locationY += speedY;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.WHITE);

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private Random random;

    private float mass, radius, locationX, locationY, speedX, speedY;

    private final static int BASE_MASS = 4;

    private final static int MAX_ADDITIONAL_MASS = 12;

    private final static float RADIUS_TO_MASS_RATIO = 16;

    private final static float BASE_SPEEDX = 0f;

    private final static float BASE_SPEEDY = 0.001f;

}
