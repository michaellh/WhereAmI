package com.badlogic.gamescreentest;

import com.badlogic.gamescreentest.headsUP.Options.MainMenuOption;
import com.badlogic.gamescreentest.headsUP.Options.OptionsBackground;
import com.badlogic.gamescreentest.headsUP.Options.QuitGameOption;
import com.badlogic.gamescreentest.headsUP.Options.ResumeGameOption;
import com.badlogic.gamescreentest.headsUP.Options.SaveGameOption;
import com.badlogic.gamescreentest.headsUP.Map.MapExit;
import com.badlogic.gamescreentest.headsUP.Map.MapSeePlayer;
import com.badlogic.gamescreentest.headsUP.Map.MapHUD;
import com.badlogic.gamescreentest.headsUP.Options.gameOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/*
Decisions Kevin MacLeod (incompetech.com)
Licensed under Creative Commons: By Attribution 3.0 License
http://creativecommons.org/licenses/by/3.0/
*/

/*
Dying Sound by Mike Koenig (http://soundbible.com/810-Dying.html)
Licensed under Creative Commons: By Attribution 3.0 License
http://creativecommons.org/licenses/by/3.0/
 */

/*
1 Person Cheering Sound by Jett Rifkin (http://soundbible.com/2103-1-Person-Cheering.html)
Licensed under Creative Commons: By Attribution 3.0 License
http://creativecommons.org/licenses/by/3.0/
 */

/**
 * Created by admin on 6/8/2016.
 */
public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;
    AssetManager assetManager;
    Texture hpBarTex, hpBarPivotTex,
            mapExitTex, mapTex, optionsTex, mapLocateTex,
            optionsBackTex, mainMenuTex, quitGameTex, resumeGameTex,
            playerIdleTex, playerAtkRightTex, playerAtkLeftTex,
            enemyIdleTex, enemyAtkTex, portalTex, wallTex, floorTex;
    Random rand;

    gameOptions optButton;
    OptionsBackground optionsBackground;
    ResumeGameOption resumeGameOption;
    SaveGameOption saveGameOption;
    MainMenuOption mainMenuOption;
    QuitGameOption quitGameOption;

    MapHUD mapButton;
    MapExit mapExit;
    MapSeePlayer mapSeePlayer;

    com.badlogic.gamescreentest.headsUP.PlayerHealthBar.HPBarStyle hpBarStyle;
    com.badlogic.gamescreentest.headsUP.PlayerHealthBar.PlayerHPBar playerHPBar;
    PlayerCharacter playerChar;

    Animation playerIdleAnimation;
    TextureRegion[] playerIdleFrames;
    TextureRegion playerIdleCurrentFrame;
    float playerIdleStateTime;

    Label saveGameText;
    Label.LabelStyle saveGameTextStyle;
    BitmapFont saveGameFont;

    Array<standardEnemy> enemies;
    int[][] newMap;
    ArrayList<Node> closed;
    PriorityQueue<Node> open;
    Node startNode, goalNode;
    Array<Node> path;

    float screenWidth, screenHeight, cellWidth, cellHeight;
    int tiledMapWidth, tiledMapHeight;
    int FLOOR, WALL, PLAYER, ENEMY, EXIT, TEXTURESIZE;
    int wallCount, floorLevel;
    int enemyX;

    boolean options, mapPressed, gameOver, attackedEnemy, attackedPlayer;
    int ranPosX, ranPosY, startPosX, startPosY, endPosX, endPosY;

    ArrayList<Vector2> mapDiscovered;
    Vector2 userTouch, dragOld, dragNew;
    Vector3 worldTouch;

    Music worldMusic;

    public GameplayScreen(final GameScreen gam) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        cellWidth = screenWidth / 10;
        cellHeight = screenHeight / 8;

        gameOver = false;
        options = false;
        mapPressed = false;
        attackedEnemy = false;
        attackedPlayer = false;

        TEXTURESIZE = 32;
        FLOOR = 0;
        WALL = 1;
        PLAYER = 2;
        ENEMY = 3;
        EXIT = 4;

        rand = new Random();

        //set up the Game, SpriteBatch, and OrthographicCamera
        this.game = gam;
        camera = new OrthographicCamera(14 * TEXTURESIZE, 10 * TEXTURESIZE);
        stage = new Stage(new ScreenViewport(), game.batch);

        //get, after loading, the assets
        assetManager = game.assetManager;
        worldMusic = assetManager.get("Decisions.mp3", Music.class);
        worldMusic.setLooping(true);
        playerIdleTex = assetManager.get("playerCharacter.png", Texture.class);
        playerAtkRightTex = assetManager.get("playerAttackRight.png", Texture.class);
        playerAtkLeftTex = assetManager.get("playerAttackLeft.png", Texture.class);
        enemyIdleTex = assetManager.get("enemyIdle_001.png", Texture.class);
        enemyAtkTex = assetManager.get("enemyAttack_001.png", Texture.class);
        portalTex = assetManager.get("portal_001.png", Texture.class);
        wallTex = assetManager.get("wall2_001.png", Texture.class);
        floorTex = assetManager.get("floor2_001.png", Texture.class);
        mapTex = assetManager.get("map.jpg", Texture.class);
        mapLocateTex = assetManager.get("mapLocate.jpg", Texture.class);
        mapExitTex = assetManager.get("mapExit.jpg", Texture.class);
        optionsTex = assetManager.get("OptionsButton.jpg", Texture.class);
        optionsBackTex = assetManager.get("OptionsBackground.jpg", Texture.class);
        mainMenuTex = assetManager.get("MainMenu.jpg", Texture.class);
        //saveGameTex = assetManager.get("SaveGame.jpg", Texture.class);
        quitGameTex = assetManager.get("QuitGame.jpg", Texture.class);
        resumeGameTex = assetManager.get("ResumeGame.jpg", Texture.class);
        hpBarTex = assetManager.get("healthBar.jpg", Texture.class);
        hpBarPivotTex = assetManager.get("healthBarPivot.jpg", Texture.class);

        if (this.game.getNewGame()) {
            SaveFile saveFile = new SaveFile();
            saveFile.deleteSaveData();
            recreateWorld();
            playerChar.setHpBeforeSave(playerChar.HP);
            saveFile = new SaveFile(newMap, mapDiscovered, enemies, playerChar,
                    tiledMapWidth, tiledMapHeight, floorLevel);
            saveFile.saveSaveData();
        }
        else {
            SaveFile saveFile = new SaveFile();
            playerChar = saveFile.readPlayer();
            enemies = saveFile.readEnemies();
            newMap = saveFile.readSaveMap();
            mapDiscovered = saveFile.readMapDiscovered();
            floorLevel = saveFile.readFloorLevel();
            Vector2 mapWidthHeight = saveFile.readMapWidthHeight();
            tiledMapWidth = (int) mapWidthHeight.x;
            tiledMapHeight = (int) mapWidthHeight.y;
            camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
        }

        //initialize the HUD buttons and add them to the stage as actors
        Sprite spriteBack = new Sprite(hpBarTex);
        spriteBack.setSize(cellWidth * 3, (cellHeight / 2));
        final SpriteDrawable spriteDrawableBack = new SpriteDrawable(spriteBack);
        Sprite spriteKnob = new Sprite(hpBarPivotTex);
        spriteKnob.setSize(0, spriteBack.getHeight());
        SpriteDrawable spriteDrawableKnob = new SpriteDrawable(spriteKnob);
        hpBarStyle = new com.badlogic.gamescreentest.headsUP.PlayerHealthBar.HPBarStyle(spriteDrawableBack, spriteDrawableKnob);
        hpBarStyle.knobBefore = hpBarStyle.knob;
        playerHPBar = new com.badlogic.gamescreentest.headsUP.PlayerHealthBar.PlayerHPBar(0, playerChar.hpBeforeSave, 1, false, hpBarStyle);
        playerHPBar.setSize(spriteBack.getWidth(), spriteBack.getHeight());
        playerHPBar.setPosition(0, 7 * cellHeight + spriteBack.getHeight());
        playerHPBar.setValue(playerChar.HP);
        stage.addActor(playerHPBar);

        // Initialize the player character animations
        TextureRegion[][] tmp = TextureRegion.split(playerIdleTex, (playerIdleTex.getWidth()/4 + 5), (playerIdleTex.getHeight()/2 + 5));
        playerIdleFrames = new TextureRegion[2];
        int index = 0;
            for (int j = 0; j < 2; j++) {
                playerIdleFrames[index++] = tmp[0][j];
            }

        playerIdleAnimation = new Animation(1f, playerIdleFrames);
        playerIdleStateTime = 0f;

        // Add the map button to the stage
        mapButton = new MapHUD(mapTex, cellWidth, cellHeight);
        mapButton.setBounds(cellWidth * 8, cellHeight * 7, cellWidth, cellHeight);
        mapButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("map touched down");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                System.out.println("map touched up");
                // Temporarily disable the HP bar, map mode button, options button, and menu button
                for (int i = 0; i < stage.getActors().size; i++) {
                    stage.getActors().get(i).setVisible(false);
                }
                // Make the camera view the entire world instead of the window in game mode
                camera.setToOrtho(false, screenWidth, screenHeight);
                camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                mapPressed = true;
                // Adds a map exit button in map mode to the stage
                mapExit = new MapExit(mapExitTex, cellWidth, cellHeight);
                mapExit.setBounds(cellWidth * 9, cellHeight * 7, cellWidth, cellHeight);
                stage.addActor(mapExit);
                mapExit.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        mapPressed = false;
                        // Sets the camera back to game mode view
                        camera.setToOrtho(false, 14 * TEXTURESIZE, 10 * TEXTURESIZE);
                        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                        // Disable the find player button and the map exit button
                        int mapExitLocation = stage.getActors().indexOf(mapExit, true);
                        stage.getActors().get(mapExitLocation).addAction(Actions.removeActor());
                        int mapFindPlayer = stage.getActors().indexOf(mapSeePlayer, true);
                        stage.getActors().get(mapFindPlayer).addAction(Actions.removeActor());

                        // Enables the HP bar, map mode button, options button, and menu button
                        int mapButtonIndex = stage.getActors().indexOf(mapButton, true);
                        stage.getActors().get(mapButtonIndex).setVisible(true);
                        int optButtonIndex = stage.getActors().indexOf(optButton, true);
                        stage.getActors().get(optButtonIndex).setVisible(true);
                        int hpBarIndex = stage.getActors().indexOf(playerHPBar, true);
                        stage.getActors().get(hpBarIndex).setVisible(true);
                        //int menuButtonIndex = stage.getActors().indexOf(menuButton, true);
                        //stage.getActors().get(menuButtonIndex).setVisible(true);
                    }
                });
                // Adds a see player button in map mode
                mapSeePlayer = new MapSeePlayer(mapLocateTex, cellWidth, cellHeight);
                mapSeePlayer.setBounds(cellWidth * 8, cellHeight * 7, cellWidth, cellHeight);
                stage.addActor(mapSeePlayer);
                mapSeePlayer.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                    }
                });
            }
        });
        stage.addActor(mapButton);

        optButton = new gameOptions(optionsTex, cellWidth, cellHeight);
        optButton.setBounds(cellWidth * 9, cellHeight * 7, cellWidth, cellHeight);
        optButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("options touched down");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                System.out.println("options touched up");
                options = true;
                for (int i = 0; i < stage.getActors().size; i++) {
                    stage.getActors().get(i).setVisible(false);
                }

                optionsBackground = new OptionsBackground(optionsBackTex, cellWidth, cellHeight);
                stage.addActor(optionsBackground);

                resumeGameOption = new ResumeGameOption(resumeGameTex, cellWidth, cellHeight);
                resumeGameOption.setBounds(cellWidth * 1.5f, cellHeight * 5, cellWidth * 7, cellHeight);
                resumeGameOption.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        int optBackIndex = stage.getActors().indexOf(optionsBackground, true);
                        stage.getActors().get(optBackIndex).addAction(Actions.removeActor());
                        int optResumeIndex = stage.getActors().indexOf(resumeGameOption, true);
                        stage.getActors().get(optResumeIndex).addAction(Actions.removeActor());
                        int optSaveIndex = stage.getActors().indexOf(saveGameOption, true);
                        stage.getActors().get(optSaveIndex).addAction(Actions.removeActor());
                        int optMenuIndex = stage.getActors().indexOf(mainMenuOption, true);
                        stage.getActors().get(optMenuIndex).addAction(Actions.removeActor());
                        int optQuitIndex = stage.getActors().indexOf(quitGameOption, true);
                        stage.getActors().get(optQuitIndex).addAction(Actions.removeActor());

                        for (int i = 0; i < stage.getActors().size; i++) {
                            stage.getActors().get(i).setVisible(true);
                        }

                        options = false;
                    }
                });
                stage.addActor(resumeGameOption);

                /*
                saveGameOption = new SaveGameOption(saveGameTex, cellWidth, cellHeight);
                saveGameOption.setBounds(cellWidth * 1.5f, cellHeight * 4, cellWidth * 7, cellHeight);
                saveGameOption.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        SaveFile saveData = new SaveFile(newMap, mapDiscovered, enemies, playerChar,
                                tiledMapWidth, tiledMapHeight, floorLevel);
                        saveData.saveSaveData();
                        saveGameFont = new BitmapFont();
                        saveGameFont.getData().setScale(15, 20);
                        saveGameTextStyle = new Label.LabelStyle(saveGameFont, Color.WHITE);
                        saveGameText = new Label("Game saved!", saveGameTextStyle);
                        saveGameText.setPosition(cellWidth * 1.6f, cellHeight * 3);
                        saveGameText.addAction(Actions.fadeOut(3));
                        saveGameText.addAction(Actions.after(Actions.removeActor()));
                        stage.addActor(saveGameText);
                    }
                });
                stage.addActor(saveGameOption);
                */

                mainMenuOption = new MainMenuOption(mainMenuTex, cellWidth, cellHeight);
                mainMenuOption.setBounds(cellWidth * 1.5f, cellHeight * 3, cellWidth * 7, cellHeight);
                mainMenuOption.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        for (int actors = 0; actors < stage.getActors().size; actors++) {
                            stage.getActors().get(actors).addAction(Actions.removeActor());
                        }
                        game.setScreen(new MainMenuScreen(game));
                    }
                });
                stage.addActor(mainMenuOption);

                quitGameOption = new QuitGameOption(quitGameTex, cellWidth, cellHeight);
                quitGameOption.setBounds(cellWidth * 1.5f, cellHeight * 2, cellWidth * 7, cellHeight);
                quitGameOption.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        SaveFile saveFile = new SaveFile(newMap, mapDiscovered, enemies, playerChar,
                                tiledMapWidth, tiledMapHeight, floorLevel);
                        saveFile.saveSaveData();
                        dispose();
                        Gdx.app.exit();
                    }
                });
                stage.addActor(quitGameOption);
            }
        });
        stage.addActor(optButton);


        //give priority touch to the stage actors
        InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(im);
    }

    public int[][] createWorld() {
        newMap = new int[tiledMapWidth][tiledMapHeight];

        // populate the map with walls
        for (int i = tiledMapHeight - 1; i > 0; i--) {
            for (int j = 0; j < tiledMapWidth; j++) {
                if (randInt(0, 100) < 40) {
                    newMap[j][i] = WALL;
                    //System.out.print("*");
                }
                else {
                    //System.out.print("!");
                }
            }
            //System.out.println();
        }
        //System.out.println("----------------------------------------------");

        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter1(newMap);
        newMap = mapIter1(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter2(newMap);
        newMap = mapIter1(newMap);

        int mapWallCount = 0;
        for (int i = tiledMapHeight - 1; i > 0; i--) {
            for (int j = 0; j < tiledMapWidth; j++) {
                if (newMap[j][i] == WALL) {
                    //System.out.print("*");
                    mapWallCount = mapWallCount + 1;
                }
                else if(newMap[j][i] == FLOOR) {
                    //System.out.print("!");
                }
            }
            //System.out.println();
        }
        //System.out.println("----------------------------------------------");

        int[][] copyNewMap = deepCopyArray(newMap);
        ranPosX = randInt(1, (tiledMapWidth - 1));
        ranPosY = randInt(1, (tiledMapHeight - 1));
        while (copyNewMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        //System.out.println("Random pt: " + ranPosX + ", " + ranPosY);
        int floodMapCount = 0;
        floodFill(copyNewMap, ranPosX, ranPosY, 0, 1);

        for (int i = tiledMapHeight - 1; i > 0; i--) {
            for (int j = 0; j < tiledMapWidth; j++) {
                if(copyNewMap[j][i] == WALL) {
                    floodMapCount = floodMapCount + 1;
                    //System.out.print("*");
                }
                else if(copyNewMap[j][i] == FLOOR) {
                    newMap[j][i] = WALL;
                    //System.out.print("!");
                }
            }
            //System.out.println();
        }

        float floorCovered = ((floodMapCount - mapWallCount)/ (float)(tiledMapWidth * tiledMapHeight));
        if(floorCovered > 0.45) {
            return newMap;
        }
        return null;
    }

    public void floodFill(int[][] node, int x, int y, int target, int replace) {
        // if the tile read is outside of tilemapwidth and tilemapheight then return
        if (x <= 0 || x >= tiledMapWidth) {
            return;
        }
        if (y <= 0 || y >= tiledMapHeight) {
            return;
        }
        // if the tile read isn't a floor tile then return
        if (node[x][y] != target) {
            return;
        }
        // if the tile read is a floor and has already been checked then return
        if (target == replace) {
            return;
        }
        // otherwise replace the floor tile at that xy position as read
        node[x][y] = replace;
        // now check the surrounding tiles to see if they have been read or not
        floodFill(node, x - 1, y - 1, target, replace); //bot left
        floodFill(node, x - 1, y, target, replace);   //left
        floodFill(node, x - 1, y + 1, target, replace); //top left
        floodFill(node, x, y - 1, target, replace);   //bot
        floodFill(node, x, y + 1, target, replace);   //top
        floodFill(node, x + 1, y - 1, target, replace); //bot right
        floodFill(node, x + 1, y, target, replace);   //right
        floodFill(node, x + 1, y + 1, target, replace); //top right
    }

    public int[][] deepCopyArray(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    public int[][] mapIter1(int[][] oldMap) {
        wallCount = 0;
        int[][] newMap = new int[tiledMapWidth][tiledMapHeight];

        for (int i = 0; i < tiledMapWidth; i++) {
            for (int j = 0; j < tiledMapHeight; j++) {
                startPosX = (i - 1 < 0) ? i : i - 1;
                startPosY = (j - 1 < 0) ? j : j - 1;
                endPosX = (i + 1 > (tiledMapWidth - 1)) ? i : i + 1;
                endPosY = (j + 1 > (tiledMapHeight - 1)) ? j : j + 1;

                for (int rowNum = startPosX; rowNum <= endPosX; rowNum++) {
                    for (int colNum = startPosY; colNum <= endPosY; colNum++) {
                        if (oldMap[rowNum][colNum] == WALL) {
                            wallCount++;
                        }
                    }
                }
                if (oldMap[i][j] == WALL && wallCount >= 4) {
                    newMap[i][j] = WALL;
                } else if (wallCount >= 5) {
                    newMap[i][j] = WALL;
                }
                wallCount = 0;
            }
        }
        return newMap;
    }

    public int[][] mapIter2(int[][] oldMap) {
        int[][] newMap = oldMap;

        for (int i = 0; i < tiledMapWidth; i++) {
            for (int j = 0; j < tiledMapHeight; j++) {
                startPosX = (i - 2 < 0) ? i : i - 2;
                startPosY = (j - 2 < 0) ? j : j - 2;
                endPosX = (i + 2 > (tiledMapWidth - 1)) ? i : i + 2;
                endPosY = (j + 2 > (tiledMapHeight - 1)) ? j : j + 2;

                for (int rowNum = startPosX; rowNum <= endPosX; rowNum++) {
                    for (int colNum = startPosY; colNum <= endPosY; colNum++) {
                        if (oldMap[rowNum][colNum] == WALL) {
                            wallCount++;
                        }
                    }
                }
                if (wallCount <= 2) {
                    newMap[i][j] = WALL;
                }
                wallCount = 0;
            }
        }
        return newMap;
    }

    /*
    Description: creates a new world that's randomly populated
    Function: recreateWorld()
    Inputs: None
    Outputs: None
     */
    public void recreateWorld() {
        tiledMapWidth = randInt(10 + (5 * floorLevel), 10 + (10 * floorLevel));
        tiledMapHeight = randInt(5 + (5 * floorLevel), 10 + (10 * floorLevel));
        mapDiscovered = new ArrayList<Vector2>();

        //reinitialize the player character and enemy stats
        playerChar = new PlayerCharacter(15, 1);
        //initialize a random-sized array of standard enemies and their stats
        enemies = new Array<standardEnemy>();
        for (int i = 0; i < (1 + floorLevel); i++) {
            enemies.add(new standardEnemy(3, 1));
        }

        //generate a new world map
        newMap = createWorld();
        while(newMap == null) {
            newMap = createWorld();
        }

        //initialize player character, enemies, and exit positions
        while (newMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        playerChar.x = ranPosX;
        playerChar.y = ranPosY;
        newMap[playerChar.x][playerChar.y] = PLAYER;
        updateMapDiscovered();
        camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);

        for (int i = 0; i < enemies.size; i++) {
            while (newMap[ranPosX][ranPosY] != FLOOR) {
                ranPosX = randInt(1, (tiledMapWidth - 1));
                ranPosY = randInt(1, (tiledMapHeight - 1));
            }
            enemies.get(i).x = ranPosX;
            enemies.get(i).y = ranPosY;
            newMap[enemies.get(i).x][enemies.get(i).y] = ENEMY;
        }

        while (newMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        newMap[ranPosX][ranPosY] = EXIT;
        System.out.println("EXIT: " + ranPosX + " " + ranPosY);
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
        if ((start.getCurrentX() == goal.getCurrentX()) &&
                (start.getCurrentY() == goal.getCurrentY())) {
            return makePath(start);
        }

        //if not, store the start node into open
        open.add(start);

        //while there are still nodes unchecked in open, perform the following:
        while (!open.isEmpty()) {
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
                if (!closed.contains(neighbours.get(i))) {
                    int tempGScore = nextBest.getgScore() + 1;
                    neighbours.get(i).setfScore(neighbours.get(i).getgScore() +
                            heuristic(neighbours.get(i), goal));

                    if (!open.contains(neighbours.get(i))) {
                        open.add(neighbours.get(i));
                    } else {
                        if (tempGScore >= neighbours.get(i).getgScore()) {
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

        // 3x3 surroundings
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }

                //if a neighbour isn't within the bounds of the world/isn't a floor,
                //don't initialize and store them into the neighbours array
                if(((x + node.getCurrentX()) >= tiledMapWidth) ||
                        ((x + node.getCurrentX()) < 0) ||
                        ((y + node.getCurrentY()) >= tiledMapHeight) ||
                        ((y + node.getCurrentY()) < 0)) {
                    continue;
                }
                if (newMap[x + node.getCurrentX()][y + node.getCurrentY()] == FLOOR ||
                        newMap[x + node.getCurrentX()][y + node.getCurrentY()] == PLAYER) {
                    //create a new Node object and store it into neighbours
                    Node adjNode = new Node();
                    adjNode.setCurrentX(x + node.getCurrentX());
                    adjNode.setCurrentY(y + node.getCurrentY());
                    neighbours.add(adjNode);
                }

                // test code to see if the enemies have trouble following the player
                /*
                if(((x + node.getCurrentX()) >= tiledMapWidth) ||
                        ((x + node.getCurrentX()) < 0) ||
                        ((y + node.getCurrentY()) >= tiledMapHeight) ||
                        ((y + node.getCurrentY()) < 0)) {
                    continue;
                }
                try {
                    if (newMap[x + node.getCurrentX()][y + node.getCurrentY()] == FLOOR ||
                            newMap[x + node.getCurrentX()][y + node.getCurrentY()] == PLAYER) {
                        //create a new Node object and store it into neighbours
                        Node adjNode = new Node();
                        adjNode.setCurrentX(x + node.getCurrentX());
                        adjNode.setCurrentY(y + node.getCurrentY());
                        neighbours.add(adjNode);
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("player x: " + playerChar.getX() + " y: " + playerChar.getY());
                    System.out.println("x: " + (x + node.getCurrentX()) + " y: " + (y + node.getCurrentY()));
                }
                */
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
        boolean tileDiscovered = false;

        if (mapDiscovered.size() == 0) {
            mapDiscovered.add(new Vector2(playerChar.getX(), playerChar.getY()));
        }
        int mapDiscoveredSize = mapDiscovered.size();
        for (int i = (playerChar.getX() - 4); i <= (playerChar.getX() + 4); i++) {
            for (int j = (playerChar.getY() - 4); j <= (playerChar.getY() + 4); j++) {
                if ((i >= 0 && i <= (tiledMapWidth - 1)) &&
                        (j >= 0 && j <= (tiledMapHeight - 1))) {
                    mapTileDiscovered = new Vector2(i, j);
                    for (int k = 0; k < mapDiscoveredSize; k++) {
                        if (((mapDiscovered.get(k).x == mapTileDiscovered.x) &&
                                (mapDiscovered.get(k).y == mapTileDiscovered.y))) {
                            tileDiscovered = true;
                        }
                    }
                    if (tileDiscovered) {
                        tileDiscovered = false;
                    } else {
                        mapDiscovered.add(mapTileDiscovered);
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

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        playerIdleStateTime += Gdx.graphics.getDeltaTime();
        playerIdleCurrentFrame = playerIdleAnimation.getKeyFrame(playerIdleStateTime, true);

        stage.getBatch().begin();
        if (gameOver) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
        else if (mapPressed) {
            for (int i = 0; i < mapDiscovered.size(); i++) {
                if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == WALL) {
                    //background
                    Color c = stage.getBatch().getColor();
                    stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                    stage.getBatch().draw(floorTex, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                    //foreground
                    c = stage.getBatch().getColor();
                    stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 1
                    stage.getBatch().draw(wallTex, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                } else if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == PLAYER) {
                    //background
                    Color c = stage.getBatch().getColor();
                    stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                    stage.getBatch().draw(floorTex, playerChar.x * TEXTURESIZE,
                            playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                    //foreground
                    c = stage.getBatch().getColor();
                    stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 1
                    stage.getBatch().draw(playerIdleCurrentFrame, playerChar.x * TEXTURESIZE,
                            playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }
                /*
                else if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == ENEMY) {
                    stage.getBatch().draw(neutralFace, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }*/
                else if (newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y] == EXIT) {
                    //background
                    Color c = stage.getBatch().getColor();
                    stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                    stage.getBatch().draw(floorTex, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                    //foreground
                    c = stage.getBatch().getColor();
                    stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                    stage.getBatch().draw(portalTex, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                } else {
                    stage.getBatch().draw(floorTex, mapDiscovered.get(i).x * TEXTURESIZE,
                            mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                }
            }
        }
        else {
            for (int i = (playerChar.getX() - (8)); i < (playerChar.getX() + (9)); i++) {
                for (int j = (playerChar.getY() - (5)); j < (playerChar.getY() + (6)); j++) {
                    if (i < 0 || i >= tiledMapWidth || j < 0 || j >= tiledMapHeight) {
                        // continue
                    }
                    else if (newMap[i][j] == WALL) {
                        //background
                        Color c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                        stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                        //foreground
                        c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 1
                        stage.getBatch().draw(wallTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    }
                    else if (newMap[i][j] == PLAYER) {
                        if(attackedEnemy) {
                            if(playerChar.getX() > enemyX) {
                                //background
                                Color c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                                stage.getBatch().draw(floorTex, playerChar.x * TEXTURESIZE,
                                        playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                                //foreground
                                c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                                stage.getBatch().draw(playerAtkLeftTex, playerChar.x * TEXTURESIZE,
                                        playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                            }
                            else {
                                //background
                                Color c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                                stage.getBatch().draw(floorTex, playerChar.x * TEXTURESIZE,
                                        playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                                //foreground
                                c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                                stage.getBatch().draw(playerAtkRightTex, playerChar.x * TEXTURESIZE,
                                        playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                            }
                        }
                        else {
                            //background
                            Color c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                            stage.getBatch().draw(floorTex, playerChar.x * TEXTURESIZE,
                                    playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                            //foreground
                            c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                            stage.getBatch().draw(playerIdleCurrentFrame, playerChar.x * TEXTURESIZE,
                                    playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        }
                    }
                    else if (newMap[i][j] == ENEMY) {
                        if(attackedPlayer) {
                            //background
                            Color c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                            stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                    TEXTURESIZE, TEXTURESIZE);
                            //foreground
                            c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                            stage.getBatch().draw(enemyAtkTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                    TEXTURESIZE, TEXTURESIZE);
                        }
                        else {
                            //background
                            Color c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                            stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                    TEXTURESIZE, TEXTURESIZE);
                            //foreground
                            c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                            stage.getBatch().draw(enemyIdleTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                    TEXTURESIZE, TEXTURESIZE);
                        }
                    }
                    else if (newMap[i][j] == EXIT) {
                        //background
                        Color c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                        stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                        //foreground
                        c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                        stage.getBatch().draw(portalTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    }
                    else {
                        stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                    }
                }
            }
        }
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    /*
    Description: Generates a pseudo-random integer between two input integers
    Function: randInt
    Inputs: min : int, max : int
    Outputs: int
     */
    public int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public void show() {
        worldMusic.setPosition(0);
        worldMusic.play();
    }

    @Override
    public void hide() {
        worldMusic.pause();
    }

    @Override
    public void pause() {
        SaveFile saveFile = new SaveFile(newMap, mapDiscovered, enemies, playerChar,
                tiledMapWidth, tiledMapHeight, floorLevel);
        saveFile.saveSaveData();
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        for (int actors = 0; actors < stage.getActors().size; actors++) {
            stage.getActors().get(actors).addAction(Actions.removeActor());
        }
        assetManager.dispose();
        stage.dispose();
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
        if (gameOver) {
            this.game.setScreen(new MainMenuScreen(game));
        }
        else if (mapPressed) {
            return false;
        }
        else if (options) {
            return false;
        }
        else {
            float delay = 0.02f; // seconds
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    worldTouch = new Vector3((int) worldTouch.x / TEXTURESIZE,
                            (int) worldTouch.y / TEXTURESIZE, 0);
                    //if within the bounds of the world
                    if ((worldTouch.x >= 0 && worldTouch.x <= tiledMapWidth) &&
                            (worldTouch.y >= 0 && worldTouch.y <= tiledMapHeight)) {
                        //if within one tile from the player character
                        if ((newMap[(int) worldTouch.x][(int) worldTouch.y] != WALL) &&
                                ((Math.abs(worldTouch.x - playerChar.x) == 1 ||
                                        Math.abs(worldTouch.x - playerChar.x) == 0) &&
                                        (Math.abs(worldTouch.y - playerChar.y) == 1 ||
                                                (Math.abs(worldTouch.y - playerChar.y) == 0)))) {
                            if (newMap[(int) worldTouch.x][(int) worldTouch.y] == ENEMY) {
                                for (int i = 0; i < enemies.size; i++) {
                                    if (worldTouch.x == enemies.get(i).getX() &&
                                            worldTouch.y == enemies.get(i).getY()) {
                                        enemyX = enemies.get(i).getX();
                                        attackedEnemy = true;
                                        enemies.get(i).takeDamage(playerChar.ATK);
                                        if (enemies.get(i).isDead()) {
                                            assetManager.get("Dying.mp3", Sound.class).play();
                                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                                            enemies.removeIndex(i);
                                        }
                                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                                    }
                                }
                            }
                            else if (newMap[(int) worldTouch.x][(int) worldTouch.y] == EXIT) {
                                attackedEnemy = false;
                                assetManager.get("Floor Climb.mp3", Sound.class).play();
                                floorLevel = floorLevel + 1;
                                recreateWorld();
                                playerChar.setHpBeforeSave(playerChar.HP);
                                playerHPBar.setRange(0, playerChar.HP);
                                playerHPBar.setValue(playerChar.HP);
                                SaveFile saveData = new SaveFile(newMap, mapDiscovered, enemies, playerChar,
                                        tiledMapWidth, tiledMapHeight, floorLevel);
                                saveData.saveSaveData();
                            }
                            else if (newMap[(int) worldTouch.x][(int) worldTouch.y] == FLOOR) {
                                attackedEnemy = false;
                                newMap[playerChar.x][playerChar.y] = FLOOR;
                                playerChar.x = (int) worldTouch.x;
                                playerChar.y = (int) worldTouch.y;
                                newMap[playerChar.x][playerChar.y] = PLAYER;
                                updateMapDiscovered();
                                camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                            }
                            //initialize a goal node for pathfinding (player character)
                            goalNode = new Node();
                            goalNode.setCurrentX(playerChar.getX());
                            goalNode.setCurrentY(playerChar.getY());

                            //enemies move towards the player one step at a time too
                            for (int i = 0; i < enemies.size; i++) {
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
                                    attackedPlayer = true;
                                    // continue
                                }
                                else if (path.size == 1) {
                                    attackedPlayer = true;
                                    assetManager.get("Attacked.mp3", Sound.class).play(0.5f);
                                    playerChar.takeDamage(enemies.get(i).ATK);
                                    playerHPBar.setValue(playerChar.HP);
                                    if (playerChar.isDead()) {
                                        gameOver = true;
                                        for (int actors = 0; actors < stage.getActors().size; actors++) {
                                            stage.getActors().get(actors).addAction(Actions.removeActor());
                                        }
                                        BitmapFont font = new BitmapFont();
                                        font.getData().setScale(5, 5);
                                        Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);
                                        Label text = new Label("Game over!\nFloors cleared: " + floorLevel
                                                + "\nPress anywhere to return to the main menu!", textStyle);
                                        text.setPosition(cellWidth, cellHeight * 3);
                                        stage.addActor(text);
                                        SaveFile saveFiles = new SaveFile();
                                        saveFiles.deleteSaveData();
                                    }
                                }
                                else {
                                    attackedPlayer = false;
                                    //move the enemy units one tile closer to the player
                                    newMap[enemies.get(i).x][enemies.get(i).y] = FLOOR;
                                    enemies.get(i).x = path.get(1).getCurrentX();
                                    enemies.get(i).y = path.get(1).getCurrentY();
                                    newMap[enemies.get(i).x][enemies.get(i).y] = ENEMY;
                                    path = null;
                                }
                            }
                        }
                    }
                }
            }, delay);
        }
        //System.out.println("\n" + "Touch: " + worldTouch.x + " " + worldTouch.y);
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