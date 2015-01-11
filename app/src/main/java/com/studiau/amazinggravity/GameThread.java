package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * @Author: Daniel Au
 */

public class GameThread extends Thread {

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {

        super();

        this.surfaceHolder = surfaceHolder;

        this.gameView = gameView;

    }

    public void setRunning(Boolean running) {

        this.running = running;

    }

    @Override
    public void run() {

        this.setPriority(Thread.MAX_PRIORITY);

        long next_game_tick = System.nanoTime();

        long current_game_tick;

        Canvas canvas;

        while (running) {

            canvas = null;

            try {

                canvas = this.surfaceHolder.lockCanvas();

                if (canvas != null) {

                    synchronized (surfaceHolder) {

                        current_game_tick = System.nanoTime();

                        if (current_game_tick > next_game_tick) {

                            this.gameView.update();

                            this.gameView.render(canvas);

                            next_game_tick += SKIP_TICKS;

                        } else {

                            try {

                                sleep(next_game_tick - current_game_tick);

                            } catch (InterruptedException e) {

                                e.printStackTrace();

                            }

                        }

                    }

                }

            } finally {

                if (canvas != null) {

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private final SurfaceHolder surfaceHolder;

    private GameView gameView;

    private Boolean running;

    private static final int UPDATES_PER_SECOND = 60;

    private static final int SKIP_TICKS = 1000000000 / UPDATES_PER_SECOND;

}
