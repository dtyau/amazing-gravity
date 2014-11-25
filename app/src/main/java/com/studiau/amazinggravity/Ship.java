package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au.
 */

public class Ship {

    public Ship() {

        radius = BASE_RADIUS;

        locationX = BASE_LOCATIONX;

        locationY = BASE_LOCATIONY;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.WHITE);

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    public float getBaseSpeedY() {

        return BASE_SPEEDY;

    }

    private float radius, locationX, locationY;

    private static final float BASE_RADIUS = 24;
    
    private static final float BASE_SPEEDX = 0.001f

    private static final float BASE_SPEEDY = 0.001f;

    private static final float BASE_LOCATIONX = ( GameView.getCanvasWidth() * 0.5f );

    private static final float BASE_LOCATIONY = ( GameView.getCanvasHeight() * 0.8f );


}
