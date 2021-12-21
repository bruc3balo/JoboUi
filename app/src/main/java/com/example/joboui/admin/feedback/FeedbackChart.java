package com.example.joboui.admin.feedback;

import static com.example.joboui.admin.CashFlowActivity.LABEL_POSITION;
import static com.example.joboui.admin.CashFlowActivity.LEGEND_POSITION;
import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.joboui.R;
import com.example.joboui.admin.CashFlowActivity;
import com.example.joboui.databinding.FragmentFeedbackChartBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.example.joboui.utils.MyLinkedMap;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vertx.core.json.JsonArray;

public class FeedbackChart extends Fragment {

    private FragmentFeedbackChartBinding binding;
    private AnyChartView pieChart;
    private final List<DataEntry> data = new ArrayList<>();
    private Pie pie;


    public FeedbackChart() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackChartBinding.inflate(inflater);

        pieChart = binding.pieChart;
        pieChart.setProgressBar(binding.pieChartPb);

        pie = AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(requireContext(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        pie.title("Feedback pie chart");


        pie.labels().position(LABEL_POSITION);
        pie.legend().title().enabled(true);
        pie.legend().title().text("Show feedback by rating").padding(10d, 10d, 10d, 10d);
        pie.legend().position(LEGEND_POSITION).itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER.getJsBase());

        refreshList();

        return binding.getRoot();
    }


    private void refreshList() {

        new ViewModelProvider(this).get(JobViewModel.class).getAFeedbackChart().observe(getViewLifecycleOwner(), jsonResponse -> {
            if (jsonResponse.isPresent()) {
                try {
                    JsonArray feedbacks = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.get().getData()));

                    feedbacks.forEach(f -> {
                        try {
                            Models.FeedbackChart feedback = getObjectMapper().readValue(f.toString(), Models.FeedbackChart.class);
                            ValueDataEntry dataEntry = new ValueDataEntry(feedback.getRating(), feedback.getAmount());
                            data.add(dataEntry);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });

                    pie.data(data);
                    pieChart.setChart(pie);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}