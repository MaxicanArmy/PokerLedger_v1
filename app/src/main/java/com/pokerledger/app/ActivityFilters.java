package com.pokerledger.app;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Max on 11/5/2016.
 */

public class ActivityFilters extends ActivityBase {
    View activeView;

    private static final int locationsIdBase = 1000000;
    private static final int gamesIdBase = 2000000;
    private static final int gameFormatsIdBase = 3000000;
    private static final int blindsIdBase = 4000000;

    ArrayList<Location> locations = new ArrayList<>();
    ArrayList<Game> games = new ArrayList<>();
    ArrayList<GameFormat> gameFormats = new ArrayList<>();
    ArrayList<Blinds> blinds = new ArrayList<>();

    ArrayList<Integer> filteredLocations = new ArrayList<>();
    ArrayList<Integer> filteredGames = new ArrayList<>();
    ArrayList<Integer> filteredGameFormats = new ArrayList<>();
    ArrayList<Integer> filteredBlinds = new ArrayList<>();
    //String filterStartDate;
    //String filterEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        FlurryAgent.logEvent("Activity_Filters");

        new LoadLocationsFilter().execute();
        new LoadGamesFilter().execute();
        new LoadGameFormatsFilter().execute();
        new LoadBlindsFilter().execute();
        //new LoadDateFilter().execute();
    }

    public void saveFilters(View v) {
        new ClearFilters().execute();

        filteredLocations = new ArrayList<>();
        filteredGames = new ArrayList<>();
        filteredGameFormats = new ArrayList<>();
        filteredBlinds = new ArrayList<>();

        CheckBox currentLocation;
        CheckBox currentGame;
        CheckBox currentGameFormat;
        CheckBox currentBlinds;

        for (Location l : locations) {
            currentLocation = (CheckBox) findViewById(locationsIdBase + l.getId());
            if (!currentLocation.isChecked()) {
                filteredLocations.add(l.getId());
            }
        }

        for (Game g : games) {
            currentGame = (CheckBox) findViewById(gamesIdBase + g.getId());
            if (!currentGame.isChecked()) {
                filteredGames.add(g.getId());
            }
        }

        for (GameFormat gf : gameFormats) {
            currentGameFormat = (CheckBox) findViewById(gameFormatsIdBase + gf.getId());
            if (!currentGameFormat.isChecked()) {
                filteredGameFormats.add(gf.getId());
            }
        }

        for (Blinds b : blinds) {
            currentBlinds = (CheckBox) findViewById(blindsIdBase + b.getId());
            if (!currentBlinds.isChecked()) {
                filteredBlinds.add(b.getId());
            }
        }
        new ApplyFilters().execute();
        /*
        new SaveDateFilter().execute();
        */
    }

    public class LoadLocationsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            ActivityFilters.this.locations = db.getAllLocations(null);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            LinearLayout locationsWrapper = (LinearLayout) findViewById(R.id.location_wrapper);
            CheckBox current;

            for (Location l : ActivityFilters.this.locations) {
                current = new CheckBox(ActivityFilters.this);
                current.setId(locationsIdBase + l.getId());
                current.setText(l.getLocation());

                if (l.getFiltered() == 0) {
                    current.setChecked(true);
                }
                locationsWrapper.addView(current);
            }
        }
    }

    public class LoadGamesFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            ActivityFilters.this.games = db.getAllGames(null);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            LinearLayout gamesWrapper = (LinearLayout) findViewById(R.id.game_wrapper);
            CheckBox current;

            for (Game g : ActivityFilters.this.games) {
                current = new CheckBox(ActivityFilters.this);
                current.setId(gamesIdBase + g.getId());
                current.setText(g.getGame());

                if (g.getFiltered() == 0) {
                    current.setChecked(true);
                }
                gamesWrapper.addView(current);
            }
        }
    }

    public class LoadGameFormatsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            ActivityFilters.this.gameFormats = db.getAllGameFormats(null);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            LinearLayout gameFormatsWrapper = (LinearLayout) findViewById(R.id.game_format_wrapper);
            CheckBox current;

            for (GameFormat g : ActivityFilters.this.gameFormats) {
                current = new CheckBox(ActivityFilters.this);
                current.setId(gameFormatsIdBase + g.getId());
                current.setText(g.toString());

                if (g.getFiltered() == 0) {
                    current.setChecked(true);
                }
                gameFormatsWrapper.addView(current);
            }
        }
    }

    public class LoadBlindsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            ActivityFilters.this.blinds = db.getAllBlinds(null);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            LinearLayout blindsWrapper = (LinearLayout) findViewById(R.id.blinds_wrapper);
            CheckBox current;

            for (Blinds b : ActivityFilters.this.blinds) {
                current = new CheckBox(ActivityFilters.this);
                current.setId(blindsIdBase + b.getId());
                current.setText(b.toString());

                if (b.getFiltered() == 0) {
                    current.setChecked(true);
                }
                blindsWrapper.addView(current);
            }
        }
    }

    public class ClearFilters extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.clearFilters();

            return null;
        }
    }

    public class ApplyFilters extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.applyFilters(ActivityFilters.this.filteredLocations, ActivityFilters.this.filteredGames, ActivityFilters.this.filteredGameFormats, ActivityFilters.this.filteredBlinds);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Toast.makeText(ActivityFilters.this, "Your new filter settings have been applied throughout Poker Ledger.", Toast.LENGTH_LONG).show();
        }
    }
/*
    public class SaveGamesFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.filterGames(ActivityFilters.this.filteredGames);

            return null;
        }
    }

    public class SaveGameFormatsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            db.filterGameFormats(ActivityFilters.this.filteredGameFormats);

            return null;
        }
    }

    public class SaveBlindsFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());

            ArrayList<Blinds> blinds = db.getAllBlinds(null);
            CheckBox current;
            String query;
            String sessionQuery;

            for (Blinds b : blinds) {

                current = (CheckBox) findViewById(blindsIdBase + b.getId());
                if (!current.isChecked()) {

                    query = "UPDATE blinds SET filtered=1 WHERE blind_id=" + b.getId() + ";";
                    sessionQuery = "UPDATE sessions SET filtered=1 WHERE session_id IN (SELECT session_id FROM cash WHERE blinds=" + b.getId() + ");";

                    db.runQuery(query);
                    db.runQuery(sessionQuery);
                }
            }

            return null;
        }
    }
*/
    /*
    public class LoadDateFilter extends AsyncTask<Void, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());

            return db.getFilterDates();
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            Button startBtn = (Button) findViewById(R.id.start_date);
            Button endBtn = (Button) findViewById(R.id.end_date);

            startBtn.setHint(result.get("start"));
            endBtn.setHint(result.get("end"));
        }
    }

    public class SaveDateFilter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());

            String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();
            String endDate = ((Button) findViewById(R.id.end_date)).getHint().toString();

            String query;
            String sessionQuery;

            if (startDate != "Start Date") {
                query = "UPDATE date_filter SET start_date='" + startDate + "';";
                sessionQuery = "UPDATE sessions SET filtered=1 WHERE start_date < '" + startDate + "';";

                db.runQuery(query);
                db.runQuery(sessionQuery);
            }

            if (endDate != "End Date") {
                query = "UPDATE date_filter SET end_date='" + endDate + "';";
                sessionQuery = "UPDATE sessions SET filtered=1 WHERE end_date > '" + endDate + "';";

                db.runQuery(query);
                db.runQuery(sessionQuery);
            }

            return null;
        }
    }
    */

    public void showDatePickerDialog(View v) {
        activeView = v;

        FragmentDatePicker date = new FragmentDatePicker();
        /**
         * Set Up Current Date Into dialog
         */
        Button dateBtn = (Button) v;
        Bundle args = new Bundle();
        if (dateBtn.getHint().toString().matches("[A-Za-z]* Date$")) {
            Calendar calender = Calendar.getInstance();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        }
        else {
            Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})");
            Matcher m = DATE_PATTERN.matcher(dateBtn.getHint().toString());

            while (m.find()) {
                args.putInt("year", Integer.parseInt(m.group(1)));
                args.putInt("month", Integer.parseInt(m.group(2)) - 1);
                args.putInt("day", Integer.parseInt(m.group(3)));
            }
        }
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "DatePicker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Button b = (Button) activeView;
            b.setHint(String.format("%04d-%02d-%02d", year, month+1, day));

            Button endDate = (Button) findViewById(R.id.end_date);

            if (endDate != null && endDate.getHint().toString().matches("[A-Za-z]* Date$")) {
                endDate.setHint(String.format("%04d-%02d-%02d", year, month+1, day));
            }
        }
    };
}
