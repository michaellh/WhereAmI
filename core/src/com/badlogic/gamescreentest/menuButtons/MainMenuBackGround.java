package com.badlogic.gamescreentest.menuButtons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuBackGround extends Actor {
    private Texture img;
    private float x;
    private float y;
    private float width;
    private float height;

    public MainMenuBackGround(float cellWidth, float cellHeight) {
        img = new Texture("gameTitle.jpg");
        x = 0;
        y = cellHeight * 2;
        width = cellWidth * 5;
        height = cellHeight;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
