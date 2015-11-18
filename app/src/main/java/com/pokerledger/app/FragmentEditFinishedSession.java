package com.pokerledger.app;

import android.support.v7.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/28/15.
 */
public class FragmentEditFinishedSession extends DialogFragment implements AdapterView.OnItemClickListener {
    ActivityHistory activity;
    private Session active;
    private ArrayList<String> options = new ArrayList<>();
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_session, null, false);
        list = (ListView) view.findViewById(R.id.list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString("SESSION_JSON") != null) {
            Gson gson = new Gson();
            this.active = gson.fromJson(getArguments().getString("SESSION_JSON"), Session.class);

            options.add("Edit Session");
            options.add("Delete Session");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.activity = (ActivityHistory) getActivity();
        dismiss();

        if (position == 0) {
            Intent intent = new Intent(this.activity, ActivityEditSession.class);
            intent.putExtra("SESSION_JSON", getArguments().getString("SESSION_JSON"));
            this.activity.startActivityForResult(intent, 2);
        }
        else if (position == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Confirmation");
            adb.setMessage("Are you sure you want to delete this session?");
            //dialog.setCancelable(false);
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    new DeleteFinished().execute();
                    FlurryAgent.logEvent("Action_Delete_Finished");
                }
            });
            adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                }
            });
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.show();
        }
    }

    public class DeleteFinished extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = new DatabaseHelper(FragmentEditFinishedSession.this.activity);
            db.deleteSession(FragmentEditFinishedSession.this.active.getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            FragmentEditFinishedSession.this.activity.notifyListChange();
        }
    }
}