package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * @Author: Daniel Au
 */

public class Star {

    public Star(float canvasWidth, float canvasHeight) {

        random = new Random();

        blurMaskFilter = new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL);

        gameViewCanvasWidth = canvasWidth;

        gameViewCanvasHeight = canvasHeight;

        radiusModifier = gameViewCanvasWidth / RADIUS_TO_SCREEN_RATIO;

        reset();

    }

    private void reset() {

        radius = (random.nextInt(MAX_RADIUS) + BASE_RADIUS) * radiusModifier;

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

        locationY += (speedY + Ship.BASE_SPEEDY) * gameViewCanvasHeight;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(blurMaskFilter);

        canvas.drawCircle(locationX, locationY, radius, paint);

        paint.setMaskFilter(null);

    }

    private Random random;

    private BlurMaskFilter blurMaskFilter;

    private float gameViewCanvasWidth, gameViewCanvasHeight, radius, locationX, locationY, speedX, speedY, radiusModifier;

    private final int MAX_RADIUS = 3;

    private final int BASE_RADIUS = 2;

    private final int RADIUS_TO_SCREEN_RATIO = 1080;

    private final static float BASE_SPEEDX = 0.0004f;

    private final static float BASE_SPEEDY = 0.0004f;

    private final static float SHIP_SPEEDX_DAMPENER = 0.3f;

}
