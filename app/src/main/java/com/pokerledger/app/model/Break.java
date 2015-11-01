package com.pokerledger.app.model;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Break {
    private int id = 0;
    private String startDate, startTime, endDate, endTime = "";

    //constructors
    public Break() {}

    public Break(String sd, String st, String ed, String et) {
        this(0, sd, st, ed, et);
    }

    public Break(int i, String sd, String st, String ed, String et) {
        this.id = i;
        this.startDate = sd;
        this.startTime = st;
        this.endDate = ed;
        this.endTime = et;
    }

    @Override
    public String toString() {
        return "start: " + this.startDate + " " + this.startTime + "\n  end: " + this.endDate + " " + this.endTime;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setStartDate(String s) {
        this.startDate = s;
    }

    public void setStartTime(String s) {
        this.startTime = s;
    }

    public void setEndDate(String s) {
        this.endDate = s;
    }

    public void setEndTime(String s) {
        this.endTime = s;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String getEndTime() {
        return this.endTime;
    }
}