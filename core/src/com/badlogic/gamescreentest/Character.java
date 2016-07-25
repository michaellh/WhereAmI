package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/9/2016.
 */
public class Character {
    int x;
    int y;

    int STATUS;
    int HP;
    int ATK;
    int DEF;
    int LUK;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int takeDamage(int dmg) {return STATUS;}

    public void die() {}
}
