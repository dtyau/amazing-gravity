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

    public void update(float shipSpeedX) {

        if (locationY > GameView.canvasHeight) {

            locationY = 0;

        }

        if (locationX < 0) {

            locationX = GameView.canvasWidth;

        } else if (locationX > GameView.canvasWidth) {

            locationX = 0;

        } else {

            locationX -= (speedX + (shipSpeedX * SHIP_SPEEDX_DAMPENER) * GameView.canvasWidth);

        }

        locationY += (speedY + Ship.BASE_SPEEDY) * GameView.canvasHeight;

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private Random random;

    private volatile float radius, locationX, locationY, speedX, speedY, radiusModifier;

    private static final int MAX_RADIUS = 2;

    private static final int BASE_RADIUS = 2;

    private static final float RADIUS_TO_SCREEN_RATIO = 1080;

    private static final float BASE_SPEEDX = 0.0004f;

    private static final float BASE_SPEEDY = 0.0004f;

    private static final float SHIP_SPEEDX_DAMPENER = 0.3f;

}
