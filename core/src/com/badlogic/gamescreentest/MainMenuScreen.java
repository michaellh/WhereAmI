package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by admin on 6/8/2016.
 */
public class MainMenuScreen implements Screen {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;
    AssetManager assetManager;
    SaveFile saveFile;
    Label text;
    Label.LabelStyle textStyle;
    BitmapFont font;

    Music menuMusic;

    com.badlogic.gamescreentest.menuButtons.MainMenuBackGround backGround;
    com.badlogic.gamescreentest.menuButtons.MainMenuCont contButton;
    com.badlogic.gamescreentest.menuButtons.MainMenuQuit quitButton;
    com.badlogic.gamescreentest.menuButtons.MainMenuNewGame newGameButton;

    float cellWidth, cellHeight, screenWidth, screenHeight;

    public MainMenuScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 3;

        game = gam;

        assetManager = game.assetManager;
        menuMusic = assetManager.get("I'm Still Here.mp3", Music.class);
        menuMusic.setLooping(true);

        // Create an orthographic camera and attach it to the stage with the game batch
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), game.batch);

        // Add the main menu background to the stage
        backGround = new com.badlogic.gamescreentest.menuButtons.MainMenuBackGround(cellWidth, cellHeight);
        stage.addActor(backGround);

        // Add the continue game button to the stage
        contButton = new com.badlogic.gamescreentest.menuButtons.MainMenuCont(cellWidth, cellHeight);
        contButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("neutralface.jpg touched");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                //continue from the save file
                saveFile = new SaveFile();
                if(saveFile.saveFilesExist()) {
                    dispose();
                    game.setNewGame(false);
                    game.setScreen(new GameplayScreen(game));
                }
                else {
                    font = new BitmapFont();
                    font.getData().setScale(5, 5);
                    textStyle = new Label.LabelStyle(font, Color.WHITE);
                    text = new Label("No save files found!", textStyle);
                    text.setPosition(screenWidth/3, cellHeight - (cellHeight/2));
                    text.addAction(Actions.fadeOut(3));
                    stage.addActor(text);
                    System.out.println("No save files detected!");
                }
            }
        });
        stage.addActor(contButton);

        // Add the quit game button to the stage
        quitButton = new com.badlogic.gamescreentest.menuButtons.MainMenuQuit(cellWidth, cellHeight);
        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("sadface.jpg touched");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                dispose();
                Gdx.app.exit();
            }
        });
        stage.addActor(quitButton);

        // Add the new game button to the stage
        newGameButton = new com.badlogic.gamescreentest.menuButtons.MainMenuNewGame(cellWidth, cellHeight);
        newGameButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("happyface.jpg touched");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                dispose();
                game.setNewGame(true);
                game.setScreen(new GameplayScreen(game));
            }
        });
        stage.addActor(newGameButton);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        for(Actor actor : stage.getActors()) {
            actor.addAction(Actions.removeActor());
        }
        stage.dispose();
    }

    @Override
    public void show() {
        menuMusic.setPosition(0);
        menuMusic.play();
    }

    @Override
    public void hide() {
        menuMusic.pause();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {

    }
}
