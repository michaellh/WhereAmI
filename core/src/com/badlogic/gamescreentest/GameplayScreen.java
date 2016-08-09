package com.badlogic.gamescreentest;

import com.badlogic.gamescreentest.headsUP.Map.MapExit;
import com.badlogic.gamescreentest.headsUP.Map.MapSeePlayer;
import com.badlogic.gamescreentest.headsUP.MapHUD;
import com.badlogic.gamescreentest.headsUP.gameOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;
    AssetManager assetManager;
    Texture happyFace, neutralFace, sadFace, badLogic, uglySean, humBird, objection, chinPo, uglySean2, opm, man;
    Random rand;

    Skin skin;
    TextButton textButton;
    gameOptions optButton;
    MapHUD mapButton;
    MapExit mapExit;
    MapSeePlayer mapSeePlayer;
    HPBarStyle hpBarStyle;
    PlayerHPBar playerHPBar;
    PlayerCharacter playerChar;
    Array<standardEnemy> enemies;

    int[][] ogMap, newMap;
    ArrayList<Node> closed;
    PriorityQueue<Node> open;
    Node startNode, goalNode;
    Array<Node> path;

    float screenWidth, screenHeight, cellWidth, cellHeight;
    int tiledMapWidth, tiledMapHeight;
    int FLOOR, WALL, PLAYER, ENEMY, EXIT;
    int TEXTURESIZE;
    int enemyNum, wallCount;

    boolean options;
    boolean mapPressed;
    int ranPosX, ranPosY, startPosX, startPosY, endPosX, endPosY;
    int damage;
    int result;

    ArrayList<Vector2> mapDiscovered;
    Vector2 userTouch, dragOld, dragNew;
    Vector3 worldTouch;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 10;
        cellHeight = screenHeight / 8;
        options = false;
        mapPressed = false;
        mapDiscovered = new ArrayList<Vector2>();

        TEXTURESIZE = 32;
        FLOOR = 0;
        WALL = 1;
        PLAYER = 2;
        ENEMY = 3;
        EXIT = 4;

        rand = new Random();
        tiledMapWidth = randInt(50, 100);
        tiledMapHeight = randInt(50, 100);
        enemyNum = randInt(5, 10);

        //initialize the player character's stats
        playerChar = new PlayerCharacter(randInt(10, 15), 5,
                0, randInt(0, 10));
        //initialize a random-sized array of standard enemies and their stats
        enemies = new Array<standardEnemy>();
        for(int i = 0; i < randInt(5, 10); i++) {
            enemies.add(new standardEnemy(randInt(10, 15), 1,
                    0, randInt(0, 10)));
        }

        //set up the Game, SpriteBatch, and OrthographicCamera
        this.game = gam;
        camera = new OrthographicCamera(14 * TEXTURESIZE, 10 * TEXTURESIZE);
        stage = new Stage(new ScreenViewport(), game.batch);

        //get, after loading, the assets
        assetManager = game.assetManager;
        uglySean = assetManager.get("ugly face sean.jpg", Texture.class);
        happyFace = assetManager.get("happyface.jpg", Texture.class);
        neutralFace = assetManager.get("neutralface.jpg", Texture.class);
        sadFace = assetManager.get("sadface.jpg", Texture.class);
        badLogic = assetManager.get("badlogic.jpg", Texture.class);
        humBird = assetManager.get("cool hummingbird.jpg", Texture.class);
        objection = assetManager.get("OBJECTION.jpg", Texture.class);
        chinPo = assetManager.get("Chin_po.jpg", Texture.class);
        uglySean2 = assetManager.get("ugly face sean 2.png", Texture.class);
        opm = assetManager.get("1pman.jpg", Texture.class);
        man = assetManager.get("man.png", Texture.class);

        //create the game world
        newMap = createWorld();

        //initialize player character position
        while(newMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        playerChar.x = ranPosX;
        playerChar.y = ranPosY;
        newMap[playerChar.x][playerChar.y] = PLAYER;
        Vector2 playerPos = new Vector2(playerChar.getX(), playerChar.getY());
        mapDiscovered.add(new Vector2(playerChar.getX(), playerChar.getY()));
        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
        camera.update();

        for(int i = 0; i < enemies.size; i++) {
            while(newMap[ranPosX][ranPosY] != FLOOR) {
                ranPosX = randInt(1, (tiledMapWidth - 1));
                ranPosY = randInt(1, (tiledMapHeight - 1));
            }
            enemies.get(i).x = ranPosX;
            enemies.get(i).y = ranPosY;
            newMap[enemies.get(i).x][enemies.get(i).y] = ENEMY;
        }

        while(newMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        newMap[ranPosX][ranPosY] = EXIT;
        System.out.println(ranPosX + " " + ranPosY);

        //initialize the HUD buttons and add them to the stage as actors
        Sprite spriteBack = new Sprite(opm);
        spriteBack.setSize(cellWidth * 3, (cellHeight / 2));
        final SpriteDrawable spriteDrawableBack = new SpriteDrawable(spriteBack);
        Sprite spriteKnob = new Sprite(man);
        spriteKnob.setSize(0, spriteBack.getHeight());
        SpriteDrawable spriteDrawableKnob = new SpriteDrawable(spriteKnob);
        hpBarStyle = new HPBarStyle(spriteDrawableBack, spriteDrawableKnob);
        hpBarStyle.knobBefore = hpBarStyle.knob;
        playerHPBar = new PlayerHPBar(0, playerChar.HP, 1, false, hpBarStyle);
        playerHPBar.setSize(spriteBack.getWidth(), spriteBack.getHeight());
        playerHPBar.setPosition(0, 7 * cellHeight + spriteBack.getHeight());
        playerHPBar.setValue(playerChar.HP);

        mapButton = new MapHUD(chinPo, cellWidth, cellHeight);
        mapButton.setBounds(cellWidth * 8, cellHeight * 7, chinPo.getWidth(), chinPo.getHeight());
        mapButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("map touched down");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("map touched up");
                for(int i = 0; i < stage.getActors().size; i++) {
                    stage.getActors().get(i).setVisible(false);
                }
                camera.setToOrtho(false, screenWidth, screenHeight);
                camera.position.set(screenWidth, screenHeight, 0);
                mapPressed = true;
                mapExit = new MapExit(objection, cellWidth, cellHeight);
                mapExit.setBounds(cellWidth * 9, cellHeight * 7, objection.getWidth(), objection.getHeight());
                stage.addActor(mapExit);
                mapExit.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        mapPressed = false;
                        camera.setToOrtho(false, 14 * TEXTURESIZE, 10 * TEXTURESIZE);
                        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                        int mapExitLocation = stage.getActors().indexOf(mapExit, true);
                        stage.getActors().get(mapExitLocation).setVisible(false);
                        int mapFindPlayer = stage.getActors().indexOf(mapSeePlayer, true);
                        stage.getActors().get(mapFindPlayer).setVisible(false);

                        int mapButtonIndex = stage.getActors().indexOf(mapButton, true);
                        stage.getActors().get(mapButtonIndex).setVisible(true);
                        int optButtonIndex = stage.getActors().indexOf(optButton, true);
                        stage.getActors().get(optButtonIndex).setVisible(true);
                    }
                });
                mapSeePlayer = new MapSeePlayer(chinPo, cellWidth, cellHeight);
                mapSeePlayer.setBounds(cellWidth * 8, cellHeight * 7, chinPo.getWidth(), chinPo.getHeight());
                stage.addActor(mapSeePlayer);
                mapSeePlayer.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                    }
                });
            }
        });
        optButton = new gameOptions(objection, cellWidth, cellHeight);
        optButton.setBounds(cellWidth * 9, cellHeight * 7, objection.getWidth(), objection.getHeight());
        optButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("options touched down");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("options touched up");
                options = true;
            }
        });

        /*
        skin = new Skin(Gdx.files.local("uiskin.json"));
        textButton = new TextButton("Resume", skin, "default");
        textButton.setWidth(cellWidth * 2);
        textButton.setHeight(cellHeight);
        Table table = new Table(skin);
        table.setBackground(spriteDrawableBack);
        table.addActor(textButton);
        stage.addActor(table);
        //stage.getActors().;*/

        stage.addActor(mapButton);
        stage.addActor(optButton);
        stage.addActor(playerHPBar);

        //give priority touch to the stage actors
        InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(im);
    }

    public int[][] createWorld() {
        ogMap = new int[tiledMapWidth][tiledMapHeight];
        for(int i = 0; i < tiledMapWidth; i++) {
            for(int j = 0; j < tiledMapHeight; j++) {
                if(randInt(0, 100) < 40) {
                    ogMap[i][j] = WALL;
                }
            }
        }

        ranPosX = randInt(1, (tiledMapWidth - 1));
        ranPosY = randInt(1, (tiledMapHeight - 1));
        while(ogMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        //newMap = floodFill(ogMap, ranPosX, ranPosY, 0, 1);

        newMap = mapIter1(ogMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter1(newMap);
        newMap = mapIter1(newMap);
        newMap = mapIter1(newMap);
        newMap = mapBorders(newMap);
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
        floodFill(node, x--, y++, target, replace); //top left
        floodFill(node, x, y--, target, replace);   //bot
        floodFill(node, x, y++, target, replace);   //top
        floodFill(node, x++, y++, target, replace); //top right
        floodFill(node, x--, y--, target, replace); //bot left
        floodFill(node, x++, y--, target, replace); //bot right
        floodFill(node, x--, y, target, replace);   //left
        floodFill(node, x++, y, target, replace);   //right
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
                        if(oldMap[rowNum][colNum] == WALL) {
                            wallCount++;
                        }
                    }
                }
                if(oldMap[i][j] == WALL && wallCount >= 4) {
                    newMap[i][j] = WALL;
                }
                else if(wallCount >= 5) {
                    newMap[i][j] = WALL;
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
                        if(oldMap[rowNum][colNum] == WALL) {
                            wallCount++;
                        }
                    }
                }
                if(wallCount <= 2){
                    newMap[i][j] = WALL;
                }
                wallCount = 0;
            }
        }
        return newMap;
    }

    public int[][] mapBorders(int[][] map) {
        for(int i = 0; i < tiledMapWidth; i++) {
            map[i][0] = WALL;
            map[i][tiledMapHeight - 1] = WALL;
        }
        for(int j = 0; j < tiledMapHeight; j++) {
            map[0][j] = WALL;
            map[tiledMapWidth - 1][j] = WALL;
        }
        return map;
    }

    /*
    Description: determines the optimal path from start to goal and returns it in
                 in the form of an array
    Function: aStarPathFinding
    Inputs: start : Node, goal : Node
    Outputs: Array<Node>
     */
    public Array<Node> aStarPathFinding(Node start, Node goal) {
        //initialize an open and closed data structure
        open = new PriorityQueue<Node>();
        closed = new ArrayList<Node>();

        //clear the closed list to ensure a new closed list
        closed.clear();
        //cost of going from start to start is 0
        start.setgScore(0);
        start.setfScore(heuristic(start, goal));

        //check if start node is already the goal node and if it is,
        //return the path
        if((start.getCurrentX() == goal.getCurrentX()) &&
                (start.getCurrentY() == goal.getCurrentY())) {
            return makePath(start);
        }

        //if not, store the start node into open
        open.add(start);

        //while there are still nodes unchecked in open, perform the following:
        while(!open.isEmpty()) {
            //sort the open list & in this sort, the compareTo() should move
            //the node with the least cost to the head **priority q auto does it

            //remove the first node with lowest cost in open & set it as the best next step
            Node nextBest = open.poll();

            //if nextBest is the goal then return with its path
            if ((nextBest.getCurrentX() == goal.getCurrentX()) &&
                    (nextBest.getCurrentY() == goal.getCurrentY())) {
                return makePath(nextBest);
            }

            //expand this next best step's neighbours into an array of nodes
            Array<Node> neighbours = expand(nextBest);

            for (int i = 0; i < neighbours.size; i++) {
                //if the node isn't in closed then set its parent to the
                //next best node and then add the node to the open list
                if(!closed.contains(neighbours.get(i))) {
                    int tempGScore = nextBest.getgScore() + 1;
                    neighbours.get(i).setfScore(neighbours.get(i).getgScore() +
                            heuristic(neighbours.get(i), goal));

                    if(!open.contains(neighbours.get(i))) {
                        open.add(neighbours.get(i));
                    }
                    else{
                        if(tempGScore >= neighbours.get(i).getgScore()) {
                            continue;
                        }
                    }
                    neighbours.get(i).setParent(nextBest);
                    neighbours.get(i).setgScore(tempGScore);

                    if ((neighbours.get(i).getCurrentX() == goal.getCurrentX()) &&
                            neighbours.get(i).getCurrentY() == goal.getCurrentY()) {
                        return makePath(neighbours.get(i));
                    }
                }
            }
            //mark nextBest as having been read
            closed.add(nextBest);
        }
        return null;
    }

    /*
    Description: expands an input node and stores its neighbours into a Node array
    Function: expand
    Inputs: node : Node
    Outputs: Array<Node>
     */
    public Array<Node> expand(Node node) {
        Array<Node> neighbours = new Array<Node>();

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }

                //if a neighbour isn't within the bounds of the world/isn't a floor,
                //don't initialize and store them into the neighbours array
                if(newMap[x + node.getCurrentX()][y + node.getCurrentY()] == FLOOR ||
                        newMap[x + node.getCurrentX()][y + node.getCurrentY()] == PLAYER) {
                    //create a new Node object and store it into neighbours
                    Node adjNode = new Node();
                    adjNode.setCurrentX(x + node.getCurrentX());
                    adjNode.setCurrentY(y + node.getCurrentY());
                    neighbours.add(adjNode);
                }
            }
        }
        return neighbours;
    }

    /*
    Description: calculates the cost required to reach the goal from a node
    Function: heuristic
    Inputs: node : Node, goal : Node
    Outputs: int
     */
    public int heuristic(Node node, Node goal) {
        int D = TEXTURESIZE;
        int D2 = 45;
        int dx = Math.abs(node.getCurrentX() - goal.getCurrentX());
        int dy = Math.abs(node.getCurrentY() - goal.getCurrentY());
        return D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
    }

    /*
    Description: creates a path from input start node to the goal node
    Function: makePath
    Inputs: node : Node
    Outputs: Array<Node>
     */
    public Array<Node> makePath(Node node) {
        Array<Node> path = new Array<Node>();
        while (node.getParent() != null) {
            node = node.getParent();
            path.add(node);
        }
        path.reverse();
        return path;
    }

    public void updateMapDiscovered() {
        Vector2 mapTileDiscovered;
        for(int i = (playerChar.getX() - 2); i <= (playerChar.getX() + 2); i++) {
            for(int j = (playerChar.getY() - 2); j <= (playerChar.getY() + 2); j++) {
                if((i >= 0 && i <= tiledMapWidth) &&
                        (j >= 0 && j <= tiledMapHeight)) {
                    mapTileDiscovered = new Vector2(i, j);
                    for (int k = 0; k < mapDiscovered.size(); k++) {
                        if (!mapDiscovered.get(k).equals(mapTileDiscovered)
                                && (k == (mapDiscovered.size() - 1))) {
                            mapDiscovered.add(mapTileDiscovered);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        camera.update();
        stage.getCamera().update();
        stage.getBatch().setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        if(mapPressed) {
            for(int i = 0; i < mapDiscovered.size(); i++) {
                if(newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == WALL) {
                    stage.getBatch().draw(happyFace, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }
                else if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == PLAYER) {
                    stage.getBatch().draw(sadFace, playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE,
                            TEXTURESIZE, TEXTURESIZE);
                }
                else if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == ENEMY) {
                    stage.getBatch().draw(neutralFace, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }
                else if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == EXIT) {
                    stage.getBatch().draw(uglySean2, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }
                else {
                    stage.getBatch().draw(badLogic, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y* TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }
            }
        }
        else {
            for (int i = (playerChar.getX() - 8); i < (playerChar.getX() + 9); i++) {
                for (int j = (playerChar.getY() - 5); j < (playerChar.getY() + 6); j++) {
                    if (i < 0 || i >= tiledMapWidth || j < 0 || j >= tiledMapHeight) {
                        continue;
                    }
                    if (newMap[i][j] == WALL) {
                        stage.getBatch().draw(happyFace, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    } else if (newMap[i][j] == PLAYER) {
                        stage.getBatch().draw(sadFace, playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    } else if (newMap[i][j] == ENEMY) {
                        stage.getBatch().draw(neutralFace, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    } else if (newMap[i][j] == EXIT) {
                        stage.getBatch().draw(uglySean2, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    } else {
                        stage.getBatch().draw(badLogic, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    }
                }
            }
        }
        //checkOptions();
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    public void checkOptions() {
        if(options == true){
            stage.getActors().first().setVisible(true);
        }
    }

    /*
    Description: Generates a pseudo-random integer between two input integers
    Function: randInt
    Inputs: min : int, max : int
    Outputs: int
     */
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
        this.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        userTouch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        dragOld = userTouch;
        worldTouch = camera.unproject(new Vector3(userTouch, 0));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(mapPressed) {
            return false;
        }
        worldTouch = new Vector3((int) worldTouch.x / TEXTURESIZE,
                (int) worldTouch.y / TEXTURESIZE, 0);

        //if within the bounds of the world
        if((worldTouch.x > 0 && worldTouch.x < tiledMapWidth) &&
                (worldTouch.y > 0 && worldTouch.y < tiledMapHeight)) {
            //if within one tile from the player character
            if((newMap[(int) worldTouch.x][(int) worldTouch.y] != WALL) &&
                    ((Math.abs(worldTouch.x - playerChar.x) == 1 ||
                    Math.abs(worldTouch.x - playerChar.x) == 0) &&
                            (Math.abs(worldTouch.y - playerChar.y) == 1 ||
                                    (Math.abs(worldTouch.y - playerChar.y) == 0)))) {
                if (newMap[(int) worldTouch.x][(int) worldTouch.y] == ENEMY) {
                    for(int i = 0; i < enemies.size; i++) {
                        if(worldTouch.x == enemies.get(i).getX() &&
                                worldTouch.y == enemies.get(i).getY()) {
                            damage = playerChar.ATK - enemies.get(i).DEF;
                            result = enemies.get(i).takeDamage(damage);
                            if(result == FLOOR) {
                                newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                                enemies.removeIndex(i);
                            }
                        }
                    }
                }
                else if (newMap[(int) worldTouch.x][(int) worldTouch.y] == EXIT) {
                    System.out.println("YOU WIN!");
                    System.out.println("\n" + "Touch: " + worldTouch.x + " " + worldTouch.y);
                    return true;
                }
                else if (newMap[(int) worldTouch.x][(int) worldTouch.y] == FLOOR) {
                    newMap[playerChar.x][playerChar.y] = FLOOR;
                    playerChar.x = (int) worldTouch.x;
                    playerChar.y = (int) worldTouch.y;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                    updateMapDiscovered();
                }
                //initialize a goal node for pathfinding (player character)
                goalNode = new Node();
                goalNode.setCurrentX(playerChar.getX());
                goalNode.setCurrentY(playerChar.getY());

                //enemies move towards the player one step at a time too
                for(int i = 0; i < enemies.size; i++) {
                    if (enemies.size <= 0) {
                        break;
                    }

                    //initialize a start node for pathfinding (enemies)
                    startNode = new Node();
                    startNode.setCurrentX(enemies.get(i).getX());
                    startNode.setCurrentY(enemies.get(i).getY());

                    //create the path from the enemy to the player
                    //and if the enemy is beside the player or the path is null
                    //then move onto the next enemy in the array
                    path = aStarPathFinding(startNode, goalNode);
                    if (path == null || path.size == 0) {
                        continue;
                    }
                    if (path.size == 1) {
                        damage = enemies.get(i).ATK - playerChar.DEF;
                        playerHPBar.setValue(playerChar.HP - damage);
                        result = playerChar.takeDamage(damage);
                        if (result == FLOOR) {
                            newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                            playerChar.die();
                        }
                        continue;
                    }
                    //move the enemy units one tile closer to the player
                    newMap[enemies.get(i).x][enemies.get(i).y] = FLOOR;
                    enemies.get(i).x = path.get(1).getCurrentX();
                    enemies.get(i).y = path.get(1).getCurrentY();
                    newMap[enemies.get(i).x][enemies.get(i).y] = ENEMY;
                    path = null;
                }
            }
        }
        System.out.println("\n" + "Touch: " + worldTouch.x + " " + worldTouch.y);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!mapPressed) {
            return false;
        }
        else{
            dragNew = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            if(!dragNew.equals(dragOld)) {
                camera.translate(dragOld.x - dragNew.x, dragNew.y - dragOld.y);
                dragOld = dragNew;
            }
        }
        return true;
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
