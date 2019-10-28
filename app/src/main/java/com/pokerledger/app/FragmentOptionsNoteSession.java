package com.pokerledger.app;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Note;

import java.util.ArrayList;

public class FragmentOptionsNoteSession extends DialogFragment implements AdapterView.OnItemClickListener  {
    NoteCompleteListener listener;
    private Note activeNote;
    private ArrayList<String> options = new ArrayList<>();
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

        if (getArguments().getString("NOTE_JSON") != null) {
            Gson gson = new Gson();
            this.activeNote = gson.fromJson(getArguments().getString("NOTE_JSON"), Note.class);

            options.add("Edit Note");
            options.add("Delete Note");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();

        if (position == 0) {
            FragmentManager manager = getFragmentManager();
            Gson gson = new Gson();

            Bundle b = new Bundle();
            b.putString("NOTE_JSON", gson.toJson(activeNote));

            FragmentNote dialog = new FragmentNote();
            dialog.setArguments(b);
            dialog.show(manager, "Notes Fragment");
        }
        else if (position == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Confirmation");
            adb.setMessage("Are you sure you want to delete this note?");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    new FragmentOptionsNoteSession.DeleteNote().execute();
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

    public class DeleteNote extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper db = DatabaseHelper.getInstance(FragmentOptionsNoteSession.this.getActivity());
            db.deleteNote(activeNote);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listener.onEditNoteComplete(activeNote);
        }
    }
}
