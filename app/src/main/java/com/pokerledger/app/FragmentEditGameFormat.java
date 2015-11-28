package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;

/**
 * Created by max on 9/3/15.
 */
public class FragmentEditGameFormat extends DialogFragment {
    GameFormat gameFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game_format, null, false);
        getDialog().setTitle("Edit Game Format");

        Gson gson = new Gson();
        String json = getArguments().getString("TARGET_GAME_FORMAT");
        gameFormat = gson.fromJson(json, GameFormat.class);

        ((EditText) view.findViewById(R.id.game_format_name)).setText(gameFormat.getGameFormat());

        if (gameFormat.getBaseFormatId() == 2) {
            ((RadioButton) view.findViewById(R.id.radio_tourney)).setChecked(true);
            ((RadioButton) view.findViewById(R.id.radio_cash)).setChecked(false);
        }

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
                    Toast.makeText(getActivity(), R.string.error_no_game_format_name, Toast.LENGTH_SHORT).show();
                } else {
                    gameFormat.setGameFormat(gameFormatName);
                    gameFormat.setBaseFormatId(baseFormatId);
                    gameFormat.setBaseFormat(baseFormat);
                    ((ActivitySettings) getActivity()).notifyEditGameFormat(gameFormat);
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