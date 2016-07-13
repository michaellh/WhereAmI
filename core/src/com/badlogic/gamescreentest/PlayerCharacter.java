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
}
