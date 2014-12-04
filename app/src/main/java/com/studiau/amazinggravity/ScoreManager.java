package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

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

        paint.setTypeface(Typeface.MONOSPACE);

        canvas.drawText(Integer.toString(score), scorePositionX, scorePositionY, paint);

    }

    public void incrementScore() {

        score += 1;

    }

    public void reset() {

        score = 0;

    }

    public int getScore() {

        return score;

    }

    private int score;

    private float scorePositionX, scorePositionY;

    private final static float SCORE_POSITION_X = 0.9f;

    private final static float SCORE_POSITION_Y = 0.05f;

}
