package com.badlogic.gamescreentest.menuButtons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * Created by admin on 6/9/2016.
 */
public class MainMenuCont extends Actor {
    private Texture img;
    private float x;
    private float y;
    private float width;
    private float height;

    public MainMenuCont(float cellWidth, float cellHeight) {
        img = new Texture("neutralface.jpg");
        x = cellWidth * 1;
        y = cellHeight;
        width = cellWidth;
        height = cellHeight;
        this.setBounds(x, y, width, width);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
