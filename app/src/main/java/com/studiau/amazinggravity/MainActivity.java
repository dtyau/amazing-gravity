package com.studiau.amazinggravity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import com.kskkbys.rate.RateThisApp;

/**
 * Author: Daniel Au
 */

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        createGoogleApiClient();

        Effects.initEffects(this);

        loadSharedPreferences();

        //setSoundEnabled();

        setVibrationEnabled();

    }

    @Override
    protected void onStart() {

        super.onStart();

        RateThisApp.onStart(this);

        RateThisApp.showRateDialogIfNeeded(this);

        if (!resolvingConnectionFailure && googlePlayAutoSignIn) {

            googleApiClient.connect();

        }

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

    }

    @Override
    public void onStop() {

        super.onStop();

        if (googleApiClient.isConnected()) {

            googleApiClient.disconnect();

        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        googlePlaySignedIn = true;

        googlePlayAutoSignIn = true;

        editSharedPreferencesGooglePlayAutoSignIn();

    }

    @Override
    public void onConnectionSuspended(int cause) {

        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Set boolean of Google play sign in to false
        googlePlaySignedIn = false;

        googlePlayAutoSignIn = false;

        editSharedPreferencesGooglePlayAutoSignIn();

        if (resolvingConnectionFailure) {

            // already resolving
            return;

        }

        resolvingConnectionFailure = true;

        // Attempt to resolve the connection failure using BaseGameUtils.
        if (!BaseGameUtils.resolveConnectionFailure(this,
                googleApiClient, connectionResult,
                RC_SIGN_IN, getString(R.string.googlePlaySignInFailure))) {

            resolvingConnectionFailure = false;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            // Failure resolved
            resolvingConnectionFailure = false;

            // If the resolution is good, we prompt sign-in
            if (resultCode == RESULT_OK) {

                googleApiClient.connect();

            } else {

                // Bring up an error dialog to alert the user that sign-in failed
                BaseGameUtils.showActivityResultError(this, requestCode,
                        resultCode, R.string.googlePlaySignInFailure);

            }

        }

    }

    private void createGoogleApiClient() {

        // Create the Google Api Client for the play games services
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

    }

    private void loadSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        soundEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCES_SOUND_ENABLED_KEY, true);

        vibrationEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCES_VIBRATION_ENABLED_KEY, true);

        googlePlayAutoSignIn = sharedPreferences.getBoolean(SHARED_PREFERENCES_GOOGLE_PLAY_AUTO_SIGN_IN_KEY, true);

    }

    private void editSharedPreferencesSound() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SHARED_PREFERENCES_SOUND_ENABLED_KEY, soundEnabled);

        editor.apply();

    }

    private void editSharedPreferencesVibration() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SHARED_PREFERENCES_VIBRATION_ENABLED_KEY, vibrationEnabled);

        editor.apply();

    }

    private void editSharedPreferencesGooglePlayAutoSignIn() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SHARED_PREFERENCES_GOOGLE_PLAY_AUTO_SIGN_IN_KEY, googlePlayAutoSignIn);

        editor.apply();

    }

    private void setSoundEnabled() {

        if(soundEnabled) {

            setImage(R.id.imagebutton_sound, R.drawable.volume);

        } else {

            setImage(R.id.imagebutton_sound, R.drawable.volume_off);

        }

    }

    private void setVibrationEnabled() {

        if(vibrationEnabled) {

            setImage(R.id.imagebutton_vibration, R.drawable.vibration);

        } else {

            setImage(R.id.imagebutton_vibration, R.drawable.vibration_off);

        }

    }

    public void toggleSoundEnabled(View view) {

        if(soundEnabled) {

            soundEnabled = false;

        } else {

            soundEnabled = true;

        }

        editSharedPreferencesSound();

        setSoundEnabled();

    }

    public void toggleVibrationEnabled(View view) {

        if(vibrationEnabled) {

            vibrationEnabled = false;

        } else {

            vibrationEnabled = true;

        }

        editSharedPreferencesVibration();

        setVibrationEnabled();

        actionsOnPress();

    }

    public void play(View view) {

        actionsOnPress();

        Intent intent = new Intent(this, GameActivity.class);

        startActivity(intent);

    }

    public void rateGame(View view) {

        actionsOnPress();

        Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());

        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {

            startActivity(goToMarket);

        } catch (ActivityNotFoundException e) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));

        }

    }

    public void showLeaderboard(View view) {

        if (googleApiClient.isConnected()) {

            startActivityForResult(Games.Leaderboards
                    .getLeaderboardIntent(googleApiClient, HIGHSCORES_LEADERBOARD_ID), REQUEST_LEADERBOARD);

        } else {

            googleApiClient.connect();

        }

        actionsOnPress();

    }

    private void setImage(int RidButton, int RidDrawable) {

        ImageButton button = (ImageButton) findViewById(RidButton);

        button.setImageResource(RidDrawable);

    }

    private void actionsOnPress() {

        if(vibrationEnabled) {

            Effects.vibrate(60);

        }

    }

    public static boolean isGooglePlaySignedIn() {

        return googlePlaySignedIn;

    }

    private boolean soundEnabled, vibrationEnabled, googlePlayAutoSignIn;

    public final static String SHARED_PREFERENCES_SOUND_ENABLED_KEY = "398BC";

    public final static String SHARED_PREFERENCES_VIBRATION_ENABLED_KEY = "75DN8";

    private final static String SHARED_PREFERENCES_GOOGLE_PLAY_AUTO_SIGN_IN_KEY = "83BG7";

    // Google Play Services below
    // Initialize the google api client
    private GoogleApiClient googleApiClient;

    // Some value used for connection failure to Google Api Client
    private static final int RC_SIGN_IN = 8891;

    private static final int REQUEST_ACHIEVEMENTS = 7891;

    public static final int REQUEST_LEADERBOARD = 6891;

    private static boolean googlePlaySignedIn = false;

    private boolean resolvingConnectionFailure = false;

    public static final String HIGHSCORES_LEADERBOARD_ID = "CgkI8bfIso0dEAIQBg";

}
