package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by admin on 6/8/2016.
 */
public class MainMenuScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;

    private MainMenuBackGround backGround;
    private MainMenuCont contButton;
    private MainMenuQuit quitButton;
    private MainMenuNewGame newGameButton;

    Vector2 storeTouch;
    float cellWidth;
    float cellHeight;
    float screenWidth;
    float screenHeight;

    public MainMenuScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 3;

        game = gam;
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), game.batch);

        backGround = new MainMenuBackGround(cellWidth, cellHeight);
        contButton = new MainMenuCont(cellWidth, cellHeight);
        quitButton = new MainMenuQuit(cellWidth, cellHeight);
        newGameButton = new MainMenuNewGame(cellWidth, cellHeight);
        stage.addActor(backGround);
        stage.addActor(contButton);
        stage.addActor(quitButton);
        stage.addActor(newGameButton);

        InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //System.out.println(Gdx.input.getX() + " " + (stage.getHeight() - Gdx.input.getY()));
        storeTouch = new Vector2(Gdx.input.getX(), (stage.getHeight() - Gdx.input.getY()));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(storeTouch.x >= contButton.getX() && storeTouch.x <= contButton.getX() + cellWidth
                && storeTouch.y >= contButton.getY() && storeTouch.y <= contButton.getY() + cellHeight) {
            System.out.println("neutralface.jpg touched");
        }
        else if(storeTouch.x >= quitButton.getX() && storeTouch.x <= quitButton.getX() + cellWidth
                && storeTouch.y >= quitButton.getY() && storeTouch.y <= quitButton.getY() + cellHeight) {
            System.out.println("sadface.jpg touched");
            Gdx.app.exit();
        }
        else if(storeTouch.x >= newGameButton.getX() && storeTouch.x <= newGameButton.getX() + cellWidth
                && storeTouch.y >= newGameButton.getY() && storeTouch.y <= newGameButton.getY() + cellHeight) {
            System.out.println("happyface.jpg touched");
            dispose();
            game.setScreen(new GameplayScreen(game));
        }
        return true;
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

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
