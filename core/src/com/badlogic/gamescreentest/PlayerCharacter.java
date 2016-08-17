package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/11/2016.
 */
public class PlayerCharacter extends Character {
    int hpBeforeSave;

    public PlayerCharacter(int health, int attack, int defence, int luck) {
        this.HP = health;
        this.ATK = attack;
        this.DEF = defence;
        this.LUK = luck;
    }

    public void setHpBeforeSave(int hp) {
        this.hpBeforeSave = hp;
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        System.out.println("I'VE BEEN HIT!" + " " +"REMAINING HP: " + this.HP);
    }
}
