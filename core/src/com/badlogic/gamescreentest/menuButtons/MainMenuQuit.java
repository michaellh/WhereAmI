package com.badlogic.gamescreentest.menuButtons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by admin on 6/9/2016.
 */
public class MainMenuQuit extends Actor {
    private float width;
    private float height;
    private Sprite quitButton;

    public MainMenuQuit(float cellWidth, float cellHeight) {
        width = cellWidth;
        height = cellHeight;
        quitButton = new Sprite(new Texture(Gdx.files.internal("sadface.jpg")));
        quitButton.setPosition(width * 2, height);
        quitButton.setSize(width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        quitButton.draw(batch);
    }

    @Override
    public float getX() {
        return quitButton.getX();
    }

    @Override
    public float getY() {
        return quitButton.getY();
    }
}
