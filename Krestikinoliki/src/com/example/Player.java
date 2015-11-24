package com.example;

/**
 * Created by sharlamov on 18.08.2015.
 */
public class Player {
    private String name;

    public Player(String name) {
        this.name = name;
    }

    public CharSequence getName() {
        return (CharSequence) name;
    }
}
