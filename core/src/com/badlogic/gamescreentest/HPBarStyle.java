package com.badlogic.gamescreentest;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by admin on 8/2/2016.
 */
public class HPBarStyle extends ProgressBar.ProgressBarStyle {
    public HPBarStyle (Drawable background, Drawable knob) {
        this.background = background;
        this.knob = knob;
    }
}
