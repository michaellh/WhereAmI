package com.badlogic.gamescreentest;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 6/10/2016.
 */
public class MainMenuBackGround extends Actor {
    private Texture img;
    private float x;
    private float y;
    private float width;
    private float height;

    public MainMenuBackGround(float cellWidth, float cellHeight) {
        img = new Texture("ugly face sean.jpg");
        x = 0;
        y = 0;
        width = cellWidth * 5;
        height = cellHeight * 3;
    }

    /*
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }*/
}
