package com.pokerledger.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flurry.android.FlurryAgent;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Break;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/25/15.
 * DatabseHelper is a buffer class for all interactions with the sqlite database
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;
    //database name
    private static final String DATABASE_NAME = "sessionManager";

    //database version
    private static final int DATABASE_VERSION = 4;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static synchronized void resetDatabaseHelper() {
        sInstance = null;
    }


    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //table names
    private static final String TABLE_SESSION = "sessions";
    private static final String TABLE_LOCATION = "locations";
    private static final String TABLE_GAME = "games";
    private static final String TABLE_NOTE = "notes";
    private static final String TABLE_BREAK = "breaks";
    private static final String TABLE_GAME_FORMAT = "game_formats";
    private static final String TABLE_BASE_FORMAT = "base_formats";
    private static final String TABLE_CASH = "cash";
    private static final String TABLE_BLINDS = "blinds";
    private static final String TABLE_TOURNAMENT = "tournament";

    //common column names
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_START = "start";
    private static final String KEY_END = "end";
    private static final String KEY_START_DATE = "start_date"; //deprecated, but needed for onUpgrade
    private static final String KEY_START_TIME = "start_time"; //deprecated, but needed for onUpgrade
    private static final String KEY_END_DATE = "end_date"; //deprecated, but needed for onUpgrade
    private static final String KEY_END_TIME = "end_time"; //deprecated, but needed for onUpgrade
    private static final String KEY_GAME = "game";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_FILTERED = "filtered";
    private static final String KEY_GAME_FORMAT = "game_format";
    private static final String KEY_BASE_FORMAT = "base_format";

    //BLINDS table - column names
    private static final String KEY_BLIND_ID = "blind_id";
    private static final String KEY_SMALL_BLIND = "sb";
    private static final String KEY_BIG_BLIND = "bb";
    private static final String KEY_STRADDLE = "straddle";
    private static final String KEY_BRING_IN = "bring_in";
    private static final String KEY_ANTE = "ante";
    private static final String KEY_PER_POINT = "per_point";

    //BREAKS table - column names
    private static final String KEY_BREAK_ID = "break_id";

    //CASH table - column names
    private static final String KEY_BLINDS = "blinds";

    //GAMES table - column names
    private static final String KEY_GAME_ID = "game_id";

    //LOCATIONS table - column names
    private static final String KEY_LOCATION_ID = "location_id";

    //NOTES table
    private static final String KEY_NOTE = "note";

    //SESSION table - column names
    private static final String KEY_BUY_IN = "buy_in";
    private static final String KEY_CASH_OUT = "cash_out";
    private static final String KEY_STATE = "state";

    //TOURNAMENT table - column names
    private static final String KEY_ENTRANTS = "entrants";
    private static final String KEY_PLACED = "placed";

    //GAME FORMAT table - column names
    private static final String KEY_GAME_FORMAT_ID = "game_format_id";

    //BASE FORMAT table - column names
    private static final String KEY_BASE_FORMAT_ID = "base_format_id";

    //create statements for tables

    //SESSIONS
    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSION + " (" + KEY_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_START + " INTEGER, " + KEY_END + " INTEGER, " + KEY_BUY_IN + " REAL, " + KEY_CASH_OUT + " REAL, " + KEY_GAME + " INTEGER, " +
            KEY_GAME_FORMAT + " INTEGER, " + KEY_LOCATION + " INTEGER, " + KEY_STATE + " INTEGER, " + KEY_FILTERED + " INTEGER);";

    //GAMES
    private static final String CREATE_TABLE_GAMES = "CREATE TABLE " + TABLE_GAME + " (" + KEY_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_GAME + " VARCHAR(40), " + KEY_FILTERED + " INTEGER);";

    //GAME FORMATS
    private static final String CREATE_TABLE_GAME_FORMATS = "CREATE TABLE " + TABLE_GAME_FORMAT + " (" + KEY_GAME_FORMAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_GAME_FORMAT + " VARCHAR(50), " + KEY_BASE_FORMAT + " INTEGER, " + KEY_FILTERED + " INTEGER);";

    //LOCATIONS
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATION + " (" + KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCATION + " VARCHAR(40), " + KEY_FILTERED + " INTEGER);";

    //BLINDS
    private static final String CREATE_TABLE_BLINDS = "CREATE TABLE " + TABLE_BLINDS + " (" + KEY_BLIND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SMALL_BLIND + " REAL, " + KEY_BIG_BLIND + " REAL, " + KEY_STRADDLE + " REAL, " + KEY_BRING_IN + " REAL, " +
            KEY_ANTE + " REAL, " + KEY_PER_POINT + " REAL, " + KEY_FILTERED + " INTEGER)";

    //BREAKS
    private static final String CREATE_TABLE_BREAKS = "CREATE TABLE " + TABLE_BREAK + " (" + KEY_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SESSION_ID + " INTEGER, " + KEY_START + " INTEGER, " + KEY_END + " INTEGER);";

    //NOTES
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTE + " (" + KEY_SESSION_ID + " INTEGER, " + KEY_NOTE + " TEXT);";

    //CASH
    private static final String CREATE_TABLE_CASH = "CREATE TABLE " + TABLE_CASH + " (" + KEY_SESSION_ID + " INTEGER UNIQUE, " + KEY_BLINDS + " INTEGER);";

    //TOURNAMENT
    private static final String CREATE_TABLE_TOURNAMENT = "CREATE TABLE " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + " INTEGER UNIQUE, " + KEY_ENTRANTS + " INTEGER, "
            + KEY_PLACED + " INTEGER);";

    //BASE FORMATS
    private static final String CREATE_TABLE_BASE_FORMATS = "CREATE TABLE " + TABLE_BASE_FORMAT + " (" + KEY_BASE_FORMAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_BASE_FORMAT + " VARCHAR(20));";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SESSIONS);
        db.execSQL(CREATE_TABLE_BREAKS);
        db.execSQL(CREATE_TABLE_CASH);
        db.execSQL(CREATE_TABLE_GAMES);
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_NOTES);
        db.execSQL(CREATE_TABLE_TOURNAMENT);
        db.execSQL(CREATE_TABLE_BLINDS);
        db.execSQL(CREATE_TABLE_GAME_FORMATS);
        db.execSQL(CREATE_TABLE_BASE_FORMATS);

        ContentValues values;

        //populate game table
        values = new ContentValues();
        values.put(KEY_GAME, "No Limit Hold'em");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Pot Limit Omaha");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Pot Limit Omaha HiLo");
        db.insert(TABLE_GAME, null, values);

        //populate base_formats table
        values = new ContentValues();
        values.put(KEY_BASE_FORMAT, "Cash Game");
        db.insert(TABLE_BASE_FORMAT, null, values);
        values = new ContentValues();
        values.put(KEY_BASE_FORMAT, "Tournament");
        db.insert(TABLE_BASE_FORMAT, null, values);

        //populate game_formats table
        values = new ContentValues();
        values.put(KEY_GAME_FORMAT, "Full Ring");
        values.put(KEY_BASE_FORMAT, 1);
        values.put(KEY_FILTERED, 0);
        db.insert(TABLE_GAME_FORMAT, null, values);
        values = new ContentValues();
        values.put(KEY_GAME_FORMAT, "Full Ring");
        values.put(KEY_BASE_FORMAT, 2);
        values.put(KEY_FILTERED, 0);
        db.insert(TABLE_GAME_FORMAT, null, values);

        //populate blinds table
        values = new ContentValues();
        values.put(KEY_SMALL_BLIND, 1);
        values.put(KEY_BIG_BLIND, 2);
        values.put(KEY_STRADDLE, 0);
        values.put(KEY_ANTE, 0);
        values.put(KEY_BRING_IN, 0);
        values.put(KEY_PER_POINT, 0);
        values.put(KEY_FILTERED, 0);
        db.insert(TABLE_BLINDS, null, values);

        values = new ContentValues();
        values.put(KEY_SMALL_BLIND, 2);
        values.put(KEY_BIG_BLIND, 5);
        values.put(KEY_STRADDLE, 0);
        values.put(KEY_ANTE, 0);
        values.put(KEY_BRING_IN, 0);
        values.put(KEY_PER_POINT, 0);
        values.put(KEY_FILTERED, 0);
        db.insert(TABLE_BLINDS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.beginTransaction();
            try {
                //1. Add columns start, end to tables sessions and breaks
                db.execSQL("ALTER TABLE " + TABLE_SESSION + " ADD COLUMN " + KEY_START + " INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_SESSION + " ADD COLUMN " + KEY_END + " INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_BREAK + " ADD COLUMN " + KEY_START + " INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_BREAK + " ADD COLUMN " + KEY_END + " INTEGER");

                //2. Select all rows from table breaks
                Cursor c = db.rawQuery("SELECT * FROM " + TABLE_BREAK, null);

                //3. process date/times
                //4. insert timestamps
                if (c.moveToFirst()) {
                    do {
                        db.execSQL("UPDATE " + TABLE_BREAK + " SET " + KEY_START + "=" + PLCommon.datetimeToTimestamp(c.getString(c.getColumnIndex(KEY_START_DATE)) + " " +
                                c.getString(c.getColumnIndex(KEY_START_TIME))) + ", " + KEY_END + "=" + PLCommon.datetimeToTimestamp(c.getString(c.getColumnIndex(KEY_END_DATE)) + " " +
                                c.getString(c.getColumnIndex(KEY_END_TIME))) + " WHERE " + KEY_BREAK_ID + "=" + c.getInt(c.getColumnIndex(KEY_BREAK_ID)));

                    } while (c.moveToNext());
                }
                c.close();

                //5. select all rows from table sessions
                c = db.rawQuery("SELECT * FROM " + TABLE_SESSION, null);

                //6. process date/times
                //7. insert timestamps
                if (c.moveToFirst()) {
                    do {
                        db.execSQL("UPDATE " + TABLE_SESSION + " SET " + KEY_START + "=" + PLCommon.datetimeToTimestamp(c.getString(c.getColumnIndex(KEY_START_DATE)) + " " +
                                c.getString(c.getColumnIndex(KEY_START_TIME))) + ", " + KEY_END + "=" + PLCommon.datetimeToTimestamp(c.getString(c.getColumnIndex(KEY_END_DATE)) + " " +
                                c.getString(c.getColumnIndex(KEY_END_TIME))) + " WHERE " + KEY_SESSION_ID + "=" + c.getInt(c.getColumnIndex(KEY_SESSION_ID)));
                    } while (c.moveToNext());
                }
                c.close();

                //8. create temp_session table that leaves off old columns
                db.execSQL("CREATE TABLE temp_sessions (" + KEY_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_START + " INTEGER, " + KEY_END + " INTEGER, " +
                        KEY_BUY_IN + " INTEGER, " + KEY_CASH_OUT + " INTEGER, " + KEY_GAME + " INTEGER, " + KEY_GAME_FORMAT + " INTEGER, " + KEY_LOCATION + " INTEGER, " +
                        KEY_STATE + " INTEGER, " + KEY_FILTERED + " INTEGER);");

                //9. copy session to temp_session
                db.execSQL("INSERT INTO temp_sessions (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " +
                        KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME + ", " + KEY_GAME_FORMAT + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " +
                        KEY_FILTERED + ") SELECT " + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " + KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME + ", " +
                        KEY_GAME_FORMAT + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " + KEY_FILTERED + " FROM sessions;");

                //10. delete session table
                db.execSQL("DROP TABLE " + TABLE_SESSION);

                //11. rename temp_tables to sessions
                db.execSQL("ALTER TABLE temp_sessions RENAME TO " + TABLE_SESSION);

                //12. create temp_break that leaves off old columns
                db.execSQL("CREATE TABLE temp_breaks (" + KEY_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_SESSION_ID + " INTEGER, " + KEY_START + " INTEGER, " + KEY_END + " INTEGER);");

                //13. copy breaks to temp_break
                db.execSQL("INSERT INTO temp_breaks (" + KEY_BREAK_ID + ", " + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") SELECT " +
                        KEY_BREAK_ID + ", " + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + " FROM breaks;");

                //14. delete breaks table
                db.execSQL("DROP TABLE " + TABLE_BREAK);

                //15. rename temp_break to breaks
                db.execSQL("ALTER TABLE temp_breaks RENAME TO " + TABLE_BREAK);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        if (oldVersion < 3) {
            db.beginTransaction();
            //1. create temp_tables with column values REAL
            db.execSQL("CREATE TABLE temp_sessions (" + KEY_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_START + " INTEGER, " + KEY_END + " INTEGER, " + KEY_BUY_IN + " REAL, " + KEY_CASH_OUT + " REAL, " + KEY_GAME + " INTEGER, " +
                    KEY_GAME_FORMAT + " INTEGER, " + KEY_LOCATION + " INTEGER, " + KEY_STATE + " INTEGER, " + KEY_FILTERED + " INTEGER);");

            db.execSQL("CREATE TABLE temp_blinds (" + KEY_BLIND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SMALL_BLIND + " REAL, " + KEY_BIG_BLIND + " REAL, " + KEY_STRADDLE + " REAL, " + KEY_BRING_IN + " REAL, " +
                    KEY_ANTE + " REAL, " + KEY_PER_POINT + " REAL, " + KEY_FILTERED + " INTEGER)");

            //2. copy data to temp_tables
            db.execSQL("INSERT INTO temp_sessions (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " +
                    KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME + ", " + KEY_GAME_FORMAT + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " +
                    KEY_FILTERED + ") SELECT " + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " + KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME + ", " +
                    KEY_GAME_FORMAT + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " + KEY_FILTERED + " FROM sessions;");

            db.execSQL("INSERT INTO temp_blinds (" + KEY_BLIND_ID + ", " + KEY_SMALL_BLIND + ", " + KEY_BIG_BLIND + ", " + KEY_STRADDLE + ", " + KEY_BRING_IN + ", " +
                    KEY_ANTE + ", " + KEY_PER_POINT + ", " + KEY_FILTERED + ") SELECT " + KEY_BLIND_ID + ", " + KEY_SMALL_BLIND + ", " + KEY_BIG_BLIND + ", " + KEY_STRADDLE + ", " + KEY_BRING_IN + ", " +
                    KEY_ANTE + ", " + KEY_PER_POINT + ", " + KEY_FILTERED + " FROM blinds;");

            //3. delete originals
            db.execSQL("DROP TABLE " + TABLE_SESSION);
            db.execSQL("DROP TABLE " + TABLE_BLINDS);

            //4. rename temp_tables
            db.execSQL("ALTER TABLE temp_sessions RENAME TO " + TABLE_SESSION);
            db.execSQL("ALTER TABLE temp_blinds RENAME TO " + TABLE_BLINDS);
            db.setTransactionSuccessful();
            db.endTransaction();

        }
        if (oldVersion < 4) {
            db.beginTransaction();
            db.execSQL("UPDATE " + TABLE_BLINDS + " SET " + KEY_STRADDLE + "=0, " + KEY_BRING_IN + "=0, " + KEY_ANTE + "=0, " + KEY_PER_POINT + "=0 WHERE " + KEY_SMALL_BLIND + "=1 AND " +
                    KEY_BIG_BLIND + "=2 AND " + KEY_STRADDLE + " IS NULL AND " + KEY_ANTE + " IS NULL AND " + KEY_BRING_IN + " IS NULL AND " + KEY_PER_POINT + " IS NULL;");
            db.execSQL("UPDATE " + TABLE_BLINDS + " SET " + KEY_STRADDLE + "=0, " + KEY_BRING_IN + "=0, " + KEY_ANTE + "=0, " + KEY_PER_POINT + "=0 WHERE " + KEY_SMALL_BLIND + "=2 AND " +
                    KEY_BIG_BLIND + "=5 AND " + KEY_STRADDLE + " IS NULL AND " + KEY_ANTE + " IS NULL AND " + KEY_BRING_IN + " IS NULL AND " + KEY_PER_POINT + " IS NULL;");
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public ArrayList<Location> getAllLocations(String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        ArrayList<Location> locations = new ArrayList<>();

        Cursor c = db.query(TABLE_LOCATION, null, null, null, null, null, KEY_LOCATION + " ASC");

        if (c.moveToFirst()) {
            do {
                Location l = new Location();
                l.setId(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)));
                l.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                l.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                locations.add(l);
            } while (c.moveToNext());
        }
        c.close();

        if (order != null && order.equals("frequency")) {
            Cursor sessionCursor = db.rawQuery("SELECT " + KEY_LOCATION + ", count(" + KEY_LOCATION + ") AS total FROM " +
                    TABLE_SESSION + " WHERE " + KEY_FILTERED + " != -1 GROUP BY " + KEY_LOCATION + " ORDER BY total DESC;", null);

            if (sessionCursor.moveToFirst()) {
                int locationID;
                int count = 0;
                do {
                    locationID = sessionCursor.getInt(sessionCursor.getColumnIndex(KEY_LOCATION));
                    for (int i = 0; i < locations.size(); i++) {
                        Location temp = locations.get(i);
                        if (locationID == temp.getId()) {
                            locations.remove(i);
                            locations.add(count, temp);
                        }
                    }

                    count ++;
                } while (sessionCursor.moveToNext());
            }
            sessionCursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return locations;
    }

    public ArrayList<Game> getAllGames(String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        ArrayList<Game> games = new ArrayList<>();

        Cursor c = db.query(TABLE_GAME, null, null, null, null, null, KEY_GAME + " ASC");

        //loop through rows and add to games array if any results returned
        if (c.moveToFirst()) {
            do {
                Game g = new Game();
                g.setId(c.getInt(c.getColumnIndex(KEY_GAME_ID)));
                g.setGame(c.getString(c.getColumnIndex(KEY_GAME)));
                g.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                games.add(g);
            } while (c.moveToNext());
        }
        c.close();

        if (order != null && order.equals("frequency")) {
            Cursor sessionCursor = db.rawQuery("SELECT " + KEY_GAME + ", count(" + KEY_GAME + ") AS total FROM " +
                    TABLE_SESSION + " WHERE " + KEY_FILTERED + " != -1 GROUP BY " + KEY_GAME + " ORDER BY total DESC;", null);

            if (sessionCursor.moveToFirst()) {
                int gameID;
                int count = 0;
                do {
                    gameID = sessionCursor.getInt(sessionCursor.getColumnIndex(KEY_GAME));
                    for (int i = 0; i < games.size(); i++) {
                        Game temp = games.get(i);
                        if (gameID == temp.getId()) {
                            games.remove(i);
                            games.add(count, temp);
                        }
                    }

                    count ++;
                } while (sessionCursor.moveToNext());
            }
            sessionCursor.close();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return games;
    }

    public ArrayList<GameFormat> getAllGameFormats(String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        ArrayList<GameFormat> formats = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT " + KEY_GAME_FORMAT_ID + ", " + KEY_GAME_FORMAT + ", " + KEY_FILTERED + ", " + KEY_BASE_FORMAT_ID + ", " +
                TABLE_BASE_FORMAT + "." + KEY_BASE_FORMAT + " FROM " + TABLE_GAME_FORMAT + " INNER JOIN " + TABLE_BASE_FORMAT + " ON " +
                TABLE_GAME_FORMAT + "." + KEY_BASE_FORMAT + "=" + TABLE_BASE_FORMAT + "." + KEY_BASE_FORMAT_ID + " ORDER BY " + KEY_BASE_FORMAT_ID + " ASC", null);

        //loop through rows and add to location array if any results returned
        if (c.moveToFirst()) {
            do {
                GameFormat gf = new GameFormat();
                gf.setId(c.getInt(c.getColumnIndex(KEY_GAME_FORMAT_ID)));
                gf.setGameFormat(c.getString(c.getColumnIndex(KEY_GAME_FORMAT)));
                gf.setBaseFormatId(c.getInt(c.getColumnIndex(KEY_BASE_FORMAT_ID)));
                gf.setBaseFormat(c.getString(c.getColumnIndex(KEY_BASE_FORMAT)));
                gf.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                formats.add(gf);
            } while (c.moveToNext());
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return formats;
    }

    public ArrayList<Blinds> getAllBlinds(String order) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        ArrayList<Blinds> blinds = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BLINDS + " ORDER BY " + KEY_PER_POINT + " ASC," + KEY_BIG_BLIND + " ASC, " + KEY_SMALL_BLIND + " ASC, " +
                KEY_STRADDLE + " ASC, " + KEY_ANTE + " ASC, " + KEY_BRING_IN + " ASC;";

        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to location array if any results returned
        if (c.moveToFirst()) {
            do {
                Blinds b = new Blinds();
                b.setId(c.getInt(c.getColumnIndex(KEY_BLIND_ID)));
                b.setSB(c.getDouble(c.getColumnIndex(KEY_SMALL_BLIND)));
                b.setBB(c.getDouble(c.getColumnIndex(KEY_BIG_BLIND)));
                b.setStraddle(c.getDouble(c.getColumnIndex(KEY_STRADDLE)));
                b.setBringIn(c.getDouble(c.getColumnIndex(KEY_BRING_IN)));
                b.setAnte(c.getDouble(c.getColumnIndex(KEY_ANTE)));
                b.setPerPoint(c.getDouble(c.getColumnIndex(KEY_PER_POINT)));
                b.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                blinds.add(b);
            } while (c.moveToNext());
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return blinds;
    }

    public Location createLocation(Location loc) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Cursor c = db.query(TABLE_LOCATION, null, KEY_LOCATION + "=?", new String[]{loc.getLocation()}, null, null, null);

        if (c.moveToFirst()) {
            loc.setId(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)));
        }
        else {
            ContentValues locationValues = new ContentValues();
            locationValues.put(KEY_LOCATION, loc.getLocation());
            locationValues.put(KEY_FILTERED, 0);

            long locationId = db.insert(TABLE_LOCATION, null, locationValues);

            if (locationId != -1) {
                loc.setId((int) locationId);
            }
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return loc;
    }

    public Game createGame(Game g) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Cursor c = db.query(TABLE_GAME, null, KEY_GAME + "=?", new String[]{g.getGame()}, null, null, null);

        if (c.moveToFirst()) {
            g.setId(c.getInt(c.getColumnIndex(KEY_GAME_ID)));
        }
        else {
            ContentValues gameValues = new ContentValues();
            gameValues.put(KEY_GAME, g.getGame());
            gameValues.put(KEY_FILTERED, 0);

            long gameId = db.insert(TABLE_GAME, null, gameValues);

            if (gameId != -1) {
                g.setId((int) gameId);
            }
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return g;
    }

    public GameFormat createGameFormat(GameFormat g) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Cursor c = db.query(TABLE_GAME_FORMAT, null, KEY_GAME_FORMAT + "=? AND " + KEY_BASE_FORMAT + "=?", new String[]{g.getGameFormat(), Integer.toString(g.getBaseFormatId())}, null, null, null);

        if (c.moveToFirst()) {
            g.setId(c.getInt(c.getColumnIndex(KEY_GAME_FORMAT_ID)));
        }
        else {
            ContentValues gameFormatValues = new ContentValues();
            gameFormatValues.put(KEY_GAME_FORMAT, g.getGameFormat());
            gameFormatValues.put(KEY_BASE_FORMAT, g.getBaseFormatId());
            gameFormatValues.put(KEY_FILTERED, 0);

            long gameFormatId = db.insert(TABLE_GAME_FORMAT, null, gameFormatValues);

            if (gameFormatId != -1) {
                g.setId((int) gameFormatId);
            }
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return g;
    }

    public Blinds createBlinds(Blinds blindSet) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Cursor c = db.query(TABLE_BLINDS, null, KEY_SMALL_BLIND + "=? AND " + KEY_BIG_BLIND + "=? AND " + KEY_STRADDLE + "=? AND " + KEY_BRING_IN + "=? AND " +
                KEY_ANTE + "=? AND " + KEY_PER_POINT + "=?", new String[] {Double.toString(blindSet.getSB()), Double.toString(blindSet.getBB()),
                Double.toString(blindSet.getStraddle()), Double.toString(blindSet.getBringIn()), Double.toString(blindSet.getAnte()),
                Double.toString(blindSet.getPerPoint())}, null, null, null);

        if (c.moveToFirst()) {
            blindSet.setId(c.getInt(c.getColumnIndex(KEY_BLIND_ID)));
        }
        else {
            ContentValues blindValues = new ContentValues();
            blindValues.put(KEY_SMALL_BLIND, blindSet.getSB());
            blindValues.put(KEY_BIG_BLIND, blindSet.getBB());
            blindValues.put(KEY_STRADDLE, blindSet.getStraddle());
            blindValues.put(KEY_BRING_IN, blindSet.getBringIn());
            blindValues.put(KEY_ANTE, blindSet.getAnte());
            blindValues.put(KEY_PER_POINT, blindSet.getPerPoint());
            blindValues.put(KEY_FILTERED, blindSet.getFiltered());

            long blind_id = db.insert(TABLE_BLINDS, null, blindValues);

            if (blind_id != -1) {
                blindSet.setId((int) blind_id);
            }
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return blindSet;
    }

    public void editLocation(Location loc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(KEY_LOCATION, loc.getLocation());

        db.beginTransaction();
        db.update(TABLE_LOCATION, args, KEY_LOCATION_ID + "=?", new String[]{Integer.toString(loc.getId())});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void editGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(KEY_GAME, game.getGame());

        db.beginTransaction();
        db.update(TABLE_GAME, args, KEY_GAME_ID + "=?", new String[]{Integer.toString(game.getId())});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void editGameFormat(GameFormat gameFormat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(KEY_GAME_FORMAT, gameFormat.getGameFormat());
        args.put(KEY_BASE_FORMAT, gameFormat.getBaseFormatId());

        db.beginTransaction();
        db.update(TABLE_GAME_FORMAT, args, KEY_GAME_FORMAT_ID + "=?", new String[]{Integer.toString(gameFormat.getId())});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void editBlinds(Blinds blinds) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues blindValues = new ContentValues();
        blindValues.put(KEY_SMALL_BLIND, blinds.getSB());
        blindValues.put(KEY_BIG_BLIND, blinds.getBB());
        blindValues.put(KEY_STRADDLE, blinds.getStraddle());
        blindValues.put(KEY_BRING_IN, blinds.getBringIn());
        blindValues.put(KEY_ANTE, blinds.getAnte());
        blindValues.put(KEY_PER_POINT, blinds.getPerPoint());
        blindValues.put(KEY_FILTERED, blinds.getFiltered());

        db.beginTransaction();
        db.update(TABLE_BLINDS, blindValues, KEY_BLIND_ID + "=?", new String[]{Integer.toString(blinds.getId())});
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteLocation(Location loc) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_LOCATION + " WHERE " + KEY_LOCATION_ID + "=" + loc.getId() + ";";
        String deleteSessionsQuery = "DELETE FROM " + TABLE_SESSION + " WHERE " + KEY_LOCATION + "=" + loc.getId() + ";";
        db.beginTransaction();
        db.execSQL(query);
        db.execSQL(deleteSessionsQuery);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_GAME + " WHERE " + KEY_GAME_ID + "=" + game.getId() + ";";
        String deleteSessionsQuery = "DELETE FROM " + TABLE_SESSION + " WHERE " + KEY_GAME + "=" + game.getId() + ";";
        db.beginTransaction();
        db.execSQL(query);
        db.execSQL(deleteSessionsQuery);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteGameFormat(GameFormat gameFormat) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_GAME_FORMAT + " WHERE " + KEY_GAME_FORMAT_ID + "=" + gameFormat.getId() + ";";
        String deleteSessionsQuery = "DELETE FROM " + TABLE_SESSION + " WHERE " + KEY_GAME_FORMAT + "=" + gameFormat.getId() + ";";
        db.beginTransaction();
        db.execSQL(query);
        db.execSQL(deleteSessionsQuery);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteBlinds(Blinds blinds) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + TABLE_BLINDS + " WHERE " + KEY_BLIND_ID + "=" + blinds.getId() + ";";
        String deleteJoinerQuery = "DELETE FROM " + TABLE_CASH + " WHERE " + KEY_BLINDS + "=" + blinds.getId() + ";";
        String deleteSessionsQuery = "DELETE FROM " + TABLE_SESSION + " WHERE " + KEY_SESSION_ID + " IN (SELECT " + KEY_SESSION_ID + " FROM " + TABLE_CASH + " WHERE " +
                KEY_BLINDS + "=" + blinds.getId() + ");";
        db.beginTransaction();
        db.execSQL(deleteSessionsQuery);
        db.execSQL(query);
        db.execSQL(deleteJoinerQuery);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private ArrayList<Session> querySessions(String q) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Session> sessions = new ArrayList<>();

        db.beginTransaction();
        Cursor c = db.rawQuery(q, null);

        //loop through rows and add to session if any results returned
        if (c.moveToFirst()) {
            do {
                Session s = new Session();
                s.setId(c.getInt(c.getColumnIndex(KEY_SESSION_ID)));
                s.setStart(c.getLong(c.getColumnIndex(KEY_START)));
                s.setEnd(c.getLong(c.getColumnIndex(KEY_END)));
                s.setBuyIn(c.getDouble(c.getColumnIndex(KEY_BUY_IN)));
                s.setCashOut(c.getDouble(c.getColumnIndex(KEY_CASH_OUT)));
                s.setGame(new Game(c.getInt(c.getColumnIndex(KEY_GAME_ID)), c.getString(c.getColumnIndex(KEY_GAME))));
                s.setGameFormat(new GameFormat(c.getInt(c.getColumnIndex(KEY_GAME_FORMAT_ID)), c.getString(c.getColumnIndex(KEY_GAME_FORMAT)),
                        c.getInt(c.getColumnIndex(KEY_BASE_FORMAT_ID)), c.getString(c.getColumnIndex(KEY_BASE_FORMAT))));
                s.setLocation(new Location(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)), c.getString(c.getColumnIndex(KEY_LOCATION))));
                s.setState(c.getInt(c.getColumnIndex(KEY_STATE)));

                if (s.getGameFormat().getBaseFormatId() == 1) {
                    s.setBlinds(new Blinds(c.getInt(c.getColumnIndex(KEY_BLIND_ID)), c.getDouble(c.getColumnIndex(KEY_SMALL_BLIND)), c.getDouble(c.getColumnIndex(KEY_BIG_BLIND)),
                            c.getDouble(c.getColumnIndex(KEY_STRADDLE)), c.getDouble(c.getColumnIndex(KEY_BRING_IN)), c.getDouble(c.getColumnIndex(KEY_ANTE)),
                            c.getDouble(c.getColumnIndex(KEY_PER_POINT)), 0));

                } else {
                    if (!c.isNull(c.getColumnIndex(KEY_ENTRANTS))) {
                        s.setEntrants(c.getInt(c.getColumnIndex(KEY_ENTRANTS)));
                    }

                    if (!c.isNull(c.getColumnIndex(KEY_PLACED))) {
                        s.setPlaced(c.getInt(c.getColumnIndex(KEY_PLACED)));
                    }
                }

                if (!c.isNull(c.getColumnIndex(KEY_NOTE))) {
                    s.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                }

                if (!c.isNull(c.getColumnIndex(KEY_FILTERED))) {
                    s.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));
                }

                SQLiteDatabase db2 = this.getReadableDatabase();
                Cursor b = db2.rawQuery("SELECT * FROM " + TABLE_BREAK + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + " ORDER BY " + KEY_BREAK_ID + " ASC", null);

                if (b.moveToFirst()) {
                    ArrayList<Break> breaks = new ArrayList<>();
                    do {
                        breaks.add(new Break(b.getInt(b.getColumnIndex(KEY_BREAK_ID)), b.getLong(b.getColumnIndex(KEY_START)), b.getLong(b.getColumnIndex(KEY_END))));
                    } while (b.moveToNext());

                    s.setBreaks(breaks);
                }
                b.close();
                sessions.add(s);
            } while (c.moveToNext());
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return sessions;
    }

    public ArrayList<Session> getSessions(String state, String order, String filtered) {
        String query = "SELECT " + TABLE_SESSION + "." + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " +
                KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME_FORMAT_ID + ", " + TABLE_GAME_FORMAT + "." + KEY_GAME_FORMAT + ", " +
                KEY_BASE_FORMAT_ID + ", " + TABLE_BASE_FORMAT + "." + KEY_BASE_FORMAT + ", " + KEY_STATE + ", " + KEY_GAME_ID + ", " + TABLE_GAME + "." + KEY_GAME + ", " +
                KEY_LOCATION_ID + ", " + TABLE_LOCATION + "." + KEY_LOCATION + ", " + TABLE_BLINDS + "." + KEY_BLIND_ID + ", " +
                KEY_SMALL_BLIND + ", " + KEY_BIG_BLIND + ", " + KEY_STRADDLE + ", " + KEY_BRING_IN + ", " + KEY_ANTE + ", " + KEY_PER_POINT + ", " +
                KEY_ENTRANTS + ", " + KEY_PLACED + ", " + KEY_NOTE + ", " + TABLE_SESSION + "." + KEY_FILTERED + " FROM " + TABLE_SESSION + " INNER JOIN " + TABLE_GAME +
                " ON " + TABLE_SESSION + "." + KEY_GAME + "=" + KEY_GAME_ID + " INNER JOIN " + TABLE_LOCATION +
                " ON " + TABLE_SESSION + "." + KEY_LOCATION + "=" + KEY_LOCATION_ID + " INNER JOIN " + TABLE_GAME_FORMAT +
                " ON " + TABLE_SESSION + "." + KEY_GAME_FORMAT + "=" + KEY_GAME_FORMAT_ID + " INNER JOIN " + TABLE_BASE_FORMAT +
                " ON " + TABLE_GAME_FORMAT + "." + KEY_BASE_FORMAT + "=" + TABLE_BASE_FORMAT + "." + KEY_BASE_FORMAT_ID + " LEFT JOIN " + TABLE_NOTE +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_NOTE + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_TOURNAMENT +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_TOURNAMENT + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_CASH +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_CASH + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_BLINDS +
                " ON " + TABLE_CASH + "." + KEY_BLINDS + "=" + TABLE_BLINDS + "." + KEY_BLIND_ID +
                " WHERE 1=1";
        /* this is where the magic happens */
        if (state != null)
            query += " AND " + KEY_STATE + " IN (" + state + ")";

        if (filtered != null)
            query += " AND " + TABLE_SESSION + "." + KEY_FILTERED + "=" + filtered;

        query += " ORDER BY " + KEY_START + " " + order + ";";

        return querySessions(query);
    }

    public ArrayList<Session> getNotes() {
        String query = "SELECT " + TABLE_SESSION + "." + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " +
                KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME_FORMAT_ID + ", " + TABLE_GAME_FORMAT + "." + KEY_GAME_FORMAT + ", " +
                KEY_BASE_FORMAT_ID + ", " + TABLE_BASE_FORMAT + "." + KEY_BASE_FORMAT + ", " + KEY_STATE + ", " + KEY_GAME_ID + ", " + TABLE_GAME + "." + KEY_GAME + ", " +
                KEY_LOCATION_ID + ", " + TABLE_LOCATION + "." + KEY_LOCATION + ", " + TABLE_BLINDS + "." + KEY_BLIND_ID + ", " +
                KEY_SMALL_BLIND + ", " + KEY_BIG_BLIND + ", " + KEY_STRADDLE + ", " + KEY_BRING_IN + ", " + KEY_ANTE + ", " + KEY_PER_POINT + ", " +
                KEY_ENTRANTS + ", " + KEY_PLACED + ", " + KEY_NOTE + ", " + TABLE_SESSION + "." + KEY_FILTERED + " FROM " + TABLE_SESSION + " INNER JOIN " + TABLE_GAME +
                " ON " + TABLE_SESSION + "." + KEY_GAME + "=" + KEY_GAME_ID + " INNER JOIN " + TABLE_LOCATION +
                " ON " + TABLE_SESSION + "." + KEY_LOCATION + "=" + KEY_LOCATION_ID + " INNER JOIN " + TABLE_GAME_FORMAT +
                " ON " + TABLE_SESSION + "." + KEY_GAME_FORMAT + "=" + KEY_GAME_FORMAT_ID + " INNER JOIN " + TABLE_BASE_FORMAT +
                " ON " + TABLE_GAME_FORMAT + "." + KEY_BASE_FORMAT + "=" + TABLE_BASE_FORMAT + "." + KEY_BASE_FORMAT_ID + " LEFT JOIN " + TABLE_NOTE +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_NOTE + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_TOURNAMENT +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_TOURNAMENT + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_CASH +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_CASH + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_BLINDS +
                " ON " + TABLE_CASH + "." + KEY_BLINDS + "=" + TABLE_BLINDS + "." + KEY_BLIND_ID +
                " WHERE " + TABLE_SESSION + "." + KEY_FILTERED + "=0" +
                " AND " + TABLE_SESSION + "." + KEY_SESSION_ID + " IN (SELECT " + TABLE_NOTE + "." + KEY_SESSION_ID + " FROM " + TABLE_NOTE +
                " WHERE " + KEY_NOTE + " !='')" +
                " ORDER BY " + KEY_START + " DESC;";

        return querySessions(query);
    }

    private void isFiltered(int id) {
        int filtered = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        String query = "SELECT " + TABLE_LOCATION + "." + KEY_FILTERED + " AS locationFilter, " + TABLE_GAME + "." + KEY_FILTERED + " AS gameFilter, " +
                TABLE_GAME_FORMAT + "." + KEY_FILTERED + " AS gameFormatFilter, " + TABLE_BLINDS + "." + KEY_FILTERED + " AS blindsFilter FROM " +
                TABLE_SESSION + " INNER JOIN " + TABLE_GAME +
                " ON " + TABLE_SESSION + "." + KEY_GAME + "=" + KEY_GAME_ID + " INNER JOIN " + TABLE_LOCATION +
                " ON " + TABLE_SESSION + "." + KEY_LOCATION + "=" + KEY_LOCATION_ID + " INNER JOIN " + TABLE_GAME_FORMAT +
                " ON " + TABLE_SESSION + "." + KEY_GAME_FORMAT + "=" + KEY_GAME_FORMAT_ID + " LEFT JOIN " + TABLE_CASH +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_CASH + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_BLINDS +
                " ON " + TABLE_CASH + "." + KEY_BLINDS + "=" + TABLE_BLINDS + "." + KEY_BLIND_ID +
                " WHERE " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + Integer.toString(id) + ";";

        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            if (c.getInt(c.getColumnIndex("locationFilter")) == 1 || c.getInt(c.getColumnIndex("gameFilter")) == 1 ||
            c.getInt(c.getColumnIndex("gameFormatFilter")) == 1 || c.getInt(c.getColumnIndex("blindsFilter")) == 1 )
                filtered = 1;
        }

        db.execSQL("UPDATE " + TABLE_SESSION + " SET " + KEY_FILTERED + "=" + filtered + " WHERE " + KEY_SESSION_ID + "=" + id + ";");

        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void addSession(Session s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        String query = "INSERT INTO " + TABLE_SESSION + " (" + KEY_START + ", " + KEY_END + ", " +
                KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_GAME + ", " + KEY_GAME_FORMAT + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " +
                KEY_FILTERED + ") VALUES (" + s.getStart() + ", " + s.getEnd() + ", " + s.getBuyIn() + ", " +
                s.getCashOut() + ", " + s.getGame().getId() + ", " + s.getGameFormat().getId() + ", " + s.getLocation().getId() + ", " +
                s.getState() + ", 0);";

        db.execSQL(query);

        Cursor c = db.rawQuery("SELECT last_insert_rowid() AS " + KEY_SESSION_ID + ";", null);

        Boolean filterFlag = false;
        int sessionId = 0;

        if (c.moveToFirst()) {
            sessionId = c.getInt(c.getColumnIndex(KEY_SESSION_ID));
            filterFlag = true;

            if (s.getGameFormat().getBaseFormatId() == 1) {
                db.execSQL("INSERT INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + sessionId + ", " + s.getBlinds().getId() + ");");
            } else {
                db.execSQL("INSERT INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + sessionId + ", " +
                        s.getEntrants() + ", " + s.getPlaced() + ");");
            }

            ArrayList<Break> breaks = s.getBreaks();
            for (int i = 0; i < breaks.size(); i++) {
                db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") VALUES (" + sessionId + ", " +
                        breaks.get(i).getStart() + ", " + breaks.get(i).getEnd() + ");");
            }

            if (!s.getNote().equals("")) {
                db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + sessionId + ", " +
                        DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
            }
        }
        c.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        if (filterFlag)
            isFiltered(sessionId);
    }

    public void addSessions(ArrayList<Session> sessions) {
        for (Session s : sessions)
            addSession(verifySession(s));
    }

    public Session verifySession(Session s) {

        s.setLocation(createLocation(s.getLocation()));
        s.setGame(createGame(s.getGame()));
        s.setGameFormat(createGameFormat(s.getGameFormat()));

        if (!s.getBlinds().toString().equals(""))
            s.setBlinds(createBlinds(s.getBlinds()));

        return s;
    }

    public void editSession(Session s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_START + "=" + s.getStart() + ", " + KEY_END + "=" + s.getEnd() + ", " + KEY_BUY_IN + "=" +
                s.getBuyIn() + ", " + KEY_CASH_OUT + "=" + s.getCashOut() + ", " + KEY_GAME +
                "=" + s.getGame().getId() + ", " + KEY_GAME_FORMAT + "=" + s.getGameFormat().getId() + ", " + KEY_LOCATION + "=" + s.getLocation().getId() + ", " +
                KEY_STATE + "=" + s.getState() + ", " + KEY_FILTERED + "=" + s.getFiltered() + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";";

        db.execSQL(query);

        if (s.getGameFormat().getBaseFormatId() == 1) {
            //if the user saves an active session as a tournament then changes it to a cash game when finishing the session the database would be compromised
            //without the insert or ignore and delete queries
            db.execSQL("INSERT OR IGNORE INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + s.getId() + ", " + s.getBlinds().getId() + ");");
            db.execSQL("UPDATE " + TABLE_CASH + " SET " + KEY_SESSION_ID + "=" + s.getId() + ", " + KEY_BLINDS + "=" + s.getBlinds().getId() + " WHERE " +
                    KEY_SESSION_ID + "=" + s.getId() + ";");
            //run delete query on tournament table to be certain the user didn't change session type between creating and finishing
            db.execSQL("DELETE FROM " + TABLE_TOURNAMENT + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
        } else {
            db.execSQL("INSERT OR IGNORE INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + s.getId() + ", " +
                    s.getEntrants() + ", " + s.getPlaced() + ");");
            db.execSQL("UPDATE " + TABLE_TOURNAMENT + " SET " + KEY_SESSION_ID + "=" + s.getId() + ", " + KEY_ENTRANTS + "=" + s.getEntrants() + ", " +
                    KEY_PLACED + "=" + s.getPlaced() + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
            //run delete query on cash table to be certain the user didn't change session type between creating and finishing
            db.execSQL("DELETE FROM " + TABLE_CASH + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
        }

        //run delete statement then insert them all fresh to clear out any breaks that were created by toggleBreak then deleted in finishSessionActivity
        db.execSQL("DELETE FROM " + TABLE_BREAK + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");

        if (s.getBreaks().size() > 0) {
            ArrayList<Break> breaks = s.getBreaks();
            for (int i = 0; i < breaks.size(); i++) {
                db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") VALUES (" +
                        s.getId() + ", " + breaks.get(i).getStart() + ", " + breaks.get(i).getEnd() + ");");
            }
        }

        //run delete query for this id on notes table so we can just run an insert and not have to check for duplicates
        db.execSQL("DELETE FROM " + TABLE_NOTE + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
        if (s.getNote() != null) {
            db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + s.getId() + ", " +
                    DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public void toggleBreak(Session s) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        Calendar cal = Calendar.getInstance();
        Long time = cal.getTimeInMillis();

        if (s.onBreak()) {
            String setBreakEndQuery = "UPDATE " + TABLE_BREAK + " SET " + KEY_END + "=" + time +
                    " WHERE " + KEY_BREAK_ID + "=" + s.getBreaks().get(s.getBreaks().size() - 1).getId() + ";";
            db.execSQL(setBreakEndQuery);
            FlurryAgent.logEvent("Action_Resume");
        } else {
            ContentValues breakValues = new ContentValues();
            breakValues.put(KEY_SESSION_ID, s.getId());
            breakValues.put(KEY_START, time);

            db.insert(TABLE_BREAK, null, breakValues);
            FlurryAgent.logEvent("Action_Pause");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void deleteSession(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_STATE + "=-1 WHERE " + KEY_SESSION_ID + "=" + id + ";";
        db.execSQL(query);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void rebuyAddon(int id, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_BUY_IN + "=" + KEY_BUY_IN + "+" + amount + " WHERE " + KEY_SESSION_ID + "=" + id + ";";
        db.execSQL(query);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void clearFilters() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        db.execSQL("UPDATE " + TABLE_SESSION + " SET " + KEY_FILTERED + "=0;");
        db.execSQL("UPDATE " + TABLE_LOCATION + " SET " + KEY_FILTERED + "=0;");
        db.execSQL("UPDATE " + TABLE_GAME + " SET " + KEY_FILTERED + "=0;");
        db.execSQL("UPDATE " + TABLE_GAME_FORMAT + " SET " + KEY_FILTERED + "=0;");
        db.execSQL("UPDATE " + TABLE_BLINDS + " SET " + KEY_FILTERED + "=0;");
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void applyFilters(ArrayList<Integer> locationIds, ArrayList<Integer> gameIds, ArrayList<Integer> gameFormatIds, ArrayList<Integer> blindsIds) {
        clearFilters();
        String locations = "(";

        if (locationIds.size() > 0) {
            for (Integer l : locationIds) {
                if (!locations.equals("("))
                    locations += ", ";
                locations += Integer.toString(l);
            }
        }
        locations += ")";

        String games = "(";

        if (gameIds.size() > 0) {
            for (Integer g : gameIds) {
                if (!games.equals("("))
                    games += ", ";
                games += Integer.toString(g);
            }
        }
        games += ")";

        String gameFormats = "(";

        if (gameFormatIds.size() > 0) {
            for (Integer gf : gameFormatIds) {
                if (!gameFormats.equals("("))
                    gameFormats += ", ";
                gameFormats += Integer.toString(gf);
            }
        }
        gameFormats += ")";

        String blinds = "(";

        if (blindsIds.size() > 0) {
            for (Integer b : blindsIds) {
                if (!blinds.equals("("))
                    blinds += ", ";
                blinds += Integer.toString(b);
            }
        }
        blinds += ")";

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.execSQL("UPDATE " + TABLE_LOCATION + " SET " + KEY_FILTERED + "=1 WHERE " + KEY_LOCATION_ID + " IN " + locations + ";");
        db.execSQL("UPDATE " + TABLE_GAME + " SET " + KEY_FILTERED + "=1 WHERE " + KEY_GAME_ID + " IN " + games + ";");
        db.execSQL("UPDATE " + TABLE_GAME_FORMAT + " SET " + KEY_FILTERED + "=1 WHERE " + KEY_GAME_FORMAT_ID + " IN " + gameFormats + ";");
        db.execSQL("UPDATE " + TABLE_BLINDS + " SET " + KEY_FILTERED + "=1 WHERE " + KEY_BLIND_ID + " IN " + blinds + ";");

        db.execSQL("UPDATE " + TABLE_SESSION + " SET " + KEY_FILTERED + "=1 WHERE " +
                KEY_LOCATION + " IN " + locations + " OR " +
                KEY_GAME + " IN " + games + " OR " +
                KEY_GAME_FORMAT + " IN " + gameFormats + ";");

        db.execSQL("UPDATE " + TABLE_SESSION + " SET " + KEY_FILTERED + "=1 WHERE " +
                KEY_SESSION_ID + " IN (SELECT " + TABLE_CASH + "." + KEY_SESSION_ID + " FROM " + TABLE_CASH +
                " INNER JOIN " +  TABLE_BLINDS + " ON " + TABLE_CASH + "." + KEY_BLINDS + "=" + TABLE_BLINDS + "." + KEY_BLIND_ID +
                " WHERE " + TABLE_BLINDS + "." + KEY_FILTERED + "=1)");
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}