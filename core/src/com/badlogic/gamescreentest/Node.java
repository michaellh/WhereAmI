package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/16/2016.
 */
public class Node {
    int x;
    int y;
    int cost;
    Node parent;

    public void setParent(Node node) {
        parent = node;
    }
}
