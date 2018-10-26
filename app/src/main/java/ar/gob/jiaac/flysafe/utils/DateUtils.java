package ar.gob.jiaac.flysafe.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    static public Date dateFromString(String strDate) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = df.parse(strDate);
        } catch (NullPointerException | ParseException e) {
        }
        return date;
    }

    static public String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
