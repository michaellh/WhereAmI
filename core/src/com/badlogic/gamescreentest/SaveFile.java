package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by admin on 8/11/2016.
 */
public class SaveFile {
    int[][] saveMap;
    ArrayList<Vector2> saveMapDiscovered;
    Array<standardEnemy> saveEnemies;
    PlayerCharacter savePlayer;

    public SaveFile(int[][] map, ArrayList<Vector2> mapDiscovered, Array<standardEnemy> enemies, PlayerCharacter player) {
        saveMap = map;
        saveMapDiscovered = mapDiscovered;
        saveEnemies = enemies;
        savePlayer = player;
        System.out.println("x: " + saveMap.length);
        System.out.println("y: " + saveMap[0].length);
    }

    public boolean ifFileExists(String file) {
        FileHandle file1 = Gdx.files.local(file);
        boolean fileExists = file1.exists();
        if(fileExists) {
            return true;
        }
        return false;
    }


    public void writeToSaveFile() {
        if(!ifFileExists("saveFile.txt")) {
            try {
                FileHandle saveFileHandle = Gdx.files.local("saveFile.txt");
                for (int i = 0; i < saveMap.length; i++) {
                    for (int j = 0; j < saveMap[0].length; j++) {
                        saveFileHandle.writeString(Integer.toString(saveMap[i][j]), true);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error when writing to save file!");
            }
            FileHandle shit = Gdx.files.local("poop.txt");
            System.out.println("is poop real? " + shit.exists());
            FileHandle saveFile2 = Gdx.files.local("saveFile.txt");
            System.out.println("is save real? " + saveFile2.exists());
            System.out.println("Just made.");
        }
        else {
            try {
                FileHandle saveFile = Gdx.files.local("assets/saveFile.txt");
                for (int i = 0; i < saveMap.length; i++) {
                    for (int j = 0; j < saveMap[0].length; j++) {
                        saveFile.writeString(Integer.toString(saveMap[i][j]), false);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error when writing to save file!");
            }
            FileHandle shit = Gdx.files.local("poop.txt");
            System.out.println("is poop real? " + shit.exists());
            FileHandle saveFile2 = Gdx.files.local("assets/saveFile.txt");
            System.out.println("is save real? " + saveFile2.exists());
            System.out.println("Wrote over.");
        }
    }
}