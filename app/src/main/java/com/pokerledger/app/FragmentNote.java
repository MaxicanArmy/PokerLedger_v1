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

import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Note;

public class FragmentNote extends DialogFragment {
    NoteCompleteListener listener;
    Note note = new Note();

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
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, null, false);

        Gson gson = new Gson();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("NOTE_JSON")) {
            String json = getArguments().getString("NOTE_JSON");
            note = gson.fromJson(json, Note.class);
        }

        ((EditText) view.findViewById(R.id.note)).setText(note.getNote());

        Button addNote = view.findViewById(R.id.save_note);
        addNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String noteText = ((EditText) getView().findViewById(R.id.note)).getText().toString();

                note.setNote(noteText);
                if (note.getId() == 0) {
                    new CreateNote().execute(note);
                } else {
                    new EditNote().execute(note);
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

    public class CreateNote extends AsyncTask<Note, Void, Note> {
        @Override
        protected Note doInBackground(Note... n) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getActivity());
            return db.createNote(n[0]);
        }

        @Override
        protected void onPostExecute(Note result) {
            note = result;
            listener.onEditNoteComplete(note);
            dismiss();
        }
    }

    public class EditNote extends AsyncTask<Note, Void, Note> {
        @Override
        protected Note doInBackground(Note... n) {
            DatabaseHelper db;
            db = DatabaseHelper.getInstance(getActivity());
            db.editNote(n[0]);
            return n[0];
        }

        @Override
        protected void onPostExecute(Note result) {
            note = result;
            listener.onEditNoteComplete(note);
            dismiss();
        }
    }
}
