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
            ((EditText) view.findViewById(R.id.small_blind)).setText(Integer.toString(blinds.getSB()));
        }

        if (blinds.getBB() != 0) {
            ((EditText) view.findViewById(R.id.big_blind)).setText(Integer.toString(blinds.getBB()));
        }

        if (blinds.getStraddle() != 0) {
            ((EditText) view.findViewById(R.id.straddle)).setText(Integer.toString(blinds.getStraddle()));
        }

        if (blinds.getBringIn() != 0) {
            ((EditText) view.findViewById(R.id.bring_in)).setText(Integer.toString(blinds.getBringIn()));
        }

        if (blinds.getAnte() != 0) {
            ((EditText) view.findViewById(R.id.ante)).setText(Integer.toString(blinds.getAnte()));
        }

        if (blinds.getPerPoint() != 0) {
            ((EditText) view.findViewById(R.id.points)).setText(Integer.toString(blinds.getPerPoint()));
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
                    Toast.makeText(getActivity(), "Blinds not added. If there is only one blind enter it in SB.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!ppText.equals("") && (!sbText.equals("") || !bbText.equals("") || !strText.equals("") || !biText.equals("") || !anteText.equals(""))) {
                    Toast.makeText(getActivity(), "Blinds not added. You cannot enter other blinds when using points.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!strText.equals("") && (sbText.equals("") || bbText.equals(""))) {
                    Toast.makeText(getActivity(), "Blinds not added. You must enter a SB and BB to enter a straddle.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!sbText.equals("")) {
                    blinds.setSB(Integer.parseInt(sbText));
                }
                if (!bbText.equals("")) {
                    blinds.setBB(Integer.parseInt(bbText));
                }
                if (!strText.equals("")) {
                    blinds.setStraddle(Integer.parseInt(strText));
                }
                if (!biText.equals("")) {
                    blinds.setBringIn(Integer.parseInt(biText));
                }
                if (!anteText.equals("")) {
                    blinds.setAnte(Integer.parseInt(anteText));
                }
                if (!ppText.equals("")) {
                    blinds.setPerPoint(Integer.parseInt(ppText));
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