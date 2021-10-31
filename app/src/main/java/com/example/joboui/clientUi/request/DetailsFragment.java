package com.example.joboui.clientUi.request;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.joboui.R;
import com.example.joboui.databinding.FragmentDetailsBinding;
import com.example.joboui.domain.Domain;

import java.util.Calendar;
import java.util.Date;


public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private final Calendar scheduledDate = Calendar.getInstance();
    private Domain.Services services;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public DetailsFragment(Domain.Services services) {
        this.services = services;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater);

        EditText descriptionField = binding.descriptionField;
        RadioGroup timeGroup = binding.timeGroup;
        timeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.asap.getId()) {
                binding.scheduledTimeTv.setVisibility(View.GONE);
            } else if (checkedId == binding.schedules.getId()) {
                binding.scheduledTimeTv.setVisibility(View.VISIBLE);
                Calendar c = Calendar.getInstance();

                final int[] hourS = {0};
                final int[] minS = {0};
                final int[] yearS = {0};
                final int[] monthS = {0};
                final int[] dayS = {0};

                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                    hourS[0] = hourOfDay;
                    minS[0] = minute;
                    scheduledDate.set(yearS[0],monthS[0],dayS[0], hourS[0], minS[0]);
                    binding.scheduledTimeTv.setText(scheduledDate.getTime().toString());
                },c.getTime().getHours(),c.getTime().getMinutes(),true);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    yearS[0] = year;
                    monthS[0] = month + 1;
                    dayS[0] = dayOfMonth;
                    timePickerDialog.show();
                }, c.getTime().getYear(), c.getTime().getMonth(), c.getTime().getDay());
                datePickerDialog.show();
            }
        });


        return binding.getRoot();
    }
}