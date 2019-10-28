package com.pokerledger.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pokerledger.app.model.Session;
import com.pokerledger.app.helper.PLCommon;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/28/15.
 */

public class ListAdapterHistory extends ArrayAdapter<Session> {
    private final Activity context;
    private final ArrayList<Session> active;

    public ListAdapterHistory(Activity context, ArrayList<Session> active) {
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
        TextView txtTimePlayed = (TextView) rowView.findViewById(R.id.time_played);
        TextView txtProfit = (TextView) rowView.findViewById(R.id.profit);
        TextView txtLocation = (TextView) rowView.findViewById(R.id.location);

        Session current = active.get(position);

        txtGame.setText(current.displayFormat());
        txtDate.setText(PLCommon.timestampToDate(current.getStart()));
        txtTimePlayed.setText(current.lengthFormatted(false));

        if (current.getProfit() < 0 ) {
            txtProfit.setTextColor(Color.parseColor("#ff0000"));
        }
        txtProfit.setText(current.profitFormatted());

        txtLocation.setText(current.getLocation().getLocation());

        return rowView;
    }
}