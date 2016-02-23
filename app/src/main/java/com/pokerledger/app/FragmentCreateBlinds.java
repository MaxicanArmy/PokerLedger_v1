package com.pokerledger.app;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Catface Meowmers on 8/16/15.
 */
public class FragmentCreateBlinds extends DialogFragment {
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_blinds, container, false);
        getDialog().setTitle("Create Blinds");

        Button createBlinds = (Button) view.findViewById(R.id.create_blinds);
        createBlinds.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sbText = ((EditText) getView().findViewById(R.id.small_blind)).getText().toString();
                String bbText = ((EditText) getView().getRootView().findViewById(R.id.big_blind)).getText().toString();
                String strText = ((EditText) getView().getRootView().findViewById(R.id.straddle)).getText().toString();
                String biText = ((EditText) getView().getRootView().findViewById(R.id.bring_in)).getText().toString();
                String anteText = ((EditText) getView().getRootView().findViewById(R.id.ante)).getText().toString();
                String ppText = ((EditText) getView().getRootView().findViewById(R.id.points)).getText().toString();

                double sb = 0, bb = 0, straddle = 0, bringIn = 0, ante = 0, perPoint = 0;

                if (!bbText.equals("") && sbText.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_single_blind), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!ppText.equals("") && (!sbText.equals("") || !bbText.equals("") || !strText.equals("") || !biText.equals("") || !anteText.equals(""))) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_points_plus), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!strText.equals("") && (sbText.equals("") || bbText.equals(""))) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_straddle_no_blinds), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!sbText.equals("")) {
                    sb = Double.parseDouble(sbText);
                }
                if (!bbText.equals("")) {
                    bb = Double.parseDouble(bbText);
                }
                if (!strText.equals("")) {
                    straddle = Double.parseDouble(strText);
                }
                if (!biText.equals("")) {
                    bringIn = Double.parseDouble(biText);
                }
                if (!anteText.equals("")) {
                    ante = Double.parseDouble(anteText);
                }
                if (!ppText.equals("")) {
                    perPoint = Double.parseDouble(ppText);
                }

                ((ActivityBase) getActivity()).notifyCreateBlinds(sb, bb, straddle, bringIn, ante, perPoint);
                dismiss();
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

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}