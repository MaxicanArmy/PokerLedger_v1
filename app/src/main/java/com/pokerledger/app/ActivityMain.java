package com.pokerledger.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityMain extends ActivityBase {
    TextView profit;
    TextView timePlayed;
    TextView hourlyWage;
    LinearLayout activeSessionsWrapper;
    ListView list;
    SessionListAdapter adapter;
    //private static final int ACTIVE_RESULT = 1;
    private static final int FINISHED_RESULT = 2;


    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            timerHandler.postDelayed(this, 1000); // run every second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlurryAgent.logEvent("Activity_Main");

        activeSessionsWrapper = (LinearLayout) findViewById(R.id.active_sessions_wrapper);
        list = (ListView)findViewById(R.id.active_sessions);

        new LoadActiveSessions().execute();

        this.profit = (TextView) findViewById(R.id.profit);
        this.timePlayed = (TextView) findViewById(R.id.time_played);
        this.hourlyWage = (TextView) findViewById(R.id.hourly_wage);
        new LoadStatistics().execute();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                FragmentEditActiveSession dialog = new FragmentEditActiveSession();
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

    public class LoadActiveSessions extends AsyncTask<Void, Void, Void> {
        ArrayList<Session> sessions = new ArrayList<>();

        public LoadActiveSessions() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            sessions = dbHelper.getSessions(1);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new SessionListAdapter(ActivityMain.this, sessions);
            list.setAdapter(adapter);

            if (sessions.size() > 0) {
                activeSessionsWrapper.setVisibility(View.VISIBLE);
                timerHandler.postDelayed(timerRunnable, 500);
            } else {
                activeSessionsWrapper.setVisibility(View.GONE);
                timerHandler.removeCallbacks(timerRunnable);
            }

            welcome();
        }
    }

    public class LoadStatistics extends AsyncTask<Void, Void, SessionSet> {

        @Override
        protected SessionSet doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            return new SessionSet(dbHelper.getSessions(0));
        }

        @Override
        protected void onPostExecute(SessionSet stats) {
            if (stats.getSessions().size() < 1) {
                ActivityMain.this.findViewById(R.id.overview_wrapper).setVisibility(LinearLayout.GONE);
            } else {
                ActivityMain.this.findViewById(R.id.overview_wrapper).setVisibility(LinearLayout.VISIBLE);
                if (stats.getProfit() < 0 ) {
                    ActivityMain.this.profit.setTextColor(Color.parseColor("#ff0000"));
                    ActivityMain.this.hourlyWage.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    ActivityMain.this.profit.setTextColor(Color.parseColor("#4eb502"));
                    ActivityMain.this.hourlyWage.setTextColor(Color.parseColor("#4eb502"));
                }

                ActivityMain.this.profit.setText(stats.profitFormatted());
                ActivityMain.this.timePlayed.setText(stats.lengthFormatted());
                ActivityMain.this.hourlyWage.setText(stats.wageFormatted());

            }

            welcome();
        }
    }

    public void welcome() {
        LinearLayout ovw = (LinearLayout) ActivityMain.this.findViewById(R.id.overview_wrapper);
        if (activeSessionsWrapper.getVisibility() == View.GONE && ovw.getVisibility() == View.GONE) {
            findViewById(R.id.welcome_wrapper).setVisibility(LinearLayout.VISIBLE);
        } else {
            findViewById(R.id.welcome_wrapper).setVisibility(LinearLayout.GONE);
        }
    }
}