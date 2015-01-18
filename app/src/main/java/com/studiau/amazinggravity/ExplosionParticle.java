package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * @Author: Daniel Au
 */

public class ExplosionParticle {

    public ExplosionParticle() {

        random = new Random();

        reset();

    }

    public ExplosionParticle(String colour, int radius, float locationX, float locationY) {

        random = new Random();

        this.radius = radius;

        this.locationX = locationX;

        this.locationY = locationY;

        angle = (float) Math.toRadians(random.nextInt(359));

        speedX = (float) (Math.sin(angle) * random.nextFloat() * BASE_SPEEDX);

        speedY = (float) (Math.cos(angle) * random.nextFloat() * BASE_SPEEDY);

        speedZ = random.nextInt(2);

        this.colour = colour;

        alpha = 255;

        updateCounter = 0;

    }

    public void reset() {

        radius = BASE_RADIUS + random.nextInt(MAX_ADDITIONAL_RADIUS);

        locationX = Ship.locationX;

        locationY = Ship.locationY;

        angle = (float) Math.toRadians(random.nextInt(359));

        speedX = (float) (Math.sin(angle) * random.nextFloat() * BASE_SPEEDX);

        speedY = (float) (Math.cos(angle) * random.nextFloat() * BASE_SPEEDY);

        speedZ = random.nextInt(2);

        colour = getRandomColour();

        alpha = 255;

        updateCounter = 0;

    }

    public void reset(String colour, int radius) {

        this.colour = colour;

        this.radius = radius;

        angle = (float) Math.toRadians(random.nextInt(359));

        speedX = (float) (Math.sin(angle) * random.nextFloat() * BASE_SPEEDX);

        speedY = (float) (Math.cos(angle) * random.nextFloat() * BASE_SPEEDY);

        speedZ = random.nextInt(2);

        alpha = 255;

        updateCounter = 0;

    }

    public void update() {

        if (alpha >= ALPHA_FADE) {

            locationX += speedX * GameView.canvasWidth;

            locationY += speedY * GameView.canvasHeight;

            if (radius > RADIUS_CHANGE && updateCounter > RADIUS_CHANGE_COUNTER) {

                switch (speedZ) {

                    case 0:

                        radius = radius + RADIUS_CHANGE;

                        break;

                    case 1:

                        radius = radius - RADIUS_CHANGE;

                        break;

                    default:

                        break;

                }

                updateCounter = 0;

            }

            alpha = alpha - ALPHA_FADE;

            updateCounter++;

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        if (alpha > ALPHA_FADE) {

            paint.setColor(Color.parseColor(colour));

            paint.setAlpha(alpha);

            canvas.drawCircle(locationX, locationY, radius, paint);

            paint.setAlpha(255);

        }

    }

    public void setLocation(float locationX, float locationY) {

        this.locationX = locationX;

        this.locationY = locationY;

    }

    private String getRandomColour() {

        int randomColourInteger = random.nextInt(NUMBER_OF_COLOURS);

        switch (randomColourInteger) {

            default:

                return "#F44336"; // red is default

            case 0:

                return "#F44336"; // red

            case 1:

                return "#FFEB3B"; // yellow

            case 2:

                return "#FFC107"; // amber

            case 3:

                return "#FF9800"; // orange

            case 4:

                return "#FF5722"; // deep orange

        }

    }

    private Random random;

    private String colour;

    private volatile float radius, locationX, locationY, speedX, speedY, angle;

    private volatile int speedZ, updateCounter, alpha;

    private static final int BASE_RADIUS = 2;

    private static final int MAX_ADDITIONAL_RADIUS = 10;

    private static final int RADIUS_CHANGE = 3;

    private static final int RADIUS_CHANGE_COUNTER = 10;

    private static final int ALPHA_FADE = 4;

    private static final int NUMBER_OF_COLOURS = 4;

    private static final float BASE_SPEEDX = 0.02f;

    private final float BASE_SPEEDY = 0.02f;

}
