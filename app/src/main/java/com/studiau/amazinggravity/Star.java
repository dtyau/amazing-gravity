package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * @Author: Daniel Au
 */

public class Star {

    public Star() {

        random = new Random();

        blurMaskFilter = new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL);

        radiusModifier = GameView.canvasWidth / RADIUS_TO_SCREEN_RATIO;

        reset();

    }

    private void reset() {

        radius = (random.nextInt(MAX_RADIUS) + BASE_RADIUS) * radiusModifier;

        locationX = random.nextFloat() * GameView.canvasWidth;

        locationY = random.nextFloat() * GameView.canvasHeight;

        speedX = ((random.nextFloat() - 0.5f) * 2) * BASE_SPEEDX;

        speedY = (radius / (BASE_RADIUS + MAX_RADIUS)) * BASE_SPEEDY;

    }

    public void update(Ship ship) {

        if (locationY > GameView.canvasHeight) {

            reset();

        }

        if (locationX < 0) {

            locationX = GameView.canvasWidth;

        } else if (locationX > GameView.canvasWidth) {

            locationX = 0;

        } else {

            locationX -= (speedX + (ship.getSpeedX() * SHIP_SPEEDX_DAMPENER) * GameView.canvasWidth);

        }

        locationY += (speedY + Ship.BASE_SPEEDY) * GameView.canvasHeight;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setMaskFilter(blurMaskFilter);

        canvas.drawCircle(locationX, locationY, radius, paint);

        paint.setMaskFilter(null);

    }

    private Random random;

    private BlurMaskFilter blurMaskFilter;

    private float radius, locationX, locationY, speedX, speedY, radiusModifier;

    private static final int MAX_RADIUS = 3;

    private static final int BASE_RADIUS = 2;

    private static final int RADIUS_TO_SCREEN_RATIO = 1080;

    private final static float BASE_SPEEDX = 0.0004f;

    private final static float BASE_SPEEDY = 0.0004f;

    private final static float SHIP_SPEEDX_DAMPENER = 0.3f;

}
