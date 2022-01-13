package com.example.joboui.clientUi.review;

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
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.databinding.FragmentLspProfileBinding;
import com.example.joboui.databinding.WorkingHourPickerBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.ServiceProviderProfile;
import com.example.joboui.utils.DataOps;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LspProfileFragment extends Fragment {

    private FragmentLspProfileBinding binding;
    private Models.AppUser lsp;
    private LinkedHashMap<String, String> workingHours = new LinkedHashMap<>();


    public LspProfileFragment(Models.AppUser lsp) {
        // Required empty public constructor
        this.lsp = lsp;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLspProfileBinding.inflate(inflater);

        setUserData(lsp);

        return binding.getRoot();
    }


    //set the profile data to ui
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUserData(Models.AppUser lsp) {
        this.lsp = lsp;
        workingHours = DataOps.getMapFromString(lsp.getPreferred_working_hours());
        LinkedList<String> specialities = new LinkedList<>(getListFromString(lsp.getSpecialities()));
        List<String> selectedWorkingDays = workingHours.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());


        binding.names.setText(lsp.getNames());
        binding.username.setText(lsp.getUsername());
        binding.bio.setText(lsp.getBio());
        binding.rating.setRating(lsp.getRating());
        binding.rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                binding.rating.setRating(lsp.getRating());
            }
        });

        binding.rating.setIsIndicator(true);
        binding.speciality.setText(specialities.toString());

        /*TextView[] tv = new TextView[]{binding.su, binding.mo, binding.tu, binding.w, binding.th, binding.fr, binding.sa};
        String[] days = new String[]{SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

        for (int i = 0; i < tv.length; i++) {
            //tv[i].setOnClickListener(v -> showDayLayout(tv[finalI], days[finalI]));
            if (selectedWorkingDays.contains(days[i])) {
                tv[i].setBackground(requireContext().getDrawable(R.drawable.circle_day_bg_selected));
                System.out.println("Day is selected " + days[i]);
            } else {
                tv[i].setBackground(requireContext().getDrawable(R.drawable.circle_day_bg_unselected));
                System.out.println("Day is not selected " + days[i]);
            }
        }

        workingHours.forEach((d, t) -> System.out.println("Day is " + d + " time is " + getWorkingTimeFromString(true, t) + " and " + getWorkingTimeFromString(false, t)));
        System.out.println("Day is " + workingHours.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList()));*/
    }



}