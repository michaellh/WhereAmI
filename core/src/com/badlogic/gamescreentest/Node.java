package com.badlogic.gamescreentest;

/**
 * Created by admin on 7/16/2016.
 */
public class Node implements Comparable<Node>{
    int currentX;
    int currentY;
    int gScore;
    int fScore;
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

    public void setgScore(int g) {
        gScore = g;
    }

    public int getgScore() {
        return gScore;
    }

    public void setfScore(int f) {
        fScore = f;
    }

    public int getfScore() {
        return fScore;
    }

    public void setParent(Node node) {
        parent = node;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.fScore, o.getfScore());
    }

    @Override
    public boolean equals(Object obj) {
        boolean identical = false;
        if(obj instanceof Node) {
            identical = (this.getCurrentX() == ((Node) obj).getCurrentX())  &&
                    (this.getCurrentY() == ((Node) obj).getCurrentY());
        }
        return identical;
    }

    @Override
    public int hashCode() {
        return this.getfScore();
    }
}
