package com.badlogic.gamescreentest;

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
    }
}
