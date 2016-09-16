package com.badlogic.gamescreentest;

public class Node implements Comparable<Node>{
    int currentX;
    int currentY;
    int gScore;
    int fScore;

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

    public int getfScore() {
        return fScore;
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
        return 0;
    }
}
