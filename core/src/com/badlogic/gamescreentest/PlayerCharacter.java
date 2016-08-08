package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/11/2016.
 */
public class PlayerCharacter extends Character {
    public PlayerCharacter(int health, int attack, int defence, int luck) {
        this.HP = health;
        this.ATK = attack;
        this.DEF = defence;
        this.LUK = luck;
    }

    @Override
    public int takeDamage(int dmg) {
        System.out.println("I'VE BEEN HIT!" + " " +"REMAINING HP: " + this.HP);
        this.HP = (this.HP + this.DEF) - dmg;
        if(this.HP <= 0) {
            return 0;
        }
        return 2;
    }

    @Override
    public void die() {
        System.out.println("YOU HAVE CEASED TO EXIST");
    }
}
