package com.studiau.amazinggravity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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

        gameView = new GameView(this);

        setContentView(gameView);

    }

    @Override
    public void onResume() {

        super.onResume();

        gameView.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

        gameView.onPause();

    }

    @Override
    public void onStop() {

        super.onStop();

        finish();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    private GameView gameView;

}
