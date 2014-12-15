package com.studiau.amazinggravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        setContentView(R.layout.activity_main);

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

    public void play(View view) {

        Intent intent = new Intent(this, GameActivity.class);

        startActivity(intent);

    }


}
