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

/**
 * Author: Daniel Au
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        loadSharedPreferences();

        setSoundEnabled();

        setVibrationEnabled();

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

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    private void loadSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        soundEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCES_SOUND_ENABLED_KEY, true);

        vibrationEnabled = sharedPreferences.getBoolean(SHARED_PREFERENCES_VIBRATION_ENABLED_KEY, true);

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

    }



    public void play(View view) {

        Intent intent = new Intent(this, GameActivity.class);

        startActivity(intent);

    }

    public void rateMyGame(View view) {

        Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());

        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        try {

            startActivity(goToMarket);

        } catch (ActivityNotFoundException e) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));

        }

    }

    private void setImage(int RidButton, int RidDrawable) {

        ImageButton button = (ImageButton) findViewById(RidButton);

        button.setImageResource(RidDrawable);

    }

    private boolean soundEnabled, vibrationEnabled;

    private final static String SHARED_PREFERENCES_SOUND_ENABLED_KEY = "398BC";
    private final static String SHARED_PREFERENCES_VIBRATION_ENABLED_KEY = "75DN8";

}
