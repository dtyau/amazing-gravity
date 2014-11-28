package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class ExhaustParticle {

    public ExhaustParticle(Ship ship) {

        random = new Random();

        reset(ship);

    }

    private void reset(Ship ship) {

        radius = BASE_RADIUS + random.nextInt(MAX_ADDITIONAL_RADIUS);

        locationX = ship.getLocationX();

        locationY = ship.getExhaustLocationY();

        speedX = ( ( random.nextFloat() - 0.5f ) / 0.5f ) * BASE_SPEEDX;

        speedY = random.nextFloat() * BASE_SPEEDY;

    }

    public void update(Ship ship) {

        locationX += ( speedX + ship.getSpeedX() ) * GameView.getCanvasWidth();

        locationY += speedY * GameView.getCanvasHeight();

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.WHITE);

        //paint.setMaskFilter(blurMaskFilter);

        canvas.drawCircle( locationX, locationY, radius, paint);

    }

    private Random random;

    private float radius, locationX, locationY, speedX, speedY;

    private static final int BASE_RADIUS = 1;

    private static final int MAX_ADDITIONAL_RADIUS = 2;

    private static final float BASE_SPEEDX = 0.0001f;

    private static final float BASE_SPEEDY = 0.01f;

}
