package com.pokerledger.app.model;

public class Note {
    private int id = 0;
    private int sessionId = 0;
    private String note = "";

    //constructors
    public Note() {}

    public Note(int sid) {
        this.sessionId = sid;
    }

    public Note(int i, int sid, String s) {
        this.id = i;
        this.sessionId = sid;
        this.note = s;
    }

    @Override
    public String toString() {
        return this.note;
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setSessionId(int sid) {
        this.sessionId = sid;
    }

    public void setNote(String s) {
        this.note = s;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public String getNote() {
        return this.note;
    }
}
