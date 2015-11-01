package com.pokerledger.app;

/**
 * Created by Catface Meowmers on 7/27/15.
 */
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokerledger.app.model.Break;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Max on 9/26/14.
 */
public class FragmentAddBreak extends DialogFragment {
    View view;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("BREAK_START_DATE_HINT", ((Button) view.findViewById(R.id.break_start_date)).getHint().toString());
        outState.putString("BREAK_START_TIME_HINT", ((Button) view.findViewById(R.id.break_start_time)).getHint().toString());
        outState.putString("BREAK_END_DATE_HINT", ((Button) view.findViewById(R.id.break_end_date)).getHint().toString());
        outState.putString("BREAK_END_TIME_HINT", ((Button) view.findViewById(R.id.break_end_time)).getHint().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ((Button) view.findViewById(R.id.break_start_date)).setHint(savedInstanceState.getString("BREAK_START_DATE_HINT"));
            ((Button) view.findViewById(R.id.break_start_time)).setHint(savedInstanceState.getString("BREAK_START_TIME_HINT"));
            ((Button) view.findViewById(R.id.break_end_date)).setHint(savedInstanceState.getString("BREAK_END_DATE_HINT"));
            ((Button) view.findViewById(R.id.break_end_time)).setHint(savedInstanceState.getString("BREAK_END_TIME_HINT"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_break, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button startDate = (Button) view.findViewById(R.id.break_start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ActivitySession) getActivity()).showDatePickerDialog(v);
            }
        });

        Button startTime = (Button) view.findViewById(R.id.break_start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ActivitySession) getActivity()).showTimePickerDialog(v);
            }
        });

        Button endDate = (Button) view.findViewById(R.id.break_end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ActivitySession) getActivity()).showDatePickerDialog(v);
            }
        });

        Button endTime = (Button) view.findViewById(R.id.break_end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ActivitySession) getActivity()).showTimePickerDialog(v);
            }
        });

        Button addBreak = (Button) view.findViewById(R.id.add_break);
        addBreak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivitySession a = (ActivitySession) getActivity();

                String startDate = ((Button) getView().findViewById(R.id.break_start_date)).getHint().toString();
                String endDate = ((Button) getView().findViewById(R.id.break_end_date)).getHint().toString();
                String startTime = ((Button) getView().findViewById(R.id.break_start_time)).getHint().toString();
                String endTime = ((Button) getView().findViewById(R.id.break_end_time)).getHint().toString();

                String start = startDate + " " + startTime;
                String end = endDate + " " + endTime;

                //error conditions
                //add error condition for if nothing has been entered in one of the buttons
                //1. Break end datetime is before break start datetime
                if (end.compareTo(start) <= 0) {
                    Toast.makeText(a, "End date/time must be after start date/time.", Toast.LENGTH_SHORT).show();
                }
                //2. Break starts before session start datetime
                else if (start.compareTo(a.activeSession.getStartDate() + " " + a.activeSession.getStartTime()) < 0) {
                    Toast.makeText(a, "The break can't start before the session's start date/time.", Toast.LENGTH_SHORT).show();
                }
                //3. Break ends after session end datetime
                else if (end.compareTo(a.activeSession.getEndDate() + " " + a.activeSession.getEndTime()) > 0) {
                    Toast.makeText(a, "The break can't end after the session's end date/time.", Toast.LENGTH_SHORT).show();
                }
                //4. Any part of the break overlaps with another break
                else {
                    ArrayList<Break> ba = a.activeSession.getBreaks();
                    if (!ba.isEmpty()) {
                        for (Break b : ba) {
                            if ((start.compareTo(b.getEndDate() + " " + b.getEndTime()) < 0) && (end.compareTo(b.getStartDate() + " " + b.getEndTime()) > 0)) {
                                Toast.makeText(a, "The break cannot overlap with another break in this session.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    ba.add(new Break(startDate, startTime, endDate, endTime));
                    a.activeSession.setBreaks(ba);
                    dismiss();
                }
            }
        });

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}