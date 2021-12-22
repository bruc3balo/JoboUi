package com.example.joboui.clientUi.request;

import static com.example.joboui.clientUi.ServiceRequestActivity.jobRequestForm;
import static com.example.joboui.globals.GlobalVariables.ASAP;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.utils.DataOps.TIMESTAMP_PATTERN;

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
import com.example.joboui.utils.ConvertDate;

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

        jobRequestForm.setScheduled_at(ASAP);

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

        EditText estimate = binding.estimate;
        estimate.addTextChangedListener(priceRangeWatcher());

        RadioGroup timeGroup = binding.timeGroup;
        timeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.asap.getId()) {
                binding.scheduledTimeTv.setVisibility(View.GONE);
                jobRequestForm.setScheduled_at(ASAP);
            } else if (checkedId == binding.schedules.getId()) {
                binding.scheduledTimeTv.setVisibility(View.VISIBLE);
                final Calendar cldr = Calendar.getInstance();
                int dayM = cldr.get(Calendar.DAY_OF_MONTH);
                final int monthM = cldr.get(Calendar.MONTH);
                int yearM = cldr.get(Calendar.YEAR);
                int hr = cldr.get(Calendar.HOUR);
                int min = cldr.get(Calendar.MINUTE);

                final int[] hourS = {0};
                final int[] minS = {0};
                final int[] yearS = {0};
                final int[] monthS = {0};
                final int[] dayS = {0};

                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                    hourS[0] = hourOfDay;
                    minS[0] = minute;
                    scheduledDate.set(yearS[0], monthS[0], dayS[0], hourS[0], minS[0]);
                    String date = ConvertDate.formatDate(scheduledDate.getTime(),TIMESTAMP_PATTERN);
                    binding.scheduledTimeTv.setText(date);
                    jobRequestForm.setScheduled_at(date);
                }, hr, min, true);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    yearS[0] = year;
                    monthS[0] = month + 1;
                    dayS[0] = dayOfMonth;
                    timePickerDialog.show();
                }, yearM, monthM, dayM);

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

        binding.asap.setChecked(true);

        return binding.getRoot();
    }

     private TextWatcher priceRangeWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (binding.estimate.getText().toString().isEmpty() ) {
                    return;
                }

                try {
                    Double.parseDouble(binding.estimate.getText().toString());
                    jobRequestForm.setJob_price_range(binding.estimate.getText().toString());
                } catch (Exception e) {
                    if (e instanceof NumberFormatException) {
                        binding.estimate.setError("Value to be a number");
                        binding.estimate.requestFocus();
                        return;
                    }
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}