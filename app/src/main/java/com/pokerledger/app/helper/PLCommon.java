package com.pokerledger.app.helper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        int wholeNum = (int) d;
        double remainder = d - wholeNum;
        DecimalFormat df;
        if (remainder > 0) {
            df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        } else {
            df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        }
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(d);
    }
}
