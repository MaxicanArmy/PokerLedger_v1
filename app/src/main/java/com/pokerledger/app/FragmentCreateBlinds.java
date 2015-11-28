package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pokerledger.app.model.Blinds;

/**
 * Created by Catface Meowmers on 8/16/15.
 */
public class FragmentCreateBlinds extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_blinds, null, false);
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

                int sb = 0, bb = 0, straddle = 0, bringIn = 0, ante = 0, perPoint = 0;

                if (!bbText.equals("") && sbText.equals("")) {
                    Toast.makeText(getActivity(), R.string.error_single_blind, Toast.LENGTH_LONG).show();
                    return;
                }

                if (!ppText.equals("") && (!sbText.equals("") || !bbText.equals("") || !strText.equals("") || !biText.equals("") || !anteText.equals(""))) {
                    Toast.makeText(getActivity(), R.string.error_points_plus, Toast.LENGTH_LONG).show();
                    return;
                }

                if (!strText.equals("") && (sbText.equals("") || bbText.equals(""))) {
                    Toast.makeText(getActivity(), R.string.error_straddle_no_blinds, Toast.LENGTH_LONG).show();
                    return;
                }

                if (!sbText.equals("")) {
                    sb = Integer.parseInt(sbText);
                }
                if (!bbText.equals("")) {
                    bb = Integer.parseInt(bbText);
                }
                if (!strText.equals("")) {
                    straddle = Integer.parseInt(strText);
                }
                if (!biText.equals("")) {
                    bringIn = Integer.parseInt(biText);
                }
                if (!anteText.equals("")) {
                    ante = Integer.parseInt(anteText);
                }
                if (!ppText.equals("")) {
                    perPoint = Integer.parseInt(ppText);
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
}