package com.pokerledger.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityFinishedSession extends ActivitySession  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_session);
        FlurryAgent.logEvent("Activity_Finished_Session");

        String json = getIntent().getStringExtra("SESSION_JSON");
        if (json != null) { //active session being finished or finished session being edited
            Gson gson = new Gson();
            this.activeSession = gson.fromJson(json, Session.class);

            ((EditText) findViewById(R.id.buy_in)).setText(Integer.toString(this.activeSession.getBuyIn()));

            if (this.activeSession.getEntrants() != 0) {
                ((EditText) findViewById(R.id.entrants)).setText(Integer.toString(this.activeSession.getEntrants()));
            }

            ((Button) findViewById(R.id.start_date)).setHint(this.activeSession.getStartDate());
            ((Button) findViewById(R.id.start_time)).setHint(this.activeSession.getStartTime());

            if (this.activeSession.onBreak()) {
                this.activeSession.breakEnd();
            }

            if (!this.activeSession.getNote().equals("")) {
                ((EditText) findViewById(R.id.note)).setText(this.activeSession.getNote());
            }

            if (this.activeSession.getState() == 0) {
                ((EditText) findViewById(R.id.cash_out)).setText(Integer.toString(this.activeSession.getCashOut()));

                if (this.activeSession.getGameFormat().getBaseFormatId() == 2) {
                    ((EditText) findViewById(R.id.placed)).setText(Integer.toString(this.activeSession.getPlaced()));
                }

                ((Button) findViewById(R.id.end_date)).setHint(this.activeSession.getEndDate());
                ((Button) findViewById(R.id.end_time)).setHint(this.activeSession.getEndTime());
            } else {
                Calendar cal = Calendar.getInstance();
                DecimalFormat df = new DecimalFormat("00");
                ((Button) findViewById(R.id.end_date)).setHint(cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)));
                ((Button) findViewById(R.id.end_time)).setHint(df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE)));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();

        activeSession.setLocation((Location) ((Spinner) this.findViewById(R.id.location)).getSelectedItem());
        activeSession.setGame((Game) ((Spinner) this.findViewById(R.id.game)).getSelectedItem());
        activeSession.setGameFormat((GameFormat) ((Spinner) this.findViewById(R.id.game_format)).getSelectedItem());
        activeSession.setBlinds((Blinds) ((Spinner) this.findViewById(R.id.blinds)).getSelectedItem());
        outState.putString("ACTIVE_SESSION_JSON", gson.toJson(activeSession));
        outState.putString("START_DATE_HINT", ((Button) this.findViewById(R.id.start_date)).getHint().toString());
        outState.putString("START_TIME_HINT", ((Button) this.findViewById(R.id.start_time)).getHint().toString());
        outState.putString("END_DATE_HINT", ((Button) this.findViewById(R.id.end_date)).getHint().toString());
        outState.putString("END_TIME_HINT", ((Button) this.findViewById(R.id.end_time)).getHint().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Gson gson = new Gson();

        activeSession = gson.fromJson(savedInstanceState.getString("ACTIVE_SESSION_JSON"), Session.class);
        ((Button) this.findViewById(R.id.start_date)).setHint(savedInstanceState.getString("START_DATE_HINT"));
        ((Button) this.findViewById(R.id.start_time)).setHint(savedInstanceState.getString("START_TIME_HINT"));
        ((Button) this.findViewById(R.id.end_date)).setHint(savedInstanceState.getString("END_DATE_HINT"));
        ((Button) this.findViewById(R.id.end_time)).setHint(savedInstanceState.getString("END_TIME_HINT"));
    }
}