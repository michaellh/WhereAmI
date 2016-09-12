package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/9/2016.
 */
public class Character {
    int x;
    int y;

    int HP;
    int ATK;

    int state;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getState() {
        return state;
    };

    public void setState(int change) {
        state = change;
    }

    public void takeDamage(int dmg) {
        this.HP = this.HP - dmg;
    }

    public boolean isDead() {
        if(this.HP <= 0) {
            return true;
        }
        return false;
    }
}
