package com.badlogic.gamescreentest.headsUP.PlayerHealthBar;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

public class PlayerHPBar extends ProgressBar {
    public PlayerHPBar(float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
        super(min, max, stepSize, vertical, style);
    }
}
