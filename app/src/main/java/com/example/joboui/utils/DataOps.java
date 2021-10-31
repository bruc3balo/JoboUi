package com.example.joboui.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class DataOps {
    public static String getStringFromList(LinkedList<String> specialities) {
        if (specialities.isEmpty()) {
            return "";
        } else {
            StringBuilder specString = new StringBuilder();
            for (String spec : specialities) {
                specString.append(',').append(spec);
            }
            return specString.substring(1);
        }
    }

    public static LinkedList<String> getListFromString(String specialityString) {
        if ( specialityString == null || specialityString.isEmpty()) {
            return new LinkedList<>();
        } else {
            LinkedList<String> specialities = new LinkedList<>();
            String[] a = specialityString.split(",");
            Collections.addAll(specialities, a);
            return specialities;
        }
    }

    public static String getStringFromMap(LinkedHashMap<String,String> workingHoursMap) {
        if (!workingHoursMap.isEmpty()) {
            StringBuilder rs = new StringBuilder();

            workingHoursMap.forEach((day,time)-> {
                String spec = day.concat("^").concat(time);
                rs.append(',').append(spec);
            });

            return rs.substring(1);
        } else {
            return "";
        }
    }

    public static LinkedHashMap<String,String> getMapFromString(String workingString) {
        if (workingString == null || workingString.isEmpty()) {
            return new LinkedHashMap<>();
        } else {
            LinkedHashMap<String,String> workingHoursMap = new LinkedHashMap<>();
            String[] a = workingString.split(",");

            for (String item : a) {
                String key = item.split("\\^")[0];
                String val = item.split("\\^")[1];
                workingHoursMap.put(key,val);
            }

            // Collections.addAll(specialities, a);
            return workingHoursMap;
        }
    }

    public static SpannableString getBoldSpannable(String normal , String bold) {
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        int end = normal.length() + bold.length();

        SpannableString farmNameFormatted = new SpannableString(normal.concat(bold));
        farmNameFormatted.setSpan(boldSpan, normal.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return farmNameFormatted;
    }

}
