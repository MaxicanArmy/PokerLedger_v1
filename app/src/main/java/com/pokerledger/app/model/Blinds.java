package com.pokerledger.app.model;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Blinds {
    private int id, filtered = 0;
    private double sb, bb, straddle, bringIn, ante, perPoint = 0.00;

    //constructors
    public Blinds() {}

    public Blinds(double sb, double bb, double str, double bi, double a, double pp) {
        this.sb = sb;
        this.bb = bb;
        this.straddle = str;
        this.bringIn = bi;
        this.ante = a;
        this.perPoint = pp;
    }

    public Blinds(double sb, double bb, double str, double bi, double a, double pp, int f) {
        this.sb = sb;
        this.bb = bb;
        this.straddle = str;
        this.bringIn = bi;
        this.ante = a;
        this.perPoint = pp;
        this.filtered = f;
    }

    public Blinds(int id, double sb, double bb, double str, double bi, double a, double pp, int f) {
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

    public void setSB(double i) {
        this.sb = i;
    }

    public void setBB(double i) {
        this.bb = i;
    }

    public void setStraddle(double i) {
        this.straddle = i;
    }

    public void setBringIn(double i) {
        this.bringIn = i;
    }

    public void setAnte(double i) {
        this.ante = i;
    }

    public void setPerPoint(double i) {
        this.perPoint = i;
    }

    public void setFiltered(int f) {
        this.filtered = f;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public double getSB() {
        return this.sb;
    }

    public double getBB() {
        return this.bb;
    }

    public double getStraddle() {
        return this.straddle;
    }

    public double getBringIn() {
        return this.bringIn;
    }

    public double getAnte() {
        return this.ante;
    }

    public double getPerPoint() {
        return this.perPoint;
    }

    public int getFiltered() {
        return this.filtered;
    }
}