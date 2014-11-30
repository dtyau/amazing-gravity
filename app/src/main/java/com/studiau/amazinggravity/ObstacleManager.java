package com.studiau.amazinggravity;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Author: Daniel Au.
 */

public class ObstacleManager {

    public ObstacleManager() {

        blurMaskFilter = new BlurMaskFilter(50, BlurMaskFilter.Blur.OUTER);

        obstacles = new ArrayList<Obstacle>();

        addObstacle();

        addObstacle();

    }

    public void addObstacle() {

        obstacles.add( new Obstacle() );

    }

    public void update(Ship ship) {

        for ( int i = 0; i < obstacles.size(); i++ ) {

            obstacles.get(i).update(ship);

        }

    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.parseColor("#90CAF9"));

        paint.setMaskFilter(blurMaskFilter);

        for ( int i = 0; i < obstacles.size(); i++ ) {

            obstacles.get(i).draw(canvas, paint);

        }

        paint.setMaskFilter(null);

    }

    private BlurMaskFilter blurMaskFilter;

    private ArrayList<Obstacle> obstacles;

}
