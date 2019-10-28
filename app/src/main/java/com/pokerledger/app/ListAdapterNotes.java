package com.pokerledger.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pokerledger.app.helper.PLCommon;
import com.pokerledger.app.model.Note;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Max on 10/9/2016.
 */

public class ListAdapterNotes extends ArrayAdapter<Note> {
    private final Activity context;
    private final ArrayList<Note> notes;

    public ListAdapterNotes(Activity context, ArrayList<Note> active) {
        super(context, R.layout.list_notes, active);
        this.context = context;
        this.notes = active;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_notes, null, true);

        TextView txtNote = (TextView) rowView.findViewById(R.id.note);

        Note current = notes.get(position);

        try {
            txtNote.setText(current.getNote());
        } catch (NullPointerException e) {
            txtNote.setText("");
        }

        return rowView;
    }
}