package com.studiau.amazinggravity;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Author: Daniel Au
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

        Canvas canvas;

        while (running) {

            canvas = null;

            try {

                canvas = this.surfaceHolder.lockCanvas();

                if (canvas != null) {

                    synchronized (surfaceHolder) {

                        this.gameView.update();

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

}
