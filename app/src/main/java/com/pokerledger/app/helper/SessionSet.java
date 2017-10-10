package com.pokerledger.app.helper;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.series.DataPoint;
import com.pokerledger.app.model.Break;
import com.pokerledger.app.model.Session;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class SessionSet {
    private ArrayList<Session> sessions = new ArrayList<>();
    double profit = 0, minY = 0, maxY = 0;
    Long lengthMillis = 0L;
    ArrayList<DataPoint> dataPoints = new ArrayList<>();
    HashMap<String, SessionSet> children = new HashMap<>();

    HashMap<Integer, Double> profitByDayOfWeek = new HashMap<>();

    public SessionSet() {
        profitByDayOfWeek.put(1,0.0);
        profitByDayOfWeek.put(2,0.0);
        profitByDayOfWeek.put(3,0.0);
        profitByDayOfWeek.put(4,0.0);
        profitByDayOfWeek.put(5,0.0);
        profitByDayOfWeek.put(6,0.0);
        profitByDayOfWeek.put(7,0.0);
    }

    public SessionSet(Session s) {
        this();
        this.addSession(s);
    }

    public SessionSet(ArrayList<Session> sessionList) {
        this();
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

    public double getWage() {
        double time = getLengthHours();
        double hourly;

        if (time == 0) {
            hourly = 0;
        }
        else {
            hourly = this.profit / time;
        }

        return hourly;
    }

    public DataPoint[] getDataPoints() {
        ArrayList<DataPoint> result = new ArrayList<>();
        result.add(new DataPoint(0, 0));
        result.addAll(this.dataPoints);
        return result.toArray(new DataPoint[result.size()]);
    }

    public DataPoint[] getDayOfWeekDataPoints() {
        ArrayList<DataPoint> result = new ArrayList<>();
        Iterator it = this.profitByDayOfWeek.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            result.add(new DataPoint((Integer) pair.getKey(),(Double) pair.getValue()));
            //it.remove(); // avoids a ConcurrentModificationException
        }
        return result.toArray(new DataPoint[result.size()]);
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMaxY() {
        return this.maxY;
    }

    public double getMinBarY() {
        Double minBarY = 0.0;
        Iterator it = this.profitByDayOfWeek.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            if ((Double) pair.getValue() < minBarY) {
                minBarY = (Double) pair.getValue();
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        minBarY = minBarY - (500 + (minBarY % 500));
        return minBarY;
    }

    public double getMaxBarY() {
        Double maxBarY = 0.0;
        Iterator it = this.profitByDayOfWeek.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            if ((Double) pair.getValue() > maxBarY) {
                maxBarY = (Double) pair.getValue();
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        maxBarY = maxBarY + (500 - (maxBarY % 500));
        return maxBarY;
    }

    public HashMap<String, SessionSet> getChildren() {
        return this.children;
    }

    //other
    public void addSession(Session s) {
        Double currentProfit = s.getProfit();
        this.sessions.add(s);
        this.lengthMillis += s.lengthMillis();
        this.profit += currentProfit;
        this.dataPoints.add(new DataPoint(this.getLengthHours(), this.profit));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(s.getStart());
        Integer day = cal.get(Calendar.DAY_OF_WEEK);
        if (this.profitByDayOfWeek.containsKey(day)) {
            this.profitByDayOfWeek.put(day, this.profitByDayOfWeek.get(day) + currentProfit);
        } else {
            this.profitByDayOfWeek.put(day, currentProfit);
        }

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
                FlurryAgent.logEvent("Error_InvokeReflection");
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

    public String exportCSV() {
        String csv = "POKERLEDGER CSV1.0\r\nbuy_in, cash_out, start_time, end_time, location, game, game_format, blinds, breaks, entrants, placed, state, note, filtered\r\n";

        for (Session current : sessions) {
            csv += "\"" + Double.toString(current.getBuyIn()) + "\",\"" +
                    Double.toString(current.getCashOut()) + "\",\"" +
                    PLCommon.timestampToDatetime(current.getStart()) + "\",\"";

            if (current.getEnd() != 0)
                csv += PLCommon.timestampToDatetime(current.getEnd());
            csv += "\",\"" +
                    current.getLocation().toString() + "\",\"" +
                    current.getGame().toString() + "\",\"" +
                    current.getGameFormat().toString() + "\",\"" +
                    current.getBlinds().toStringCSV() + "\",\"";

            String tempBreaks = "";
            if (current.getBreaks().size() > 0) {
                for (Break b : current.getBreaks()) {
                    if (!tempBreaks.equals(""))
                        tempBreaks += "^";
                    tempBreaks += PLCommon.timestampToDatetime(b.getStart()) + "/" + PLCommon.timestampToDatetime(b.getEnd());
                }
            }
            csv += tempBreaks + "\",\"" + Integer.toString(current.getEntrants()) + "\",\"" +
                    Integer.toString(current.getPlaced()) + "\",\"";
            csv += (current.getState() == 0) ? "Finished" : "Active";
            csv += "\",\"" + current.getNote() + "\",\"";
            csv += (current.getFiltered() == 0) ? "No" : "Yes";
            csv += "\"\r\n";
        }
        return csv;
    }
}