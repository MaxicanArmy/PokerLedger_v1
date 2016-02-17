package com.pokerledger.app.helper;

import com.jjoe64.graphview.series.DataPoint;
import com.pokerledger.app.model.Session;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class SessionSet {
    private ArrayList<Session> sessions = new ArrayList<Session>();
    double profit = 0, minY = 0, maxY = 0;
    Long lengthMillis = 0L;
    ArrayList<DataPoint> dataPoints = new ArrayList<>();
    HashMap<String, SessionSet> children = new HashMap<>();

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

    public double getLengthSeconds() {
        return Math.round(this.lengthMillis.doubleValue() / 1000);
    }

    public double getLengthHours() {
        return this.getLengthSeconds() / 3600;
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

    public HashMap<String, SessionSet> getChildren() {
        return this.children;
    }

    //other
    public void addSession(Session s) {
        this.sessions.add(s);
        this.lengthMillis += s.lengthMillis();
        this.profit += s.getProfit();
        this.dataPoints.add(new DataPoint(this.getLengthHours(), this.profit));
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
            profitText = "($" + PLCommon.formatDouble(Math.abs(profit)) + ")";
        } else {
            profitText = "$" + PLCommon.formatDouble(profit);
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
        double time = getLengthHours();
        double hourly;
        String hourlyWage;

        if (time == 0) {
            hourly = 0;
        }
        else {
            hourly = this.profit / time;
        }

        if (hourly < 0 ) {
            hourlyWage = "($" + PLCommon.formatDouble(Math.abs(hourly)) + ")";
        } else {
            hourlyWage = "$" + PLCommon.formatDouble(hourly);
        }

        return hourlyWage;
    }

    public void createHierarchy(ArrayList<Method> levels) {
        /*
        levels needs to be ordered from lowest to highest (i.e. index 0 will be the lowest tier)
         */
        for (Session s : sessions) {
            try {
                String key = (String) levels.get(levels.size() - 1).invoke(s);

                if (children.containsKey(key)) {
                    children.get(key).addSession(s);
                } else {
                    children.put(key, new SessionSet(s));
                }
            } catch (Exception e) {
                //java blah blah
            }
        }

        ArrayList<Method> nextLevels = new ArrayList<>();

        for (Method m : levels) {
            nextLevels.add(m);
        }
        nextLevels.remove(nextLevels.size() - 1);

        if (nextLevels.size() > 0) {
            for (HashMap.Entry<String, SessionSet> entry : children.entrySet()) {
                entry.getValue().createHierarchy(nextLevels);
            }
        }
    }
}