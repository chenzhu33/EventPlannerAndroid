package com.carelife.eventplanner.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by carelifead on 2016/4/6.
 */
public class TimeUtil {
    public static String toDate(long timestamp) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String sd = sdf.format(new Date(timestamp));
        return sd;
    }

    public static String toSimpleDate(long timestamp) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(timestamp));
        return sd;
    }

    public static long toTimeStamp(String date) {
        String format = "yyyy-MM-dd hh:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
