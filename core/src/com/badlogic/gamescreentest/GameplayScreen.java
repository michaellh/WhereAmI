package com.badlogic.gamescreentest;

import com.badlogic.gamescreentest.headsUP.ArrowTouchPad.Arrow;
import com.badlogic.gamescreentest.headsUP.ArrowTouchPad.AttackButton;
import com.badlogic.gamescreentest.headsUP.Options.AboutOption;
import com.badlogic.gamescreentest.headsUP.Options.AboutSection;
import com.badlogic.gamescreentest.headsUP.Options.MainMenuOption;
import com.badlogic.gamescreentest.headsUP.Options.QuitGameOption;
import com.badlogic.gamescreentest.headsUP.Options.ResumeGameOption;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

public class GameplayScreen implements Screen, InputProcessor {
    final GameScreen game;
    OrthographicCamera camera;
    Stage stage;
    AssetManager assetManager;
    Texture hpBarTex, hpBarPivotTex, mapExitTex, mapTex, optionsTex, mapLocateTex,
            mainMenuTex, quitGameTex, resumeGameTex, aboutTex, aboutSectionTex,
            playerIdleTex, playerAtkRightTex, playerAtkLeftTex,
            enemyIdleTex, enemyAtkTex, portalTex, wallTex, floorTex,
            arrowUpTex, arrowRightTex, arrowDownTex, arrowLeftTex,
            atkTopRightTex, atkBotRightTex, atkBotLeftTex, atkTopLeftTex,
            atkUpTex, atkRightTex, atkDownTex, atkLeftTex,
            arrowTopRightTex, arrowBotRightTex, arrowBotLeftTex, arrowTopLeftTex,
            gameOverTex;
    Random rand;

    gameOptions optButton;
    ResumeGameOption resumeGameOption;
    MainMenuOption mainMenuOption;
    AboutOption aboutOption;
    AboutSection aboutSection;
    QuitGameOption quitGameOption;

    MapHUD mapButton;
    MapExit mapExit;
    MapSeePlayer mapSeePlayer;

    GameOver gameOverActor;

    com.badlogic.gamescreentest.headsUP.PlayerHealthBar.HPBarStyle hpBarStyle;
    com.badlogic.gamescreentest.headsUP.PlayerHealthBar.PlayerHPBar playerHPBar;
    PlayerCharacter playerChar;

    Arrow arrowUp, arrowTopRight, arrowRight, arrowBotRight, arrowDown,
            arrowBotLeft, arrowLeft, arrowTopLeft;
    AttackButton attackUpButton, attackTopRightButton, attackRightButton, attackDownButton,
            attackBotRightButton, attackBotLeftButton, attackLeftButton, attackTopLeftButton;

    Animation playerIdleAnimation;
    TextureRegion[] playerIdleFrames;
    TextureRegion playerIdleCurrentFrame;
    float playerIdleStateTime;

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
        mainMenuTex = assetManager.get("MainMenu.jpg", Texture.class);
        quitGameTex = assetManager.get("QuitGame.jpg", Texture.class);
        aboutTex = assetManager.get("AboutOption.jpg", Texture.class);
        aboutSectionTex = assetManager.get("AboutSection.jpg", Texture.class);
        resumeGameTex = assetManager.get("ResumeGame.jpg", Texture.class);
        hpBarTex = assetManager.get("healthBar.jpg", Texture.class);
        hpBarPivotTex = assetManager.get("healthBarPivot.jpg", Texture.class);
        arrowUpTex = assetManager.get("arrowUp.png", Texture.class);
        arrowTopRightTex = assetManager.get("arrowTopRight.png", Texture.class);
        arrowRightTex = assetManager.get("arrowRight.png", Texture.class);
        arrowBotRightTex = assetManager.get("arrowBotRight.png", Texture.class);
        arrowDownTex = assetManager.get("arrowDown.png", Texture.class);
        arrowBotLeftTex = assetManager.get("arrowBotLeft.png", Texture.class);
        arrowLeftTex = assetManager.get("arrowLeft.png", Texture.class);
        arrowTopLeftTex = assetManager.get("arrowTopLeft.png", Texture.class);
        atkUpTex = assetManager.get("attackUpButton.png", Texture.class);
        atkTopRightTex = assetManager.get("attackTopRightButton.png", Texture.class);
        atkRightTex = assetManager.get("attackRightButton.png", Texture.class);
        atkBotRightTex = assetManager.get("attackBotRightButton.png", Texture.class);
        atkDownTex = assetManager.get("attackDownButton.png", Texture.class);
        atkBotLeftTex = assetManager.get("attackBotLeftButton.png", Texture.class);
        atkLeftTex = assetManager.get("attackLeftButton.png", Texture.class);
        atkTopLeftTex = assetManager.get("attackTopLeftButton.png", Texture.class);
        gameOverTex = assetManager.get("GameOver.jpg", Texture.class);

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

                        // Enables the HP bar, map mode button, options button, menu button, and d-pad
                        int mapButtonIndex = stage.getActors().indexOf(mapButton, true);
                        stage.getActors().get(mapButtonIndex).setVisible(true);
                        int optButtonIndex = stage.getActors().indexOf(optButton, true);
                        stage.getActors().get(optButtonIndex).setVisible(true);
                        int hpBarIndex = stage.getActors().indexOf(playerHPBar, true);
                        stage.getActors().get(hpBarIndex).setVisible(true);
                        detectChangeArrows();
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
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                options = true;
                for (int i = 0; i < stage.getActors().size; i++) {
                    stage.getActors().get(i).setVisible(false);
                }

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

                        for (int i = 0; i < stage.getActors().size; i++) {
                            stage.getActors().get(i).setVisible(true);
                        }

                        int optResumeIndex = stage.getActors().indexOf(resumeGameOption, true);
                        stage.getActors().get(optResumeIndex).addAction(Actions.removeActor());
                        int optMenuIndex = stage.getActors().indexOf(mainMenuOption, true);
                        stage.getActors().get(optMenuIndex).addAction(Actions.removeActor());
                        int optAboutIndex = stage.getActors().indexOf(aboutOption, true);
                        stage.getActors().get(optAboutIndex).addAction(Actions.removeActor());
                        int optQuitIndex = stage.getActors().indexOf(quitGameOption, true);
                        stage.getActors().get(optQuitIndex).addAction(Actions.removeActor());

                        detectChangeArrows();

                        options = false;
                    }
                });
                stage.addActor(resumeGameOption);

                mainMenuOption = new MainMenuOption(mainMenuTex, cellWidth, cellHeight);
                mainMenuOption.setBounds(cellWidth * 1.5f, cellHeight * 4, cellWidth * 7, cellHeight);
                mainMenuOption.addListener(new InputListener() {
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

                        assetManager.get("Button Push.mp3", Sound.class).play();
                        for (int actors = 0; actors < stage.getActors().size; actors++) {
                            stage.getActors().get(actors).addAction(Actions.removeActor());
                        }
                        enemies.clear();
                        mapDiscovered.clear();
                        stage.dispose();
                        game.setScreen(new MainMenuScreen(game));
                    }
                });
                stage.addActor(mainMenuOption);

                aboutOption = new AboutOption(aboutTex, cellWidth, cellHeight);
                aboutOption.setBounds(cellWidth * 1.5f, cellHeight * 3, cellWidth * 7, cellHeight);
                aboutOption.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        assetManager.get("Button Push.mp3", Sound.class).play();
                        aboutSection = new AboutSection(aboutSectionTex, cellWidth, cellHeight);
                        aboutSection.setBounds(0, 0, screenWidth, screenHeight);
                        aboutSection.addListener(new InputListener() {
                            @Override
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                return true;
                            }

                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                assetManager.get("Button Push.mp3", Sound.class).play();
                                aboutSection.addAction(Actions.removeActor());
                            }
                        });
                        stage.addActor(aboutSection);
                    }
                });
                stage.addActor(aboutOption);

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
                        enemies.clear();
                        mapDiscovered.clear();
                        dispose();
                        Gdx.app.exit();
                    }
                });
                stage.addActor(quitGameOption);
            }
        });
        stage.addActor(optButton);

        // Up arrow touch key
        arrowUp = new Arrow(arrowUpTex, cellWidth, cellHeight * 2, cellWidth, cellHeight);
        arrowUp.setBounds(cellWidth, cellHeight * 2, cellWidth, cellHeight);
        arrowUp.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getY() + 1) >= tiledMapHeight) {
                    enemyTurn();
                    detectChangeArrows();
                    return;
                }
                else if(newMap[playerChar.getX()][playerChar.getY() + 1] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.y = playerChar.getY() + 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX()][playerChar.getY() + 1] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowUp);

        // Up attack button
        attackUpButton = new AttackButton(atkUpTex, cellWidth, cellHeight * 2, cellWidth, cellHeight);
        attackUpButton.setBounds(cellWidth, cellHeight * 2, cellWidth, cellHeight);
        attackUpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if (playerChar.getX() == enemies.get(i).getX() &&
                            (playerChar.getY() + 1) == enemies.get(i).getY()) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackUpButton.setVisible(false);
                            arrowUp.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackUpButton.setVisible(false);
        stage.addActor(attackUpButton);

        // Top right arrow touch key
        arrowTopRight = new Arrow(arrowTopRightTex, cellWidth * 2, cellHeight * 2, cellWidth, cellHeight);
        arrowTopRight.setBounds(cellWidth * 2, cellHeight * 2, cellWidth, cellHeight);
        arrowTopRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getY() + 1 >= tiledMapHeight) ||
                        (playerChar.getX() + 1 >= tiledMapWidth)) {
                    enemyTurn();
                    detectChangeArrows();
                    return;
                }
                if(newMap[playerChar.getX() + 1][playerChar.getY() + 1] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.x = playerChar.getX() + 1;
                    playerChar.y = playerChar.getY() + 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX() + 1][playerChar.getY() + 1] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowTopRight);

        // Up attack button
        attackTopRightButton = new AttackButton(atkTopRightTex, cellWidth * 2, cellHeight * 2, cellWidth, cellHeight);
        attackTopRightButton.setBounds(cellWidth * 2, cellHeight * 2, cellWidth, cellHeight);
        attackTopRightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if (playerChar.getX() + 1 == enemies.get(i).getX() &&
                            (playerChar.getY() + 1) == enemies.get(i).getY()) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackTopRightButton.setVisible(false);
                            arrowTopRight.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackTopRightButton.setVisible(false);
        stage.addActor(attackTopRightButton);

        // Right arrow touch key
        arrowRight = new Arrow(arrowRightTex, cellWidth * 2, cellHeight, cellWidth, cellHeight);
        arrowRight.setBounds(cellWidth * 2, cellHeight, cellWidth, cellHeight);
        arrowRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getX() + 1) >= tiledMapWidth) {
                    enemyTurn();
                    detectChangeArrows();
                    return;
                }
                if(newMap[playerChar.getX() + 1][playerChar.getY()] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.x = playerChar.getX() + 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX() + 1][playerChar.getY()] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowRight);

        // Right attack button
        attackRightButton = new AttackButton(atkRightTex, cellWidth * 2, cellHeight, cellWidth, cellHeight);
        attackRightButton.setBounds(cellWidth * 2, cellHeight, cellWidth, cellHeight);
        attackRightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if (((playerChar.getX() + 1) == enemies.get(i).getX() &&
                            playerChar.getY() == enemies.get(i).getY())) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackRightButton.setVisible(false);
                            arrowRight.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackRightButton.setVisible(false);
        stage.addActor(attackRightButton);

        // Bot right arrow touch key
        arrowBotRight = new Arrow(arrowBotRightTex, cellWidth * 2, 0, cellWidth, cellHeight);
        arrowBotRight.setBounds(cellWidth * 2, 0, cellWidth, cellHeight);
        arrowBotRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getY() - 1 < 0) ||
                        (playerChar.getX() + 1 >= tiledMapWidth)) {
                    enemyTurn();
                    detectChangeArrows();
                    return;
                }
                if(newMap[playerChar.getX() + 1][playerChar.getY() - 1] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.x = playerChar.getX() + 1;
                    playerChar.y = playerChar.getY() - 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX() + 1][playerChar.getY() - 1] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowBotRight);

        // Bot right attack button
        attackBotRightButton = new AttackButton(atkBotRightTex, cellWidth * 2, 0, cellWidth, cellHeight);
        attackBotRightButton.setBounds(cellWidth * 2, 0, cellWidth, cellHeight);
        attackBotRightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if (playerChar.getX() + 1 == enemies.get(i).getX() &&
                            (playerChar.getY() - 1) == enemies.get(i).getY()) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackTopRightButton.setVisible(false);
                            arrowTopRight.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackBotRightButton.setVisible(false);
        stage.addActor(attackBotRightButton);

        // Down arrow touch key
        arrowDown = new Arrow(arrowDownTex, cellWidth, 0, cellWidth, cellHeight);
        arrowDown.setBounds(cellWidth, 0, cellWidth, cellHeight);
        arrowDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getY() - 1) < 0) {
                    enemyTurn();
                    detectChangeArrows();
                    return;
                }
                if(newMap[playerChar.getX()][playerChar.getY() - 1] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.y = playerChar.getY() - 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX()][playerChar.getY() - 1] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowDown);

        // Down attack button
        attackDownButton = new AttackButton(atkDownTex, cellWidth, 0, cellWidth, cellHeight);
        attackDownButton.setBounds(cellWidth, 0, cellWidth, cellHeight);
        attackDownButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if ((playerChar.getX() == enemies.get(i).getX() &&
                            (playerChar.getY() - 1) == enemies.get(i).getY())) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackDownButton.setVisible(false);
                            arrowDown.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackDownButton.setVisible(false);
        stage.addActor(attackDownButton);

        // Bot left arrow touch key
        arrowBotLeft = new Arrow(arrowBotLeftTex, 0, 0, cellWidth, cellHeight);
        arrowBotLeft.setBounds(0, 0, cellWidth, cellHeight);
        arrowBotLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getY() - 1 < 0) ||
                        (playerChar.getX() - 1 < 0)) {
                    enemyTurn();
                    detectChangeArrows();
                    return;
                }
                if(newMap[playerChar.getX() - 1][playerChar.getY() - 1] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.x = playerChar.getX() - 1;
                    playerChar.y = playerChar.getY() - 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX() - 1][playerChar.getY() - 1] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowBotLeft);

        // Bot left attack button
        attackBotLeftButton = new AttackButton(atkBotLeftTex, 0, 0, cellWidth, cellHeight);
        attackBotLeftButton.setBounds(0, 0, cellWidth, cellHeight);
        attackBotLeftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if (playerChar.getX() - 1 == enemies.get(i).getX() &&
                            (playerChar.getY() - 1) == enemies.get(i).getY()) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackTopRightButton.setVisible(false);
                            arrowTopRight.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackBotLeftButton.setVisible(false);
        stage.addActor(attackBotLeftButton);

        // Left arrow touch key
        arrowLeft = new Arrow(arrowLeftTex, 0, cellHeight, cellWidth, cellHeight);
        arrowLeft.setBounds(0, cellHeight, cellWidth, cellHeight);
        arrowLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getX() - 1) < 0) {
                    enemyTurn();
                    return;
                }
                if(newMap[playerChar.getX() - 1][playerChar.getY()] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.x = playerChar.getX() - 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX() - 1][playerChar.getY()] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowLeft);

        // Left attack button
        attackLeftButton = new AttackButton(atkLeftTex, 0, cellHeight, cellWidth, cellHeight);
        attackLeftButton.setBounds(0, cellHeight, cellWidth, cellHeight);
        attackLeftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if ((playerChar.getX() - 1) == enemies.get(i).getX() &&
                            playerChar.getY() == enemies.get(i).getY()) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackLeftButton.setVisible(false);
                            arrowLeft.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackLeftButton.setVisible(false);
        stage.addActor(attackLeftButton);

        // Top left arrow touch key
        arrowTopLeft = new Arrow(arrowTopLeftTex, 0, cellHeight * 2, cellWidth, cellHeight);
        arrowTopLeft.setBounds(0, cellHeight * 2, cellWidth, cellHeight);
        arrowTopLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetManager.get("Button Push.mp3", Sound.class).play();
                if((playerChar.getY() + 1 >= tiledMapHeight) ||
                        (playerChar.getX() - 1 < 0)) {
                    enemyTurn();
                    return;
                }
                if(newMap[playerChar.getX() - 1][playerChar.getY() + 1] == FLOOR) {
                    attackedEnemy = false;
                    newMap[playerChar.getX()][playerChar.getY()] = FLOOR;
                    playerChar.x = playerChar.getX() - 1;
                    playerChar.y = playerChar.getY() + 1;
                    newMap[playerChar.x][playerChar.y] = PLAYER;
                    updateMapDiscovered();
                    camera.position.set(playerChar.x * TEXTURESIZE, playerChar.y * TEXTURESIZE, 0);
                }
                else if(newMap[playerChar.getX() - 1][playerChar.getY() + 1] == EXIT) {
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
                enemyTurn();
                detectChangeArrows();
            }
        });
        stage.addActor(arrowTopLeft);

        // Top left attack button
        attackTopLeftButton = new AttackButton(atkTopLeftTex, 0, cellHeight * 2, cellWidth, cellHeight);
        attackTopLeftButton.setBounds(0, cellHeight * 2, cellWidth, cellHeight);
        attackTopLeftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < enemies.size; i++) {
                    if (playerChar.getX() - 1 == enemies.get(i).getX() &&
                            (playerChar.getY() + 1) == enemies.get(i).getY()) {
                        enemyX = enemies.get(i).getX();
                        attackedEnemy = true;
                        enemies.get(i).takeDamage(playerChar.ATK);
                        if (enemies.get(i).isDead()) {
                            playerChar.HP = playerChar.HP + 1;
                            playerHPBar.setValue(playerChar.HP);
                            assetManager.get("Dying.mp3", Sound.class).play();
                            newMap[enemies.get(i).getX()][enemies.get(i).getY()] = FLOOR;
                            enemies.removeIndex(i);
                            attackTopRightButton.setVisible(false);
                            arrowTopRight.setVisible(true);
                        }
                        assetManager.get("Pain.mp3", Sound.class).play(0.1f);
                    }
                }
                enemyTurn();
                detectChangeArrows();
            }
        });
        attackTopLeftButton.setVisible(false);
        stage.addActor(attackTopLeftButton);

        detectChangeArrows();

        //give priority touch to the stage actors
        InputMultiplexer im = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(im);
    }

    public boolean[] enemyDetected() {
        boolean[] whichEnemy = new boolean[8];
        int index = 0;

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x == 0) && (y == 0)) {
                    continue;
                }
                else {
                    if ((playerChar.getX() + x < 0) ||
                            (playerChar.getX() + x >= tiledMapWidth) ||
                            (playerChar.getY() + y < 0) ||
                            (playerChar.getY() + y >= tiledMapHeight)) {
                        whichEnemy[index] = false;
                    }
                    else if (newMap[playerChar.getX() + x][playerChar.getY() + y] == ENEMY) {
                        whichEnemy[index] = true;
                    }
                }
                index++;
            }
        }
        return whichEnemy;
    }

    public void detectChangeArrows() {
        boolean[] whichEnemy = enemyDetected();
        for (int i = 0; i < whichEnemy.length; i++) {
            if(whichEnemy[i] && (i == 0)) {
                arrowBotLeft.setVisible(false);
                attackBotLeftButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 0)) {
                attackBotLeftButton.setVisible(false);
                arrowBotLeft.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 1)) {
                arrowLeft.setVisible(false);
                attackLeftButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 1)) {
                attackLeftButton.setVisible(false);
                arrowLeft.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 2)) {
                arrowTopLeft.setVisible(false);
                attackTopLeftButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 2)) {
                attackTopLeftButton.setVisible(false);
                arrowTopLeft.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 3)) {
                arrowDown.setVisible(false);
                attackDownButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 3)) {
                attackDownButton.setVisible(false);
                arrowDown.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 4)) {
                arrowUp.setVisible(false);
                attackUpButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 4)) {
                attackUpButton.setVisible(false);
                arrowUp.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 5)) {
                arrowBotRight.setVisible(false);
                attackBotRightButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 5)) {
                attackBotRightButton.setVisible(false);
                arrowBotRight.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 6)) {
                arrowRight.setVisible(false);
                attackRightButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 6)) {
                attackRightButton.setVisible(false);
                arrowRight.setVisible(true);
            }
            else if (whichEnemy[i] && (i == 7)) {
                arrowTopRight.setVisible(false);
                attackTopRightButton.setVisible(true);
            }
            else if (!whichEnemy[i] && (i == 7)) {
                attackTopRightButton.setVisible(false);
                arrowTopRight.setVisible(true);
            }
        }
    }

    public int[][] createWorld() {
        newMap = new int[tiledMapWidth][tiledMapHeight];

        // populate the map with walls
        for (int i = 0; i < tiledMapWidth; i++) {
            for (int j = 0; j < tiledMapHeight; j++) {
                if (randInt(0, 100) < 40) {
                    newMap[i][j] = WALL;
                }
            }
        }

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
                    mapWallCount = mapWallCount + 1;
                }
            }
        }

        int[][] copyNewMap = deepCopyArray(newMap);
        ranPosX = randInt(1, (tiledMapWidth - 1));
        ranPosY = randInt(1, (tiledMapHeight - 1));
        while (copyNewMap[ranPosX][ranPosY] != FLOOR) {
            ranPosX = randInt(1, (tiledMapWidth - 1));
            ranPosY = randInt(1, (tiledMapHeight - 1));
        }
        int floodMapCount = 0;
        floodFill(copyNewMap, ranPosX, ranPosY, 0, 1);

        for (int i = tiledMapHeight - 1; i > 0; i--) {
            for (int j = 0; j < tiledMapWidth; j++) {
                if(copyNewMap[j][i] == WALL) {
                    floodMapCount = floodMapCount + 1;
                }
                else if(copyNewMap[j][i] == FLOOR) {
                    newMap[j][i] = WALL;
                }
            }
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
        for (int i = (playerChar.getX() - 7); i <= (playerChar.getX() + 8); i++) {
            for (int j = (playerChar.getY() - 5); j <= (playerChar.getY() + 4); j++) {
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
            // do nothing and skip to rendering the game over screen
        }
        else if (options) {
            // do nothing and skip to rendering the options
        }
        else if (mapPressed) {
            for (int i = 0; i < mapDiscovered.size(); i++) {
                int tile = newMap[(int) mapDiscovered.get(i).x][(int) mapDiscovered.get(i).y];
                switch (tile) {
                    // floor
                    case 0:
                        stage.getBatch().draw(floorTex, mapDiscovered.get(i).x * TEXTURESIZE,
                                mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        break;

                    // wall
                    case 1:
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
                        break;

                    // player
                    case 2:
                        //background
                        c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                        stage.getBatch().draw(floorTex, playerChar.x * TEXTURESIZE,
                                playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        //foreground
                        c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 1
                        stage.getBatch().draw(playerIdleCurrentFrame, playerChar.x * TEXTURESIZE,
                                playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        break;

                    // map view doesn't display enemies, but will instead show the floor in their place
                    case 3:
                        stage.getBatch().draw(floorTex, mapDiscovered.get(i).x * TEXTURESIZE,
                                mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        break;

                    // portal
                    case 4:
                        //background
                        c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                        stage.getBatch().draw(floorTex, mapDiscovered.get(i).x * TEXTURESIZE,
                                mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        //foreground
                        c = stage.getBatch().getColor();
                        stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                        stage.getBatch().draw(portalTex, mapDiscovered.get(i).x * TEXTURESIZE,
                                mapDiscovered.get(i).y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                        break;
                }
            }
        }
        else {
            for (int i = (playerChar.getX() - (8)); i < (playerChar.getX() + (9)); i++) {
                for (int j = (playerChar.getY() - (5)); j < (playerChar.getY() + (6)); j++) {
                    if (i < 0 || i >= tiledMapWidth || j < 0 || j >= tiledMapHeight) {
                        continue;
                    }
                    int tile = newMap[i][j];
                    switch (tile) {
                        //floor
                        case 0:
                            stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                TEXTURESIZE, TEXTURESIZE);
                            break;

                        // wall
                        case 1:
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
                            break;

                        // player
                        case 2:
                            if(attackedEnemy) {
                                if(playerChar.getX() > enemyX) {
                                    //background
                                    c = stage.getBatch().getColor();
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
                                    c = stage.getBatch().getColor();
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
                                c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                                stage.getBatch().draw(floorTex, playerChar.x * TEXTURESIZE,
                                        playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                                //foreground
                                c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                                stage.getBatch().draw(playerIdleCurrentFrame, playerChar.x * TEXTURESIZE,
                                        playerChar.y * TEXTURESIZE, TEXTURESIZE, TEXTURESIZE);
                            }
                            break;

                        // enemy
                        case 3:
                            if(attackedPlayer) {
                                //background
                                c = stage.getBatch().getColor();
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
                                c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                                stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                        TEXTURESIZE, TEXTURESIZE);
                                //foreground
                                c = stage.getBatch().getColor();
                                stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                                stage.getBatch().draw(enemyIdleTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                        TEXTURESIZE, TEXTURESIZE);
                            }
                            break;

                        // portal
                        case 4:
                            //background
                            c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f); //set alpha to 1
                            stage.getBatch().draw(floorTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                    TEXTURESIZE, TEXTURESIZE);
                            //foreground
                            c = stage.getBatch().getColor();
                            stage.getBatch().setColor(c.r, c.g, c.b, 1f);//set alpha to 0.3
                            stage.getBatch().draw(portalTex, i * TEXTURESIZE, j * TEXTURESIZE,
                                    TEXTURESIZE, TEXTURESIZE);
                            break;
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
        detectChangeArrows();
    }

    @Override
    public void dispose() {
        for (int actors = 0; actors < stage.getActors().size; actors++) {
            stage.getActors().get(actors).addAction(Actions.removeActor());
        }
        enemies.clear();
        mapDiscovered.clear();
        assetManager.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    public void playerDead() {
        for (int actors = 0; actors < stage.getActors().size; actors++) {
            stage.getActors().get(actors).addAction(Actions.removeActor());
        }
        gameOver = true;

        SaveFile highScoreFile = new SaveFile();
        highScoreFile.writeHighScore(floorLevel);
        int highScore = highScoreFile.readHighScore();

        SaveFile saveFiles = new SaveFile();
        saveFiles.deleteSaveData();

        gameOverActor = new GameOver(gameOverTex, 0, 0, screenWidth, screenHeight);
        gameOverActor.setBounds(0, 0, screenWidth, screenHeight);
        gameOverActor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(gameOverActor);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(5, 5);
        Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);
        Label text = new Label(floorLevel + "\n\n" + highScore, textStyle);
        text.setPosition(cellWidth * 8, cellHeight * 2.45f);
        stage.addActor(text);
    }

    public void enemyTurn() {
        //enemies move towards the player one step at a time too
        for (int i = 0; i < enemies.size; i++) {
            if (enemies.size <= 0) {
                break;
            }

            //initialize a goal node
            goalNode = new Node();
            goalNode.setCurrentX(playerChar.getX());
            goalNode.setCurrentY(playerChar.getY());

            //initialize a start node for pathfinding (enemies)
            startNode = new Node();
            startNode.setCurrentX(enemies.get(i).getX());
            startNode.setCurrentY(enemies.get(i).getY());

            //create the path from the enemy to the player
            //and if the enemy is beside the player or the path is null
            //then move onto the next enemy in the array
            path = aStarPathFinding(startNode, goalNode);
            if (path == null || path.size == 0) {
                // the enemy will do nothing if the path doesn't exist
            }
            else if (path.size == 1) {
                attackedPlayer = true;
                assetManager.get("Attacked.mp3", Sound.class).play(0.5f);
                playerChar.takeDamage(enemies.get(i).ATK);
                playerHPBar.setValue(playerChar.HP);
                if (playerChar.isDead()) {
                    gameOver = true;
                    playerDead();
                }
            }
            else {
                attackedPlayer = false;
                //move the enemy units one tile closer to the player
                newMap[enemies.get(i).x][enemies.get(i).y] = FLOOR;
                enemies.get(i).x = path.get(1).getCurrentX();
                enemies.get(i).y = path.get(1).getCurrentY();
                newMap[enemies.get(i).x][enemies.get(i).y] = ENEMY;
            }
            path = null;
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        userTouch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        dragOld = userTouch;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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