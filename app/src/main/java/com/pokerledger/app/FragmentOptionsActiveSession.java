package com.pokerledger.app;

import android.app.FragmentManager;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Note;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class FragmentOptionsActiveSession extends DialogFragment implements AdapterView.OnItemClickListener {
    NoteCompleteListener listener;
    ActivityMain activity;
    private Session current;
    private ArrayList<String> options = new ArrayList<String>();
    private ListView list;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoteCompleteListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoteCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_session, null, false);
        list = view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString("SESSION_JSON") != null) {
            Gson gson = new Gson();
            this.current = gson.fromJson(getArguments().getString("SESSION_JSON"), Session.class);

            if (current.onBreak()) {
                options.add("Resume Session");
            }
            else {
                options.add("Pause Session");
            }
            options.add("Add Note");
            options.add("Rebuy / Addon");
            options.add("Finish Session");
            options.add("Delete Session");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.activity = (ActivityMain) getActivity();

        if (position == 0) {
            new ToggleBreak().execute();
        }
        else if (position == 1) {

            FragmentManager manager = getFragmentManager();
            Gson gson = new Gson();
            Note newNote = new Note(current.getId());

            Bundle b = new Bundle();
            b.putString("NOTE_JSON", gson.toJson(newNote));
            FragmentNote dialog = new FragmentNote();

            dialog.setArguments(b);
            dialog.show(manager, "Notes Fragment");
        }
        else if (position == 2) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View adbView = inflater.inflate(R.layout.fragment_rebuy_addon, null);

            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Rebuy/Addon");
            adb.setView(adbView);
            final EditText input = (EditText) adbView.findViewById(R.id.amount);
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    double value = Double.parseDouble(input.getText().toString());
                    new RebuyAddon().execute(value);
                }
            });

            adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            AlertDialog dialog = adb.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }
        else if (position == 3) {
            Intent intent = new Intent(this.activity, ActivityFinishedSession.class);
            intent.putExtra("SESSION_JSON", getArguments().getString("SESSION_JSON"));
            this.activity.startActivityForResult(intent, 2);
        }
        else if (position == 4) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Confirmation");
            adb.setMessage("Are you sure you want to delete this session?");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    new DeleteActive().execute();
                }
            });
            adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                }
            });
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.show();
        }
        dismiss();
    }

    public class ToggleBreak extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(FragmentOptionsActiveSession.this.getActivity());
            db.toggleBreak(FragmentOptionsActiveSession.this.current);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            FragmentOptionsActiveSession.this.activity.notifyListChange();
        }
    }

    public class DeleteActive extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(FragmentOptionsActiveSession.this.activity);
            db.deleteSession(FragmentOptionsActiveSession.this.current.getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            FragmentOptionsActiveSession.this.activity.notifyListChange();
        }
    }

    public class RebuyAddon extends AsyncTask<Double, Void, Void> {
        @Override
        protected Void doInBackground(Double... amount) {
            DatabaseHelper db = DatabaseHelper.getInstance(FragmentOptionsActiveSession.this.activity);
            db.rebuyAddon(FragmentOptionsActiveSession.this.current.getId(), amount[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            FragmentOptionsActiveSession.this.activity.notifyListChange();
        }
    }
}