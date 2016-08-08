package com.badlogic.gamescreentest;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

/**
 * Created by admin on 8/2/2016.
 */
public class PlayerHPBar extends ProgressBar {
    public PlayerHPBar(float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
        super(min, max, stepSize, vertical, style);
    }
}
