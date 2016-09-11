package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/11/2016.
 */
public class standardEnemy extends Character {
    public standardEnemy(int health, int attack) {
        this.HP = health;
        this.ATK = attack;
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        System.out.println("Enemy HP remaining: " + this.HP);
    }
}
