package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

/**
 * @Author: Daniel Au
 */

public class ExhaustParticle {

    public ExhaustParticle(Ship ship, float canvasWidth, float canvasHeight) {

        random = new Random();

        gameViewCanvasWidth = canvasWidth;

        gameViewCanvasHeight = canvasHeight;

        reset(ship);

    }

    public void reset(Ship ship) {

        float randomFloat = random.nextFloat();

        radius = BASE_RADIUS + (randomFloat * MAX_ADDITIONAL_RADIUS);

        locationX = ship.getExhaustLocationX();

        locationY = ship.getExhaustLocationY();

        speedX = ((randomFloat - 0.5f) / 0.5f) * BASE_SPEEDX;

        speedY = BASE_SPEEDY + (randomFloat * MAX_ADDITIONAL_SPEEDY);

    }

    public void update(Ship ship, ObstacleManager obstacleManager) {

        if ((locationX < 0) || (locationX > gameViewCanvasWidth) || (locationY > gameViewCanvasHeight) ||
                (obstacleManager.checkCollisionForParticles(ship, locationX, locationY))) {

            reset(ship);

        }

        locationX += (speedX - ship.getSpeedX() * SPEEDX_MODIFIER) * gameViewCanvasWidth;

        locationY += speedY * gameViewCanvasHeight;

    }

    public void draw(Canvas canvas, Paint paint) {

        canvas.drawCircle(locationX, locationY, radius, paint);

    }

    private Random random;

    private float gameViewCanvasWidth, gameViewCanvasHeight, radius, locationX, locationY, speedX, speedY;

    private static final int BASE_RADIUS = 2;

    private static final int MAX_ADDITIONAL_RADIUS = 2;

    private static final float BASE_SPEEDX = 0.003f;

    private static final float SPEEDX_MODIFIER = 3;

    private static final float BASE_SPEEDY = 0.003f;

    private static final float MAX_ADDITIONAL_SPEEDY = 0.007f;

}
