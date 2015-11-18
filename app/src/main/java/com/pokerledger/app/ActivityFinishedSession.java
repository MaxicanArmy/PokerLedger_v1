package com.pokerledger.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
        if (json != null) {
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

    public void saveFinishedSession(View v) {
        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, "You must enter a buy in amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            this.activeSession.setBuyIn(Integer.parseInt(buyinText));
        }

        String cashOutText = ((EditText) findViewById(R.id.cash_out)).getText().toString();

        if (cashOutText.equals("")) {
            Toast.makeText(this, "You must enter a cash out amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.cash_out).requestFocus();
            return;
        }
        else {
            this.activeSession.setCashOut(Integer.parseInt(cashOutText));
        }

        Spinner locationSpinner = (Spinner) findViewById(R.id.location);

        if (locationSpinner.getSelectedItem() != null) {
            this.activeSession.setLocation((Location) locationSpinner.getSelectedItem());
        } else {
            Toast.makeText(this, "You must enter the location.", Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

        if (gameSpinner.getSelectedItem() != null) {
            this.activeSession.setGame((Game) gameSpinner.getSelectedItem());
        } else {
            Toast.makeText(this, "You must enter the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);

        if (gameFormatSpinner.getSelectedItem() != null) {
            this.activeSession.setGameFormat((GameFormat) gameFormatSpinner.getSelectedItem());

            if (this.activeSession.getGameFormat().getBaseFormatId() == 2) {
                String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

                if (entrantsText.equals("")) {
                    Toast.makeText(this, "You must enter the number of entrants.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.entrants).requestFocus();
                    return;
                }
                else {
                    this.activeSession.setEntrants(Integer.parseInt(entrantsText));
                }

                String placedText = ((EditText) findViewById(R.id.placed)).getText().toString();

                if (placedText.equals("")) {
                    Toast.makeText(this, "You must enter what position you placed.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.placed).requestFocus();
                    return;
                }
                else {
                    this.activeSession.setPlaced(Integer.parseInt(placedText));
                }
            }
            else {
                Spinner blinds = (Spinner) findViewById(R.id.blinds);

                if (blinds.getSelectedItem() != null) {
                    this.activeSession.setBlinds((Blinds) blinds.getSelectedItem());
                } else {
                    Toast.makeText(this, "You must enter the blinds.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(this, "You must select a format.", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();

        if (startDate.equals("Start Date")) {
            Toast.makeText(this, "Select a start date for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        String startTime = ((Button) findViewById(R.id.start_time)).getHint().toString();

        if (startTime.equals("Start Time")) {
            Toast.makeText(this, "Select a start time for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        String endDate = ((Button) findViewById(R.id.end_date)).getHint().toString();

        if (endDate.equals("End Date")) {
            Toast.makeText(this, "Select an end date for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        String endTime = ((Button) findViewById(R.id.end_time)).getHint().toString();

        if (endTime.equals("End Time")) {
            Toast.makeText(this, "Select an end time for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.activeSession.setStartDate(startDate);
        this.activeSession.setStartTime(startTime);
        this.activeSession.setEndDate(endDate);
        this.activeSession.setEndTime(endTime);

        if ((this.activeSession.getEndDate() + this.activeSession.getEndTime()).compareTo(this.activeSession.getStartDate() + this.activeSession.getStartTime()) <= 0) {
            Toast.makeText(this, "Session end time must be after start time.", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((EditText) findViewById(R.id.note)).getText().toString();

        if (!note.equals("")) {
            this.activeSession.setNote(note);
        }

        this.activeSession.setState(0);

        if (this.activeSession.getId() == 0) {
            FlurryAgent.logEvent("Session_Create_Finished");
            new AddSession().execute(this.activeSession);
        } else {
            FlurryAgent.logEvent("Session_Finish_Active");
            new EditSession().execute(this.activeSession);
        }

        setResult(RESULT_OK);
        finish();
    }
}