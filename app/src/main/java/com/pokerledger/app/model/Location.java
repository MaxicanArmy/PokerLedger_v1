package com.pokerledger.app.model;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Location {
    private int id = 0;
    private String location = "";
    private int filtered = 0;

    //constructors
    public Location() {}

    public Location(int i, String s) {
        this.id = i;
        this.location = s;
    }

    public Location(int i, String s, int f) {
        this.id = i;
        this.location = s;
        this.filtered = f;
    }

    @Override
    public String toString() {
        return this.location;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setLocation(String s) {
        this.location = s;
    }

    public void setFiltered(int f) {
        this.filtered = f;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getLocation() {
        return this.location;
    }

    public int getFiltered() {
        return this.filtered;
    }
}