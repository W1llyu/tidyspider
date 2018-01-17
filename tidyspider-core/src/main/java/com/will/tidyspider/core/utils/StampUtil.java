package com.will.tidyspider.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by will on 02/01/2018.
 */
public abstract class StampUtil {
    public static long strToStamp(String str, String format){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(format);
        return strToStamp(str, simpleDateFormat);
    }

    public static long strToStamp(String str, String format, String zone){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(zone));
        return strToStamp(str, simpleDateFormat);
    }

    private static long strToStamp(String str, SimpleDateFormat simpleDateFormat){
        try {
            Date date = simpleDateFormat.parse(str);
            return (date.getTime())/1000;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String stampToStr(Long stamp, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(stamp);
        return simpleDateFormat.format(date);
    }
}
