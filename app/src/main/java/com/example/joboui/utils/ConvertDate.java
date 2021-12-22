package com.example.joboui.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConvertDate {
    public static String formatDate(Date date, String dateFormat){
        if (date == null)
            return null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return format.format(date);
        }catch (Exception ex){
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null)
            return null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        }catch (Exception ex){
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime formatLocalDateTime(String strDate){
        if (strDate == null)
            return null;

        if(strDate.isEmpty())
            return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(strDate, formatter);
            return date;
        }catch (Exception ex){
//            ex.printStackTrace();
            return null;
        }
    }


    public static Date formatDate(String strDate){
        if (strDate == null)
            return null;

        if(strDate.isEmpty())
            return null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = format.parse(strDate);
            if (!date.equals(format.parse(format.format(date)))) {
                date = null;
            }
            return date;
        }catch (Exception ex){
//            ex.printStackTrace();
            return null;
        }
    }

    public static Date formatDate(String strDate, String dateFormat){
        if (strDate == null)
            return null;

        if(strDate.isEmpty())
            return null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);

            Date date = format.parse(strDate);
            if (!date.equals(format.parse(format.format(date)))) {
                date = null;
            }
            return date;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static int getDayOfWeek(Date date) {
        if (date == null )
            return 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayOfWeekName(Date date) {
        if (date == null )
            return null;
        DateFormat format = new SimpleDateFormat("EEEE", Locale.getDefault(Locale.Category.FORMAT));
        return format.format(date);
    }

    public static Date getFirstDateOfWeek(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.trim().isEmpty())
            return null;

        Calendar calendar = Calendar.getInstance();
        if (dayOfWeek.trim().toUpperCase().equals("MONDAY")) {
            return new Date();
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return formatDate(formatDate(calendar.getTime(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getLastDateOfWeek(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.trim().isEmpty())
            return null;

        Calendar calendar = Calendar.getInstance();
        if (dayOfWeek.trim().toUpperCase().equals("SUNDAY")) {
            return new Date();
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        return formatDate(formatDate(calendar.getTime(), "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getFirstDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return formatDate(formatDate(calendar.getTime(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getLastDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return formatDate(formatDate(calendar.getTime(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
    }


}
