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


/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    //Stage stage;
    AssetManager assetManager;
    Texture happyFace, neutralFace, sadFace, badLogic, uglySean;
    Random rand;

    PlayerCharacter playerChar;
    Array<standardEnemy> enemies;

    int[][] ogMap, newMap;
    ArrayList<Node> closed;
    PriorityQueue<Node> open;
    Node startNode, goalNode;
    Array<Node> path;

    float screenWidth, screenHeight, cellWidth, cellHeight;
    int tiledMapWidth, tiledMapHeight;
    int FLOOR, WALL, PLAYER, ENEMY;
    int TEXTURESIZE;
    int enemyNum, wallCount;

    int ranPosX, ranPosY, startPosX, startPosY, endPosX, endPosY;
    int damage;
    int result;

    Vector2 userTouch;
    Vector3 worldTouch;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        //cellWidth = screenWidth / 5;
        //cellHeight = screenHeight / 4;

        TEXTURESIZE = 32;
        FLOOR = 0;
        WALL = 1;
        PLAYER = 2;
        ENEMY = 3;

        rand = new Random();
        tiledMapWidth = randInt(50, 100);
        tiledMapHeight = randInt(50, 100);
        enemyNum = randInt(5, 10);

        //initialize the player character's stats
        playerChar = new PlayerCharacter(randInt(10, 15), randInt(1, 2),
                0, randInt(0, 10));
        //initialize a random-sized array of standard enemies and their stats
        enemies = new Array<standardEnemy>();
        for(int i = 0; i < randInt(5, 10); i++) {
            enemies.add(new standardEnemy(randInt(10, 15), randInt(1, 2),
                    0, randInt(0, 10)));
        }

        //set up the Game, SpriteBatch, and OrthographicCamera
        this.game = gam;
        camera = new OrthographicCamera(14 * TEXTURESIZE, 10 * TEXTURESIZE);
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
        while(newMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        playerChar.x = ranPosX;
        playerChar.y = ranPosY;
        newMap[playerChar.x][playerChar.y] = PLAYER;
        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
        //camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0);
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

        //InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(this);
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
            //System.out.println("WE DID IT");
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
                    (nextBest.getCurrentX() == nextBest.getCurrentY())) {
                //System.out.println("WE DID IT1");
                return makePath(nextBest);
            }

            //System.out.println("\n" + "ENEMY NODE: " + nextBest.getCurrentX() + " " + nextBest.getCurrentY());
            //System.out.println(nextBest.getfScore());

            //expand this next best step's neighbours into an array of nodes
            Array<Node> neighbours = expand(nextBest);
            //System.out.println("NUMBER OF NEIGHBOURS: " + neighbours.size);

            for (int i = 0; i < neighbours.size; i++) {
                //System.out.println(neighbours.get(i).getCurrentX() + " " + neighbours.get(i).getCurrentY());

                //if the node isn't in closed then set its parent to the
                //next best node and then add the node to the open list
                if(!closed.contains(neighbours.get(i))) {
                    int tempGScore = nextBest.getgScore() + 1;
                    neighbours.get(i).setfScore(neighbours.get(i).getgScore() +
                            heuristic(neighbours.get(i), goal));
                    //System.out.print(neighbours.get(i).getfScore() + " ");

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
                            (neighbours.get(i).getCurrentY() == goal.getCurrentY())) {
                        //System.out.println("\n"+ "WE DID IT2");
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
        while (node.parent != null) {
            node = node.parent;
            path.add(node);
        }
        path.reverse();
        return path;
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        for(int i = (playerChar.getX() - 8); i < (playerChar.getX() + 9); i++) {
            for (int j = (playerChar.getY() - 5); j < (playerChar.getY() + 6); j++) {
                if(i < 0 || i >= tiledMapWidth || j < 0 || j >= tiledMapHeight) {continue;}
                if (newMap[i][j] == WALL) {
                    game.batch.draw(happyFace, i * TEXTURESIZE, j * TEXTURESIZE,
                            TEXTURESIZE, TEXTURESIZE);
                }
                else if (newMap[i][j] == PLAYER) {
                    game.batch.draw(sadFace, playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE,
                            TEXTURESIZE, TEXTURESIZE);
                }
                else if (newMap[i][j] == ENEMY) {
                    game.batch.draw(neutralFace, i * TEXTURESIZE, j * TEXTURESIZE,
                            TEXTURESIZE, TEXTURESIZE);
                }
                else {
                    game.batch.draw(badLogic, i * TEXTURESIZE, j * TEXTURESIZE,
                            TEXTURESIZE, TEXTURESIZE);
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
        goalNode = new Node();
        startNode = new Node();
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
                        if(worldTouch.x == enemies.get(i).x &&
                                worldTouch.y == enemies.get(i).y) {
                            damage = playerChar.ATK - enemies.get(i).DEF;
                            result = enemies.get(i).takeDamage(damage);
                            if(result == FLOOR) {
                                newMap[enemies.get(i).x][enemies.get(i).y] = FLOOR;
                                enemies.removeIndex(i);
                            }
                        }
                    }
                }
                else if (newMap[(int) worldTouch.x][(int) worldTouch.y] == FLOOR) {
                    newMap[playerChar.x][playerChar.y] = FLOOR;
                    playerChar.x = (int) worldTouch.x;
                    playerChar.y = (int) worldTouch.y;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                //initialize a goal node for pathfinding (player character)
                goalNode.setCurrentX(playerChar.getX());
                goalNode.setCurrentY(playerChar.getY());

                //enemies move towards the player one step at a time too
                for(int i = 0; i < enemies.size; i++) {
                    if (enemies.size <= 0) {
                        break;
                    }

                    //initialize a start node for pathfinding (enemies)
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
