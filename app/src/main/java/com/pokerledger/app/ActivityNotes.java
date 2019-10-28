package com.pokerledger.app;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Note;

import java.util.ArrayList;

/**
 * Created by Max on 10/9/2016.
 */

public class ActivityNotes extends ActivityBase implements NoteCompleteListener<Note> {
    ListView list;
    ListAdapterNotes adapter;
    LinearLayout notesWrapper;
    ArrayList<Note> notes = new ArrayList<>();

    public void onEditNoteComplete(Note n) {
        new LoadNotes().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        list = findViewById(R.id.notes_list);
        adapter = new ListAdapterNotes(ActivityNotes.this, notes);
        list.setAdapter(adapter);

        notesWrapper = (LinearLayout) findViewById(R.id.notes_wrapper);
        new LoadNotes().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("NOTE_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                FragmentOptionsNote dialog = new FragmentOptionsNote();

                dialog.setArguments(b);
                dialog.show(manager, "EditSession");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class LoadNotes extends AsyncTask<Void, Void, ArrayList<Note>> {

        public LoadNotes() {}

        @Override
        protected ArrayList<Note> doInBackground(Void... params) {
            ArrayList<Note> notes;

            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
            notes = dbHelper.getAllNotes();

            return notes;
        }

        @Override
        protected void onPostExecute(ArrayList<Note> result) {
            ActivityNotes.this.notes.clear();
            ActivityNotes.this.notes.addAll(result);
            adapter.notifyDataSetChanged();
        }
    }
}

