package com.example;

/**
 * Created by sharlamov on 18.08.2015.
 */
public class Square {
    private Player player = null;

    public void fill(Player player) {
        this.player = player;
    }

    public boolean isFilled() {
        if (player != null) {
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return player;
    }
}
