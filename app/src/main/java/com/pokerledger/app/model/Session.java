package com.pokerledger.app.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Session {
    private int id, entrants, placed, buyIn, cashOut, state = 0;
    private String startDate, startTime, endDate, endTime, note = "";
    private Location location = new Location();
    private Game game = new Game();
    private GameFormat gameFormat = new GameFormat();
    private Blinds blinds = new Blinds();
    private ArrayList<Break> breaks = new ArrayList<>();

    //constructors
    public Session() {}

    public Session(String sd, String st, String ed, String et, int bi, int co, Game game, GameFormat gf, Location loc, int state) {
        this(0, sd, st, ed, et, bi, co, game, gf, loc, state);
    }

    public Session(int id, String sd, String st, String ed, String et, int bi, int co, Game game, GameFormat gf, Location loc, int state) {
        this.id = id;
        this.startDate = sd;
        this.startTime = st;
        this.endDate = ed;
        this.endTime = et;
        this.buyIn = bi;
        this.cashOut = co;
        this.game = game;
        this.gameFormat = gf;
        this.location = loc;
        this.state = state;
    }

    @Override
    public String toString() {
        return Integer.toString(this.id) + " " + this.startDate + " " + this.startTime + " " + this.location.getLocation();
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

    public void setBuyIn(int i) {
        this.buyIn = i;
    }

    public void setCashOut(int i) {
        this.cashOut = i;
    }

    public void setGame(Game i) {
        this.game = i;
    }

    public void setGameFormat(GameFormat gf) {
        this.gameFormat = gf;
    }

    public void setLocation(Location i) {
        this.location = i;
    }

    public void setState(int s) {
        this.state = s;
    }

    public void setEntrants(int e) {
        this.entrants = e;
    }

    public void setPlaced(int p) {
        this.placed = p;
    }

    public void setBlinds(Blinds b) {
        this.blinds = b;
    }

    public void setNote(String n) {
        this.note = n;
    }

    public void setBreaks(ArrayList<Break> b) {
        this.breaks = b;
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

    public int getBuyIn() {
        return this.buyIn;
    }

    public int getCashOut() {
        return this.cashOut;
    }

    public Game getGame() {
        return this.game;
    }

    public GameFormat getGameFormat() {
        return this.gameFormat;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getState () {
        return this.state;
    }

    public int getEntrants() {
        return this.entrants;
    }

    public int getPlaced() {
        return this.placed;
    }

    public Blinds getBlinds() {
        return this.blinds;
    }

    public String getNote() {
        return this.note;
    }

    public ArrayList<Break> getBreaks() {
        return this.breaks;
    }

    //other
    public boolean onBreak() {
        boolean flag = false;
        if (this.breaks.size() > 0) {
            Break current = this.breaks.get(this.breaks.size() - 1);
            if (current.getEndDate() == null || current.getEndDate().equals("")) {
                flag = true;
            }
        }

        return flag;
    }

    public void breakEnd() {
        Calendar cal = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("00");
        int position = this.breaks.size() - 1;

        this.breaks.get(position).setEndDate(cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)));
        this.breaks.get(position).setEndTime(df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE)));
    }

    public int minutesPlayed() {
        Calendar t1 = Calendar.getInstance();
        Calendar t2 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            t1.setTime(sdf.parse(this.startDate + " " + this.startTime));
            t2.setTime(sdf.parse(this.endDate + " " + this.endTime));
        } catch (Exception e) {
            //fucking parse exception needs to be handled
        }

        int minutes = (int) (t2.getTimeInMillis() - t1.getTimeInMillis())/60000;

        //start break time code
        if (this.breaks != null) {
            if (!this.breaks.isEmpty()) {
                int breakMinutes = 0;
                Calendar bs = Calendar.getInstance();
                Calendar be = Calendar.getInstance();
                for (Break b : this.breaks) {
                    try {
                        bs.setTime(sdf.parse(b.getStartDate() + " " + b.getStartTime()));
                        be.setTime(sdf.parse(b.getEndDate() + " " + b.getEndTime()));
                    } catch (Exception e) {
                        //make an alert message here i guess
                    }

                    breakMinutes += (int) (be.getTimeInMillis() - bs.getTimeInMillis()) / 60000;
                }
                minutes -= breakMinutes;
            }
        }
        //end break time code

        return minutes;
    }

    public double getProfit() {
        return this.cashOut - this.buyIn;
    }

    public String displayFormat() {
        String output = "";

        if (this.getGameFormat().getBaseFormatId() == 1) {
            output = this.getBlinds().toString() + " " + this.getGame().getGame();
        } else {
            output = this.getGame().getGame();
        }

        return output;
    }
}