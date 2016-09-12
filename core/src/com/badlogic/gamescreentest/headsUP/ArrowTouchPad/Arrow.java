package com.badlogic.gamescreentest.headsUP.ArrowTouchPad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by admin on 9/11/2016.
 */
public class Arrow extends Actor {
    Texture img;
    float x;
    float y;
    float height;
    float width;

    public Arrow(Texture img, float x, float y, float width, float height) {
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
