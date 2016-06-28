package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Random;

/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;
    Sprite backGround;
    Random rand;

    int wallCount;
    int[][] ogMap;
    int[][] newMap;
    float cellWidth;
    float cellHeight;
    float screenWidth;
    float screenHeight;
    int tiledMapWidth;
    int tiledMapHeight;
    int startPosX;
    int startPosY;
    int endPosX;
    int endPosY;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 4;
        tiledMapWidth = 30;
        tiledMapHeight = 30;
        rand = new Random();

        this.game = gam;
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), game.batch);

        ogMap = new int[tiledMapWidth][tiledMapHeight];
        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                if(randInt(0, 100) < 40) {
                    ogMap[i][j] = 1;
                }
            }
        }
        //displayWorld(ogMap);
        newMap = mapIter(ogMap);
        //displayWorld(newMap);
        newMap = mapIter(newMap);
        //displayWorld(newMap);
        newMap = mapIter(newMap);
        //displayWorld(newMap);
        newMap = mapIter(newMap);
        //displayWorld(newMap);
        newMap = mapIter(newMap);

        //InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(stage);
    }

    public int[][] mapIter(int[][] oldMap) {
        wallCount = 0;
        int[][] newMap = new int[tiledMapWidth][tiledMapHeight];

        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                startPosX = (i - 1 < 0) ? i : i - 1;
                startPosY = (j - 1 < 0) ? j : j - 1;
                endPosX =   (i + 1 > (tiledMapWidth - 1)) ? i : i + 1;
                endPosY =   (j + 1 > (tiledMapHeight - 1)) ? j : j + 1;

                for (int rowNum = startPosX; rowNum <= endPosX; rowNum++) {
                    for (int colNum = startPosY; colNum <= endPosY; colNum++) {
                        if(oldMap[rowNum][colNum] == 1) {
                            wallCount++;
                        }
                    }
                }
                if(oldMap[i][j] == 1 && wallCount >= 4) {
                    newMap[i][j] = 1;
                }
                else if(oldMap[i][j] == 0 && wallCount >= 5) {
                    newMap[i][j] = 1;
                }
                wallCount = 0;
            }
        }
        return newMap;
    }

    public void displayWorld(int[][] map) {
        for(int i = 0; i < tiledMapHeight; i++) {
            for(int j = 0; j < tiledMapWidth; j++) {
                if(map[j][i] == 1) {
                    System.out.print("*" + " ");
                }
                else {
                    System.out.print("  ");
                }
            }
            System.out.println("");
        }
        System.out.println("_________________________________________________________________"+ "\n");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        for(int i = 0; i < tiledMapWidth; i++) {
            for (int j = 0; j < tiledMapHeight; j++) {
                if(newMap[i][j] == 1) {
                    backGround = new Sprite(new Texture(Gdx.files.internal("happyface.jpg")));
                    backGround.setPosition(i * 32, j * 32);
                    backGround.setSize(32, 32);
                    backGround.draw(stage.getBatch());
                }
            }
        }
        stage.getBatch().end();

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
