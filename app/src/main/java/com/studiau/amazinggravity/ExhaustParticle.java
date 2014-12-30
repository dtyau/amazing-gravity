package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Author: Daniel Au
 */

public class ExhaustParticle {

    public ExhaustParticle(Ship ship, float canvasWidth, float canvasHeight) {

        random = new Random();

        gameViewCanvasWidth = canvasWidth;

        gameViewCanvasHeight = canvasHeight;

        reset(ship);

    }

    public void reset(Ship ship) {

        radius = BASE_RADIUS + random.nextInt(MAX_ADDITIONAL_RADIUS);

        locationX = ship.getExhaustLocationX();

        locationY = ship.getExhaustLocationY();

        speedX = ((random.nextFloat() - 0.5f) / 0.5f) * BASE_SPEEDX;

        speedY = BASE_SPEEDY + (random.nextFloat() * MAX_ADDITIONAL_SPEEDY);

    }

    public void update(Ship ship, ObstacleManager obstacleManager) {

        if ((locationX < 0) || (locationX > gameViewCanvasWidth) || (locationY > gameViewCanvasHeight) ||
                (obstacleManager.checkCollisionForParticles(ship, locationX, locationY))) {

            reset(ship);

        }

        float speedXFromObstacle = obstacleManager.giveSpeedXForParticles(ship, locationX);

        float speedYFromObstacle = obstacleManager.giveSpeedYForParticles(ship, locationY);

        locationX += ((speedX - ship.getSpeedX() * SPEEDX_MODIFIER) + speedXFromObstacle) * gameViewCanvasWidth;

        locationY += (speedY + speedYFromObstacle) * gameViewCanvasHeight;

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

    private static final float BASE_SPEEDY = 0.005f;

    private static final float MAX_ADDITIONAL_SPEEDY = 0.01f;

}
