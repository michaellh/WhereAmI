package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Random;

/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;
    Room room;

    Random rand;
    int wallCount;
    Array<Room> rooms;
    int[][] mapTiles;
    float cellWidth;
    float cellHeight;
    float screenWidth;
    float screenHeight;
    int tiledMapWidth;
    int tiledMapHeight;
    int thisPosX;
    int thisPosY;
    int MIN_X;
    int MIN_Y;
    int MAX_X;
    int MAX_Y;
    int startPosX;
    int startPosY;
    int endPosX;
    int endPosY;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 4;
        tiledMapWidth = 13;
        tiledMapHeight = 15;
        rand = new Random();

        this.game = gam;
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), game.batch);

        mapTiles = new int[tiledMapWidth][tiledMapHeight];
        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                if(randInt(1, 100) <= 45) {
                    mapTiles[i][j] = 1;
                }
            }
        }

        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                System.out.print(mapTiles[i][j] + " ");
            }
            System.out.println("");
        }
        System.out.println("");

        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                startPosX = (i - 1 < 0) ? i : i - 1;
                startPosY = (j - 1 < 0) ? j : j-1;
                endPosX =   (i + 1 > 12) ? i : i+1;
                endPosY =   (j + 1 > 14) ? j : j+1;

                // See how many are alive
                for (int rowNum=startPosX; rowNum<=endPosX; rowNum++) {
                    for (int colNum=startPosY; colNum<=endPosY; colNum++) {
                        if(mapTiles[rowNum][colNum] == 1) {
                            wallCount++;
                        }
                    }
                }
                if(wallCount < 5) {
                    mapTiles[i][j] = 0;
                }
                wallCount = 0;
            }
        }

        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                System.out.print(mapTiles[i][j] + " ");
            }
            System.out.println("");
        }

        //InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public int randInt(int min, int max) {
        int randNum = rand.nextInt((max - min) + 1) + min;
        return randNum;
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
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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
