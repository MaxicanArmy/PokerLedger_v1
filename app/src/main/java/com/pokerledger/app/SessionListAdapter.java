package com.pokerledger.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pokerledger.app.helper.PLCommon;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

public class SessionListAdapter extends ArrayAdapter<Session>{
    private final Activity context;


    public SessionListAdapter(Activity context, ArrayList<Session> active) {
        super(context, R.layout.list_session, active);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        viewItem.buyin.setText("$" + PLCommon.formatDouble(currentSession.getBuyIn()));
        viewItem.game.setText(currentSession.getGame().getGame());
        return convertView;
    }

    private void setTime(final Session currentSession, final TextView tv, final String tag) {
        if (tv.getTag().toString().equals(tag)) {
            tv.setText(currentSession.lengthFormatted());
        }
    }

    public class ViewItem {
        public TextView location;
        public TextView buyin;
        public TextView game;
        public TextView time;
    }
}