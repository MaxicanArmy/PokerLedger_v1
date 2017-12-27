package com.pokerledger.app.model;

/**
 * Created by Max on 9/12/14.
 */
public class    Game {
    private int id = 0;
    private String game = "";
    private int filtered = 0;

    //constructors
    public Game() {}

    public Game(int i, String s) {
        this.id = i;
        this.game = s;
    }

    public Game(int i, String s, int f) {
        this.id = i;
        this.game = s;
        this.filtered = f;
    }

    @Override
    public String toString() {
        return this.game;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setGame(String s) {
        this.game = s;
    }

    public void setFiltered(int f) {
        this.filtered = f;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getGame() {
        return this.game;
    }

    public int getFiltered() {
        return this.filtered;
    }
}