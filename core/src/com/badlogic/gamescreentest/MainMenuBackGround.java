package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 6/10/2016.
 */
public class MainMenuBackGround extends Actor {
    private float width;
    private float height;
    private Sprite backGround;

    public MainMenuBackGround(float cellWidth, float cellHeight) {
        width = cellWidth;
        height = cellHeight;
        backGround = new Sprite(new Texture(Gdx.files.internal("ugly face sean.jpg")));
        backGround.setPosition(0, 0);
        backGround.setSize(cellWidth * 5, cellHeight * 3);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        backGround.draw(batch);
    }
}
