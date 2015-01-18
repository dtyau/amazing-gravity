package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @Author: Daniel Au
 */

public class ScoreManager {

    public ScoreManager(int sharedPreferencesBestScore) {

        scorePlayPositionX = GameView.canvasWidth * SCORE_PLAY_POSITION_X;

        scorePlayPositionY = GameView.canvasHeight * SCORE_PLAY_POSITION_Y;

        scoreOverPositionX = GameView.canvasWidth * SCORE_OVER_POSITION_X;

        scoreOverPositionY = GameView.canvasHeight * SCORE_OVER_POSITION_Y;

        bestScoreOverPositionX = GameView.canvasWidth * BEST_SCORE_OVER_POSITION_X;

        textScoreOverPositionY = GameView.canvasHeight * TEXT_OVER_POSITION_Y;

        textGameOverPositionX = GameView.canvasWidth * TEXT_GAME_OVER_POSITION_X;

        textGameOverPositionY = GameView.canvasHeight * TEXT_GAME_OVER_POSITION_Y;

        bestScore = sharedPreferencesBestScore;

        reset();

    }

    public void update() {

        if (score > bestScore) {

            newBest = true;

            bestScore = score;

        }

    }

    public void drawWhenPlay(Canvas canvas, Paint paint) {

        paint.setTextSize(GameView.canvasWidth / RELATIVE_FONT_SIZE);

        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(Integer.toString(score), scorePlayPositionX, scorePlayPositionY, paint);

    }

    public void drawWhenOver(Canvas canvas, Paint paint) {

        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(GameView.canvasWidth / RELATIVE_FONT_SIZE_GAME_OVER);

        canvas.drawText("Game Over", textGameOverPositionX, textGameOverPositionY, paint);

        paint.setTextSize(GameView.canvasWidth / RELATIVE_FONT_SIZE);

        canvas.drawText("score", scoreOverPositionX, textScoreOverPositionY, paint);

        canvas.drawText("best", bestScoreOverPositionX, textScoreOverPositionY, paint);

        canvas.drawText(Integer.toString(score), scoreOverPositionX, scoreOverPositionY, paint);

        canvas.drawText(Integer.toString(bestScore), bestScoreOverPositionX, scoreOverPositionY, paint);

    }

    public static void incrementScore() {

        score += 1;

    }

    public void reset() {

        newBest = false;

        score = 0;

    }

    public boolean isNewBest() {

        return newBest;

    }

    public static int getScore() {

        return score;

    }

    private float scorePlayPositionX, scorePlayPositionY, scoreOverPositionX, scoreOverPositionY,
            bestScoreOverPositionX, textScoreOverPositionY, textGameOverPositionX,
            textGameOverPositionY;

    private boolean newBest;

    private static int score, bestScore;

    private final static float TEXT_GAME_OVER_POSITION_X = 0.5f;

    private final static float TEXT_GAME_OVER_POSITION_Y = 0.23f;

    private final static float SCORE_PLAY_POSITION_X = 0.95f;

    private final static float SCORE_PLAY_POSITION_Y = 0.05f;

    private final static float SCORE_OVER_POSITION_X = 0.3f;

    private final static float SCORE_OVER_POSITION_Y = 0.50f;

    private final static float BEST_SCORE_OVER_POSITION_X = 0.7f;

    private final static float TEXT_OVER_POSITION_Y = 0.44f;

    private static final float RELATIVE_FONT_SIZE = 12;

    private static final float RELATIVE_FONT_SIZE_GAME_OVER = 8;

}
