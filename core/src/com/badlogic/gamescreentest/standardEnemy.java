package com.badlogic.gamescreentest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Array;

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
    public int takeDamage(int dmg) {
        System.out.println("Touched my body!!!");
        System.out.println(this.HP);
        this.HP = (this.HP + this.DEF) -  dmg;
        if(this.HP <= 0) {
            return 0;
        }
        return 3;
    }

    @Override
    public void die() {
        System.out.println("The enemy has died!!!");
    }
}
