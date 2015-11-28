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
    public View getView(int position, View convertView, ViewGroup parent) {
        /* old code that "works"
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_session, null, true);

        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);
        //TextView txtStart = (TextView) rowView.findViewById(R.id.start);
        TextView txtBuyIn = (TextView) rowView.findViewById(R.id.buyin);
        TextView txtGame = (TextView) rowView.findViewById(R.id.game);

        txtLocation.setText(active.get(position).getLocation().getLocation());
        //txtStart.setText(active.get(position).getStartDate() + " " + active.get(position).getStartTime());
        txtBuyIn.setText("$" + Integer.toString(active.get(position).getBuyIn()));
        txtGame.setText(active.get(position).getGame().getGame());

        return rowView;
        */
        LayoutInflater inflater = context.getLayoutInflater();
        ViewItem viewItem;
        Session currentSession = getItem(position);
        if (convertView == null) {
            viewItem = new ViewItem();
            convertView = inflater.inflate(R.layout.list_session, null);
            viewItem.location = (TextView) convertView.findViewById(R.id.location);
            viewItem.buyin = (TextView) convertView.findViewById(R.id.buyin);
            viewItem.game = (TextView) convertView.findViewById(R.id.game);
            viewItem.time = (TextView) convertView.findViewById(R.id.start);
            viewItem.time.setTag(position);
            convertView.setTag(viewItem);
        } else {
            viewItem = (ViewItem) convertView.getTag();
        }
        setTime(currentSession, viewItem.time, viewItem.time.getTag().toString());
        viewItem.location.setText(currentSession.getLocation().getLocation());
        viewItem.buyin.setText("$" + Integer.toString(currentSession.getBuyIn()));
        viewItem.game.setText(currentSession.getGame().getGame());
        return convertView;
    }

    private void setTime(final Session currentSession, final TextView tv, final String tag) {
        if (tv.getTag().toString().equals(tag)) {
            long timeMillis = currentSession.lengthMillis();
            int secs = (int) (timeMillis / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            tv.setText("" + mins + ":"
                    + String.format("%02d", secs));
        }
    }

    public class ViewItem {
        public TextView location;
        public TextView buyin;
        public TextView game;
        public TextView time;
    }
}