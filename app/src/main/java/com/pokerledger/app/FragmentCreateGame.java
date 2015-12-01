package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Catface Meowmers on 8/16/15.
 */
public class FragmentCreateGame extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, null, false);
        getDialog().setTitle("Create Game");

        Button createGame = (Button) view.findViewById(R.id.create_game);
        createGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String gameName = ((EditText) getView().findViewById(R.id.game_name)).getText().toString();

                if (gameName.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_game_name), Toast.LENGTH_SHORT).show();
                } else {
                    ((ActivityBase) getActivity()).notifyCreateGame(gameName);
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