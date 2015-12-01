package com.pokerledger.app.model;

import com.pokerledger.app.helper.PLCommon;

import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Break {
    private int id = 0;
    private Long start, end = 0L;

    //constructors
    public Break() {}

    public Break(Long s, Long e) {
        this(0, s, e);
    }

    public Break(int i, Long s, Long e) {
        this.id = i;
        this.start = s;
        this.end = e;
    }

    @Override
    public String toString() {
        return "start: " + PLCommon.timestampToDatetime(this.start) + "\n  end: " + PLCommon.timestampToDatetime(this.end);
    }

    //setters
    public void setId(int i) {
        this.id = i;
    }

    public void setStart(Long l) {
        this.start = l;
    }

    public void setEnd(Long l) {
        this.end = l;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public Long getStart() {
        return this.start;
    }

    public Long getEnd() {
        return this.end;
    }

    public Long lengthMillis() {
        Calendar cal = Calendar.getInstance();
        Long endTime;

        if (end == 0L) {
            endTime = cal.getTimeInMillis();
        }
        else {
            endTime = end;
        }

        return endTime - this.start;
    }

    public String lengthFormatted() {
        Long length = lengthMillis();
        Long seconds = (length / 1000) % 60;
        Long minutes = (length / (1000 * 60)) % 60;
        Long hours = length / (1000 * 60 * 60);

        String timePlayed = "";

        if (hours > 0) {
            timePlayed += Long.toString(hours) + "h ";
        }

        if (hours > 0 || minutes > 0) {
            timePlayed += minutes + "m ";
        }

        timePlayed += seconds + "s";

        return timePlayed;
    }
}