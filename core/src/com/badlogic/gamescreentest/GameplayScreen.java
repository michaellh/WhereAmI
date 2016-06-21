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
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Random;

/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    private Sprite backGround;
    Sprite room1;
    Sprite room2;
    Sprite room3;
    Sprite room4;
    Stage stage;
    Room room;

    Random rand;
    int roomNum;
    float cellWidth;
    float cellHeight;
    float screenWidth;
    float screenHeight;
    float tileWidth;
    int tiledMapWidth;
    int tiledMapHeight;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 4;
        tiledMapWidth = 120;
        tiledMapHeight = 120;
        //tileWidth = screenWidth / 32;
        rand = new Random();
/*
        roomNum = randInt(4, 10);
        for(int i = 1; i <= roomNum; i++) {
            room = new Room(randInt(0, tiledMapWidth), randInt(0, tiledMapHeight),
                    tiledMapWidth, tiledMapHeight, "badlogic.jpg");
            roomGen = room.createRoom();
        }
*/
        room = new Room(randInt(0, tiledMapWidth), randInt(0, tiledMapHeight),
                randInt(5, 10), randInt(5, 10), "badlogic.jpg");
        room1 = room.createRoom();

        room = new Room(randInt(0, tiledMapWidth), randInt(0, tiledMapHeight),
                randInt(5, 10), randInt(5, 10), "badlogic.jpg");
        room2 = room.createRoom();

        room = new Room(randInt(0, tiledMapWidth), randInt(0, tiledMapHeight),
                randInt(5, 10), randInt(5, 10), "badlogic.jpg");
        room3 = room.createRoom();

        room = new Room(randInt(0, tiledMapWidth), randInt(0, tiledMapHeight),
                randInt(5, 10), randInt(5, 10), "badlogic.jpg");
        room4 = room.createRoom();

        this.game = gam;
        camera = new OrthographicCamera();
        stage = new Stage(new FillViewport(tiledMapWidth, tiledMapHeight, camera));
/*
        backGround = new Sprite(new Texture(Gdx.files.internal("ugly face sean 2.png")));
        backGround.setPosition(0, 0);
        backGround.setSize(cellWidth * 5, cellHeight * 3);
        */

        //InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
/*
        stage.getBatch().begin();
        backGround.draw(stage.getBatch());
        stage.getBatch().end();
*/
        /*
        stage.getBatch().begin();
        roomGen.draw(stage.getBatch());
        stage.getBatch().end();
*/
        stage.getBatch().begin();
        room1.draw(stage.getBatch());
        room2.draw(stage.getBatch());
        room3.draw(stage.getBatch());
        room4.draw(stage.getBatch());
        stage.getBatch().end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public int randInt(int min, int max) {
        int randNum = rand.nextInt((max - min) + 1) + min;
        return randNum;
    }

    public float getTileWidth() {
        return tileWidth;
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
