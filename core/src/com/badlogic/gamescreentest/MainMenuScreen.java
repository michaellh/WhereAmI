package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

    private MainMenuBackGround backGround;
    private com.badlogic.gamescreentest.menuButtons.MainMenuCont contButton;
    private com.badlogic.gamescreentest.menuButtons.MainMenuQuit quitButton;
    private com.badlogic.gamescreentest.menuButtons.MainMenuNewGame newGameButton;

    boolean assetsLoaded;
    float cellWidth, cellHeight, screenWidth, screenHeight;

    public MainMenuScreen(final GameScreen gam) {
        assetsLoaded = false;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 3;

        game = gam;
        saveFile = new SaveFile();

        // Load the assets needed for the game screen
        assetManager = game.assetManager;
        assetManager.load("happyface.jpg", Texture.class);
        assetManager.load("sadface.jpg", Texture.class);
        assetManager.load("neutralface.jpg", Texture.class);
        assetManager.load("badlogic.jpg", Texture.class);
        assetManager.load("ugly face sean.jpg", Texture.class);
        assetManager.load("OBJECTION.jpg", Texture.class);
        assetManager.load("cool hummingbird.jpg", Texture.class);
        assetManager.load("Chin_po.jpg", Texture.class);
        assetManager.load("ugly face sean 2.png", Texture.class);
        assetManager.load("man.png", Texture.class);
        assetManager.load("1pman.jpg", Texture.class);

        // Create an orthographic camera and attach it to the stage with the game batch
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), game.batch);

        // Add the main menu background to the stage
        backGround = new MainMenuBackGround(cellWidth, cellHeight);
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
                //continue from the save file
                if(assetsLoaded && saveFile.saveFilesExist()) {
                    dispose();
                    game.setNewGame(false);
                    game.setScreen(new GameplayScreen(game));
                }
                else {
                    font = new BitmapFont();
                    font.getData().setScale(5, 5);
                    textStyle = new Label.LabelStyle(font, Color.BLACK);
                    text = new Label("No save files found!", textStyle);
                    text.setPosition(screenWidth/3, cellHeight * 1);
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
                dispose();
                game.setNewGame(true);
                if(assetsLoaded) {
                    game.setScreen(new GameplayScreen(game));
                }
            }
        });
        stage.addActor(newGameButton);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(assetManager.update()) {
            assetsLoaded = true;
        }

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

    }

    @Override
    public void hide() {

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
