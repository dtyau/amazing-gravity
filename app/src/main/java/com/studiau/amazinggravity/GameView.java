package com.studiau.amazinggravity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

        createPaint();

        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        canvasWidth = getWidth();

        canvasHeight = getHeight();

        gameState = GameState.RUNNING;

        gameThread.setRunning(true);

        startGameThread();

        ship = new Ship();

        //planet = new Planet();

        stars = new ArrayList<Star>();

        for (int i = 0; i < AMOUNT_OF_STARS; i++) {

            stars.add( new Star() );

        }

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

    }

    public void update() {

        if (gameState == GameState.RUNNING) {

            for (int i = 0; i < AMOUNT_OF_STARS; i++) {

                stars.get(i).update(ship);

            }

            //planet.update(ship);

        }

    }

    public void render(Canvas canvas) {

        if (gameState == GameState.RUNNING) {

            canvas.drawColor(Color.BLACK);

            for (int i = 0; i < AMOUNT_OF_STARS; i++) {

                stars.get(i).draw(canvas, paint);

            }

            //planet.draw(canvas, paint);

            ship.draw(canvas, paint);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if ( event.getAction() == MotionEvent.ACTION_UP ) {

            if ( gameState == GameState.RUNNING ) {

                // Handle ship 'movement'

            }

        }

        return true;

    }

    public static float getCanvasWidth() {
        return canvasWidth;
    }

    public static float getCanvasHeight() {
        return canvasHeight;
    }

    enum GameState {

        RUNNING, GAMEOVER

    }

    private GameThread gameThread;

    private GameState gameState;

    private Paint paint;

    private Boolean started, paused;

    private Ship ship;

    //private Planet planet;

    private ArrayList<Star> stars;

    private static float canvasWidth, canvasHeight;

    private final int AMOUNT_OF_STARS = 100;

}
