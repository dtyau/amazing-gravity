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

    public void reset(Ship ship) {

        radius = BASE_RADIUS + random.nextInt(MAX_ADDITIONAL_RADIUS);

        locationX = ship.getLocationX();

        locationY = ship.getLocationY();

        angle = (float) Math.toRadians(random.nextInt(359));

        speedX = (float) (Math.sin(angle) * random.nextFloat() * BASE_SPEEDX);

        speedY = (float) (Math.cos(angle) * random.nextFloat() * BASE_SPEEDY);

        speedZ = random.nextInt(3);

        updateCounter = 0;

    }

    public void update() {

        if (locationX > (-0.1 * gameViewCanvasWidth) &&
                locationX < (1.1 * gameViewCanvasWidth) &&
                locationY > (-0.1 * gameViewCanvasHeight) &&
                locationY < (1.1 * gameViewCanvasHeight)) {

            locationX += speedX * gameViewCanvasHeight;

            locationY += speedY * gameViewCanvasHeight;

            if (radius > 2 && updateCounter > RADIUS_DECREMENT_COUNTER) {

                switch (speedZ) {

                    case 0:

                        radius--;

                        break;

                    case 1:

                        radius++;

                        break;

                    default:

                        break;

                }

                updateCounter = 0;

            }

            updateCounter++;

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private Random random;

    private float gameViewCanvasWidth, gameViewCanvasHeight, radius, locationX, locationY, speedX, speedY, angle;

    private int speedZ, updateCounter;

    private final int BASE_RADIUS = 2;

    private final int MAX_ADDITIONAL_RADIUS = 8;

    private final int RADIUS_DECREMENT_COUNTER = 10;

    private static final float BASE_SPEEDX = 0.008f;

    private static final float BASE_SPEEDY = 0.008f;

}
