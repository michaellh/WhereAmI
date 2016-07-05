package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    AssetManager assetManager;
    Texture happyFace;
    Texture neutralFace;
    Texture sadFace;
    Texture badLogic;
    Texture uglySean;
    Random rand;

    int[][] ogMap;
    int[][] newMap;

    float cellWidth;
    float cellHeight;
    float screenWidth;
    float screenHeight;
    int tiledMapWidth;
    int tiledMapHeight;
    int textureSize;
    int wallCount;

    int ranPosX;
    int ranPosY;
    int startPosX;
    int startPosY;
    int endPosX;
    int endPosY;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 4;
        textureSize = 32;
        rand = new Random();
        tiledMapWidth = randInt(25, 40);
        tiledMapHeight = randInt(25, 40);

        this.game = gam;
        camera = new OrthographicCamera(10 * textureSize, 5 * textureSize);
        camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
        camera.update();

        assetManager = game.assetManager;
        uglySean = assetManager.get("ugly face sean.jpg", Texture.class);
        happyFace = assetManager.get("happyface.jpg", Texture.class);
        neutralFace = assetManager.get("neutralface.jpg", Texture.class);
        sadFace = assetManager.get("sadface.jpg", Texture.class);
        badLogic = assetManager.get("badlogic.jpg", Texture.class);

        newMap = createWorld();

        //InputMultiplexer im = new InputMultiplexer(stage, this);
        //Gdx.input.setInputProcessor(im);
    }

    public int[][] createWorld() {
        ogMap = new int[tiledMapWidth][tiledMapHeight];
        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                if(randInt(0, 100) < 40) {
                    ogMap[i][j] = 1;
                }
            }
        }

        while(ogMap[ranPosX][ranPosY] != 0) {
            ranPosX = randInt(1, tiledMapWidth - 1);
            ranPosY = randInt(1, tiledMapHeight - 1);
        }
        newMap = floodFill(ogMap, ranPosX, ranPosY, 0, 1);

        newMap = mapIter1(ogMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter1(newMap);
        newMap = mapIter1(newMap);
        newMap = mapIter1(newMap);
        mapBorders();
        return newMap;
    }

    public int[][] floodFill(int[][] node, int x, int y, int target, int replace) {
        if(x <= 0 || x >= tiledMapWidth) {
            return node;
        }
        if(y <= 0 || y >= tiledMapHeight) {
            return node;
        }
        if(target == replace) {
            return node;
        }
        if(node[x][y] != target) {
            return node;
        }
        node[x][y] = replace;
        floodFill(node, x, y--, target, replace);
        floodFill(node, x, y++, target, replace);
        floodFill(node, x--, y, target, replace);
        floodFill(node, x++, y, target, replace);
        return node;
    }

    public int[][] mapIter1(int[][] oldMap) {
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
                else if(wallCount >= 5) {
                    newMap[i][j] = 1;
                }
                wallCount = 0;
            }
        }
        return newMap;
    }

    public int[][] mapIter2(int[][] oldMap) {
        int[][] newMap = oldMap;

        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                startPosX = (i - 2 < 0) ? i : i - 2;
                startPosY = (j - 2 < 0) ? j : j - 2;
                endPosX =   (i + 2 > (tiledMapWidth - 1)) ? i : i + 2;
                endPosY =   (j + 2 > (tiledMapHeight - 1)) ? j : j + 2;

                for (int rowNum = startPosX; rowNum <= endPosX; rowNum++) {
                    for (int colNum = startPosY; colNum <= endPosY; colNum++) {
                        if(oldMap[rowNum][colNum] == 1) {
                            wallCount++;
                        }
                    }
                }
                if(wallCount <= 2){
                    newMap[i][j] = 1;
                }
                wallCount = 0;
            }
        }
        return newMap;
    }

    public void mapBorders() {
        for(int i = 0; i < tiledMapWidth; i++) {
            newMap[i][0] = 1;
            newMap[i][tiledMapHeight - 1] = 1;
        }
        for(int j = 0; j < tiledMapHeight; j++) {
            newMap[0][j] = 1;
            newMap[tiledMapWidth - 1][j] = 1;
        }
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        for(int i = 0; i < tiledMapWidth; i++) {
            for (int j = 0; j < tiledMapHeight; j++) {
                if (newMap[i][j] == 1) {
                    game.batch.draw(happyFace, i * textureSize, j * textureSize,
                            textureSize, textureSize);
                }
                else {
                    game.batch.draw(badLogic, i * textureSize, j * textureSize,
                            textureSize, textureSize);
                }
            }
        }
        game.batch.end();
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
        assetManager.dispose();
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
