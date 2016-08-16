package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by admin on 8/11/2016.
 */
public class SaveFile {
    int mapWidth;
    int mapHeight;
    int[][] saveMap;
    ArrayList<Vector2> saveMapDiscovered;
    Array<standardEnemy> saveEnemies;
    PlayerCharacter savePlayer;

    public SaveFile() {
    }

    public SaveFile(int[][] map, ArrayList<Vector2> mapDiscovered, Array<standardEnemy> enemies,
                    PlayerCharacter player, int tiledMapWidth, int tiledMapHeight) {
        saveMap = map;
        saveMapDiscovered = mapDiscovered;
        saveEnemies = enemies;
        savePlayer = player;
        mapWidth = tiledMapWidth;
        mapHeight = tiledMapHeight;
        //System.out.println("x: " + saveMap.length);
        //System.out.println("y: " + saveMap[0].length);
    }

    public boolean ifFileExists(String file) {
        FileHandle file1 = Gdx.files.local(file);
        boolean fileExists = file1.exists();
        if(fileExists) {
            return true;
        }
        return false;
    }


    public void writeSaveMap() {
        FileHandle saveFile = Gdx.files.local("android/assets/saveMapFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/saveMapFile.txt").exists();
        if(saveFileExist){
            System.out.println("DELETING SAVE MAP FILE");
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
        }
        catch (Exception e) {
            System.out.println("Error when writing to save map file!");
        }
        System.out.println("Wrote to save map");
    }

    public int[][] readSaveMap() {
        if(!ifFileExists("android/assets/saveMapFile.txt")) {
            System.out.println("No save map file exists!");
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the 2D array of the map
            // In the file it should be in the form of Strings
            FileHandle saveFileHandle = Gdx.files.local("android/assets/saveMapFile.txt");
            String fileText = saveFileHandle.readString();
            //System.out.println(fileText);
            String[] fileTextSplit = fileText.split(",");
            //System.out.println(fileTextSplit.length);
            int width = Integer.parseInt(fileTextSplit[fileTextSplit.length - 2]);
            int height = Integer.parseInt(fileTextSplit[fileTextSplit.length - 1]);
            saveMap = new int[width][height];
            int fileSplitLength = 0;
            for(int i = 0; i < width; i++) {
                for(int j = 0; j < height; j++) {
                    saveMap[i][j] = Integer.parseInt(fileTextSplit[fileSplitLength]);
                    fileSplitLength = fileSplitLength + 1;
                }
            }
            //System.out.println("saveMap.length: " + saveMap.length + " width: " + width + " height: " + height);
            System.out.println("Read save map");
            return saveMap;
        }
    }

    public Vector2 readMapWidthHeight() {
        FileHandle saveFileHandle = Gdx.files.local("android/assets/saveMapFile.txt");
        String fileText = saveFileHandle.readString();
        String[] fileTextSplit = fileText.split(",");
        float width = Float.parseFloat(fileTextSplit[fileTextSplit.length - 2]);
        float height = Float.parseFloat(fileTextSplit[fileTextSplit.length - 1]);
        Vector2 mapWidthHeight = new Vector2();
        mapWidthHeight.x = width;
        mapWidthHeight.y = height;
        System.out.println("Read mapwidthheight");
        return mapWidthHeight;
    }

    public void writeMapDiscovered() {
        FileHandle saveFile = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/mapDiscoveredFile.txt").exists();
        if(saveFileExist){
            System.out.println("DELETING MAP DISCOVERED FILE");
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
            for (int i = 0; i < saveMapDiscovered.size(); i++) {
                saveFileHandle.writeString(Float.toString(saveMapDiscovered.get(i).x) + ",", true);
                saveFileHandle.writeString(Float.toString(saveMapDiscovered.get(i).y) + ",", true);
            }
        } catch (Exception e) {
            System.out.println("Error when writing to map discovered file!");
        }
        //System.out.println(saveMapDiscovered.size());
        System.out.println("Wrote to discovered map");
    }

    public ArrayList<Vector2> readMapDiscovered() {
        if(!ifFileExists("android/assets/mapDiscoveredFile.txt")) {
            System.out.println("No map discovered file exists!");
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the an arraylist of the discovered map
            saveMapDiscovered = new ArrayList<Vector2>();
            FileHandle saveFileHandle = Gdx.files.local("android/assets/mapDiscoveredFile.txt");
            String fileText = saveFileHandle.readString();
            //System.out.println(fileText);
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
            System.out.println("Read map discovered");
            return saveMapDiscovered;
        }
    }

    public void writePlayer() {
        FileHandle saveFile = Gdx.files.local("android/assets/playerFile.txt");
        boolean saveFileExist = Gdx.files.local("android/assets/playerFile.txt").exists();
        if(saveFileExist){
            System.out.println("DELETING PLAYER FILE");
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/playerFile.txt");
            saveFileHandle.writeString(Integer.toString(savePlayer.x) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.y) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.HP) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.ATK) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.DEF) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.LUK) + ",", true);
            saveFileHandle.writeString(Integer.toString(savePlayer.hpBeforeSave), true);
            System.out.println("Wrote to playerFile");
        } catch (Exception e) {
            System.out.println("Error when writing to player file!");
        }
    }

    public PlayerCharacter readPlayer() {
        if(!ifFileExists("android/assets/playerFile.txt")) {
            System.out.println("No player file exists!");
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
            int DEF = Integer.parseInt(fileTextSplit[4]);
            int LUK = Integer.parseInt(fileTextSplit[5]);
            int hpBeforeSave = Integer.parseInt(fileTextSplit[6]);
            savePlayer = new PlayerCharacter(HP, ATK, DEF, LUK);
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
            System.out.println("DELETING ENEMIES FILE");
            saveFile.delete();
        }

        try {
            FileHandle saveFileHandle = Gdx.files.local("android/assets/enemiesFile.txt");
            for (int i = 0; i < saveEnemies.size; i++) {
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).getX()) + ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).getY()) + ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).HP)+ ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).ATK) + ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).DEF) + ",", true);
                saveFileHandle.writeString(Float.toString(saveEnemies.get(i).LUK) + ",", true);
            }
            saveFileHandle.writeString(Integer.toString(saveEnemies.size), true);
        } catch (Exception e) {
            System.out.println("Error when writing to enemies file!");
        }
        System.out.println("Wrote to enemies");
    }

    public Array<standardEnemy> readEnemies() {
        if(!ifFileExists("android/assets/enemiesFile.txt")) {
            System.out.println("No enemies file exists!");
            return null;
        }
        // If the file does exist, read from it
        else {
            // Read and return the player's stats
            FileHandle saveFileHandle = Gdx.files.local("android/assets/enemiesFile.txt");
            String fileText = saveFileHandle.readString();
            //System.out.println(fileText);
            String[] fileTextSplit = fileText.split(",");
            int enemySize = Integer.parseInt(fileTextSplit[fileTextSplit.length - 1]);
            System.out.println(enemySize);
            saveEnemies = new Array<standardEnemy>();
            int counter = 0;
            for (int i = 0; i < enemySize; i++) {
                int x = (int) Float.parseFloat(fileTextSplit[counter]);
                int y = (int) Float.parseFloat(fileTextSplit[counter + 1]);;
                int HP = (int) Float.parseFloat(fileTextSplit[counter + 2]);
                int ATK = (int) Float.parseFloat(fileTextSplit[counter + 3]);
                int DEF = (int) Float.parseFloat(fileTextSplit[counter + 4]);
                int LUK = (int) Float.parseFloat(fileTextSplit[counter + 5]);
                counter = counter + 6;
                saveEnemies.add(new standardEnemy(HP, ATK, DEF, LUK));
                saveEnemies.get(i).x = x;
                saveEnemies.get(i).y = y;
            }
            return saveEnemies;
        }
    }
}