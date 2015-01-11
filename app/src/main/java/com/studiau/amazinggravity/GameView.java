package com.studiau.amazinggravity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Daniel Au
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public GameView(Context context) {

        super(context);

        getHolder().addCallback(this);

        gameThread = new GameThread(getHolder(), this);

        started = false;

        paused = false;

        skipMe = false;

        tutoring = true;

        tutorialAlpha = 255;

        level = 0;

        experienceInLevel = 0;

        experienceForNextLevel = 0;

        // Defaults to 0.25f to prevent weird rectangle being drawn before shit is updated
        experienceForDraw = 0.25f;

        determineLevelAndExperience();

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

        scoreManager = new ScoreManager(getBestScoreFromPreferences());

        ship = new Ship(getContext(), controlInverted);

        ship.setSpeedBoostCounter(1);

        obstacleManager = new ObstacleManager(getContext(), ship);

        exhaustParticles = new ArrayList<>();

        while (exhaustParticles.size() < AMOUNT_OF_EXHAUST) {

            for (int i = 0; i < AMOUNT_OF_EXHAUST; i++) {

                exhaustParticles.add(new ExhaustParticle(ship));

            }

        }

        explosionParticles = new ArrayList<>();

        while (explosionParticles.size() < AMOUNT_OF_EXPLOSION) {

            for (int i = 0; i < AMOUNT_OF_EXPLOSION; i++) {

                explosionParticles.add(new ExplosionParticle());

            }

        }

        stars = new ArrayList<>();

        while (stars.size() < AMOUNT_OF_STARS) {

            for (int i = 0; i < AMOUNT_OF_STARS; i++) {

                stars.add(new Star());

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

        bitmap_rowOne = ROW_ONE_LOCATION_Y * canvasHeight;

        bitmap_rowTwo = ROW_TWO_LOCATION_Y * canvasHeight;

        bitmap_replay = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.replay);

        bitmap_replayLocationX = 0.336f * canvasWidth;

        bitmap_leaderboard = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.leaderboard);

        bitmap_leaderboard_dismissed = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.leaderboard_disabled);

        bitmap_leaderboardLocationX = 0.66f * canvasWidth;

        bitmap_share = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.share);

        bitmap_shareLocationX = 0.25f * canvasWidth;

        bitmap_twitter = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.twitter);

        bitmap_twitterLocationX = 0.5f * canvasWidth;

        bitmap_rate = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.rate);

        bitmap_rateLocationX = 0.75f * canvasWidth;

        bitmap_arrow_left = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow_left);

        bitmap_arrow_right = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.arrow_right);

    }

    public void update() {

        if (skipMe) {

            for (Star star : stars) {

                star.update(ship);

            }

        }

        if (gameState == GameState.RUNNING) {

            if (!tutoring) {

                obstacleManager.update(ship);

            }

            if ((!tutoring) && (tutorialAlpha > 0)) {

                tutorialAlpha -= TUTORIAL_ALPHA_FADE_RATE;

            }

            ship.update();

            for (ExhaustParticle exhaustParticle : exhaustParticles) {

                exhaustParticle.update(ship, obstacleManager);

            }

        } else if (gameState == GameState.GAMEOVER) {

            while (!gameOverProcessed) {

                updateExperience(ScoreManager.getScore());

                determineLevelAndExperience();

                ship.setSpeedBoostCounter(1);

                scoreManager.update();

                if (scoreManager.isNewBest()) {

                    setBestScoreInPreferences();

                }

                if (MainActivity.isGooglePlaySignedIn()) {

                    updateToGooglePlayServices();

                }

                gameOverProcessed = true;

            }

            for (ExplosionParticle explosionParticle : explosionParticles) {

                explosionParticle.update();

            }

        }

        if (skipMe) {

            skipMe = false;

        } else {

            skipMe = true;

        }

    }

    public void render(Canvas canvas) {

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE); // For stars and tutorial

        for (Star star : stars) {

            star.draw(canvas, paint);

        }

        if (gameState == GameState.RUNNING) {

            if (tutorialAlpha > 0) {

                paint.setTextSize(canvasWidth / RELATIVE_FONT_SIZE_TUTORIAL);

                paint.setTextAlign(Paint.Align.CENTER);

                paint.setAlpha(tutorialAlpha);

                canvas.drawText("slide left or right", canvasWidth * 0.5f, canvasHeight * 0.21f, paint);

                canvas.drawText("to avoid wormholes", canvasWidth * 0.5f, canvasHeight * 0.25f, paint);

                canvas.drawBitmap(bitmap_arrow_left, canvasWidth * 0.25f - bitmap_arrow_left.getWidth() * 0.5f,
                        canvasHeight * 0.75f - bitmap_arrow_left.getHeight() * 0.5f, paint);

                canvas.drawBitmap(bitmap_arrow_right, canvasWidth * 0.75f - bitmap_arrow_right.getWidth() * 0.5f,
                        canvasHeight * 0.75f - bitmap_arrow_right.getHeight() * 0.5f, paint);

                canvas.drawText("tap while turning", canvasWidth * 0.5f, canvasHeight * 0.48f, paint);

                canvas.drawText("for speed boost", canvasWidth * 0.5f, canvasHeight * 0.52f, paint);

            }

            paint.setAlpha(255); // Clean up after tutorial

            if (!tutoring) {

                obstacleManager.draw(canvas, paint);

            }

            ship.draw(canvas, paint);

            if (ship.isBoosting()) {

                paint.setColor(Color.parseColor("#2196F3")); // For boosting exhaust particles

            } else {

                paint.setColor(Color.parseColor("#B0BEC5")); // For exhaust particles

            }

            for (ExhaustParticle exhaustParticle : exhaustParticles) {

                exhaustParticle.draw(canvas, paint);

            }

            paint.setColor(Color.WHITE); // For score

            scoreManager.drawWhenPlay(canvas, paint);

        }

        if (gameState == GameState.GAMEOVER) {

            for (ExplosionParticle explosionParticle : explosionParticles) { // For explosion

                explosionParticle.draw(canvas, paint);

            }

            //drawLevelAndExperience(canvas);

            paint.setColor(Color.WHITE); // For score

            scoreManager.drawWhenOver(canvas, paint);

            drawButtons(canvas);

        }

    }

    private void drawLevelAndExperience(Canvas canvas) {

        paint.setColor(Color.GRAY); // For experience bar

        canvas.drawRect(canvasWidth * 0.25f, canvasHeight * 0.302f, canvasWidth * 0.75f, canvasHeight * 0.308f, paint);

        paint.setColor(Color.WHITE); // For current experience

        canvas.drawRect(canvasWidth * 0.25f, canvasHeight * 0.302f, canvasWidth * experienceForDraw, canvasHeight * 0.308f, paint);

        paint.setTextSize(canvasWidth / LEVEL_RELATIVE_FONT_SIZE);

        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText("Lv " + Integer.toString(level + 1), canvasWidth * 0.21f, canvasHeight * 0.316f, paint);

        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Lv " + Integer.toString(level + 2), canvasWidth * 0.79f, canvasHeight * 0.316f, paint);

    }

    private void drawButtons(Canvas canvas) {

        canvas.drawBitmap(bitmap_replay, (bitmap_replayLocationX) - (bitmap_replay.getWidth() * 0.5f),
                (bitmap_rowOne) - (bitmap_replay.getHeight() * 0.5f), paint);

        if (MainActivity.isGooglePlaySignedIn()) {

            canvas.drawBitmap(bitmap_leaderboard, (bitmap_leaderboardLocationX) - (bitmap_leaderboard.getWidth() * 0.5f),
                    (bitmap_rowOne) - (bitmap_leaderboard.getHeight() * 0.5f), paint);

        } else {

            canvas.drawBitmap(bitmap_leaderboard_dismissed, (bitmap_leaderboardLocationX) - (bitmap_leaderboard_dismissed.getWidth() * 0.5f),
                    (bitmap_rowOne) - (bitmap_leaderboard_dismissed.getHeight() * 0.5f), paint);

        }

        canvas.drawBitmap(bitmap_share, (bitmap_shareLocationX) - (bitmap_share.getWidth() * 0.5f),
                (bitmap_rowTwo) - (bitmap_share.getHeight() * 0.5f), paint);

        canvas.drawBitmap(bitmap_twitter, (bitmap_twitterLocationX) - (bitmap_twitter.getWidth() * 0.5f),
                (bitmap_rowTwo) - (bitmap_twitter.getHeight() * 0.5f), paint);

        canvas.drawBitmap(bitmap_rate, (bitmap_rateLocationX) - (bitmap_rate.getWidth() * 0.5f),
                (bitmap_rowTwo) - (bitmap_rate.getHeight() * 0.5f), paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float eventX = event.getX();

        float eventY = event.getY();

        int actionType = event.getActionMasked();

        if (gameState == GameState.RUNNING) {

            switch (actionType) {

                case (MotionEvent.ACTION_DOWN):

                    if (tutoring) {

                        tutoring = false;

                    }

                    ship.handleActionDownAndMove(eventX);

                    return true;

                case (MotionEvent.ACTION_MOVE):

                    ship.handleActionDownAndMove(eventX);

                    return false;

                case (MotionEvent.ACTION_UP):

                    ship.handleActionUp();

                    return false;

                case (MotionEvent.ACTION_POINTER_DOWN):

                    ship.activateSpeedBoost();

                    return false;

                default:

                    return false;

            }

        } else {

            if (actionType == MotionEvent.ACTION_DOWN) {

                if ((eventX > (bitmap_replayLocationX - (bitmap_replay.getWidth() * 0.5f))) &&
                        eventX < (bitmap_replayLocationX + (bitmap_replay.getWidth() * 0.5f)) &&
                        eventY > bitmap_rowOne - (bitmap_replay.getHeight() * 0.5f) &&
                        eventY < bitmap_rowOne + (bitmap_replay.getHeight() * 0.5f)) {

                    actionsOnPress();

                    reset(ship);

                } else if ((eventX > (bitmap_leaderboardLocationX - (bitmap_leaderboard.getWidth() * 0.5f))) &&
                        eventX < (bitmap_leaderboardLocationX + (bitmap_leaderboard.getWidth() * 0.5f)) &&
                        eventY > bitmap_rowOne - (bitmap_leaderboard.getHeight() * 0.5f) &&
                        eventY < bitmap_rowOne + (bitmap_leaderboard.getHeight() * 0.5f)) {

                    if (MainActivity.isGooglePlaySignedIn()) {

                        actionsOnPress();

                        showLeaderboard();

                    } else {

                        actionsOnPress();

                        showLeaderboardDisabledAlert();

                    }

                } else if ((eventX > (bitmap_shareLocationX - (bitmap_share.getWidth() * 0.5f))) &&
                        eventX < (bitmap_shareLocationX + (bitmap_share.getWidth() * 0.5f)) &&
                        eventY > bitmap_rowTwo - (bitmap_share.getHeight() * 0.5f) &&
                        eventY < bitmap_rowTwo + (bitmap_share.getHeight() * 0.5f)) {

                    actionsOnPress();

                    share();

                } else if ((eventX > (bitmap_twitterLocationX - (bitmap_twitter.getWidth() * 0.5f))) &&
                        eventX < (bitmap_twitterLocationX + (bitmap_twitter.getWidth() * 0.5f)) &&
                        eventY > bitmap_rowTwo - (bitmap_twitter.getHeight() * 0.5f) &&
                        eventY < bitmap_rowTwo + (bitmap_twitter.getHeight() * 0.5f)) {

                    actionsOnPress();

                    tweet();

                } else if ((eventX > (bitmap_rateLocationX - (bitmap_rate.getWidth() * 0.5f))) &&
                        eventX < (bitmap_rateLocationX + (bitmap_rate.getWidth() * 0.5f)) &&
                        eventY > bitmap_rowTwo - (bitmap_rate.getHeight() * 0.5f) &&
                        eventY < bitmap_rowTwo + (bitmap_rate.getHeight() * 0.5f)) {

                    actionsOnPress();

                    rateGame();

                }

            }

            return false;

        }

    }

    private void reset(Ship ship) {

        gameState = GameState.RUNNING;

        skipMe = false;

        tutoring = true;

        tutorialAlpha = 255;

        scoreManager.reset();

        gameOverProcessed = false;

        ship.reset();

        obstacleManager.reset(ship);

        for (ExhaustParticle exhaustParticle : exhaustParticles) {

            exhaustParticle.reset(ship);

        }

        for (ExplosionParticle explosionParticle : explosionParticles) {

            explosionParticle.reset();

        }

    }

    public static void setGameState(GameState newGameState) {

        gameState = newGameState;

        if ((gameState == GameState.GAMEOVER) && (vibrationEnabled)) {

            Effects.vibrate(600);

        }

    }

    private void determineLevelAndExperience() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        int totalExperience = sharedPreferences.getInt(SHARED_PREFERENCES_TOTAL_EXPERIENCE_KEY, 0);

        level = (int) Math.pow(((float) totalExperience / EXPERIENCE_CONSTANT), (1 / EXPERIENCE_POWER));

        experienceInLevel = (int) (totalExperience - (Math.pow(level, EXPERIENCE_POWER) * EXPERIENCE_CONSTANT));

        experienceForNextLevel = (int) (((Math.pow((level + 1), EXPERIENCE_POWER) * EXPERIENCE_CONSTANT)) -
                (Math.pow(level, EXPERIENCE_POWER) * EXPERIENCE_CONSTANT));

        experienceForDraw = 0.25f + (((float) experienceInLevel / experienceForNextLevel) * 0.50f);

    }

    private void updateExperience(int score) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        int totalExperience = sharedPreferences.getInt(SHARED_PREFERENCES_TOTAL_EXPERIENCE_KEY, 0);

        totalExperience += score;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_PREFERENCES_TOTAL_EXPERIENCE_KEY, totalExperience);

        editor.apply();

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

        if (ScoreManager.getScore() == 0) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_0_SCORE);

        }

        if (ScoreManager.getScore() >= 10) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_10_SCORE);

        }

        if (ScoreManager.getScore() >= 25) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_25_SCORE);

        }

        if (ScoreManager.getScore() >= 50) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_50_SCORE);

        }

        if (ScoreManager.getScore() >= 100) {

            Games.Achievements.unlock(googleApiClient, ACHIEVEMENT_100_SCORE);

        }

    }

    private void submitToLeaderboards() {

        if (ScoreManager.getScore() > 0) {

            Games.Leaderboards.submitScore(googleApiClient,
                    MainActivity.HIGHSCORES_LEADERBOARD_ID, ScoreManager.getScore());

        }

    }

    private void loadSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        controlInverted = sharedPreferences.getBoolean(MainActivity.SHARED_PREFERENCES_INVERTED_KEY, false);

        soundEnabled = sharedPreferences.getBoolean(MainActivity.SHARED_PREFERENCES_SOUND_ENABLED_KEY, true);

        vibrationEnabled = sharedPreferences.getBoolean(MainActivity.SHARED_PREFERENCES_VIBRATION_ENABLED_KEY, true);

    }

    private void share() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");

        String shareBody = "Hey! Can you beat my score of " + Integer.toString(ScoreManager.getScore()) +
                " on #AmazingGravity for Android?! https://play.google.com/store/apps/details?id=com.studiau.amazinggravity";

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    private void tweet() {

        // Create intent using ACTION_VIEW and a normal Twitter url:
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                urlEncode("Hey! Can you beat my score of " + Integer.toString(ScoreManager.getScore()) +
                        " on #AmazingGravity for Android?! "),
                urlEncode("https://play.google.com/store/apps/details?id=com.studiau.amazinggravity"));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        // Narrow down to official Twitter app, if available:
        List<ResolveInfo> matches = getContext().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo info : matches) {

            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {

                intent.setPackage(info.activityInfo.packageName);

            }
        }

        getContext().startActivity(intent);

    }

    // This is for tweet function
    public static String urlEncode(String s) {

        try {

            return URLEncoder.encode(s, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException("URLEncoder.encode() failed for " + s);

        }

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

    private void showLeaderboardDisabledAlert() {

        new AlertDialog.Builder(getContext())
                .setTitle("Google Play Leaderboard")
                .setMessage("Please sign-in to Google Play on the main page to view or submit scores to the leaderboard.")
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // Dismiss the dialog.

                    }

                })
                .setIcon(R.drawable.leaderboard)
                .show();

    }

    private void actionsOnPress() {

        if (vibrationEnabled) {

            Effects.vibrate(60);

        }

    }

    enum GameState {

        RUNNING, GAMEOVER

    }

    private GoogleApiClient googleApiClient;

    private GameThread gameThread;

    private static GameState gameState;

    private Paint paint;

    private Ship ship;

    private ArrayList<ExhaustParticle> exhaustParticles;

    private ArrayList<ExplosionParticle> explosionParticles;

    private ObstacleManager obstacleManager;

    private ArrayList<Star> stars;

    private ScoreManager scoreManager;

    private Bitmap bitmap_replay, bitmap_twitter, bitmap_leaderboard, bitmap_leaderboard_dismissed,
            bitmap_share, bitmap_rate, bitmap_arrow_left, bitmap_arrow_right;

    private volatile boolean started, paused, gameOverProcessed, tutoring;

    private volatile float bitmap_rowOne, bitmap_rowTwo, bitmap_replayLocationX,
            bitmap_twitterLocationX, bitmap_leaderboardLocationX, bitmap_shareLocationX,
            bitmap_rateLocationX, experienceForDraw;

    private volatile int tutorialAlpha, level, experienceInLevel, experienceForNextLevel;

    public static volatile boolean skipMe;

    public static volatile int canvasWidth, canvasHeight;

    private static volatile boolean controlInverted, soundEnabled, vibrationEnabled;

    private static final int AMOUNT_OF_EXHAUST = 30;

    private static final int AMOUNT_OF_EXPLOSION = 300;

    private static final int AMOUNT_OF_STARS = 100;

    private static final int TUTORIAL_ALPHA_FADE_RATE = 5;

    private static final int EXPERIENCE_CONSTANT = 333;

    private static final int LEVEL_RELATIVE_FONT_SIZE = 18;

    private static final float EXPERIENCE_POWER = 1.8f;

    private static final float RELATIVE_FONT_SIZE_TUTORIAL = 14;

    private static final float ROW_ONE_LOCATION_Y = 0.65f;

    private static final float ROW_TWO_LOCATION_Y = 0.85f;

    private static final String SHARED_PREFERENCES_BEST_SCORE_KEY = "8V3JT";

    private static final String SHARED_PREFERENCES_TOTAL_EXPERIENCE_KEY = "HD61J";

    private static final String ACHIEVEMENT_0_SCORE = "CgkI8bfIso0dEAIQAQ";

    private static final String ACHIEVEMENT_10_SCORE = "CgkI8bfIso0dEAIQAg";

    private static final String ACHIEVEMENT_25_SCORE = "CgkI8bfIso0dEAIQAw";

    private static final String ACHIEVEMENT_50_SCORE = "CgkI8bfIso0dEAIQBA";

    private static final String ACHIEVEMENT_100_SCORE = "CgkI8bfIso0dEAIQBQ";

    private final static String TAG = GameView.class.getSimpleName();

}