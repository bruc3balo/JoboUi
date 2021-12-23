package com.example.joboui.serviceProviderUi.pages;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.FRIDAY;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.MONDAY;
import static com.example.joboui.globals.GlobalVariables.SATURDAY;
import static com.example.joboui.globals.GlobalVariables.SUNDAY;
import static com.example.joboui.globals.GlobalVariables.THURSDAY;
import static com.example.joboui.globals.GlobalVariables.TUESDAY;
import static com.example.joboui.globals.GlobalVariables.WEDNESDAY;
import static com.example.joboui.utils.DataOps.getListFromString;
import static com.example.joboui.utils.DataOps.getWorkingTimeFromString;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityServiceProviderProfileBinding;
import com.example.joboui.databinding.WorkingHourPickerBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.DataOps;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceProviderProfile extends AppCompatActivity {

    private ActivityServiceProviderProfileBinding binding;
    private LinkedHashMap<String, String> workingHours = new LinkedHashMap<>();
    private Domain.User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityServiceProviderProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v->finish());

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(this::setUserData));
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUserData(Domain.User user) {
        this.user = user;
        workingHours = DataOps.getMapFromString(user.getPreferred_working_hours());
        LinkedList<String> specialities = new LinkedList<>(getListFromString(user.getSpecialities()));
        List<String> selectedWorkingDays = workingHours.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());


        binding.names.setText(user.getNames());
        binding.username.setText(user.getUsername());
        binding.idNumber.setText(user.getId_number());
        binding.email.setText(user.getEmail_address());
        binding.phone.setText(user.getPhone_number());
        binding.bio.setText(user.getBio());
        binding.rating.setText(String.valueOf(user.getRating()));
        binding.speciality.setText(specialities.toString());

        TextView[] tv = new TextView[]{binding.su, binding.mo, binding.tu, binding.w, binding.th, binding.fr, binding.sa};
        String[] days = new String[]{SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

        for (int i = 0; i < tv.length; i++) {
            int finalI = i;
            tv[i].setOnClickListener(v -> showDayLayout(tv[finalI], days[finalI]));
            if (selectedWorkingDays.contains(days[finalI].toUpperCase())) {
                tv[i].setBackground(getDrawable(R.drawable.circle_day_bg_selected));
                System.out.println("Day is selected "+days[finalI]);
            } else {
                tv[i].setBackground(getDrawable(R.drawable.circle_day_bg_unselected));
                System.out.println("Day is not selected "+days[finalI]);

            }
        }

        workingHours.forEach((d, t) -> System.out.println("Day is "+d + " time is "+ getWorkingTimeFromString(true,t) + " and "+getWorkingTimeFromString(false,t)));
        System.out.println("Day is " + workingHours.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private void showDayLayout(TextView dayTv, String day) {
        List<String> selectedWorkingDays = workingHours.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (selectedWorkingDays.contains(day.toUpperCase())) {
            confirmDialog(day + " already added. \n Do you want to remove it ?", user -> {
                workingHours.remove(day);
                updateUser(new Models.UserUpdateForm(workingHours));
                return null;
            });
        } else {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            WorkingHourPickerBinding binding = WorkingHourPickerBinding.inflate(getLayoutInflater());
            dialog.setContentView(binding.getRoot());

            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();

            final String[] startTime = {HY};
            TextView startTimeTv = binding.startTimeTv;

            final String[] endTime = {HY};
            TextView endTimeTv = binding.endTimeTv;

            int[] nowHour = new int[]{0};
            int[] nowMinute = new int[]{0};

            TextView dayTitle = binding.dayTitle;
            dayTitle.setText("Preferred Working hours for " + day);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime now = LocalDateTime.now();
                nowHour[0] = now.getHour();
                nowMinute[0] = now.getMinute();
            } else {
                Date now = new Date();
                nowHour[0] = now.getHours();
                nowMinute[0] = now.getMinutes();
            }

            ImageButton start = binding.startPicker;
            start.setOnClickListener(view -> new TimePickerDialog(ServiceProviderProfile.this, (timePicker, hourOfDay, minute) -> {
                String hour = hourOfDay < 10 ? "0".concat(String.valueOf(hourOfDay)) : String.valueOf(hourOfDay);
                String min = minute < 10 ? "0".concat(String.valueOf(minute)) : String.valueOf(minute);

                startTime[0] = hour + "^" + min;
                startTimeTv.setText(hour + min + " hrs");
                start.setBackground(getDrawable(R.drawable.circle_day_bg_selected));
            }, nowHour[0], nowMinute[0], true).show());

            ImageButton end = binding.endPicker;
            end.setOnClickListener(view -> new TimePickerDialog(ServiceProviderProfile.this, (timePicker, hourOfDay, minute) -> {


                String hour = hourOfDay < 10 ? "0".concat(String.valueOf(hourOfDay)) : String.valueOf(hourOfDay);
                String min = minute < 10 ? "0".concat(String.valueOf(minute)) : String.valueOf(minute);


                endTime[0] = hour + "^" + min;
                endTimeTv.setText(hour + min + " hrs");
                end.setBackground(getDrawable(R.drawable.circle_day_bg_selected));
            }, nowHour[0], nowMinute[0], true).show());

            Button accept_button = binding.acceptButton;
            accept_button.setOnClickListener(view -> {
                if (startTime[0].equals(HY)) {
                    Toast.makeText(ServiceProviderProfile.this, "Choose a start time", Toast.LENGTH_SHORT).show();
                } else if (endTime[0].equals(HY)) {
                    Toast.makeText(ServiceProviderProfile.this, "Choose an end time", Toast.LENGTH_SHORT).show();
                } else {
                    String time = startTime[0].concat(HY).concat(endTime[0]);
                    workingHours.put(day, time);
                    updateUser(new Models.UserUpdateForm(workingHours));
                    dayTv.setBackground(getDrawable(R.drawable.circle_day_bg_selected));
                    dialog.dismiss();
                }
            });

            Button cancel_button = binding.cancelButton;
            cancel_button.setOnClickListener(view -> {
                dayTv.setBackground(getDrawable(R.drawable.circle_day_bg_unselected));
                dialog.dismiss();
            });
        }
    }

    private void confirmDialog(String info, Function<Domain.User, Void> function) {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.yes_no_info_layout);
        TextView infov = d.findViewById(R.id.newInfoTv);
        infov.setText(info);
        Button no = d.findViewById(R.id.noButton);
        no.setOnClickListener(v -> d.dismiss());

        Button yes = d.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            function.apply(user);
            d.dismiss();
        });

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    private void updateUser(Models.UserUpdateForm form) {
        new ViewModelProvider(this).get(UserViewModel.class).updateExistingUser(form).observe(this, optionalUser -> optionalUser.ifPresent(u-> Toast.makeText(ServiceProviderProfile.this, "Updated", Toast.LENGTH_SHORT).show()));
    }

}