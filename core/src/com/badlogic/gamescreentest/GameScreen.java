package com.badlogic.gamescreentest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends Game {
	AssetManager assetManager;
	SpriteBatch batch;
	boolean newGame;

	@Override
	public void create () {
		assetManager = new AssetManager();
		batch = new SpriteBatch();
		newGame = true;
		this.setScreen(new MainMenuScreen(this));
	}

	public boolean getNewGame() {
		return newGame;
	}

	public void setNewGame(boolean state) {
		newGame = state;
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}

