package com.mod.loan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public final static String dateformat0 = "yyyy-MM-dd HH:mm";
    public final static String dateformat1 = "yyyy-MM-dd HH:mm:ss";
    public final static String dateformat2 = "yyyy-MM-dd";
    public final static String dateformat3 = "EEE MMM dd HH:mm:ss zzzZZZ yyyy";
    public final static String dateformat4 = "yyyyMMdd";
    public final static String dateformat5 = "yyyyMMddHHmmss";
    public final static String dateformat6 = "yyyyMMddHHmmssSSS";

    private TimeUtils() {
        throw new Error("can't instance this tool class");
    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat1);
        return sdf.format(new Date());
    }

    public static Date addDate(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + day);
        return cal.getTime();
    }

    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat2);
        return sdf.format(new Date());
    }

    public static String parseTime(Date date, String dateformat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        return sdf.format(date);
    }

    /**
     * 判断当前传入时间是不是今年
     *
     * @param date
     * @return
     */
    public static boolean checkThisYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(Calendar.YEAR);
        calendar.setTime(new Date());
        int j = calendar.get(Calendar.YEAR);
        return i == j;
    }

    /**
     * 格式化这种类型的时间Wed Feb 01 00:00:00 CST+0800 2012
     *
     * @param str
     * @return
     */
    public static String parseTimeCST(String str, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(dateformat3, Locale.US);
        Date d = null;
        try {
            d = sf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parseTime(d, dateformat2);
    }

    /**
     * 格式化这种类型的时间20170111
     *
     * @param str
     * @return
     */
    public static Date parseTime(String str) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = sf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static Date parseTime(String str, String dateformat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateformat);
        Date d = null;
        try {
            d = sf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }


}
