package com.pokerledger.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Catface Meowmers on 7/28/15.
 */
public class ActivityHistory extends ActivityBase {
    protected int tbSpinnerPos = 1;
    protected int tfSpinnerPos = 0;

    //these populate the timeframes spinner and the Strings are the keys of the corresponding HashMaps
    protected ArrayList<String> weeklyList;
    protected ArrayList<String> monthlyList;
    protected ArrayList<String> yearlyList;
    protected ArrayList<String> allList;

    //these populate the history notesList and store the overview info
    protected HashMap<String, SessionSet> weekly;
    protected HashMap<String, SessionSet> monthly;
    protected HashMap<String, SessionSet> yearly;
    protected HashMap<String, SessionSet> all;

    protected ListView historySessionList;
    protected Spinner tbSpinner;
    protected Spinner tfSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LIFECYCLE", "Activity History onCreate");
        setContentView(R.layout.activity_history);

        if (savedInstanceState != null) {
            this.tbSpinnerPos = savedInstanceState.getInt("tbSpinnerPos");
            this.tfSpinnerPos = savedInstanceState.getInt("tfSpinnerPos");
        }

        this.historySessionList = (ListView)findViewById(R.id.history_session_list);
        this.tbSpinner = (Spinner) findViewById(R.id.timeblocks);
        this.tfSpinner = (Spinner) findViewById(R.id.timeframes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeblocks_array, R.layout.spinner_timeframe_view);
        adapter.setDropDownViewResource(R.layout.spinner_timeframe_view);
        this.tbSpinner.setAdapter(adapter);
        this.tbSpinner.setSelection(tbSpinnerPos);

        new PopulateSpinnerDataSources().execute();

        historySessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                FragmentOptionsFinishedSession dialog = new FragmentOptionsFinishedSession();
                dialog.setArguments(b);
                dialog.show(manager, "EditHistory");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("LIFECYCLE", "Activity History onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LIFECYCLE", "Activity History onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("LIFECYCLE", "Activity History onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("LIFECYCLE", "Activity History onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("LIFECYCLE", "Activity History onRestart");
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tbSpinnerPos", this.tbSpinnerPos);
        outState.putInt("tfSpinnerPos", this.tfSpinnerPos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new PopulateSpinnerDataSources().execute();
        }
    }

    public class PopulateSpinnerDataSources extends AsyncTask<Void, Void, ArrayList<Session>> {

        @Override
        protected ArrayList<Session> doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getApplicationContext());

            return db.getSessions("0", "DESC", "0");
        }

        @Override
        protected void onPostExecute(ArrayList<Session> result) {
            String weekOfYear;
            String month;
            String year;

            //these populate the timeframes spinner and the Strings are the keys of the corresponding HashMaps
            ActivityHistory.this.weeklyList = new ArrayList<String>();
            ActivityHistory.this.monthlyList = new ArrayList<String>();
            ActivityHistory.this.yearlyList = new ArrayList<String>();
            ActivityHistory.this.allList = new ArrayList<String>();

            //these populate the history notesList and store the overview info
            ActivityHistory.this.weekly = new HashMap<String, SessionSet>();
            ActivityHistory.this.monthly = new HashMap<String, SessionSet>();
            ActivityHistory.this.yearly = new HashMap<String, SessionSet>();
            ActivityHistory.this.all = new HashMap<String, SessionSet>();

            ActivityHistory.this.allList.add("N/A");
            ActivityHistory.this.all.put("N/A", new SessionSet());

            //for each session get session start time
            for (Session s : result) {
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTimeInMillis(s.getStart());
                } catch (Exception e) {

                }
                year = Integer.toString(cal.get(Calendar.YEAR));
                weekOfYear = "Week" + " " + cal.get(Calendar.WEEK_OF_YEAR) + " " + year;
                month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + year;

                if (ActivityHistory.this.weekly.get(weekOfYear) == null) {
                    ActivityHistory.this.weeklyList.add(weekOfYear);
                    ActivityHistory.this.weekly.put(weekOfYear, new SessionSet());
                }

                if (ActivityHistory.this.monthly.get(month) == null) {
                    ActivityHistory.this.monthlyList.add(month);
                    ActivityHistory.this.monthly.put(month, new SessionSet());
                }

                if (ActivityHistory.this.yearly.get(year) == null) {
                    ActivityHistory.this.yearlyList.add(year);
                    ActivityHistory.this.yearly.put(year, new SessionSet());
                }

                ActivityHistory.this.weekly.get(weekOfYear).addSession(s);
                ActivityHistory.this.monthly.get(month).addSession(s);
                ActivityHistory.this.yearly.get(year).addSession(s);
                ActivityHistory.this.all.get("N/A").addSession(s);
            }

            ActivityHistory.this.tbSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ActivityHistory.this.tbSpinnerPos = pos;
                    ActivityHistory.this.tfSpinner.setEnabled(true);
                    ArrayAdapter tfAdapter;

                    switch (tbSpinnerPos) {
                        case 0 :
                            tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.weeklyList);
                            break;
                        case 2 :
                            tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.yearlyList);
                            break;
                        case 3 :
                            ActivityHistory.this.tfSpinner.setEnabled(false);
                            tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.allList);
                            break;
                        default :
                            tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.monthlyList);
                    }
                    tfAdapter.setDropDownViewResource(R.layout.spinner_timeframe_view);
                    ActivityHistory.this.tfSpinner.setAdapter(tfAdapter);
                }

                public void onNothingSelected(AdapterView<?> arg0){

                }
            });

            ActivityHistory.this.tfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ActivityHistory.this.tfSpinnerPos = pos;
                    displayStats();
                }

                public void onNothingSelected(AdapterView<?> arg0){

                }
            });

            ArrayAdapter tfAdapter;
            switch (tbSpinnerPos) {
                case 0 :
                    tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.weeklyList);
                    break;
                case 2 :
                    tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.yearlyList);
                    break;
                case 3 :
                    ActivityHistory.this.tfSpinner.setEnabled(false);
                    tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.allList);
                    break;
                default :
                    tfAdapter = new ArrayAdapter(ActivityHistory.this, R.layout.spinner_timeframe_view, ActivityHistory.this.monthlyList);
            }
            tfAdapter.setDropDownViewResource(R.layout.spinner_timeframe_view);
            ActivityHistory.this.tfSpinner.setAdapter(tfAdapter);
            ActivityHistory.this.tfSpinner.setSelection(tfSpinnerPos);
            displayStats();

            if (result.size() < 1) {
                ActivityHistory.this.findViewById(R.id.welcome_wrapper).setVisibility(LinearLayout.VISIBLE);
                ActivityHistory.this.findViewById(R.id.timeframe_wrapper).setVisibility(LinearLayout.GONE);
            }
            else {
                ActivityHistory.this.findViewById(R.id.welcome_wrapper).setVisibility(LinearLayout.GONE);
                ActivityHistory.this.findViewById(R.id.timeframe_wrapper).setVisibility(LinearLayout.VISIBLE);
            }
        }
    }

    protected void displayStats() {
        ListAdapterHistory adapter;
        SessionSet stats;
        int showList = ListView.GONE;
        int showOverview = LinearLayout.GONE;
        if (tfSpinner.getAdapter().getCount() > 0) {
            switch (tbSpinnerPos) {
                case 0 :
                    stats = ActivityHistory.this.weekly.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
                    break;
                case 2 :
                    stats = ActivityHistory.this.yearly.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
                    break;
                case 3 :
                    stats = ActivityHistory.this.all.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
                    break;
                default :
                    stats = ActivityHistory.this.monthly.get(tfSpinner.getItemAtPosition(tfSpinnerPos).toString());
            }

            if (stats.getSessions().size() > 0) {
                adapter = new ListAdapterHistory(ActivityHistory.this, stats.getSessions());
                ActivityHistory.this.historySessionList.setAdapter(adapter);
                showList = ListView.VISIBLE;
                showOverview = LinearLayout.VISIBLE;
            }

            TextView profit = (TextView) findViewById(R.id.profit);
            TextView time = (TextView) findViewById(R.id.time_played);
            TextView hourly = (TextView) findViewById(R.id.hourly_wage);

            if (stats.getProfit() < 0) {
                profit.setTextColor(Color.parseColor("#ff0000"));
                hourly.setTextColor(Color.parseColor("#ff0000"));
            } else {
                profit.setTextColor(Color.parseColor("#4eb502"));
                hourly.setTextColor(Color.parseColor("#4eb502"));
            }

            profit.setText(stats.profitFormatted());
            time.setText(stats.lengthFormatted());
            hourly.setText(stats.wageFormatted());
        }

        ActivityHistory.this.historySessionList.setVisibility(showList);
        ActivityHistory.this.findViewById(R.id.overview_wrapper).setVisibility(showOverview);
    }

    public void previousTimeframe(View v) {
        if (tfSpinnerPos < tfSpinner.getAdapter().getCount() - 1) {
            tfSpinner.setSelection(++tfSpinnerPos);
        }
    }

    public void nextTimeframe(View v) {
        if (tfSpinnerPos > 0) {
            tfSpinner.setSelection(--tfSpinnerPos);
        }
    }

    protected void notifyListChange() {
        //this method is necessary because i cant get fragments to call async tasks
        new PopulateSpinnerDataSources().execute();
    }
}