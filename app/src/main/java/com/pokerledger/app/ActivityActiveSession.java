package com.pokerledger.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.pokerledger.app.helper.PLCommon;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityActiveSession extends ActivitySession {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        FlurryAgent.logEvent("Activity_Active_Session");

        Calendar calender = Calendar.getInstance();
        activeSession.setStart(calender.getTimeInMillis());
        ((Button) findViewById(R.id.start_date)).setHint(String.format("%04d-%02d-%02d", calender.get(Calendar.YEAR), calender.get(Calendar.MONTH)+1, calender.get(Calendar.DAY_OF_MONTH)));
        ((Button) findViewById(R.id.start_time)).setHint(String.format("%02d:%02d", calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE)));
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Gson gson = new Gson();

        activeSession = gson.fromJson(savedInstanceState.getString("ACTIVE_SESSION_JSON"), Session.class);
        ((Button) this.findViewById(R.id.start_date)).setHint(savedInstanceState.getString("START_DATE_HINT"));
        ((Button) this.findViewById(R.id.start_time)).setHint(savedInstanceState.getString("START_TIME_HINT"));
    }

    public void saveActiveSession(View v) {

        //begin error checking and capturing the values to be saved in the db
        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, R.string.error_enter_buy_in, Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            this.activeSession.setBuyIn(Integer.parseInt(buyinText));
        }

        Spinner formatSpinner = (Spinner) findViewById(R.id.game_format);

        if (formatSpinner.getSelectedItem() != null) {
            this.activeSession.setGameFormat((GameFormat) formatSpinner.getSelectedItem());

            if (this.activeSession.getGameFormat().getBaseFormatId() == 2) {
                String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

                if (!entrantsText.equals("")) {
                    this.activeSession.setEntrants(Integer.parseInt(entrantsText));
                }
            }
            else {
                Spinner blinds = (Spinner) findViewById(R.id.blinds);

                if (blinds.getSelectedItem() != null) {
                    this.activeSession.setBlinds((Blinds) blinds.getSelectedItem());
                } else {
                    Toast.makeText(this, R.string.error_enter_blinds, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(this, R.string.error_enter_format, Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();

        if (startDate.equals("Start Date")) {
            Toast.makeText(this, R.string.error_enter_start_date, Toast.LENGTH_SHORT).show();
            return;
        }

        String startTime = ((Button) findViewById(R.id.start_time)).getHint().toString();

        if (startTime.equals("Start Time")) {
            Toast.makeText(this, R.string.error_enter_start_time, Toast.LENGTH_SHORT).show();
            return;
        }

        Long start = PLCommon.datetimeToTimestamp(startDate + " " + startTime);

        //decide if time has been changed
        if (this.activeSession.getStart() / 60000 != start / 60000) {
            this.activeSession.setStart(start);
        }

        String note = ((EditText) findViewById(R.id.note)).getText().toString();

        if (!note.equals("")) {
            this.activeSession.setNote(note);
        }


        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

        if (gameSpinner.getSelectedItem() != null) {
            this.activeSession.setGame((Game) gameSpinner.getSelectedItem());
        } else {
            Toast.makeText(this, R.string.error_enter_game, Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner location = (Spinner) findViewById(R.id.location);

        if (location.getSelectedItem() != null) {
            this.activeSession.setLocation((Location) location.getSelectedItem());
        } else {
            Toast.makeText(this, R.string.error_enter_location, Toast.LENGTH_SHORT).show();
            return;
        }

        this.activeSession.setState(1);
        FlurryAgent.logEvent("Session_Create_Active");
        new AddSession().execute(this.activeSession);
        setResult(RESULT_OK);
        finish();
    }
}