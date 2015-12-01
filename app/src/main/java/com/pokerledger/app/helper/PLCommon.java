package com.pokerledger.app.helper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by max on 11/25/15.
 */
public class PLCommon {

    public static Long datetimeToTimestamp(String datetime) {
        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            time.setTime(sdf.parse(datetime));
        } catch (Exception e) {
            //fucking parse exception needs to be handled
        }

        return time.getTimeInMillis();
    }

    public static String timestampToDatetime(Long timestamp) {
        return timestampToDate(timestamp) + " " + timestampToTime(timestamp);
    }

    public static String timestampToDate(Long timestamp) {
        Calendar cal = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("00");
        cal.setTimeInMillis(timestamp);

        return cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH));
    }

    public static String timestampToTime(Long timestamp) {
        Calendar cal = Calendar.getInstance();
        DecimalFormat df = new DecimalFormat("00");
        cal.setTimeInMillis(timestamp);

        return df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE));
    }

    public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(d);
    }
}
