package com.badlogic.gamescreentest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends Game {
	AssetManager assetManager;
	SpriteBatch batch;

	@Override
	public void create () {

		assetManager = new AssetManager();
		assetManager.load("happyface.jpg", Texture.class);
		assetManager.load("sadface.jpg", Texture.class);
		assetManager.load("neutralface.jpg", Texture.class);

		batch = new SpriteBatch();
		//this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		if(assetManager.update()) {
			this.setScreen(new MainMenuScreen(this));
		}
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}

