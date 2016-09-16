package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.util.ArrayList;

public class SaveFile {
    int mapWidth;
    int mapHeight;
    int saveFloorLevel;
    int highScore;
    int[][] saveMap;
    ArrayList<Vector2> saveMapDiscovered;
    Array<standardEnemy> saveEnemies;
    PlayerCharacter savePlayer;

    public SaveFile() {
    }

    public SaveFile(int[][] map, ArrayList<Vector2> mapDiscovered, Array<standardEnemy> enemies,
                    PlayerCharacter player, int tiledMapWidth, int tiledMapHeight, int level) {
        saveMap = map;
        saveMapDiscovered = mapDiscovered;
        saveEnemies = enemies;
        savePlayer = player;
        mapWidth = tiledMapWidth;
        mapHeight = tiledMapHeight;
        saveFloorLevel = level;
    }

    public void writeHighScore(int hs) {
        if(fileExists("android/assets/highScore.txt")) {
            highScore = SaveFile.this.readHighScore();
            FileHandle saveFileHandle = Gdx.files.local("android/assets/highScore.txt");
            saveFileHandle.delete();
            if(highScore < hs) {
                highScore = hs;
            }
        }
        else {
            highScore = hs;
        }
        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/highScore.txt");
            saveFileHandle.writeString(Integer.toString(highScore) + ",", true);
        }
        catch (Exception e) {
            //
        }
    }

    public int readHighScore() {
        FileHandle saveHighScoreFile = Gdx.files.local("android/assets/highScore.txt");
        String fileText = saveHighScoreFile.readString();
        String[] fileTextSplit = fileText.split(",");
        highScore = Integer.parseInt(fileTextSplit[fileTextSplit.length - 1]);
        return highScore;
    }

    public boolean fileExists(String file) {
        FileHandle saveFile = Gdx.files.local(file);
        if(saveFile.exists()) {
            return true;
        }
        return false;
    }

    public void saveSaveData() {
        this.writePlayer();
        this.writeEnemies();
        this.writeSaveMap();
        this.writeMapDiscovered();
    }

    public void deleteSaveData() {
        FileHandle saveMap = Gdx.files.local("android/assets/saveMapFile.txt");
        saveMap.delete();
        FileHandle saveMapDiscovered = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
        saveMapDiscovered.delete();
        FileHandle savePlayer = Gdx.files.local("android/assets/playerFile.txt");
        savePlayer.delete();
        FileHandle saveEnemies = Gdx.files.local("android/assets/enemiesFile.txt");
        saveEnemies.delete();
    }

    public boolean saveFilesExist() {
        if(fileExists("android/assets/saveMapFile.txt") &&
                fileExists("android/assets/mapDiscoveredFile.txt") &&
                fileExists("android/assets/playerFile.txt") &&
                fileExists("android/assets/enemiesFile.txt")) {
            return true;
        }
        return false;
    }

    public void writeSaveMap() {
        FileHandle saveFile = Gdx.files.local("android/assets/saveMapFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/saveMapFile.txt").exists();
        if(saveFileExist){
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/saveMapFile.txt");
            for (int i = 0; i < saveMap.length; i++) {
                for (int j = 0; j < saveMap[0].length; j++) {
                    saveFileHandle.writeString(Integer.toString(saveMap[i][j]) + ",", true);
                }
            }
            saveFileHandle.writeString(Integer.toString(mapWidth) + ",", true);
            saveFileHandle.writeString(Integer.toString(mapHeight) + ",", true);
            saveFileHandle.writeString(Integer.toString(saveFloorLevel) + ",", true);
        }
        catch (Exception e) {
            //
        }
    }

    public int[][] readSaveMap() {
        if(!fileExists("android/assets/saveMapFile.txt")) {
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the 2D array of the map
            // In the file it should be in the form of Strings
            FileHandle saveFileHandle = Gdx.files.local("android/assets/saveMapFile.txt");
            String fileText = saveFileHandle.readString();
            String[] fileTextSplit = fileText.split(",");
            int width = Integer.parseInt(fileTextSplit[fileTextSplit.length - 3]);
            int height = Integer.parseInt(fileTextSplit[fileTextSplit.length - 2]);
            saveMap = new int[width][height];
            int fileSplitLength = 0;
            for(int i = 0; i < width; i++) {
                for(int j = 0; j < height; j++) {
                    saveMap[i][j] = Integer.parseInt(fileTextSplit[fileSplitLength]);
                    fileSplitLength = fileSplitLength + 1;
                }
            }
            return saveMap;
        }
    }

    public int readFloorLevel() {
        FileHandle saveFileHandle = Gdx.files.local("android/assets/saveMapFile.txt");
        String fileText = saveFileHandle.readString();
        String[] fileTextSplit = fileText.split(",");
        int floorLevel = Integer.parseInt(fileTextSplit[fileTextSplit.length - 1]);
        return floorLevel;
    }

    public Vector2 readMapWidthHeight() {
        FileHandle saveFileHandle = Gdx.files.local("android/assets/saveMapFile.txt");
        String fileText = saveFileHandle.readString();
        String[] fileTextSplit = fileText.split(",");
        float width = Float.parseFloat(fileTextSplit[fileTextSplit.length - 3]);
        float height = Float.parseFloat(fileTextSplit[fileTextSplit.length - 2]);
        Vector2 mapWidthHeight = new Vector2();
        mapWidthHeight.x = width;
        mapWidthHeight.y = height;
        return mapWidthHeight;
    }

    public void writeMapDiscovered() {
        FileHandle saveFile = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/mapDiscoveredFile.txt").exists();
        if(saveFileExist){
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
            for (int i = 0; i < saveMapDiscovered.size(); i++) {
                saveFileHandle.writeString(Float.toString(saveMapDiscovered.get(i).x) + ",", true);
                saveFileHandle.writeString(Float.toString(saveMapDiscovered.get(i).y) + ",", true);
            }
        } catch (Exception e) {
            //
        }
    }

    public ArrayList<Vector2> readMapDiscovered() {
        if(!fileExists("android/assets/mapDiscoveredFile.txt")) {
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the an arraylist of the discovered map
            saveMapDiscovered = new ArrayList<Vector2>();
            FileHandle saveFileHandle = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
            String fileText = saveFileHandle.readString();
            String[] fileTextSplit = fileText.split(",");
            for(int i = 0; i < fileTextSplit.length; i = i + 2) {
                if((i + 1) >= fileTextSplit.length) {
                    break;
                }
                else {
                    saveMapDiscovered.add(new Vector2(Float.parseFloat(fileTextSplit[i]),
                            Float.parseFloat(fileTextSplit[i + 1])));
                }
            }
            return saveMapDiscovered;
        }
    }

    public void writePlayer() {
        FileHandle saveFile = Gdx.files.local("android/assets/playerFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/playerFile.txt").exists();
        if(saveFileExist){
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/playerFile.txt");
            saveFileHandle.writeString(Integer.toString(savePlayer.x) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.y) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.HP) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.ATK) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.hpBeforeSave), true);
        }
        catch (Exception e) {
            //
        }
    }

    public PlayerCharacter readPlayer() {
        if(!fileExists("android/assets/playerFile.txt")) {
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the player's stats
            FileHandle saveFileHandle = Gdx.files.local("android/assets/playerFile.txt");
            String fileText = saveFileHandle.readString();
            String[] fileTextSplit = fileText.split(",");
            int x = Integer.parseInt(fileTextSplit[0]);
            int y = Integer.parseInt(fileTextSplit[1]);
            int HP = Integer.parseInt(fileTextSplit[2]);
            int ATK = Integer.parseInt(fileTextSplit[3]);
            int hpBeforeSave = Integer.parseInt(fileTextSplit[4]);
            savePlayer = new PlayerCharacter(HP, ATK);
            savePlayer.setHpBeforeSave(hpBeforeSave);
            savePlayer.x = x;
            savePlayer.y = y;
            return savePlayer;
        }
    }

    public void writeEnemies() {
        FileHandle saveFile = Gdx.files.local("android/assets/enemiesFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/enemiesFile.txt").exists();
        if(saveFileExist){
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/enemiesFile.txt");
            for (int i = 0; i < saveEnemies.size; i++) {
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).getX()) + ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).getY()) + ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).HP)+ ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).ATK) + ",", true);
            }
            saveFileHandle.writeString(Integer.toString(saveEnemies.size), true);
        } catch (Exception e) {
            //
        }
    }

    public Array<standardEnemy> readEnemies() {
        if(!fileExists("android/assets/enemiesFile.txt")) {
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the player's stats
            FileHandle saveFileHandle = Gdx.files.local("android/assets/enemiesFile.txt");
            String fileText = saveFileHandle.readString();
            String[] fileTextSplit = fileText.split(",");
            int enemySize = Integer.parseInt(fileTextSplit[fileTextSplit.length - 1]);
            saveEnemies = new Array<standardEnemy>();
            int counter = 0;
            for (int i = 0; i < enemySize; i++) {
                int x = (int) Float.parseFloat(fileTextSplit[counter]);
                int y = (int) Float.parseFloat(fileTextSplit[counter + 1]);
                int HP = (int) Float.parseFloat(fileTextSplit[counter + 2]);
                int ATK = (int) Float.parseFloat(fileTextSplit[counter + 3]);
                counter = counter + 4;
                saveEnemies.add(new standardEnemy(HP, ATK));
                saveEnemies.get(i).x = x;
                saveEnemies.get(i).y = y;
            }
            return saveEnemies;
        }
    }
}