package com.badlogic.gamescreentest.menuButtons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MainMenuNewGame extends Actor {
    private Texture img;
    private float x;
    private float y;
    private float width;
    private float height;

    public MainMenuNewGame(float cellWidth, float cellHeight) {
        img = new Texture("menuNew.jpg");
        x = cellWidth * 3;
        y = cellHeight;
        width = cellWidth;
        height = cellHeight;
        this.setBounds(x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
