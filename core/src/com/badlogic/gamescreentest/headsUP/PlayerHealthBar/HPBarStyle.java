package com.badlogic.gamescreentest.headsUP.PlayerHealthBar;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class HPBarStyle extends ProgressBar.ProgressBarStyle {
    public HPBarStyle (Drawable background, Drawable knob) {
        this.background = background;
        this.knob = knob;
    }
}
