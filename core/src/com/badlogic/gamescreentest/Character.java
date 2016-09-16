package com.badlogic.gamescreentest;

public class Character {
    int x;
    int y;

    int HP;
    int ATK;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
