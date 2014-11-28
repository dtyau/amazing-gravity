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

    public Star() {

        random = new Random();

        blurMaskFilter = new BlurMaskFilter( 2, BlurMaskFilter.Blur.NORMAL );

        reset();

    }

    private void reset() {

        radius = random.nextInt(MAX_RADIUS) + BASE_RADIUS;

        locationX = random.nextFloat() * GameView.getCanvasWidth();

        locationY = ( random.nextFloat() * GameView.getCanvasHeight() ) -
                ( LOCATIONY_SHIFT * GameView.getCanvasHeight() );

        speedX = ( ( random.nextFloat() - 0.5f ) / 0.5f ) * BASE_SPEEDX;

        speedY = random.nextFloat() * BASE_SPEEDY;

    }

    public void update(Ship ship) {

        if ( locationY > GameView.getCanvasHeight() ||
                locationX < ( 0 - GameView.getCanvasWidth() * 0.1 ) ||
                locationX > GameView.getCanvasWidth() * 1.1 ) {

            reset();

        }

        locationX -= ( speedX + ship.getSpeedX() ) * GameView.getCanvasWidth();

        locationY += ( speedY + ship.getBaseSpeedY() ) * GameView.getCanvasHeight();

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.WHITE);

        paint.setMaskFilter(blurMaskFilter);

        canvas.drawCircle( locationX, locationY, radius, paint);

        paint.setMaskFilter(null);

    }

    private Random random;

    private BlurMaskFilter blurMaskFilter;

    private float radius, locationX, locationY, speedX, speedY;

    private final static int MAX_RADIUS = 3;

    private final static int BASE_RADIUS = 2;

    private final static float BASE_SPEEDX = 0.0001f;

    private final static float BASE_SPEEDY = 0.001f;

    private final static float LOCATIONY_SHIFT = 0.3f;

}
