package com.badlogic.gamescreentest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends Game {
	AssetManager assetManager;
	SpriteBatch batch;
	boolean newGame;

	@Override
	public void create () {
		assetManager = new AssetManager();
		assetManager.load("I'm Still Here.mp3", Music.class);
		assetManager.load("Decisions.mp3", Music.class);
		assetManager.load("Button Push.mp3", Sound.class);
		assetManager.load("Attacked.mp3", Sound.class);
		assetManager.load("Pain.mp3", Sound.class);
		assetManager.load("Dying.mp3", Sound.class);
		assetManager.load("Floor Climb.mp3", Sound.class);
		assetManager.load("playerCharacter.png", Texture.class);
		assetManager.load("playerAttackRight.png", Texture.class);
		assetManager.load("playerAttackLeft.png", Texture.class);
		assetManager.load("enemyIdle_001.png", Texture.class);
		assetManager.load("enemyAttack_001.png", Texture.class);
		assetManager.load("portal_001.png", Texture.class);
		assetManager.load("wall2_001.png", Texture.class);
		assetManager.load("floor2_001.png", Texture.class);
		assetManager.load("map.jpg", Texture.class);
		assetManager.load("mapLocate.jpg", Texture.class);
		assetManager.load("mapExit.jpg", Texture.class);
		assetManager.load("healthBarPivot.jpg", Texture.class);
		assetManager.load("healthBar.jpg", Texture.class);
		assetManager.load("OptionsButton.jpg", Texture.class);
		assetManager.load("OptionsBackground.jpg", Texture.class);
		assetManager.load("MainMenu.jpg", Texture.class);
		assetManager.load("QuitGame.jpg", Texture.class);
		assetManager.load("ResumeGame.jpg", Texture.class);
		assetManager.load("arrowUp.png", Texture.class);
		assetManager.load("arrowRight.png", Texture.class);
		assetManager.load("arrowDown.png", Texture.class);
		assetManager.load("arrowLeft.png", Texture.class);
		assetManager.load("attackButton.png", Texture.class);
		assetManager.finishLoading();

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

