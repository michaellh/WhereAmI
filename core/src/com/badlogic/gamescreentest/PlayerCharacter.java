package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/11/2016.
 */
public class PlayerCharacter extends Character {
    int hpBeforeSave;

    public PlayerCharacter(int health, int attack) {
        this.HP = health;
        this.ATK = attack;
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
