package com.pokerledger.app.helper;

import android.provider.ContactsContract;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pokerledger.app.model.Session;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class SessionSet {
    private ArrayList<Session> sessions = new ArrayList<Session>();
    int profit = 0, minutesPlayed = 0, minY = 0, maxY = 0;
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

    public double getMinutesPlayed() {
        return this.minutesPlayed;
    }

    public double getHoursPlayed() {
        int hours = minutesPlayed / 60;
        double minutes = minutesPlayed % 60;

        double time = hours + (Math.round((minutes / 60.00) *100.0) / 100.0);

        return time;
    }

    public int getProfit() {
        return this.profit;
    }

    public DataPoint[] getDataPoints() {
        ArrayList<DataPoint> result = new ArrayList<>();
        result.add(new DataPoint(0, 0));
        result.addAll(this.dataPoints);
        return result.toArray(new DataPoint[result.size()]);
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.maxY;
    }

    //other
    public void addSession(Session s) {
        this.sessions.add(s);
        this.minutesPlayed += s.minutesPlayed();
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
            profitText = "($" + Integer.toString(Math.abs(profit)) + ")";
        } else {
            profitText = "$" + Integer.toString(profit);
        }

        return profitText;
    }

    public String timeFormatted() {
        int hours = minutesPlayed / 60;
        int minutes = minutesPlayed % 60;
        String timePlayed = "";

        if (hours > 0) {
            timePlayed += Integer.toString(hours) + "h ";
        }

        timePlayed += minutes + "m";

        return timePlayed;
    }

    public String wageFormatted() {
        double hourly;
        String hourlyWage;

        if (minutesPlayed == 0) {
            hourly = 0;
        }
        else {
            hourly = profit / ((double) minutesPlayed / 60);
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