package com.studiau.amazinggravity;

import android.content.Context;
import android.os.Vibrator;

/**
 * @Author: Daniel Au
 */

public class Effects {

    private static Vibrator vibrator;

    public static void initEffects(Context context) {

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public static void vibrate(int milliseconds) {

        vibrator.vibrate(milliseconds);

    }

}
