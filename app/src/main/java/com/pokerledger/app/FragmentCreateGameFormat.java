package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Created by Catface Meowmers on 8/13/15.
 */
public class FragmentCreateGameFormat extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game_format, null, false);
        getDialog().setTitle("Create Game Format");

        Button createGameFormat = (Button) view.findViewById(R.id.create_game_format);
        createGameFormat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int baseFormatId = 1;
                String baseFormat = "Cash Game";
                String gameFormatName = ((EditText) getView().findViewById(R.id.game_format_name)).getText().toString();

                RadioButton tourneyRadio = (RadioButton) getView().findViewById(R.id.radio_tourney);

                if (tourneyRadio.isChecked()) {
                    baseFormatId = 2;
                    baseFormat = "Tournament";
                }

                if (gameFormatName.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_game_format_name), Toast.LENGTH_SHORT).show();
                } else {
                    ((ActivityBase) getActivity()).notifyCreateGameFormat(gameFormatName, baseFormatId, baseFormat);
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

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}