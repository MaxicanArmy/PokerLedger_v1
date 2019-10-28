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
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.GameFormat;

/**
 * Created by max on 9/3/15.
 */
public class FragmentGameFormat extends DialogFragment {
    GameFormatCompleteListener listener;
    GameFormat gameFormat = new GameFormat();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (GameFormatCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement GameFormatCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game_format, null, false);
        getDialog().setTitle("Edit Game Format");

        Gson gson = new Gson();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("TARGET_GAME_FORMAT")) {
            String json = getArguments().getString("TARGET_GAME_FORMAT");
            gameFormat = gson.fromJson(json, GameFormat.class);
        }

        ((EditText) view.findViewById(R.id.game_format_name)).setText(gameFormat.getGameFormat());

        if (gameFormat.getBaseFormatId() == 2) {
            ((RadioButton) view.findViewById(R.id.radio_tourney)).setChecked(true);
            ((RadioButton) view.findViewById(R.id.radio_cash)).setChecked(false);
        }

        Button createGameFormat = view.findViewById(R.id.create_game_format);
        createGameFormat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int baseFormatId = 1;
                String baseFormat = "Cash Game";
                String gameFormatName = ((EditText) getView().findViewById(R.id.game_format_name)).getText().toString();

                RadioButton tourneyRadio = getView().findViewById(R.id.radio_tourney);

                if (tourneyRadio.isChecked()) {
                    baseFormatId = 2;
                    baseFormat = "Tournament";
                }

                if (gameFormatName.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_game_format_name), Toast.LENGTH_SHORT).show();
                } else {
                    gameFormat.setGameFormat(gameFormatName);
                    gameFormat.setBaseFormatId(baseFormatId);
                    gameFormat.setBaseFormat(baseFormat);
                    new SaveGameFormat().execute();
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

    public class SaveGameFormat extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getActivity());
            if (gameFormat.getId() == 0) {
                db.createGameFormat(gameFormat);
            } else {
                db.editGameFormat(gameFormat);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listener.onEditGameFormatComplete(gameFormat);
            dismiss();
        }
    }
}