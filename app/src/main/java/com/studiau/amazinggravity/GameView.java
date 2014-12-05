package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
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

        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        canvasWidth = getWidth();

        canvasHeight = getHeight();

        createPaint();

        ship = new Ship(getContext(), canvasWidth, canvasHeight);

        exhaustParticles = new ArrayList<ExhaustParticle>();

        while (exhaustParticles.size() < AMOUNT_OF_EXHAUST) {

            for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

                exhaustParticles.add(new ExhaustParticle(ship, canvasWidth, canvasHeight));

            }

        }

        explosionParticles = new ArrayList<ExplosionParticle>();

        while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.add(new ExplosionParticle(ship, canvasWidth, canvasHeight));

            }

        }

        obstacleManager = new ObstacleManager(canvasWidth, canvasHeight,
                ship.getLocationX(), ship.getLocationY(),
                ship.getWidth(), ship.getHeight());

        stars = new ArrayList<Star>();

        while (stars.size() < AMOUNT_OF_STARS) {

            for (int i = 0; i < AMOUNT_OF_STARS; i++) {

                stars.add(new Star(canvasWidth, canvasHeight));

            }

        }

        scoreManager = new ScoreManager(canvasWidth, canvasHeight);

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

        paint.setTypeface(Typeface.MONOSPACE);

        paint.setTextSize(canvasWidth / RELATIVE_FONT_SIZE);

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

            paint.setColor(Color.parseColor("#FFEB3B")); // For obstacles

            obstacleManager.draw(canvas, paint);

            ship.draw(canvas, paint);

            paint.setColor(Color.parseColor("#B0BEC5")); // For exhaust particles

            for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

                exhaustParticles.get(i).draw(canvas, paint);

            }

            paint.setColor(Color.WHITE); // For score

            scoreManager.draw(canvas, paint);

        }

        if (gameState == GameState.GAMEOVER) {

            paint.setColor(Color.WHITE); // For score

            scoreManager.draw(canvas, paint);

            paint.setColor(Color.parseColor("#FF5722")); // For explosion

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.get(i).draw(canvas, paint);

            }

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

                reset();

            }

            return super.onTouchEvent(event);

        }

    }

    private void reset() {

        ship.reset();

        obstacleManager.reset();

        scoreManager.reset();

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

    enum GameState {

        RUNNING, GAMEOVER

    }

    private GameThread gameThread;

    private Paint paint;

    private Boolean started, paused;

    private Ship ship;

    private ArrayList<ExhaustParticle> exhaustParticles;

    private ArrayList<ExplosionParticle> explosionParticles;

    private ObstacleManager obstacleManager;

    private ArrayList<Star> stars;

    private ScoreManager scoreManager;

    private static GameState gameState;

    private float canvasWidth, canvasHeight;

    private final int AMOUNT_OF_EXHAUST = 100;

    private final int AMOUNT_OF_EXPLOSION = 500;

    private final int AMOUNT_OF_STARS = 200;

    private final float RELATIVE_FONT_SIZE = 16;

    private final static String TAG = GameView.class.getSimpleName();

}
