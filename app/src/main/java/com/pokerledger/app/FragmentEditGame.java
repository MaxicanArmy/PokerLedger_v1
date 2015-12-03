package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import com.pokerledger.app.model.Game;

/**
 * Created by max on 9/3/15.
 */
public class FragmentEditGame extends DialogFragment {
    Game game;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, null, false);
        getDialog().setTitle("Edit Game");

        Gson gson = new Gson();
        String json = getArguments().getString("TARGET_GAME");
        game = gson.fromJson(json, Game.class);

        ((EditText) view.findViewById(R.id.game_name)).setText(game.getGame());

        Button createGame = (Button) view.findViewById(R.id.create_game);
        createGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String gameName = ((EditText) getView().findViewById(R.id.game_name)).getText().toString();

                if (gameName.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_game_name), Toast.LENGTH_SHORT).show();
                } else {
                    game.setGame(gameName);
                    ((ActivitySettings) getActivity()).notifyEditGame(game);
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
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}