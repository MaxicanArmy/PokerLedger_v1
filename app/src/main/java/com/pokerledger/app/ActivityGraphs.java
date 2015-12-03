package com.pokerledger.app;

import android.os.AsyncTask;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.SessionSet;

/**
 * Created by max on 9/18/15.
 */
public class ActivityGraphs extends ActivityBase {
    GraphView graph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        FlurryAgent.logEvent("Activity_Graphs");

        graph = (GraphView) findViewById(R.id.graph);
        //graph.getGridLabelRenderer().setVerticalAxisTitle("profit");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("hours");
        graph.getViewport().setScrollable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadDataPoints().execute();

    }
    public class LoadDataPoints extends AsyncTask<Void, Void, SessionSet> {

        @Override
        protected SessionSet doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            return new SessionSet(dbHelper.getSessions(0, "ASC"));
        }

        @Override
        protected void onPostExecute(SessionSet set) {
            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        // show normal x values
                        return super.formatLabel(value, isValueX);
                    } else {
                        // show currency for y values
                        return super.formatLabel(value, isValueX) + "$";
                    }
                }
            });

            if (set.getSessions().size() > 0) {
                double minY = set.getMinY(), maxY = set.getMaxY(), maxX = set.getLengthHours();
                if (minY < 0) {
                    minY -= 100 + (minY % 100);
                }

                if (maxY > 0) {
                    maxY += 100 - (maxY % 100);
                }

                if (maxY != 0 && minY != 0) {
                    double yRange = maxY - minY;
                    double first = maxY - (yRange / 4), second = maxY - (yRange / 2), third = maxY - ((yRange / 4) * 3);

                    while (first != 0 && second != 0 && third != 0) {
                        if (Math.abs(maxY) > Math.abs(minY)) {
                            if (third > 0) {
                                minY -= 100;
                            } else {
                                maxY += 100;
                            }
                        } else {
                            if (first < 0) {
                                maxY += 100;
                            } else {
                                minY -= 100;
                            }
                        }
                        yRange = maxY - minY;
                        first = maxY - (yRange / 4);
                        second = maxY - (yRange / 2);
                        third = maxY - ((yRange / 4) * 3);
                    }
                }
                maxX += 4.0 - (maxX % 4.0);
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(minY);
                graph.getViewport().setMaxY(maxY);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(maxX);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(set.getDataPoints());
                graph.addSeries(series);
            }
        }
    }
}
