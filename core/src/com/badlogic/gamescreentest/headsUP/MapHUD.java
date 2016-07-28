package com.badlogic.gamescreentest.headsUP;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 7/28/2016.
 */
public class MapHUD extends Actor {
    Texture img;
    float x;
    float y;
    float width;
    float height;

    public MapHUD(Texture texture, float cellWidth, float cellHeight) {
        img = texture;
        width = cellWidth;
        height = cellHeight;
        x = width * 7;
        y = height * 7;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }

    @Override
    public float getX() {
        return super.getX();
    }
}
