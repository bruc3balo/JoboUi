package com.example.joboui.utils;

import static com.example.joboui.globals.GlobalDb.application;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.example.joboui.model.Models.Messages.MESSAGE_SUR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.example.joboui.R;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;


import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class DataOps {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss a";
    public static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter LD_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);


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

    public static Transformation getRoundTransformation(Context context, ImageView.ScaleType scaleType) {
        return new RoundedTransformationBuilder().cornerRadiusDp(10).borderColor(context.getResources().getColor(R.color.purple)).oval(true).scaleType(scaleType).build();
    }

    public static LinkedList<String> getListFromString(String specialityString) {
        if (specialityString == null || specialityString.isEmpty()) {
            return new LinkedList<>();
        } else {
            LinkedList<String> specialities = new LinkedList<>();
            String[] a = specialityString.split(",");
            Collections.addAll(specialities, a);
            return specialities;
        }
    }

    public static String truncate(String value, int length) {
        // Ensure String length is longer than requested size.
        if (value.length() > length) {
            return value.substring(0, length);
        } else {
            return value;
        }
    }

    public static String getStringFromMap(LinkedHashMap<String, String> workingHoursMap) {
        if (!workingHoursMap.isEmpty()) {
            StringBuilder rs = new StringBuilder();

            workingHoursMap.forEach((day, time) -> {
                String spec = day.concat("^").concat(time);
                rs.append(',').append(spec);
            });

            return rs.substring(1);
        } else {
            return "";
        }
    }

    public static String getMessageId(String threadId) {
        return threadId.concat(HY).concat(Calendar.getInstance().getTime().toString()).concat(HY).concat(MESSAGE_SUR);
    }

    public static String getThreadId(String senderUsername, String receiverUsername) {
        return senderUsername.concat(HY).concat(receiverUsername);
    }

    public static void animate(View v, float min, float max, boolean showing) {

        AlphaAnimation animation = new AlphaAnimation(min, max);
        animation.setDuration(1500);
        animation.setStartOffset(0);
        animation.setFillAfter(true);
        // animation.setFillBefore(false);
        animation.setFillEnabled(true);
        v.startAnimation(animation);

        if (showing) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    public static String getMyIdFromThreadId(String threadId, String myUid) {
        if (threadId.split(HY)[0].equals(myUid)) {
            return threadId.split(HY)[0];
        } else {
            return threadId.split(HY)[1];
        }
    }

    public static LinkedHashMap<String, String> getMapFromString(String workingString) {
        if (workingString == null || workingString.isEmpty()) {
            return new LinkedHashMap<>();
        } else {
            LinkedHashMap<String, String> workingHoursMap = new LinkedHashMap<>();
            String[] a = workingString.split(",");

            for (String item : a) {
                String key = item.split("\\^")[0];
                String val = item.split("\\^")[1];
                workingHoursMap.put(key, val);
            }

            // Collections.addAll(specialities, a);
            return workingHoursMap;
        }
    }

    public static SpannableString getBoldSpannable(String normal, String bold) {
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        int end = normal.length() + bold.length();

        SpannableString farmNameFormatted = new SpannableString(normal.concat(bold));
        farmNameFormatted.setSpan(boldSpan, normal.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return farmNameFormatted;
    }

    public static String getAuthorization() {
        return "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();
    }

    public static Domain.User getDomainUserFromModelUser(Models.AppUser user) {
        return new Domain.User(user.getId(), user.getId_number(), user.getPhone_number(), user.getBio(), user.getEmail_address(), user.getNames(), user.getUsername(), user.getRole().getName(), user.getCreated_at().toString(), user.getUpdated_at().toString(), user.getDeleted(), user.getDisabled(), user.getVerified(), user.getSpecialities(), user.getPreferred_working_hours(), user.getLast_known_location(), user.getPassword(),user.getRating());
    }




}
