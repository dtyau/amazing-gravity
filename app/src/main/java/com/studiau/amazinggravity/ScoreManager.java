package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Author: Daniel Au.
 */

public class ScoreManager {

    public ScoreManager(float canvasWidth, float canvasHeight) {

        scorePositionX = canvasWidth * SCORE_POSITION_X;

        scorePositionY = canvasHeight * SCORE_POSITION_Y;

        score = 0;

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(Integer.toString(score), scorePositionX, scorePositionY, paint);

    }

    public static void incrementScore() {

        score += 1;

    }

    public void reset() {

        score = 0;

    }

    public static int getScore() {

        return score;

    }

    private static int score;

    private float scorePositionX, scorePositionY;

    private final static float SCORE_POSITION_X = 0.95f;

    private final static float SCORE_POSITION_Y = 0.05f;

}
