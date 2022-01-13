package com.example.joboui.admin;

import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.serviceProviderUi.pages.HistoryActivity.completedStatus;
import static com.example.joboui.utils.JobStatus.CANCELLED;
import static com.example.joboui.utils.JobStatus.CLIENT_CANCELLED_IN_PROGRESS;
import static com.example.joboui.utils.JobStatus.CLIENT_REPORTED;
import static com.example.joboui.utils.JobStatus.COMPLETED;
import static com.example.joboui.utils.JobStatus.SERVICE_CANCELLED_IN_PROGRESS;
import static com.example.joboui.utils.JobStatus.SERVICE_REPORTED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.joboui.adapters.HistoryRvAdapter;
import com.example.joboui.databinding.ActivityReportedAdminBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.HistoryActivity;
import com.example.joboui.utils.AppRolesEnum;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import io.vertx.core.json.JsonArray;

public class ReportedAdminActivity extends AppCompatActivity {

    private ActivityReportedAdminBinding binding;
    private final LinkedList<Models.Job> jobList = new LinkedList<>();
    private HistoryRvAdapter adapter;
    public final static Integer[] reportedStatus = new Integer[]{SERVICE_REPORTED.code, CLIENT_REPORTED.code};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportedAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());


        //set up job list
        RecyclerView rv = binding.reportsRv;
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new HistoryRvAdapter(ReportedAdminActivity.this, jobList);
        rv.setAdapter(adapter);

        populateJobs();
    }


    //get jobs data
    private void populateJobs() {
        new ViewModelProvider(this).get(JobViewModel.class).getAllTheJobs(new HashMap<>()).observe(this, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(ReportedAdminActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonResponse response = jsonResponse.get();

            if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                Toast.makeText(ReportedAdminActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            try {
                jobList.clear();
                adapter.notifyDataSetChanged();
                JsonArray jobs = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                jobs.forEach(u -> {
                    System.out.println("JOBS ARE " + jobs.size());

                    try {
                        Models.Job job = getObjectMapper().readValue(u.toString(), Models.Job.class);

                        if (Arrays.asList(reportedStatus).contains(job.getJob_status())) {
                            jobList.add(job);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(ReportedAdminActivity.this, "Problem mapping data", Toast.LENGTH_SHORT).show();
            }

        });


    }
}