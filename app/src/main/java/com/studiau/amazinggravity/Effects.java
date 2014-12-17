package com.studiau.amazinggravity;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

/**
 * Author: Daniel Au
 */

public class Effects {

    public static int SOUNDEFFECT_GAMEOVER;

    private static SoundPool pool;
    private static Vibrator vibrator;

    public static void initSoundEffects(Context context) {

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

        //SOUNDEFFECT_GAMEOVER = pool.load(context, R.raw.gameover, 1);

    }

    public static void vibrate(int milliseconds) {

        vibrator.vibrate(milliseconds);

    }

    public static void play(int sound, float volume, int priority) {

        pool.play(sound, volume, volume, priority, 0, 1.0f);

    }

}
