package com.studiau.amazinggravity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Author: Daniel Au
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public GameView(Context context) {

        super(context);

        getHolder().addCallback(this);

        gameThread = new GameThread(getHolder(), this);

        started = false;

        paused = false;

        gameOverProcessed = false;

        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        canvasWidth = getWidth();

        canvasHeight = getHeight();

        createPaint();

        scoreManager = new ScoreManager(canvasWidth, canvasHeight, getBestScoreFromPreferences());

        ship = new Ship(getContext(), canvasWidth, canvasHeight);

        exhaustParticles = new ArrayList<>();

        while (exhaustParticles.size() < AMOUNT_OF_EXHAUST) {

            for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

                exhaustParticles.add(new ExhaustParticle(ship, canvasWidth, canvasHeight));

            }

        }

        explosionParticles = new ArrayList<>();

        while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.add(new ExplosionParticle(ship, canvasWidth, canvasHeight));

            }

        }

        obstacleManager = new ObstacleManager(canvasWidth, canvasHeight, ship);

        stars = new ArrayList<>();

        while (stars.size() < AMOUNT_OF_STARS) {

            for (int i = 0; i < AMOUNT_OF_STARS; i++) {

                stars.add(new Star(canvasWidth, canvasHeight));

            }

        }

        gameState = GameState.RUNNING;

        gameThread.setRunning(true);

        startGameThread();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        // Do nothing.

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        boolean retry = true;

        while (retry) {

            try {

                gameThread.join();

                retry = false;

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }
    }

    public void onPause() {

        reset(ship);

        if (gameThread.isAlive()) {

            gameThread.setRunning(false);

        }

        paused = true;

    }

    public void onResume() {

        if (!gameThread.isAlive() && paused) {

            getHolder().addCallback(this);

            gameThread = new GameThread(getHolder(), this);

            gameThread.setRunning(true);

            gameThread.start();

            paused = false;

        }
        
    }

    private void startGameThread() {

        if (!started) {

            gameThread.start();

            started = true;

        }

    }

    private void createPaint() {

        paint = new Paint();

        paint.setAntiAlias(true);

        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(),
                "Walkway_SemiBold.ttf");

        paint.setTypeface(typeFace);

        bitmap_replay = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.replay);

        bitmap_replayLocationX = 0.5f * canvasWidth;

        bitmap_replayLocationY = 0.7f * canvasHeight;

    }

    public void update() {

        for (int i = 0; i < AMOUNT_OF_STARS; i++) {

            stars.get(i).update(ship);

        }

        if (gameState == GameState.RUNNING) {

            obstacleManager.update(ship);

            ship.update();

            for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

                exhaustParticles.get(i).update(ship);

            }

        } else if (gameState == GameState.GAMEOVER) {

            if(!gameOverProcessed) {

                scoreManager.update();

                if(scoreManager.isNewBest()) {

                    setBestScoreInPreferences();

                }

                gameOverProcessed = true;

            }

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.get(i).update();

            }

        }

    }

    public void render(Canvas canvas) {

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE); // For stars

        for (int i = 0; i < AMOUNT_OF_STARS; i++) {

            stars.get(i).draw(canvas, paint);

        }

        if (gameState == GameState.RUNNING) {

            obstacleManager.draw(canvas, paint);

            ship.draw(canvas, paint);

            paint.setColor(Color.parseColor("#B0BEC5")); // For exhaust particles

            for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

                exhaustParticles.get(i).draw(canvas, paint);

            }

            paint.setColor(Color.WHITE); // For score

            scoreManager.drawWhenPlay(canvas, paint);

        }

        if (gameState == GameState.GAMEOVER) {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) { // For explosion

                explosionParticles.get(i).draw(canvas, paint);

            }

            paint.setColor(Color.WHITE); // For score

            scoreManager.drawWhenOver(canvas, paint);

            canvas.drawBitmap(bitmap_replay, (bitmap_replayLocationX) - (bitmap_replay.getWidth() / 2),
                    (bitmap_replayLocationY) - (bitmap_replay.getHeight() / 2), paint);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int actionType = event.getActionMasked();

        if (gameState == GameState.RUNNING) {

            switch (actionType) {

                case (MotionEvent.ACTION_DOWN):

                    ship.handleActionDownAndMove(event.getX());

                    return true;

                case (MotionEvent.ACTION_MOVE):

                    ship.handleActionDownAndMove(event.getX());

                    return true;

                case (MotionEvent.ACTION_UP):

                    ship.handleActionUp();

                    return true;

                default:

                    return super.onTouchEvent(event);

            }

        } else {

            if (actionType == MotionEvent.ACTION_DOWN) {

                Log.d(TAG, Float.toString(bitmap_replayLocationX));

                if((event.getX() > (bitmap_replayLocationX - (bitmap_replay.getWidth() / 2))) &&
                event.getX() < (bitmap_replayLocationX + (bitmap_replay.getWidth() / 2)) &&
                        event.getY() > bitmap_replayLocationY - (bitmap_replay.getHeight() / 2) &&
                        event.getY() < bitmap_replayLocationY + (bitmap_replay.getHeight() / 2)) {

                    reset(ship);

                }

            }

            return super.onTouchEvent(event);

        }

    }

    private void reset(Ship ship) {

        gameOverProcessed = false;

        scoreManager.reset();

        ship.reset();

        obstacleManager.reset(ship);

        for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

            exhaustParticles.get(i).reset(ship);

        }

        for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

            explosionParticles.get(i).reset(ship);

        }

        gameState = GameState.RUNNING;

    }

    public static void setGameState(GameState newGameState) {

        gameState = newGameState;

    }

    public int getBestScoreFromPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        return sharedPreferences.getInt(SHARED_PREFERENCES_BEST_SCORE_KEY, 0);

    }

    private void setBestScoreInPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_PREFERENCES_BEST_SCORE_KEY, ScoreManager.getScore());

        editor.apply();

    }

    enum GameState {

        RUNNING, GAMEOVER

    }

    private GameThread gameThread;

    private Paint paint;

    private Boolean started, paused, gameOverProcessed;

    private Ship ship;

    private ArrayList<ExhaustParticle> exhaustParticles;

    private ArrayList<ExplosionParticle> explosionParticles;

    private ObstacleManager obstacleManager;

    private ArrayList<Star> stars;

    private ScoreManager scoreManager;

    private Bitmap bitmap_replay;

    private float bitmap_replayLocationX, bitmap_replayLocationY;

    private static GameState gameState;

    private float canvasWidth, canvasHeight;

    private final int AMOUNT_OF_EXHAUST = 42;

    private final int AMOUNT_OF_EXPLOSION = 300;

    private final int AMOUNT_OF_STARS = 100;

    private final float RELATIVE_FONT_SIZE = 12;

    private final String SHARED_PREFERENCES_BEST_SCORE_KEY = "8V3JT";

    private final static String TAG = GameView.class.getSimpleName();

}
