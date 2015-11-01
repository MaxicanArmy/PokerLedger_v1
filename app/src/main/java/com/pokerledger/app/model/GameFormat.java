package com.pokerledger.app.model;

/**
 * Created by Max on 6/21/15.
 */
public class GameFormat extends BaseFormat {
    private int id = 0;
    private String gameFormat = "";
    private int filtered = 0;

    public GameFormat() {

    }

    public GameFormat(int id, String f, int bfId, String bf) {
        this(id, f, bfId, bf, 0);
    }

    public GameFormat(int id, String f, int bfId, String bf, int filtered) {
        this.id = id;
        this.gameFormat = f;
        this.baseFormatId = bfId;
        this.baseFormat = bf;
        this.filtered = filtered;
    }

    @Override
    public String toString() {
        return this.gameFormat + " " + this.baseFormat;
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setGameFormat(String s) {
        this.gameFormat = s;
    }

    public void setFiltered(int filtered) {
        this.filtered = filtered;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getGameFormat() {
        return this.gameFormat;
    }

    public int getFiltered() {
        return this.filtered;
    }
}