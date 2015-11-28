package com.pokerledger.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Break;
import com.pokerledger.app.model.Session;
import com.pokerledger.app.helper.PLCommon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/28/15.
 */
public class HistoryListAdapter extends ArrayAdapter<Session> {
    private final Activity context;
    private final ArrayList<Session> active;

    public HistoryListAdapter(Activity context, ArrayList<Session> active) {
        super(context, R.layout.list_history, active);
        this.context = context;
        this.active = active;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_history, null, true);

        TextView txtGame = (TextView) rowView.findViewById(R.id.game);
        TextView txtDate = (TextView) rowView.findViewById(R.id.date);
        TextView txtGameFormat = (TextView) rowView.findViewById(R.id.game_format);
        TextView txtProfit = (TextView) rowView.findViewById(R.id.profit);
        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);
        TextView txtTimePlayed = (TextView) rowView.findViewById(R.id.time_played);

        Session current = active.get(position);
        SessionSet stats = new SessionSet(active.get(position));

        txtGame.setText(current.displayFormat());
        txtDate.setText(PLCommon.timestampToDate(current.getStart()));

        txtGameFormat.setText(current.getGameFormat().getBaseFormat());

        if (stats.getProfit() < 0 ) {
            txtProfit.setTextColor(Color.parseColor("#ff0000"));
        }
        txtProfit.setText(stats.profitFormatted());

        txtLocation.setText(current.getLocation().getLocation());
        txtTimePlayed.setText(stats.lengthFormatted());

        return rowView;
    }

}