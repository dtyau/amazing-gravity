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

        double nextUpdateTime = System.nanoTime();

        int loops;

        Canvas canvas;

        while (running) {

            canvas = null;

            try {

                canvas = this.surfaceHolder.lockCanvas();

                if (canvas != null) {

                    synchronized (surfaceHolder) {

                        loops = 0;

                        while ((System.nanoTime() > nextUpdateTime) &&
                                (loops < MAX_ALLOWED_FRAMESKIPS)) {

                            this.gameView.update();

                            nextUpdateTime += NANOSECONDS_PER_UPDATE;

                            loops++;

                        }

                        this.gameView.render(canvas);

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

    private static final double NANOSECONDS_PER_UPDATE = 1000000000 / UPDATES_PER_SECOND;

    private static final int MAX_ALLOWED_FRAMESKIPS = 1;

}
