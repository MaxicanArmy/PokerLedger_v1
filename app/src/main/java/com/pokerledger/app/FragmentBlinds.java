package com.pokerledger.app;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.PLCommon;
import com.pokerledger.app.model.Blinds;

/**
 * Created by max on 9/3/15.
 */
public class FragmentBlinds extends DialogFragment {
    BlindsCompleteListener listener;
    Blinds blinds = new Blinds();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BlindsCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BlindsCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_blinds, null, false);
        getDialog().setTitle("Edit Blinds");

        Gson gson = new Gson();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("TARGET_BLINDS")) {
            String json = getArguments().getString("TARGET_BLINDS");
            blinds = gson.fromJson(json, Blinds.class);
        }

        if (blinds.getSB() != 0) {
            ((EditText) view.findViewById(R.id.small_blind)).setText(PLCommon.formatDouble(blinds.getSB()));
        }

        if (blinds.getBB() != 0) {
            ((EditText) view.findViewById(R.id.big_blind)).setText(PLCommon.formatDouble(blinds.getBB()));
        }

        if (blinds.getStraddle() != 0) {
            ((EditText) view.findViewById(R.id.straddle)).setText(PLCommon.formatDouble(blinds.getStraddle()));
        }

        if (blinds.getBringIn() != 0) {
            ((EditText) view.findViewById(R.id.bring_in)).setText(PLCommon.formatDouble(blinds.getBringIn()));
        }

        if (blinds.getAnte() != 0) {
            ((EditText) view.findViewById(R.id.ante)).setText(PLCommon.formatDouble(blinds.getAnte()));
        }

        if (blinds.getPerPoint() != 0) {
            ((EditText) view.findViewById(R.id.points)).setText(PLCommon.formatDouble(blinds.getPerPoint()));
        }

        Button createBlinds = view.findViewById(R.id.create_blinds);
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
                new SaveBlinds().execute();
            }
        });

        Button cancel = view.findViewById(R.id.cancel);
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

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public class SaveBlinds extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getActivity());
            if (blinds.getId() == 0) {
                db.createBlinds(blinds);
            } else {
                db.editBlinds(blinds);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listener.onEditBlindsComplete(blinds);
            dismiss();
        }
    }
}