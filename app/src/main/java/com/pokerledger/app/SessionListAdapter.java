package com.pokerledger.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pokerledger.app.model.Session;

import java.util.ArrayList;

public class SessionListAdapter extends ArrayAdapter<Session>{
    private final Activity context;
    private final ArrayList<Session> active;

    public SessionListAdapter(Activity context, ArrayList<Session> active) {
        super(context, R.layout.list_session, active);
        this.context = context;
        this.active = active;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_session, null, true);

        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);
        TextView txtStart = (TextView) rowView.findViewById(R.id.start);
        TextView txtBuyIn = (TextView) rowView.findViewById(R.id.buyin);
        TextView txtGame = (TextView) rowView.findViewById(R.id.game);

        txtLocation.setText(active.get(position).getLocation().getLocation());
        txtStart.setText(active.get(position).getStartDate() + " " + active.get(position).getStartTime());
        txtBuyIn.setText("$" + Integer.toString(active.get(position).getBuyIn()));
        txtGame.setText(active.get(position).getGame().getGame());
        return rowView;
    }
}