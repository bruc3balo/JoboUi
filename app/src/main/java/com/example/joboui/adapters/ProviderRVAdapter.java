package com.example.joboui.adapters;


import static com.example.joboui.clientUi.ServiceRequestActivity.speciality;
import static com.example.joboui.globals.GlobalVariables.FRIDAY;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.LATITUDE;
import static com.example.joboui.globals.GlobalVariables.LONGITUDE;
import static com.example.joboui.globals.GlobalVariables.MONDAY;
import static com.example.joboui.globals.GlobalVariables.SATURDAY;
import static com.example.joboui.globals.GlobalVariables.SUNDAY;
import static com.example.joboui.globals.GlobalVariables.THURSDAY;
import static com.example.joboui.globals.GlobalVariables.TUESDAY;
import static com.example.joboui.globals.GlobalVariables.WEDNESDAY;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.services.NotificationService.myLocation;
import static com.example.joboui.utils.DataOps.getBoldSpannable;
import static com.example.joboui.utils.DataOps.getMapFromString;
import static com.example.joboui.utils.FlatEarthDist.distance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Rating;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.DataOps;
import com.example.joboui.utils.FlatEarthDist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class ProviderRVAdapter extends RecyclerView.Adapter<ProviderRVAdapter.ViewHolder> {

    private ItemClickListener mClickListener;

    private final Context mContext;
    private final ArrayList<Models.AppUser> userList;


    public ProviderRVAdapter(Context context, ArrayList<Models.AppUser> userList) {
        this.mContext = context;
        this.userList = userList;

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Models.AppUser user = userList.get(position);
        LinkedHashMap<String, String> workingHours = getMapFromString(user.getPreferred_working_hours());

        Integer p = ServicesPageGrid.getServiceDrawables().get(speciality);

        if (p != null) {
            Glide.with(mContext).load(mContext.getDrawable(p)).into(holder.providerImage);
        }

        String nameLabel = "Name : ";
        holder.name.setText(getBoldSpannable(nameLabel, user.getNames()));

        holder.rating.setRating(user.getRating());
        holder.rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                holder.rating.setRating(user.getRating());
            }
        });

        StringBuilder workingHoursS = new StringBuilder();
        workingHours.forEach((d, t) -> workingHoursS.append(d).append("=").append(t).append("\n"));

        String workingHoursLabel = "Working : ";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.workingHours.setText(getBoldSpannable(workingHoursLabel, isOpen(workingHours) ? "Open" : "closed"));
        }


        if (user.getLast_known_location() != null && !user.getLast_known_location().equals(HY) && !user.getLast_known_location().isEmpty()) {
            LinkedHashMap<String, String> map = getMapFromString(user.getLast_known_location());
            LatLng userLocation = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(LATITUDE))), Double.parseDouble(Objects.requireNonNull(map.get(LONGITUDE))));

            if (myLocation != null) {
                double distance = distance(userLocation, myLocation);
                holder.distance.setText(getBoldSpannable("Distance : ", getDistanceInKm(distance)));
            } else {
                holder.distance.setVisibility(View.GONE);
            }
        } else {
            holder.distance.setVisibility(View.GONE);
        }


    }


    public static String getDistanceInKm(double distanceInM) {
        double dist = Double.parseDouble(String.valueOf(Math.floor(distanceInM)).split("\\.")[0]);

        System.out.println("dist is "+dist);
        return (dist / 1000) + " Km";
    }


    public static int getDay(String day) {
        String[] days = new String[]{SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY};
        System.out.println("the day search is " + day);
        List<String> daysArray = new ArrayList<>(Arrays.asList(days));
        return daysArray.indexOf(day);
    }

    public static String getDay(int day) {
        String[] days = new String[]{SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY};
        System.out.println("the day search is " + day);
        return days[day];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isOpen(LinkedHashMap<String, String> workingHours) {
        Calendar c = Calendar.getInstance();

        //day == day (keys)
        //hours between working hours (values)

        List<String> workingDays = new ArrayList<>(workingHours.keySet());


        int hourNow = c.getTime().getHours();
        int minNow = c.getTime().getMinutes();
        int dayToday = c.get(Calendar.DAY_OF_WEEK);

        System.out.println("Day today " + dayToday);


        if (workingDays.contains(getDay(dayToday))) {
            final boolean[] open = {false};
            workingHours.forEach((d, t) -> {
                if (dayToday == getDay(d)) {

                    System.out.println("Day is " + d);


                    //today
                    String startTime = DataOps.getWorkingTimeFromString(true, t);
                    String endTime = DataOps.getWorkingTimeFromString(false, t);

                    System.out.println("Time is " + startTime + " end time is " + endTime);


                    LocalTime endTimeLocal = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HHmm"));
                    LocalTime startTimeLocal = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HHmm"));
                    LocalTime now = LocalTime.now();

                    open[0] = now.isAfter(startTimeLocal) && now.isBefore(endTimeLocal);

                    System.out.println("Start : " + startTimeLocal + " Now : " + now + " End : " + endTimeLocal);

                }
            });

            return open[0];
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView providerImage;
        TextView name, workingHours,distance;
        RatingBar rating;

        ViewHolder(View itemView) {
            super(itemView);

            providerImage = itemView.findViewById(R.id.providerImage);
            name = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
            workingHours = itemView.findViewById(R.id.workingHours);
            distance = itemView.findViewById(R.id.distance);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}