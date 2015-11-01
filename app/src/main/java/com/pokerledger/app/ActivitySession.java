package com.pokerledger.app;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivitySession extends ActivityBase {
    View activeView;

    @Override
    protected void onStart() {
        super.onStart();
        Spinner gameFormatSpinner = (Spinner) this.findViewById(R.id.game_format);

        gameFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                View cash = ActivitySession.this.findViewById(R.id.cash_wrapper);
                View tournament = ActivitySession.this.findViewById(R.id.tournament_wrapper);

                switch (((GameFormat) parentView.getItemAtPosition(position)).getBaseFormatId()) {
                    case 1:
                        cash.setVisibility(View.VISIBLE);
                        tournament.setVisibility(View.GONE);
                        break;
                    case 2:
                        cash.setVisibility(View.GONE);
                        tournament.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //load locations, games, gameformats and blinds from their database tables in to the spinners
        //these async tasks also call set functions to select the correct item in spinners for the activeSession
        new LoadLocations().execute();
        new LoadGames().execute();
        new LoadGameFormats().execute();
        new LoadBlinds().execute();
    }

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

        }
    };

    public void showTimePickerDialog(View v) {
        activeView = v;
        FragmentTimePicker time = new FragmentTimePicker();
        /**
         * Set Up Current Time Into dialog
         */
        Button timeBtn = (Button) v;
        Bundle args = new Bundle();
        if (timeBtn.getHint().toString().matches("[A-Za-z]* Time$")) {
            Calendar calender = Calendar.getInstance();
            args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
            args.putInt("min", calender.get(Calendar.MINUTE));
        }
        else {
            Pattern DATE_PATTERN = Pattern.compile("^(\\d{2}):(\\d{2})$");
            Matcher m = DATE_PATTERN.matcher(timeBtn.getHint().toString());

            while (m.find()) {
                args.putInt("hour", Integer.parseInt(m.group(1)));
                args.putInt("min", Integer.parseInt(m.group(2)));
            }

        }
        time.setArguments(args);
        /**
         * Set Call back to capture selected time
         */
        time.setCallBack(ontime);
        time.show(getFragmentManager(), "TimePicker");
    }

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int min) {
            Button b = (Button) activeView;
            b.setHint(String.format("%02d:%02d", hour, min));
        }
    };

    public void showBreaksDialog(View v) {
        if (activeSession.getBreaks().size() == 0) {
            Toast.makeText(this, "There are no breaks associated with this session.", Toast.LENGTH_LONG).show();
        }
        else {
            FragmentManager manager = getFragmentManager();
            Gson gson = new Gson();

            Bundle b = new Bundle();
            b.putString("SESSION_JSON", gson.toJson(activeSession));

            FragmentBreakViewer dialog = new FragmentBreakViewer();
            dialog.setArguments(b);
            dialog.show(manager, "ViewBreaks");
        }
    }

    public void showCreateBreakDialog(View v) {
        String startDate = ((Button) this.findViewById(R.id.start_date)).getHint().toString();
        String startTime = ((Button) this.findViewById(R.id.start_time)).getHint().toString();
        String endDate = ((Button) this.findViewById(R.id.end_date)).getHint().toString();
        String endTime = ((Button) this.findViewById(R.id.end_time)).getHint().toString();

        if (startDate.equals("Start Date") || startTime.equals("Start Time") || endDate.equals("End Date") || endTime.equals("End Time")) {
            Toast.makeText(this, "Set start/end date/time before adding breaks.", Toast.LENGTH_SHORT).show();
        }
        else {
            this.activeSession.setStartDate(startDate);
            this.activeSession.setStartTime(startTime);
            this.activeSession.setEndDate(endDate);
            this.activeSession.setEndTime(endTime);
            FragmentManager manager = getFragmentManager();
            FragmentAddBreak dialog = new FragmentAddBreak();
            dialog.show(manager, "AddBreak");
        }
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
            new AddSession().execute(this.activeSession);
        } else {
            new EditSession().execute(this.activeSession);
        }

        setResult(RESULT_OK);
        finish();
    }

    public class AddSession extends AsyncTask<Session, Void, Void> {

        @Override
        protected Void doInBackground(Session... s) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.addSession(s[0]);

            return null;
        }
    }

    public class EditSession extends AsyncTask<Session, Void, Void> {

        @Override
        protected Void doInBackground(Session... s) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            db.editSession(s[0]);

            return null;
        }
    }
}