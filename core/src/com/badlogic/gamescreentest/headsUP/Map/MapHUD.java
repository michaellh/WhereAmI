package com.badlogic.gamescreentest.headsUP.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

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
        x = width * 8;
        y = height * 7;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
