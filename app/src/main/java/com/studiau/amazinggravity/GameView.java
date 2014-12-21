package com.studiau.amazinggravity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

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

        loadSharedPreferences();

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
                "Walkway_UltraBold.ttf");

        paint.setTypeface(typeFace);

        bitmap_replay = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.replay);

        bitmap_replayLocationX = 0.5f * canvasWidth;

        bitmap_replayLocationY = 0.7f * canvasHeight;

        bitmap_rate = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.rate);

        bitmap_rateLocationX = 0.2f * canvasWidth;

        bitmap_leaderboard = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.leaderboard);

        bitmap_leaderboardLocationX = 0.8f * canvasWidth;

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

            while(!gameOverProcessed) {

                scoreManager.update();

                if(scoreManager.isNewBest()) {

                    setBestScoreInPreferences();

                }

                if(MainActivity.isGooglePlaySignedIn()) {

                    updateToGooglePlayServices();

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

            canvas.drawBitmap(bitmap_rate, (bitmap_rateLocationX) - (bitmap_rate.getWidth() / 2),
                    (bitmap_replayLocationY) - (bitmap_rate.getHeight() / 2), paint);

            canvas.drawBitmap(bitmap_leaderboard, (bitmap_leaderboardLocationX) - (bitmap_leaderboard.getWidth() / 2),
                    (bitmap_replayLocationY) - (bitmap_leaderboard.getHeight() / 2), paint);

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

                if((event.getX() > (bitmap_replayLocationX - (bitmap_replay.getWidth() / 2))) &&
                event.getX() < (bitmap_replayLocationX + (bitmap_replay.getWidth() / 2)) &&
                        event.getY() > bitmap_replayLocationY - (bitmap_replay.getHeight() / 2) &&
                        event.getY() < bitmap_replayLocationY + (bitmap_replay.getHeight() / 2)) {

                    actionsOnPress();

                    reset(ship);

                } else if((event.getX() > (bitmap_rateLocationX - (bitmap_rate.getWidth() / 2))) &&
                        event.getX() < (bitmap_rateLocationX + (bitmap_rate.getWidth() / 2)) &&
                        event.getY() > bitmap_replayLocationY - (bitmap_rate.getHeight() / 2) &&
                        event.getY() < bitmap_replayLocationY + (bitmap_rate.getHeight() / 2)) {

                    actionsOnPress();

                    rateGame();

                } else if((event.getX() > (bitmap_leaderboardLocationX - (bitmap_leaderboard.getWidth() / 2))) &&
                        event.getX() < (bitmap_leaderboardLocationX + (bitmap_leaderboard.getWidth() / 2)) &&
                        event.getY() > bitmap_replayLocationY - (bitmap_leaderboard.getHeight() / 2) &&
                        event.getY() < bitmap_replayLocationY + (bitmap_leaderboard.getHeight() / 2)) {

                    actionsOnPress();

                    showLeaderboard();

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

        if((gameState == GameState.GAMEOVER) && (vibrationEnabled)) {

            Effects.vibrate(600);

        }

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

    private void rateGame() {

        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());

        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {

            getContext().startActivity(goToMarket);

        } catch (ActivityNotFoundException e) {

            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));

        }

    }

    private void showLeaderboard() {

            // TODO: how?

    }

    private void updateToGooglePlayServices() {

        while (googleApiClient == null) {

            googleApiClient = MainActivity.googleApiClient;

        }

        if (googleApiClient.isConnected()) {

            unlockAchievements();

            submitToLeaderboards();

        }

    }

    private void unlockAchievements() {

        // TODO

    }

    private void submitToLeaderboards() {

        Games.Leaderboards.submitScore(googleApiClient,
                MainActivity.HIGHSCORES_LEADERBOARD_ID, ScoreManager.getScore());

    }

    private void loadSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        soundEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCES_SOUND_ENABLED_KEY, true);

        vibrationEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCES_VIBRATION_ENABLED_KEY, true);

    }

    private void actionsOnPress() {

        if(vibrationEnabled) {

            Effects.vibrate(60);

        }

    }

    enum GameState {

        RUNNING, GAMEOVER

    }

    private GoogleApiClient googleApiClient;

    private GameThread gameThread;

    private Paint paint;

    private Boolean started, paused, gameOverProcessed;

    private Ship ship;

    private ArrayList<ExhaustParticle> exhaustParticles;

    private ArrayList<ExplosionParticle> explosionParticles;

    private ObstacleManager obstacleManager;

    private ArrayList<Star> stars;

    private ScoreManager scoreManager;

    private Bitmap bitmap_replay, bitmap_rate, bitmap_leaderboard;

    private float canvasWidth, canvasHeight, bitmap_replayLocationX, bitmap_replayLocationY, bitmap_rateLocationX,
            bitmap_leaderboardLocationX;

    private static GameState gameState;

    private static boolean soundEnabled, vibrationEnabled;

    private final int AMOUNT_OF_EXHAUST = 42;

    private final int AMOUNT_OF_EXPLOSION = 300;

    private final int AMOUNT_OF_STARS = 100;

    private final float RELATIVE_FONT_SIZE = 12;

    private final String SHARED_PREFERENCES_BEST_SCORE_KEY = "8V3JT";

    private final static String SHARED_PREFERENCES_SOUND_ENABLED_KEY = "398BC";

    private final static String SHARED_PREFERENCES_VIBRATION_ENABLED_KEY = "75DN8";

    private final static String TAG = GameView.class.getSimpleName();

}
