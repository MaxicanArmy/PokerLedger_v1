package com.pokerledger.app.helper;

import com.jjoe64.graphview.series.DataPoint;
import com.pokerledger.app.model.Session;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class SessionSet {
    private ArrayList<Session> sessions = new ArrayList<Session>();
    double profit = 0, minY = 0, maxY = 0;
    Long lengthMillis = 0L;
    ArrayList<DataPoint> dataPoints = new ArrayList<>();

    public SessionSet() {}

    public SessionSet(Session s) {
        this.addSession(s);
    }

    public SessionSet(ArrayList<Session> sessionList) {
        for (Session s : sessionList) {
            this.addSession(s);
        }
    }

    //getters
    public ArrayList<Session> getSessions() {
        return this.sessions;
    }

    public Long getLengthMillis() {
        return this.lengthMillis;
    }

    public Long getLengthMinutes() {
        return this.lengthMillis / (1000 * 60);
    }

    public double getHoursPlayed() {
        int minutesPlayed = this.getLengthMinutes().intValue();
        int hours = minutesPlayed / 60;
        double minutes = minutesPlayed % 60;

        double time = hours + (Math.round((minutes / 60.00) * 100.0) / 100.0);

        return time;
    }

    public double getProfit() {
        return this.profit;
    }

    public DataPoint[] getDataPoints() {
        ArrayList<DataPoint> result = new ArrayList<>();
        result.add(new DataPoint(0, 0));
        result.addAll(this.dataPoints);
        return result.toArray(new DataPoint[result.size()]);
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMaxY() {
        return this.maxY;
    }

    //other
    public void addSession(Session s) {
        this.sessions.add(s);
        this.lengthMillis += s.lengthMillis();
        this.profit += s.getProfit();
        this.dataPoints.add(new DataPoint(this.getHoursPlayed(), this.profit));
        if (this.profit > maxY) {
            maxY = this.profit;
        }
        if (this.profit < minY) {
            minY = this.profit;
        }
    }

    public String profitFormatted() {
        String profitText;
        if (profit < 0 ) {
            profitText = "($" + Double.toString(Math.abs(profit)) + ")";
        } else {
            profitText = "$" + Double.toString(profit);
        }

        return profitText;
    }

    public String lengthFormatted() {
        Long length = lengthMillis;
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

    public String wageFormatted() {
        double time = getHoursPlayed();
        double hourly;
        String hourlyWage;

        if (time == 0) {
            hourly = 0;
        }
        else {
            hourly = this.profit / time;
        }

        DecimalFormat df = new DecimalFormat("0.00");

        if (hourly < 0 ) {
            hourlyWage = "($" + df.format(Math.abs(hourly)) + ")";
        } else {
            hourlyWage = "$" + df.format(hourly);
        }

        return hourlyWage;
    }
}