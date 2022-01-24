package com.example.joboui.admin;

import static com.example.joboui.globals.GlobalDb.serviceRepository;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.anychart.APIlib;
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
import com.example.joboui.databinding.ActivityCashFlowBinding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashFlowActivity extends AppCompatActivity {

    private ActivityCashFlowBinding binding;
    private AnyChartView pieChart;
    List<DataEntry> data = new ArrayList<>();
    public static final String LABEL_POSITION = "outside";
    public static final String LEGEND_POSITION = "center-bottom";
    private Pie pie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCashFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v->finish());


        //set up pie chart
        pieChart = binding.pieChart;
        pieChart.setProgressBar(binding.pieChartPb);


        pie = AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(CashFlowActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });


        pie.title("Cash flow for services");

        pie.labels().position(LABEL_POSITION);
        pie.legend().title().enabled(true);
        pie.legend().title().text("Show cash flow received for all the services").padding(10d, 10d, 10d, 10d);
        pie.legend().position(LEGEND_POSITION).itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);


        refreshList();

    }


    //get pie chart data
    private void refreshList() {
        if (serviceRepository != null) {
            serviceRepository.getServiceFlowsLiveData().observe(this, services -> {
                if (!services.isEmpty()) {
                    services.forEach(s -> {
                        ValueDataEntry dataEntry = new ValueDataEntry(s.getService().getName(), s.getAmount());
                        data.add(dataEntry);
                    });
                    pie.data(data);
                    pieChart.setChart(pie);
                }
            });
        }
    }
}