package com.pokerledger.app.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Max on 12/17/2017.
 */

public class SessionsData {
    public static final String DEBUG_TAG = "SessionsData";

    private SQLiteDatabase db;
    private DatabaseHelper sessionDbHelper;
/*
    private static final String[] ALL_COLUMNS = {
            CardsDBHelper.COLUMN_ID,
            CardsDBHelper.COLUMN_NAME,
            CardsDBHelper.COLUMN_COLOR_RESOURCE
    };*/

    public SessionsData(Context context) {
        this.sessionDbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() {
        db = sessionDbHelper.getWritableDatabase();
    }

    public void close() {
        if (sessionDbHelper != null) {
            sessionDbHelper.close();
        }
    }
}

