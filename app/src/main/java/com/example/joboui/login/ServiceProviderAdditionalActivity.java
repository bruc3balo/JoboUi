package com.example.joboui.login;


import static com.example.joboui.globals.GlobalVariables.FRIDAY;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_ROLE;
import static com.example.joboui.globals.GlobalVariables.MONDAY;
import static com.example.joboui.globals.GlobalVariables.ROLE;
import static com.example.joboui.globals.GlobalVariables.SATURDAY;
import static com.example.joboui.globals.GlobalVariables.SUNDAY;
import static com.example.joboui.globals.GlobalVariables.THURSDAY;
import static com.example.joboui.globals.GlobalVariables.TUESDAY;
import static com.example.joboui.globals.GlobalVariables.WEDNESDAY;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.adapters.ListRvAdapter;
import com.example.joboui.databinding.ActivityServiceProviderAdditionalBinding;
import com.example.joboui.databinding.WorkingHourPickerBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.tutorial.TutorialActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ServiceProviderAdditionalActivity extends AppCompatActivity {

    private ActivityServiceProviderAdditionalBinding serviceProviderAdditionalBinding;
    private final Map<String, String> preferredWorkingHours = new HashMap<>();
    private final ArrayList<String> speciality = new ArrayList<>();
    private Models.UserUpdateForm updateForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceProviderAdditionalBinding = ActivityServiceProviderAdditionalBinding.inflate(getLayoutInflater());
        setContentView(serviceProviderAdditionalBinding.getRoot());


        Button finishAdditionalInfoButton = serviceProviderAdditionalBinding.finishAdditionalInfoButton;
        finishAdditionalInfoButton.setOnClickListener(view -> {
            if (validateForm()) {
                showPb();
                new ViewModelProvider(this).get(UserViewModel.class).updateExistingUser(updateForm).observe(this, user -> {

                    if (user.isPresent()) {
                        Toast.makeText(ServiceProviderAdditionalActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                        goToTutorialPage();
                    } else {
                        hidePb();
                        Toast.makeText(ServiceProviderAdditionalActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

        TextView sun = serviceProviderAdditionalBinding.su;
        sun.setOnClickListener(view -> showDayLayout(sun, SUNDAY));

        TextView mon = serviceProviderAdditionalBinding.mo;
        mon.setOnClickListener(view -> showDayLayout(mon, MONDAY));

        TextView tue = serviceProviderAdditionalBinding.tu;
        tue.setOnClickListener(view -> showDayLayout(tue, TUESDAY));

        TextView wed = serviceProviderAdditionalBinding.w;
        wed.setOnClickListener(view -> showDayLayout(wed, WEDNESDAY));

        TextView thur = serviceProviderAdditionalBinding.th;
        thur.setOnClickListener(view -> showDayLayout(thur, THURSDAY));

        TextView fri = serviceProviderAdditionalBinding.fr;
        fri.setOnClickListener(view -> showDayLayout(fri, FRIDAY));

        TextView sat = serviceProviderAdditionalBinding.sa;
        sat.setOnClickListener(view -> showDayLayout(sat, SATURDAY));


        RecyclerView specialitiesRv = serviceProviderAdditionalBinding.specialitiesRv;
        specialitiesRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        specialitiesRv.setAdapter(new ListRvAdapter(this, speciality));

        EditText specialityField = serviceProviderAdditionalBinding.specialityField;
        ImageButton addSpecialityButton = serviceProviderAdditionalBinding.addSpecialityButton;
        addSpecialityButton.setOnClickListener(view -> {
            if (specialityField.getText().toString().isEmpty()) {
                specialityField.setError("Cannot be empty");
                specialityField.requestFocus();
            } else if (speciality.contains(specialityField.getText().toString())) {
                specialityField.setError("Item already is list");
                specialityField.requestFocus();
            } else {
                speciality.add(specialityField.getText().toString());
                Objects.requireNonNull(specialitiesRv.getAdapter()).notifyItemInserted(speciality.size() - 1);
                animateButton(addSpecialityButton, Color.GREEN, 300);
            }
        });

        setWindowColors();

        hidePb();
    }

    public static void animateButton(View view, int color, long delay) {
        try {
            view.setBackgroundTintList(ColorStateList.valueOf(color));
            new Handler().postDelayed(() -> view.setBackgroundTintList(null), delay);
        } catch (Exception ignored) {
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void showDayLayout(TextView dayTv, String day) {
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
        start.setOnClickListener(view -> new TimePickerDialog(ServiceProviderAdditionalActivity.this, (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
            startTime[0] = hourOfDay + " : " + minute;
            startTimeTv.setText(startTime[0]);
            start.setBackground(getDrawable(R.drawable.circle_day_bg_selected));
        }, nowHour[0], nowMinute[0], true).show());

        ImageButton end = binding.endPicker;
        end.setOnClickListener(view -> new TimePickerDialog(ServiceProviderAdditionalActivity.this, (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
            endTime[0] = hourOfDay + " : " + minute;
            endTimeTv.setText(endTime[0]);
            end.setBackground(getDrawable(R.drawable.circle_day_bg_selected));
        }, nowHour[0], nowMinute[0], true).show());

        Button accept_button = binding.acceptButton;
        accept_button.setOnClickListener(view -> {
            if (startTime[0].equals(HY)) {
                Toast.makeText(ServiceProviderAdditionalActivity.this, "Choose a start time", Toast.LENGTH_SHORT).show();
            } else if (endTime[0].equals(HY)) {
                Toast.makeText(ServiceProviderAdditionalActivity.this, "Choose an end time", Toast.LENGTH_SHORT).show();
            } else {
                String time = startTime[0].concat(HY).concat(endTime[0]);
                preferredWorkingHours.put(day, time);
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

    private void showPb() {
        serviceProviderAdditionalBinding.additionalPb.setVisibility(View.VISIBLE);
        serviceProviderAdditionalBinding.finishAdditionalInfoButton.setEnabled(false);
    }

    private void hidePb() {
        serviceProviderAdditionalBinding.additionalPb.setVisibility(View.GONE);
        serviceProviderAdditionalBinding.finishAdditionalInfoButton.setEnabled(true);
    }

    private boolean validateForm() {
        boolean valid = false;

        if (preferredWorkingHours.isEmpty()) {
            Toast.makeText(this, "You must pick at least one day", Toast.LENGTH_SHORT).show();
        } else if (speciality.isEmpty()) {
            Toast.makeText(this, "You must add at least one speciality", Toast.LENGTH_SHORT).show();
        } else if (serviceProviderAdditionalBinding.idField.getText().toString().isEmpty()) {
            serviceProviderAdditionalBinding.idField.setError("Id required");
            serviceProviderAdditionalBinding.idField.requestFocus();
        } else {
            String id = serviceProviderAdditionalBinding.idField.getText().toString();
            String bio = serviceProviderAdditionalBinding.bioField.getText().toString();

            updateForm = new Models.UserUpdateForm(id, bio, preferredWorkingHours, speciality, true);
            valid = true;
        }

        return valid;
    }


    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.deep_purple));
        getWindow().setNavigationBarColor(getColor(R.color.deep_purple));

    }

    private void goToTutorialPage() {
        startActivity(new Intent(this, TutorialActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(ROLE, LOCAL_SERVICE_PROVIDER_ROLE));
        finish();
    }
}