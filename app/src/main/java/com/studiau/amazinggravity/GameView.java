package com.studiau.amazinggravity;

import android.app.Activity;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

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

        tutoring = true;

        tutorialAlpha = 255;

        gameOverProcessed = false;

        loadSharedPreferences();

        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        createGoogleApiClient();

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

    private void createGoogleApiClient() {

        // Create the Google Api Client for the play games services
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

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

        if(MainActivity.isGooglePlaySignedIn()) {

            bitmap_leaderboard = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.leaderboard);

        } else {

            bitmap_leaderboard = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.leaderboard_disabled);

        }

        bitmap_leaderboardLocationX = 0.8f * canvasWidth;

    }

    public void update() {

        for (int i = 0; i < AMOUNT_OF_STARS; i++) {

            stars.get(i).update(ship);

        }

        if (gameState == GameState.RUNNING) {

            if (!tutoring) {

                obstacleManager.update(ship);

            }

            if ((!tutoring) && (tutorialAlpha > 0)) {

                tutorialAlpha -= TUTORIAL_ALPHA_FADE_RATE;

            }

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

        paint.setColor(Color.WHITE); // For stars and tutorial

        for (int i = 0; i < AMOUNT_OF_STARS; i++) {

            stars.get(i).draw(canvas, paint);

        }

        if (gameState == GameState.RUNNING) {

            if(tutorialAlpha > 0) {

                paint.setTextSize(canvasWidth / RELATIVE_FONT_SIZE_TUTORIAL);

                paint.setTextAlign(Paint.Align.CENTER);

                paint.setAlpha(tutorialAlpha);

                canvas.drawText("slide left or right to navigate", canvasWidth / 2, canvasHeight / 3, paint);

                canvas.drawText("< < <", canvasWidth / 4, ship.getLocationY() + (ship.getHeight() / 4), paint);

                canvas.drawText("> > >", canvasWidth * 3 / 4, ship.getLocationY() + (ship.getHeight() / 4), paint);

            }

            paint.setAlpha(255); // Clean up after tutorial

            if(!tutoring) {

                obstacleManager.draw(canvas, paint);

            }

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

                    if(tutoring) {

                        tutoring = false;

                    }

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

                    if(MainActivity.isGooglePlaySignedIn()) {

                        actionsOnPress();

                        showLeaderboard();

                    }

                }

            }

            return super.onTouchEvent(event);

        }

    }

    private void reset(Ship ship) {

        tutoring = true;

        tutorialAlpha = 255;

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

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {

            getContext().startActivity(goToMarket);

        } catch (ActivityNotFoundException e) {

            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));

        }

    }

    private void showLeaderboard() {

        while (googleApiClient == null) {

            createGoogleApiClient();

        }

        while (!googleApiClient.isConnected()) {

            googleApiClient.connect();

        }

        if (googleApiClient.isConnected()) {

            Intent intent = new Intent();

            intent.setClass(getContext(), MainActivity.class);

            ((Activity) getContext()).startActivityForResult(Games.Leaderboards
                    .getLeaderboardIntent(googleApiClient, MainActivity.HIGHSCORES_LEADERBOARD_ID),
                    MainActivity.REQUEST_LEADERBOARD);

        }

    }

    private void updateToGooglePlayServices() {

        while (googleApiClient == null) {

            createGoogleApiClient();

        }

        while (!googleApiClient.isConnected()) {

            googleApiClient.connect();

        }

        if (googleApiClient.isConnected()) {

            unlockAchievements();

            submitToLeaderboards();

        }

    }

    private void unlockAchievements() {

        if(ScoreManager.getScore() == 0) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_0_SCORE);

        }

        if(ScoreManager.getScore() >= 10) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_10_SCORE);

        }

        if(ScoreManager.getScore() >= 25) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_25_SCORE);

        }

        if(ScoreManager.getScore() >= 50) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_50_SCORE);

        }

        if(ScoreManager.getScore() >= 100) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_100_SCORE);

        }

    }

    private void submitToLeaderboards() {

        if(ScoreManager.getScore() > 0) {

            Games.Leaderboards.submitScore(googleApiClient,
                    MainActivity.HIGHSCORES_LEADERBOARD_ID, ScoreManager.getScore());

        }

    }

    private void loadSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        soundEnabled = sharedPreferences.getBoolean(MainActivity.SHARED_PREFERENCES_SOUND_ENABLED_KEY, true);

        vibrationEnabled = sharedPreferences.getBoolean(MainActivity.SHARED_PREFERENCES_VIBRATION_ENABLED_KEY, true);

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

    private Boolean started, paused, gameOverProcessed, tutoring;

    private Ship ship;

    private ArrayList<ExhaustParticle> exhaustParticles;

    private ArrayList<ExplosionParticle> explosionParticles;

    private ObstacleManager obstacleManager;

    private ArrayList<Star> stars;

    private ScoreManager scoreManager;

    private Bitmap bitmap_replay, bitmap_rate, bitmap_leaderboard;

    private float canvasWidth, canvasHeight, bitmap_replayLocationX, bitmap_replayLocationY, bitmap_rateLocationX,
            bitmap_leaderboardLocationX;

    private int tutorialAlpha;

    private static GameState gameState;

    private static boolean soundEnabled, vibrationEnabled;

    private final int AMOUNT_OF_EXHAUST = 42;

    private final int AMOUNT_OF_EXPLOSION = 300;

    private final int AMOUNT_OF_STARS = 100;

    private final int TUTORIAL_ALPHA_FADE_RATE = 5;

    private final float RELATIVE_FONT_SIZE_TUTORIAL = 14;

    private final String SHARED_PREFERENCES_BEST_SCORE_KEY = "8V3JT";

    private final String ACHIEVEMENT_0_SCORE = "CgkI8bfIso0dEAIQAQ";

    private final String ACHIEVEMENT_10_SCORE = "CgkI8bfIso0dEAIQAg";

    private final String ACHIEVEMENT_25_SCORE = "CgkI8bfIso0dEAIQAw";

    private final String ACHIEVEMENT_50_SCORE = "CgkI8bfIso0dEAIQBA";

    private final String ACHIEVEMENT_100_SCORE = "CgkI8bfIso0dEAIQBQ";

    private final static String TAG = GameView.class.getSimpleName();

}
