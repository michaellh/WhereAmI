package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 6/9/2016.
 */
public class MainMenuCont extends Actor {
    private float width;
    private float height;
    private Sprite contButton;

    public MainMenuCont(float cellWidth, float cellHeight) {
        width = cellWidth;
        height = cellHeight;
        contButton = new Sprite(new Texture(Gdx.files.internal("neutralface.jpg")));
        contButton.setPosition(width * 1, height);
        contButton.setSize(width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        contButton.draw(batch);
    }

    @Override
    public float getX() {
        return contButton.getX();
    }

    @Override
    public float getY() {
        return contButton.getY();
    }
}
