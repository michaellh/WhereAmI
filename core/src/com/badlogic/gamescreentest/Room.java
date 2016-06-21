package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by admin on 6/18/2016.
 */
public class Room {
    //4 corners of the tiled map
    int x1;
    int x2;
    int y1;
    int y2;

    //width and height respectively of the map
    int w;
    int h;

    //centre of the map
    Vector2 centre;

    int roomX;
    int roomY;
    int roomWidth;
    int roomHeight;
    String tileImg;
    Sprite makeRoom;
    Array<Room> rooms;

    //Constructor to create new rooms
    public Room(int x, int y, int w, int h, String tile) {
        x1 = x;
        x2 = x + w;
        y1 = y;
        y2 = y + h;

        roomX = (w * 32) / x;
        roomY = (h * 32) / y;
        roomWidth = w;
        roomHeight = h;
        tileImg = tile;
        centre = new Vector2((float) Math.floor((double) (x1 + x2) / 2),
                (float) Math.floor((double) (y1 + y2) / 2));
    }

    public Sprite createRoom() {
        makeRoom = new Sprite(new Texture(Gdx.files.internal(tileImg)));
        makeRoom.setPosition(x1, y1);
        makeRoom.setSize(roomX, roomY);
        return makeRoom;
    }

    // return true if this room intersects provided room
    public Boolean intersects(Room room) {
        return (x1 <= room.x2 && x2 >= room.x1 &&
                y1 <= room.y2 && room.y2 >= room.y1);
    }

    /*
    private void placeRooms() {
        rooms = new Array();


    }*/
}
