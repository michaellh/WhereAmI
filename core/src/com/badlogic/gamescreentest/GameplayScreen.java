package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import javafx.collections.transformation.SortedList;

/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    //Stage stage;
    AssetManager assetManager;
    Random rand;
    Texture happyFace;
    Texture neutralFace;
    Texture sadFace;
    Texture badLogic;
    Texture uglySean;

    PlayerCharacter playerChar;
    Array<standardEnemy> enemies;

    int[][] ogMap;
    int[][] newMap;
    int[][] path;
    Array<Node> enemyNodes;
    ArrayList<Node> closed;
    PriorityQueue<Node> open;

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
    int damage;

    Vector2 userTouch;
    Vector3 worldTouch;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 5;
        cellHeight = screenHeight / 4;
        textureSize = 32;
        rand = new Random();
        tiledMapWidth = randInt(25, 40);
        tiledMapHeight = randInt(25, 40);
        int enemyNum = randInt(5, 10);
        enemyNodes = new Array<Node>();

        playerChar = new PlayerCharacter(randInt(10, 15), randInt(1, 2),
                0, randInt(0, 10));
        enemies = new Array<standardEnemy>();
        for(int i = 0; i < enemyNum; i++) {
            enemies.add(new standardEnemy(randInt(10, 15), randInt(1, 2),
                    0, randInt(0, 10)));
        }

        //set up the Game, SpriteBatch, and OrthographicCamera
        this.game = gam;
        camera = new OrthographicCamera(15 * textureSize, 10 * textureSize);
        //camera = new OrthographicCamera(screenWidth, screenHeight);

        //get, after loading, the assets
        assetManager = game.assetManager;
        uglySean = assetManager.get("ugly face sean.jpg", Texture.class);
        happyFace = assetManager.get("happyface.jpg", Texture.class);
        neutralFace = assetManager.get("neutralface.jpg", Texture.class);
        sadFace = assetManager.get("sadface.jpg", Texture.class);
        badLogic = assetManager.get("badlogic.jpg", Texture.class);

        //create the game world
        newMap = createWorld();

        //initialize player character position
        while(newMap[ranPosX][ranPosY] != 0) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        playerChar.x = ranPosX;
        playerChar.y = ranPosY;
        newMap[playerChar.x][playerChar.y] = 2;
        camera.position.set(playerChar.x * textureSize, playerChar.y * textureSize, 0);
        //camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0);
        camera.update();

        for(int i = 0; i < enemies.size; i++) {
            Node enemy = new Node();
            while(newMap[ranPosX][ranPosY] != 0) {
                ranPosX = randInt(1, (tiledMapWidth - 1));
                ranPosY = randInt(1, (tiledMapHeight - 1));
            }
            enemies.get(i).x = enemy.x = ranPosX;
            enemies.get(i).y = enemy.y = ranPosY;
            newMap[enemies.get(i).x][enemies.get(i).y] = 3;
            enemyNodes.add(enemy);
            //System.out.println(standardEnemy.x + " " + standardEnemy.y);
        }

        //InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(this);
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

        ranPosX = randInt(1, (tiledMapWidth - 1));
        ranPosY = randInt(1, (tiledMapHeight - 1));
        while(ogMap[ranPosX][ranPosY] != 0) {
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

    public int[][] mapBorders(int[][] map) {
        for(int i = 0; i < tiledMapWidth; i++) {
            map[i][0] = 1;
            map[i][tiledMapHeight - 1] = 1;
        }
        for(int j = 0; j < tiledMapHeight; j++) {
            map[0][j] = 1;
            map[tiledMapWidth - 1][j] = 1;
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
        Node bestPath;
        Node[] neighbours;
        open = new PriorityQueue<Node>();
        closed = new ArrayList<Node>();

        if (goal == start) {
            return makePath(start);
        }

        open.clear();
        open.add(start);
        closed.clear();
        while(!open.isEmpty()) {
            bestPath = open.poll();
            neighbours = expand(bestPath);
            for(int i = 0; i < neighbours.length; i++) {
                if((newMap[neighbours[i].x][neighbours[i].y] != 0)||
                        (neighbours[i].x < 1) ||
                        (neighbours[i].x > tiledMapWidth - 1) ||
                        (neighbours[i].y < 1) ||
                        (neighbours[i].y > tiledMapHeight - 1)){
                    continue;
                }
                neighbours[i].cost = (bestPath.cost + 1) + heuristic(neighbours[i], goal);
                if(neighbours[i].x == goal.x && neighbours[i].y == goal.y) {
                    return makePath(neighbours[i]);
                }
                if((closed.contains(neighbours[i]) == false) ||
                        (open.contains(neighbours[i]) == false)) {
                    /*
                    if(!open.isEmpty()) {
                        open.add(neighbours[i].compareTo(neighbours[i-1]));
                    }*/
                }
                else{
                    System.out.println("GOODBYE");
                }
                /*
                if(closed.contains(neighbours[i]) == neighbours[i].compareTo(0)) {
                    System.out.println("HI");
                }*/
                /*
                if(open.contains(neighbours[i]) == false) {
                    if(closed.contains(neighbours[i])) {

                    }
                    open.add(neighbours[i]);
                }*/
            }
            closed.add(bestPath);
        }

        return null;
    }

    /*
    Description: expands an input node and stores its neighbours into a Node array
    Function: expand
    Inputs: node : Node
    Outputs: Node[]
     */
    public Node[] expand(Node node) {
        int pos = 0;
        Node[] neighbours = new Node[8];

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }

                //create a new Node object and store it into neighbours
                Node adjNode = new Node();
                adjNode.x = x + node.x;
                adjNode.y = y + node.y;
                neighbours[pos] = adjNode;
                pos = pos + 1;
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
        int D = textureSize;
        int D2 = 45;
        int dx = Math.abs(node.x - goal.x);
        int dy = Math.abs(node.y - goal.y);
        return D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
    }

    /*
    Description: creates a path from input start node to the goal node
    Function: makePath
    Inputs: node : Node
    Outputs: Node[]
     */
    public Array<Node> makePath(Node node) {
        Array<Node> path = new Array<Node>();
        while (node.parent != null) {
            node = node.parent;
            path.add(node);
        }
        return path;
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
                else if (newMap[i][j] == 2) {
                    game.batch.draw(sadFace, playerChar.x * textureSize, playerChar.y * textureSize,
                            textureSize, textureSize);
                }
                else if (newMap[i][j] == 3) {
                    game.batch.draw(neutralFace, i * textureSize, j * textureSize,
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
        //stage.dispose();
        this.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        userTouch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        worldTouch = camera.unproject(new Vector3(userTouch, 0));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        worldTouch = new Vector3((int) worldTouch.x / textureSize,
                (int) worldTouch.y / textureSize, 0);
        //if within the bounds of the world
        if((worldTouch.x > 0 && worldTouch.x < tiledMapWidth) &&
                (worldTouch.y > 0 && worldTouch.y < tiledMapHeight)) {
            //if within one tile from the player character
            if((newMap[(int) worldTouch.x][(int) worldTouch.y] != 1) &&
                    ((Math.abs(worldTouch.x - playerChar.x) == 1 ||
                    Math.abs(worldTouch.x - playerChar.x) == 0) &&
                            (Math.abs(worldTouch.y - playerChar.y) == 1 ||
                                    (Math.abs(worldTouch.y - playerChar.y) == 0)))) {
                if (newMap[(int) worldTouch.x][(int) worldTouch.y] == 3) {
                    for(int i = 0; i < enemies.size; i++) {
                        if(worldTouch.x == enemies.get(i).x &&
                                worldTouch.y == enemies.get(i).y) {
                            damage = playerChar.ATK - enemies.get(i).DEF;
                            //System.out.println(damage);
                            if(enemies.get(i).HP > 0) {
                                enemies.get(i).HP = enemies.get(i).HP - damage;
                            }
                            if(enemies.get(i).HP <= 0) {
                                enemies.get(i).die();
                                newMap[enemies.get(i).x][enemies.get(i).y] = 0;
                            }
                            System.out.println(enemies.get(i).HP);
                            //standardEnemy.target(playerChar);
                        }
                    }
                }
                else if (newMap[(int) worldTouch.x][(int) worldTouch.y] == 0 ) {
                    System.out.println(enemies.get(0).x + " " + enemies.get(0).y);
                    newMap[playerChar.x][playerChar.y] = 0;
                    playerChar.x = (int) worldTouch.x;
                    playerChar.y = (int) worldTouch.y;
                    newMap[playerChar.x][playerChar.y] = 2;
                    camera.position.set(playerChar.x * textureSize, playerChar.y * textureSize, 0);
                    Node start = new Node();
                    start.x = playerChar.x;
                    start.y = playerChar.y;
                    Array<Node> enemyPath = aStarPathFinding(enemyNodes.first(), start);
//                    System.out.println(enemies.get(0).x + " " + enemies.get(0).y + " " + enemyPath.size);
                    //enemies.get(0).behaviour(enemyPath);
                    //System.out.println(enemies.get(0).x + " " + enemies.get(0).y + " " + enemyPath.size);
                }
            }
        }
        //System.out.println(worldTouch.x + " " + worldTouch.y);
        return true;
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
