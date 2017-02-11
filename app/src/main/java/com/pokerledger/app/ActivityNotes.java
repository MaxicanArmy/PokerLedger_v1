package com.pokerledger.app;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Max on 10/9/2016.
 */

public class ActivityNotes extends ActivityBase {
    ListView list;
    NotesListAdapter adapter;
    LinearLayout notesWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        FlurryAgent.logEvent("Activity_Notes");

        list = (ListView)findViewById(R.id.notes_list);

        notesWrapper = (LinearLayout) findViewById(R.id.notes_wrapper);
        new ActivityNotes.LoadActiveSessions().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                FragmentEditNoteSession dialog = new FragmentEditNoteSession();

                dialog.setArguments(b);
                dialog.show(manager, "EditSession");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void notifyListChange() {
        //this method is necessary because i cant get fragments to call async tasks
        new ActivityNotes.LoadActiveSessions().execute();
    }

    public class LoadActiveSessions extends AsyncTask<Void, Void, Void> {
        ArrayList<Session> sessions = new ArrayList<>();

        public LoadActiveSessions() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            sessions = dbHelper.getNotes();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new NotesListAdapter(ActivityNotes.this, sessions);
            list.setAdapter(adapter);

            if (sessions.size() > 0) {
                notesWrapper.setVisibility(View.VISIBLE);
            } else {
                notesWrapper.setVisibility(View.GONE);
            }
        }
    }
}

