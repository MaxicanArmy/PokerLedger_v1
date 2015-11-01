package com.pokerledger.app.model;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Blinds {
    private int id = 0;
    private int sb = 0;
    private int bb = 0;
    private int straddle = 0;
    private int bringIn = 0;
    private int ante = 0;
    private int perPoint = 0;
    private int filtered = 0;

    //constructors
    public Blinds() {}

    public Blinds(int sb, int bb, int str, int bi, int a, int pp) {
        this.sb = sb;
        this.bb = bb;
        this.straddle = str;
        this.bringIn = bi;
        this.ante = a;
        this.perPoint = pp;
    }

    public Blinds(int sb, int bb, int str, int bi, int a, int pp, int f) {
        this.sb = sb;
        this.bb = bb;
        this.straddle = str;
        this.bringIn = bi;
        this.ante = a;
        this.perPoint = pp;
        this.filtered = f;
    }

    public Blinds(int id, int sb, int bb, int str, int bi, int a, int pp, int f) {
        this.id = id;
        this.sb = sb;
        this.bb = bb;
        this.straddle = str;
        this.bringIn = bi;
        this.ante = a;
        this.perPoint = pp;
        this.filtered = f;
    }

    @Override
    public String toString() {
        String blinds = "";

        if (sb != 0 && bb != 0) {
            blinds += "$" + sb + "/$" + bb;
        }
        else if (sb != 0) {
            blinds += "$" + sb + " blind";
        }

        if (straddle != 0) {
            blinds += "/$" + straddle;
        }

        if (bringIn != 0) {
            blinds += " w/$" + bringIn + " bring in";
        }

        if (ante != 0) {
            blinds += " w/$" + ante + " ante";
        }

        if (perPoint != 0) {
            blinds += "$" + perPoint + "/point";
        }

        return blinds;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setSB(int i) {
        this.sb = i;
    }

    public void setBB(int i) {
        this.bb = i;
    }

    public void setStraddle(int i) {
        this.straddle = i;
    }

    public void setBringIn(int i) {
        this.bringIn = i;
    }

    public void setAnte(int i) {
        this.ante = i;
    }

    public void setPerPoint(int i) {
        this.perPoint = i;
    }

    public void setFiltered(int f) {
        this.filtered = f;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public int getSB() {
        return this.sb;
    }

    public int getBB() {
        return this.bb;
    }

    public int getStraddle() {
        return this.straddle;
    }

    public int getBringIn() {
        return this.bringIn;
    }

    public int getAnte() {
        return this.ante;
    }

    public int getPerPoint() {
        return this.perPoint;
    }

    public int getFiltered() {
        return this.filtered;
    }
}