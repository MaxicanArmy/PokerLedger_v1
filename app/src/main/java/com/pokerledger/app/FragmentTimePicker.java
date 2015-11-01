package com.pokerledger.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class FragmentTimePicker extends DialogFragment {
    TimePickerDialog.OnTimeSetListener onTimeSet;

    public void setCallBack(TimePickerDialog.OnTimeSetListener onTime) {
        onTimeSet = onTime;
    }

    private int hour, min;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        min = args.getInt("min");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), onTimeSet, hour, min, true);
    }
}