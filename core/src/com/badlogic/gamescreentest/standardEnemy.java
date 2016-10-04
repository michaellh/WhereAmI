package com.badlogic.gamescreentest;

public class standardEnemy extends Character {
    public standardEnemy(int health, int attack) {
        this.HP = health;
        this.ATK = attack;
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
    }
}
