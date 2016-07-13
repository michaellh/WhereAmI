package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;

/**
 * Created by admin on 7/11/2016.
 */
public class standardEnemy extends Character implements EnemyCharacter {
    public standardEnemy(int health, int attack, int defence, int luck) {
        this.HP = health;
        this.ATK = attack;
        this.DEF = defence;
        this.LUK = luck;
    }

    @Override
    public void move() {

    }

    @Override
    public void collision() {
        System.out.println("Touched my body!!!");
    }

    @Override
    public void die() {
        System.out.println("The enemy has died!!!");
    }
}