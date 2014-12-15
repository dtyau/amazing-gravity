package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Star {

    public Star(float canvasWidth, float canvasHeight) {

        random = new Random();

        blurMaskFilter = new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL);

        gameViewCanvasWidth = canvasWidth;

        gameViewCanvasHeight = canvasHeight;

        reset();

    }

    private void reset() {

        radius = random.nextInt(MAX_RADIUS) + BASE_RADIUS;

        locationX = random.nextFloat() * gameViewCanvasWidth;

        locationY = random.nextFloat() * gameViewCanvasHeight;

        speedX = ((random.nextFloat() - 0.5f) / 0.5f) * BASE_SPEEDX;

        speedY = (radius / (BASE_RADIUS + MAX_RADIUS)) * BASE_SPEEDY;

    }

    public void update(Ship ship) {

        if (locationY > gameViewCanvasHeight) {

            reset();

        }

        if (locationX < 0) {

            locationX = gameViewCanvasWidth;

        } else if (locationX > gameViewCanvasWidth) {

            locationX = 0;

        } else {

            locationX -= (speedX + (ship.getSpeedX() * SHIP_SPEEDX_DAMPENER) * gameViewCanvasWidth);

        }

        locationY += (speedY + ship.getBaseSpeedY()) * gameViewCanvasHeight;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(blurMaskFilter);

        canvas.drawCircle(locationX, locationY, radius, paint);

        paint.setMaskFilter(null);

    }

    private Random random;

    private BlurMaskFilter blurMaskFilter;

    private float gameViewCanvasWidth, gameViewCanvasHeight, radius, locationX, locationY, speedX, speedY;

    private final static int MAX_RADIUS = 3;

    private final static int BASE_RADIUS = 2;

    private final static float BASE_SPEEDX = 0.0001f;

    private final static float BASE_SPEEDY = 0.001f;

    private final static float SHIP_SPEEDX_DAMPENER = 0.3f;

}
