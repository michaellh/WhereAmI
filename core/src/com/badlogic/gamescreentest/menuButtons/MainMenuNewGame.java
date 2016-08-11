package com.badlogic.gamescreentest.menuButtons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 6/9/2016.
 */
public class MainMenuNewGame extends Actor {
    private float width;
    private float height;
    private Sprite newGameButton;

    public MainMenuNewGame(float cellWidth, float cellHeight) {
        width = cellWidth;
        height = cellHeight;
        newGameButton = new Sprite(new Texture(Gdx.files.internal("happyface.jpg")));
        newGameButton.setPosition(cellWidth * 3, cellHeight);
        newGameButton.setSize(cellWidth, cellHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        newGameButton.draw(batch);
    }

    @Override
    public float getX() {
        return newGameButton.getX();
    }

    @Override
    public float getY() {
        return newGameButton.getY();
    }
}
