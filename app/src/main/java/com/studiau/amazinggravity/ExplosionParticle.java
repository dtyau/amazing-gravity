package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class ExplosionParticle {

    public ExplosionParticle(Ship ship, float canvasWidth, float canvasHeight) {

        random = new Random();

        gameViewCanvasWidth = canvasWidth;

        gameViewCanvasHeight = canvasHeight;

        reset(ship);

    }

    private void reset(Ship ship) {

        radius = BASE_RADIUS + random.nextInt(MAX_ADDITIONAL_RADIUS);

        locationX = ship.getLocationX();

        locationY = ship.getLocationY();

        speedX = ((random.nextFloat() - 0.5f) / 0.5f) * BASE_SPEEDX;

        speedY = ((random.nextFloat() - 0.5f) / 0.5f) * BASE_SPEEDY;

    }

    public void update() {

        if (locationX > 0 &&
                locationX < gameViewCanvasWidth &&
                locationY > 0 &&
                locationY < gameViewCanvasHeight) {

            locationX += speedX * gameViewCanvasWidth;

            locationY += speedY * gameViewCanvasHeight;

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private Random random;

    private float gameViewCanvasWidth, gameViewCanvasHeight, radius, locationX, locationY, speedX, speedY;

    private static final int BASE_RADIUS = 2;

    private static final int MAX_ADDITIONAL_RADIUS = 8;

    private static final float BASE_SPEEDX = 0.01f;

    private static final float BASE_SPEEDY = 0.01f;

}