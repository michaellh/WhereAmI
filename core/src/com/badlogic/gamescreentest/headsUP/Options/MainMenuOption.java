package com.badlogic.gamescreentest.headsUP.Options;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuOption extends Actor {
    Texture img;
    float x;
    float y;
    float width;
    float height;

    public MainMenuOption(Texture texture, float cellWidth, float cellHeight) {
        img = texture;
        width = cellWidth * 7;
        height = cellHeight;
        x = cellWidth * 1.5f;
        y = cellHeight * 4;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
