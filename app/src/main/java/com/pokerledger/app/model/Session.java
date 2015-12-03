package com.pokerledger.app.model;

import com.pokerledger.app.helper.PLCommon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/25/15.
 */
public class Session {
    private int id = 0, entrants = 0, placed = 0, state = 0;
    private double buyIn = 0, cashOut = 0;
    private String note = "";
    private Long start = 0L, end = 0L;
    private Location location = new Location();
    private Game game = new Game();
    private GameFormat gameFormat = new GameFormat();
    private Blinds blinds = new Blinds();
    private ArrayList<Break> breaks = new ArrayList<>();

    //constructors
    public Session() {}

    public Session(Long s, Long e, double bi, double co, Game game, GameFormat gf, Location loc, int state) {
        this(0, s, e, bi, co, game, gf, loc, state);
    }

    public Session(int id, Long s, Long e, double bi, double co, Game game, GameFormat gf, Location loc, int state) {
        this.id = id;
        this.start = s;
        this.end = e;
        this.buyIn = bi;
        this.cashOut = co;
        this.game = game;
        this.gameFormat = gf;
        this.location = loc;
        this.state = state;
    }

    @Override
    public String toString() {
        return Integer.toString(this.id) + " " + this.start + " " + this.start + " " + this.location.getLocation();
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

    public void setBuyIn(double i) {
        this.buyIn = i;
    }

    public void setCashOut(double i) {
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

    public Long getStart() {
        return this.start;
    }

    public Long getEnd() {
        return this.end;
    }

    public double getBuyIn() {
        return this.buyIn;
    }

    public double getCashOut() {
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
            if (current.getEnd() == null || current.getEnd() == 0L) {
                flag = true;
            }
        }

        return flag;
    }

    public void breakEnd() {
        Calendar cal = Calendar.getInstance();
        int position = this.breaks.size() - 1;

        this.breaks.get(position).setEnd(cal.getTimeInMillis());
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

        Long breakTotal = 0L;

        for (Break b : this.breaks) {
            breakTotal += b.lengthMillis();
        }

        return ((endTime - this.start - breakTotal) / 1000) * 1000;
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

    public String profitFormatted() {
        String profitText;
        double profit = this.getProfit();
        if (profit < 0 ) {
            profitText = "($" + PLCommon.formatDouble(Math.abs(profit)) + ")";
        } else {
            profitText = "$" + PLCommon.formatDouble(profit);
        }

        return profitText;
    }

    public double getProfit() {
        return this.cashOut - this.buyIn;
    }

    public String displayFormat() {
        String output;

        if (this.getGameFormat().getBaseFormatId() == 1) {
            output = this.getBlinds().toString() + " " + this.getGame().getGame();
        } else {
            output = this.getGame().getGame();
        }

        return output;
    }
}