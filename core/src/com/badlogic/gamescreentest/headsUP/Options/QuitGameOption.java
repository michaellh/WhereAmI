package com.badlogic.gamescreentest.headsUP.Options;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 9/5/2016.
 */
public class QuitGameOption extends Actor {
    Texture img;
    float x;
    float y;
    float width;
    float height;

    public QuitGameOption(Texture texture, float cellWidth, float cellHeight) {
        img = texture;
        width = cellWidth * 7;
        height = cellHeight;
        x = cellWidth * 1.5f;
        y = cellHeight * 2;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
