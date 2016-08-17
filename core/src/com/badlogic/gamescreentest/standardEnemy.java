package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/11/2016.
 */
public class standardEnemy extends Character {
    public standardEnemy(int health, int attack, int defence, int luck) {
        this.HP = health;
        this.ATK = attack;
        this.DEF = defence;
        this.LUK = luck;
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        System.out.println("Enemy HP remaining: " + this.HP);
    }
}
