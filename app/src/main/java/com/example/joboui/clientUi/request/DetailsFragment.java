package com.example.joboui.clientUi.request;

import static com.example.joboui.clientUi.ServiceRequestActivity.jobRequestForm;
import static com.example.joboui.globals.GlobalVariables.ASAP;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.databinding.FragmentDetailsBinding;
import com.example.joboui.domain.Domain;

import java.util.Calendar;
import java.util.Date;


public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private final Calendar scheduledDate = Calendar.getInstance();


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater);

        EditText descriptionField = binding.descriptionField;
        descriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    jobRequestForm.setJob_description(s.toString());
                } else {
                    jobRequestForm.setJob_description(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RadioGroup timeGroup = binding.timeGroup;
        timeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.asap.getId()) {
                binding.scheduledTimeTv.setVisibility(View.GONE);
                jobRequestForm.setScheduled_at(ASAP);
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
                    jobRequestForm.setScheduled_at(scheduledDate.getTime().toString());
                },c.getTime().getHours(),c.getTime().getMinutes(),true);
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    yearS[0] = year;
                    monthS[0] = month + 1;
                    dayS[0] = dayOfMonth;
                    timePickerDialog.show();
                }, c.getTime().getYear(), c.getTime().getMonth(), c.getTime().getDay());

                datePickerDialog.show();
                datePickerDialog.setOnCancelListener(dialog -> {
                    binding.asap.setChecked(true);
                    Toast.makeText(requireContext(), "Date Cancelled", Toast.LENGTH_SHORT).show();
                });
                timePickerDialog.setOnCancelListener(dialog -> {
                    binding.asap.setChecked(true);
                    Toast.makeText(requireContext(), "Time Cancelled", Toast.LENGTH_SHORT).show();
                });
            }
        });


        return binding.getRoot();
    }
}