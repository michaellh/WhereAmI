package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/16/2016.
 */
public class Node implements Comparable<Node>{
    int currentX;
    int currentY;
    int cost;
    Node parent;

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int x) {
        currentX = x;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int y) {
        currentY = y;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int c) {
        cost = c;
    }

    public void setParent(Node node) {
        parent = node;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.cost, o.getCost());
    }
}
