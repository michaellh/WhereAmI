package com.badlogic.gamescreentest.headsUP.ArrowTouchPad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AttackButton extends Actor {
    Texture img;
    float x;
    float y;
    float height;
    float width;

    public AttackButton(Texture img, float x, float y, float width, float height) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        setBounds(x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(img, x, y, width, height);
    }
}
