package com.pokerledger.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import com.pokerledger.app.helper.PLCommon;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

/**
 * Created by max on 11/7/15.
 */
public class ActivityEditSession extends ActivitySession  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_finished_session);
        super.onCreate(savedInstanceState);

        String json = getIntent().getStringExtra("SESSION_JSON");
        if (json != null) {
            Gson gson = new Gson();
            this.activeSession = gson.fromJson(json, Session.class);

            ((EditText) findViewById(R.id.buy_in)).setText(PLCommon.formatDouble(this.activeSession.getBuyIn()));
            ((EditText) findViewById(R.id.cash_out)).setText(PLCommon.formatDouble(this.activeSession.getCashOut()));

            ((Button) findViewById(R.id.start_date)).setHint(PLCommon.timestampToDate(this.activeSession.getStart()));
            ((Button) findViewById(R.id.start_time)).setHint(PLCommon.timestampToTime(this.activeSession.getStart()));

            ((Button) findViewById(R.id.end_date)).setHint(PLCommon.timestampToDate(this.activeSession.getEnd()));
            ((Button) findViewById(R.id.end_time)).setHint(PLCommon.timestampToTime(this.activeSession.getEnd()));

            if (this.activeSession.getGameFormat().getBaseFormatId() == 2) {
                ((EditText) findViewById(R.id.entrants)).setText(Integer.toString(this.activeSession.getEntrants()));
                ((EditText) findViewById(R.id.placed)).setText(Integer.toString(this.activeSession.getPlaced()));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activeSession.setLocation((Location) ((Spinner) this.findViewById(R.id.location)).getSelectedItem());
        activeSession.setGame((Game) ((Spinner) this.findViewById(R.id.game)).getSelectedItem());
        activeSession.setGameFormat((GameFormat) ((Spinner) this.findViewById(R.id.game_format)).getSelectedItem());
        activeSession.setBlinds((Blinds) ((Spinner) this.findViewById(R.id.blinds)).getSelectedItem());
        outState.putString("START_DATE_HINT", ((Button) this.findViewById(R.id.start_date)).getHint().toString());
        outState.putString("START_TIME_HINT", ((Button) this.findViewById(R.id.start_time)).getHint().toString());
        outState.putString("END_DATE_HINT", ((Button) this.findViewById(R.id.end_date)).getHint().toString());
        outState.putString("END_TIME_HINT", ((Button) this.findViewById(R.id.end_time)).getHint().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ((Button) this.findViewById(R.id.start_date)).setHint(savedInstanceState.getString("START_DATE_HINT"));
        ((Button) this.findViewById(R.id.start_time)).setHint(savedInstanceState.getString("START_TIME_HINT"));
        ((Button) this.findViewById(R.id.end_date)).setHint(savedInstanceState.getString("END_DATE_HINT"));
        ((Button) this.findViewById(R.id.end_time)).setHint(savedInstanceState.getString("END_TIME_HINT"));
    }

    public void saveFinishedSession(View v) {
        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.error_enter_buy_in), Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            this.activeSession.setBuyIn(Double.parseDouble(buyinText));
        }

        String cashOutText = ((EditText) findViewById(R.id.cash_out)).getText().toString();

        if (cashOutText.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.error_enter_cash_out), Toast.LENGTH_SHORT).show();
            findViewById(R.id.cash_out).requestFocus();
            return;
        }
        else {
            this.activeSession.setCashOut(Double.parseDouble(cashOutText));
        }

        Spinner locationSpinner = (Spinner) findViewById(R.id.location);

        if (locationSpinner.getSelectedItem() != null) {
            this.activeSession.setLocation((Location) locationSpinner.getSelectedItem());
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_enter_location), Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

        if (gameSpinner.getSelectedItem() != null) {
            this.activeSession.setGame((Game) gameSpinner.getSelectedItem());
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_enter_game), Toast.LENGTH_SHORT).show();
            return;
        }

        Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_format);

        if (gameFormatSpinner.getSelectedItem() != null) {
            this.activeSession.setGameFormat((GameFormat) gameFormatSpinner.getSelectedItem());

            if (this.activeSession.getGameFormat().getBaseFormatId() == 2) {
                String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

                if (entrantsText.equals("")) {
                    Toast.makeText(this, getResources().getString(R.string.error_enter_entrants), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.entrants).requestFocus();
                    return;
                }
                else {
                    this.activeSession.setEntrants(Integer.parseInt(entrantsText));
                }

                String placedText = ((EditText) findViewById(R.id.placed)).getText().toString();

                if (placedText.equals("")) {
                    Toast.makeText(this, getResources().getString(R.string.error_enter_placed), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, getResources().getString(R.string.error_enter_blinds), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_enter_format), Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();

        if (startDate.equals("Start Date")) {
            Toast.makeText(this, getResources().getString(R.string.error_enter_start_date), Toast.LENGTH_SHORT).show();
            return;
        }

        String startTime = ((Button) findViewById(R.id.start_time)).getHint().toString();

        if (startTime.equals("Start Time")) {
            Toast.makeText(this, getResources().getString(R.string.error_enter_start_time), Toast.LENGTH_SHORT).show();
            return;
        }

        String endDate = ((Button) findViewById(R.id.end_date)).getHint().toString();

        if (endDate.equals("End Date")) {
            Toast.makeText(this, getResources().getString(R.string.error_enter_end_date), Toast.LENGTH_SHORT).show();
            return;
        }

        String endTime = ((Button) findViewById(R.id.end_time)).getHint().toString();

        if (endTime.equals("End Time")) {
            Toast.makeText(this, getResources().getString(R.string.error_enter_end_time), Toast.LENGTH_SHORT).show();
            return;
        }

        Long start = PLCommon.datetimeToTimestamp(startDate + " " + startTime);
        Long end = PLCommon.datetimeToTimestamp(endDate + " " + endTime);

        //decide if start time has been changed
        if (this.activeSession.getStart() / 60000 != start / 60000) {
            this.activeSession.setStart(start);
        }

        //decide if end time has been changed
        if (this.activeSession.getEnd() / 60000 != end / 60000) {
            this.activeSession.setEnd(end);
        }

        if (this.activeSession.lengthMillis() < 0) {
            Toast.makeText(this, getResources().getString(R.string.error_negative_length), Toast.LENGTH_SHORT).show();
            return;
        }

        this.activeSession.setState(0);

        new EditSession().execute(this.activeSession);

        setResult(RESULT_OK);
        finish();
    }
}