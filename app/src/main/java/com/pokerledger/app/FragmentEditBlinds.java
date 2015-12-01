package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokerledger.app.model.Blinds;

/**
 * Created by max on 9/3/15.
 */
public class FragmentEditBlinds extends DialogFragment {
    Blinds blinds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_blinds, null, false);
        getDialog().setTitle("Edit Blinds");

        Gson gson = new Gson();
        String json = getArguments().getString("TARGET_BLINDS");
        blinds = gson.fromJson(json, Blinds.class);

        if (blinds.getSB() != 0) {
            ((EditText) view.findViewById(R.id.small_blind)).setText(Double.toString(blinds.getSB()));
        }

        if (blinds.getBB() != 0) {
            ((EditText) view.findViewById(R.id.big_blind)).setText(Double.toString(blinds.getBB()));
        }

        if (blinds.getStraddle() != 0) {
            ((EditText) view.findViewById(R.id.straddle)).setText(Double.toString(blinds.getStraddle()));
        }

        if (blinds.getBringIn() != 0) {
            ((EditText) view.findViewById(R.id.bring_in)).setText(Double.toString(blinds.getBringIn()));
        }

        if (blinds.getAnte() != 0) {
            ((EditText) view.findViewById(R.id.ante)).setText(Double.toString(blinds.getAnte()));
        }

        if (blinds.getPerPoint() != 0) {
            ((EditText) view.findViewById(R.id.points)).setText(Double.toString(blinds.getPerPoint()));
        }

        Button createBlinds = (Button) view.findViewById(R.id.create_blinds);
        createBlinds.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sbText = ((EditText) getView().findViewById(R.id.small_blind)).getText().toString();
                String bbText = ((EditText) getView().findViewById(R.id.big_blind)).getText().toString();
                String strText = ((EditText) getView().findViewById(R.id.straddle)).getText().toString();
                String biText = ((EditText) getView().findViewById(R.id.bring_in)).getText().toString();
                String anteText = ((EditText) getView().findViewById(R.id.ante)).getText().toString();
                String ppText = ((EditText) getView().findViewById(R.id.points)).getText().toString();

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
                    blinds.setSB(Double.parseDouble(sbText));
                }
                if (!bbText.equals("")) {
                    blinds.setBB(Double.parseDouble(bbText));
                }
                if (!strText.equals("")) {
                    blinds.setStraddle(Double.parseDouble(strText));
                }
                if (!biText.equals("")) {
                    blinds.setBringIn(Double.parseDouble(biText));
                }
                if (!anteText.equals("")) {
                    blinds.setAnte(Double.parseDouble(anteText));
                }
                if (!ppText.equals("")) {
                    blinds.setPerPoint(Double.parseDouble(ppText));
                }

                ((ActivitySettings) getActivity()).notifyEditBlinds(blinds);
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