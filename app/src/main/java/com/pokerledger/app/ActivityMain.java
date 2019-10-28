package com.pokerledger.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Note;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityMain extends ActivityBase  implements NoteCompleteListener<Note> {
    ArrayList<Session> activeSessions = new ArrayList<>();
    TextView profit;
    TextView timePlayed;
    TextView hourlyWage;
    LinearLayout activeSessionsWrapper;
    ListView activeSessionList;
    ListAdapterSession activeSessionListAdapter;
    //private static final int ACTIVE_RESULT = 1;
    private static final int FINISHED_RESULT = 2;

    public void onEditNoteComplete(Note n) {
        new LoadActiveSessions().execute();
    }


    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            activeSessionListAdapter.notifyDataSetChanged();
            timerHandler.postDelayed(this, 1000); // run every second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activeSessionsWrapper = findViewById(R.id.active_sessions_wrapper);
        activeSessionList = findViewById(R.id.active_sessions);
        activeSessionListAdapter = new ListAdapterSession(ActivityMain.this, activeSessions);
        activeSessionList.setAdapter(activeSessionListAdapter);

        new LoadActiveSessions().execute();

        this.profit = findViewById(R.id.profit);
        this.timePlayed = findViewById(R.id.time_played);
        this.hourlyWage = findViewById(R.id.hourly_wage);
        new LoadStatistics().execute();
        //new LoadBreakdown().execute();

        activeSessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                FragmentOptionsActiveSession dialog = new FragmentOptionsActiveSession();
                dialog.setArguments(b);
                dialog.show(manager, "EditSession");
            }
        });

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        Boolean initPrefs = prefs.getBoolean("prefs_initialized", false);

        if (!initPrefs) {
            initPrefs = true;
            String filterStartDate = "";
            String filterStartTime = "";
            String filterEndDate = "";
            String filterEndTime = "";
            String dateFormat = "YYYY-MM-DD";
            String dateRegex = "^(\\d{4})-(\\d{2})-(\\d{2})";
            Boolean twelveHourTime = false;
            int defaultLocation = 0;
            int defaultGame = 0;
            int defaultGameFormat = 0;
            int defaultBlinds = 0;

            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("prefs_initialized", initPrefs);
            editor.putString("filter_start_date", filterStartDate);
            editor.putString("filter_start_time", filterStartTime);
            editor.putString("filter_end_date", filterEndDate);
            editor.putString("filter_end_time", filterEndTime);
            editor.putString("date_format", dateFormat);
            editor.putString("date_regex", dateRegex);
            editor.putBoolean("twelve_hour_time", twelveHourTime);
            editor.putInt("default_location", defaultLocation);
            editor.putInt("default_game", defaultGame);
            editor.putInt("default_game_format", defaultGameFormat);
            editor.putInt("default_blinds", defaultBlinds);

            editor.commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new LoadActiveSessions().execute();

            if (requestCode == FINISHED_RESULT) {
                new LoadStatistics().execute();
            }
        }
    }

    protected void notifyListChange() {
        //this method is necessary because i cant get fragments to call async tasks
        new LoadActiveSessions().execute();
    }

    public class LoadActiveSessions extends AsyncTask<Void, Void, ArrayList<Session>> {

        public LoadActiveSessions() {}

        @Override
        protected ArrayList<Session> doInBackground(Void... params) {
            ArrayList<Session> sessions;
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            sessions = dbHelper.getSessions("1", "DESC", null);

            return sessions;
        }

        @Override
        protected void onPostExecute(ArrayList<Session> result) {

            if (result.size() > 0) {
                activeSessionsWrapper.setVisibility(View.VISIBLE);
                timerHandler.postDelayed(timerRunnable, 500);
            } else {
                activeSessionsWrapper.setVisibility(View.GONE);
                timerHandler.removeCallbacks(timerRunnable);
            }
            activeSessions.clear();
            activeSessions.addAll(result);
            activeSessionListAdapter.notifyDataSetChanged();

            welcome();
        }
    }

    public class LoadStatistics extends AsyncTask<Void, Void, SessionSet> {

        @Override
        protected SessionSet doInBackground(Void... params) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            return new SessionSet(dbHelper.getSessions("0", "DESC", "0"));
        }

        @Override
        protected void onPostExecute(SessionSet stats) {
            if (stats.getSessions().size() < 1) {
                ActivityMain.this.findViewById(R.id.overview_wrapper).setVisibility(LinearLayout.GONE);
                ActivityMain.this.findViewById(R.id.best_wrapper).setVisibility(LinearLayout.GONE);
                ActivityMain.this.findViewById(R.id.breakdown_wrapper).setVisibility(LinearLayout.GONE);
            } else {
                ActivityMain.this.findViewById(R.id.overview_wrapper).setVisibility(LinearLayout.VISIBLE);
                if (stats.getProfit() < 0 ) {
                    ActivityMain.this.profit.setTextColor(Color.parseColor("#ff0000"));
                    ActivityMain.this.hourlyWage.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    ActivityMain.this.profit.setTextColor(Color.parseColor("#4eb502"));
                    ActivityMain.this.hourlyWage.setTextColor(Color.parseColor("#4eb502"));
                }

                try {
                    stats.createHierarchy(new ArrayList<>(Arrays.asList(
                            Session.class.getDeclaredMethod("getStakes"),
                            Session.class.getDeclaredMethod("getGameName"),
                            Session.class.getDeclaredMethod("getBaseFormat"))));
                } catch (Exception e) {

                }

                if (stats.getChildren().size() > 0) {
                    LinearLayout brkContent = ((LinearLayout) ActivityMain.this.findViewById(R.id.breakdown_content));
                    brkContent.removeAllViews();
                    brkContent.addView(createStatsLayer(stats));
                }

                LinearLayout bc = ((LinearLayout) ActivityMain.this.findViewById(R.id.best_content));
                bc.removeAllViews();

                String game = "";
                String location = "";
                HashMap<String, SessionSet> byGames = new HashMap<>();
                HashMap<String, SessionSet> byLocations = new HashMap<>();

                for (Session s : stats.getSessions()) {
                    game = s.getGame().getGame();
                    location = s.getLocation().getLocation();

                    if (byGames.containsKey(game)) {
                        byGames.get(game).addSession(s);
                    } else {
                        byGames.put(game, new SessionSet(s));
                    }

                    if (byLocations.containsKey(location)) {
                        byLocations.get(location).addSession(s);
                    } else {
                        byLocations.put(location, new SessionSet(s));
                    }
                }

                double bestGameProfit = byGames.get(game).getWage();
                String bestGameName = game;
                String bestGameWage = byGames.get(game).wageFormatted();
                for (HashMap.Entry<String, SessionSet> entry : byGames.entrySet()) {
                    if (entry.getValue().getWage() > bestGameProfit) {
                        bestGameName = entry.getKey();
                        bestGameProfit = entry.getValue().getWage();
                        bestGameWage = entry.getValue().wageFormatted();
                    }
                }

                double bestLocationProfit = byLocations.get(location).getWage();
                String bestLocationName = location;
                String bestLocationWage = byLocations.get(location).wageFormatted();
                for (HashMap.Entry<String, SessionSet> entry : byLocations.entrySet()) {
                    if (entry.getValue().getWage() > bestLocationProfit) {
                        bestLocationName = entry.getKey();
                        bestLocationProfit = entry.getValue().getWage();
                        bestLocationWage = entry.getValue().wageFormatted();
                    }
                }

                LinearLayout gameRow = new LinearLayout(ActivityMain.this);
                gameRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                gameRow.setOrientation(LinearLayout.HORIZONTAL);

                TextView gameLabel = new TextView(ActivityMain.this);
                gameLabel.setText(getResources().getString(R.string.best_game) + " " + bestGameName);
                gameLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                gameRow.addView(gameLabel);

                TextView gameHourly = new TextView(ActivityMain.this);
                gameHourly.setText(bestGameWage + getResources().getString(R.string.per_hour));
                gameHourly.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                gameHourly.setGravity(Gravity.END);
                gameRow.addView(gameHourly);

                if (bestGameProfit < 0) {
                    gameHourly.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    gameHourly.setTextColor(Color.parseColor("#4eb502"));
                }

                bc.addView(gameRow);

                LinearLayout locationRow = new LinearLayout(ActivityMain.this);
                locationRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                locationRow.setOrientation(LinearLayout.HORIZONTAL);

                TextView locationLabel = new TextView(ActivityMain.this);
                locationLabel.setText(getResources().getString(R.string.best_location) + " " + bestLocationName);
                locationLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                locationRow.addView(locationLabel);

                TextView locationHourly = new TextView(ActivityMain.this);
                locationHourly.setText(bestLocationWage + getResources().getString(R.string.per_hour));
                locationHourly.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                locationHourly.setGravity(Gravity.END);
                locationRow.addView(locationHourly);

                if (bestLocationProfit < 0) {
                    locationHourly.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    locationHourly.setTextColor(Color.parseColor("#4eb502"));
                }

                bc.addView(locationRow);

                ActivityMain.this.profit.setText(stats.profitFormatted());
                ActivityMain.this.timePlayed.setText(stats.lengthFormatted());
                ActivityMain.this.hourlyWage.setText(stats.wageFormatted());
            }

            welcome();
        }
    }

    public LinearLayout createStatsLayer(SessionSet ss) {
        int padding = (int) getResources().getDimension(R.dimen.unit2);

        LinearLayout child = new LinearLayout(ActivityMain.this);
        child.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        child.setOrientation(LinearLayout.VERTICAL);
        child.setPadding(padding, 0, 0, padding/2);

        TreeMap<String, SessionSet> sorted = new TreeMap<>(ss.getChildren());

        for (HashMap.Entry<String, SessionSet> entry : sorted.entrySet()) {
            LinearLayout row = new LinearLayout(ActivityMain.this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView hourlyLabel = new TextView(this);
            hourlyLabel.setText(entry.getKey());
            hourlyLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.addView(hourlyLabel);

            TextView hourly = new TextView(this);
            hourly.setText(entry.getValue().wageFormatted() + getResources().getString(R.string.per_hour) + " x" + Math.round(entry.getValue().getLengthHours()) +"hr");
            hourly.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            hourly.setGravity(Gravity.END);

            if (entry.getValue().getProfit() < 0) {
                hourly.setTextColor(Color.parseColor("#ff0000"));
            } else {
                hourly.setTextColor(Color.parseColor("#4eb502"));
            }

            row.addView(hourly);
            child.addView(row);
            if (entry.getValue().getChildren().size() > 0) {
                child.addView(createStatsLayer(entry.getValue()));
            }
        }

        return child;
    }

    public void welcome() {
        LinearLayout ovw = ActivityMain.this.findViewById(R.id.overview_wrapper);
        if (activeSessionsWrapper.getVisibility() == View.GONE && ovw.getVisibility() == View.GONE) {
            findViewById(R.id.welcome_wrapper).setVisibility(LinearLayout.VISIBLE);
        } else {
            findViewById(R.id.welcome_wrapper).setVisibility(LinearLayout.GONE);
        }
    }
}