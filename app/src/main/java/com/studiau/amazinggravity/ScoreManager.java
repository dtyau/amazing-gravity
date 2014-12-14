package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Author: Daniel Au.
 */

public class ScoreManager {

    public ScoreManager(float canvasWidth, float canvasHeight, int sharedPreferencesBestScore) {

        scorePlayPositionX = canvasWidth * SCORE_PLAY_POSITION_X;

        scorePlayPositionY = canvasHeight * SCORE_PLAY_POSITION_Y;

        scoreOverPositionX = canvasWidth * SCORE_OVER_POSITION_X;

        scoreOverPositionY = canvasHeight * SCORE_OVER_POSITION_Y;

        bestScoreOverPositionX = canvasWidth * BEST_SCORE_OVER_POSITION_X;

        bestScore = sharedPreferencesBestScore;

        reset();

    }

    public void drawWhenPlay(Canvas canvas, Paint paint) {

        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(Integer.toString(score), scorePlayPositionX, scorePlayPositionY, paint);

    }

    public void drawWhenOver(Canvas canvas, Paint paint) {

        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(Integer.toString(score), scoreOverPositionX, scoreOverPositionY, paint);

        canvas.drawText(Integer.toString(bestScore), bestScoreOverPositionX, scoreOverPositionY, paint);

    }

    public static void incrementScore() {

        score += 1;

    }

    public void reset() {

        score = 0;

    }

    public boolean isNewBest() {

        if(score > bestScore) {

            bestScore = score;

        }

        return (score > bestScore);

    }

    public static int getScore() {

        return score;

    }

    private static int score, bestScore;

    private float scorePlayPositionX, scorePlayPositionY, scoreOverPositionX, scoreOverPositionY,
            bestScoreOverPositionX;

    private final static float SCORE_PLAY_POSITION_X = 0.95f;

    private final static float SCORE_PLAY_POSITION_Y = 0.05f;

    private final static float SCORE_OVER_POSITION_X = 0.33f;

    private final static float SCORE_OVER_POSITION_Y = 0.4f;

    private final static float BEST_SCORE_OVER_POSITION_X = 0.66f;

}
