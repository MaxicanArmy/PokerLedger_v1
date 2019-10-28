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
import com.pokerledger.app.model.Game;

/**
 * Created by max on 9/3/15.
 */
public class FragmentGame extends DialogFragment {
    GameCompleteListener listener;
    Game game = new Game();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (GameCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GameCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, null, false);
        getDialog().setTitle("Edit Game");

        Gson gson = new Gson();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("TARGET_GAME")) {
            String json = getArguments().getString("TARGET_GAME");
            game = gson.fromJson(json, Game.class);
        }

        ((EditText) view.findViewById(R.id.game_name)).setText(game.getGame());

        Button createGame = view.findViewById(R.id.create_game);
        createGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String gameName = ((EditText) getView().findViewById(R.id.game_name)).getText().toString();

                if (gameName.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_game_name), Toast.LENGTH_SHORT).show();
                } else {
                    game.setGame(gameName);
                    new SaveGame().execute();
                }
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

    public class SaveGame extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getActivity());
            if (game.getId() == 0) {
                db.createGame(game);
            } else {
                db.editGame(game);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listener.onEditGameComplete(game);
            dismiss();
        }
    }
}