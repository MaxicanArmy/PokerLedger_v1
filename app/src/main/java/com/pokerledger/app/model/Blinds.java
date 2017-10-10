package com.pokerledger.app.model;

import com.pokerledger.app.helper.PLCommon;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Blinds {
    private int id = 0, filtered = 0;
    private double sb = 0, bb = 0, straddle = 0, bringIn = 0, ante = 0, perPoint = 0;

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
            blinds += "$" + PLCommon.formatDouble(sb) + "/$" + PLCommon.formatDouble(bb);
        }
        else if (sb != 0) {
            blinds += "$" + PLCommon.formatDouble(sb) + " blind";
        }

        if (straddle != 0) {
            blinds += "/$" + PLCommon.formatDouble(straddle);
        }

        if (bringIn != 0) {
            blinds += " w/$" + PLCommon.formatDouble(bringIn) + " bring in";
        }

        if (ante != 0) {
            blinds += " w/$" + PLCommon.formatDouble(ante) + " ante";
        }

        if (perPoint != 0) {
            blinds += "$" + PLCommon.formatDouble(perPoint) + "/point";
        }

        return blinds;
    }

    public String toStringCSV() {
        String blinds = PLCommon.formatDouble(sb) + "^" +
                PLCommon.formatDouble(bb) + "^" +
                PLCommon.formatDouble(straddle) + "^" +
                PLCommon.formatDouble(bringIn) + "^" +
                PLCommon.formatDouble(ante) + "^" +
                PLCommon.formatDouble(perPoint);

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