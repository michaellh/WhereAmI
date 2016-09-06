package com.badlogic.gamescreentest.headsUP.Options;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 9/6/2016.
 */
public class OptionsBackground extends Actor {
    Texture img;
    float x;
    float y;
    float width;
    float height;

    public OptionsBackground(Texture texture, float cellWidth, float cellHeight) {
        img = texture;
        width = cellWidth * 10;
        height = cellHeight * 8;
        x = 0;
        y = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
