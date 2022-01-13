package com.example.joboui.serviceProviderUi.pages;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.JobStatus.CANCELLED;
import static com.example.joboui.utils.JobStatus.CLIENT_CANCELLED_IN_PROGRESS;
import static com.example.joboui.utils.JobStatus.CLIENT_REPORTED;
import static com.example.joboui.utils.JobStatus.COMPLETED;
import static com.example.joboui.utils.JobStatus.SERVICE_CANCELLED_IN_PROGRESS;
import static com.example.joboui.utils.JobStatus.SERVICE_REPORTED;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.HistoryRvAdapter;
import com.example.joboui.databinding.ActivityHistoryBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.AppRolesEnum;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.LinkedList;

import io.vertx.core.json.JsonArray;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private HistoryRvAdapter adapter;
    private final LinkedList<Models.Job> jobList = new LinkedList<>();
    public final static Integer[] completedStatus = new Integer[]{COMPLETED.code, CANCELLED.code, CLIENT_CANCELLED_IN_PROGRESS.code, SERVICE_CANCELLED_IN_PROGRESS.code, SERVICE_REPORTED.code, CLIENT_REPORTED.code};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());


        //set up list
        RecyclerView rv = binding.rv;
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new HistoryRvAdapter(this, jobList);
        rv.setAdapter(adapter);


        userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(this::populateJobs));
    }

    //get jobs
    private void populateJobs(Domain.User user) {
        if (user.getRole().equals(AppRolesEnum.ROLE_CLIENT.name())) {
            new ViewModelProvider(this).get(JobViewModel.class).getAllClientJobs(user.getUsername(), null).observe(this, jsonResponse -> {
                if (!jsonResponse.isPresent()) {
                    Toast.makeText(HistoryActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonResponse response = jsonResponse.get();

                if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                    Toast.makeText(HistoryActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
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


                            if (Arrays.asList(completedStatus).contains(job.getJob_status())) {
                                jobList.add(job);
                            }

                            adapter.notifyDataSetChanged();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Toast.makeText(HistoryActivity.this, "Problem mapping data", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            new ViewModelProvider(this).get(JobViewModel.class).getAllProviderJobs(user.getUsername(), null).observe(this, jsonResponse -> {
                if (!jsonResponse.isPresent()) {
                    Toast.makeText(HistoryActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonResponse response = jsonResponse.get();

                if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                    Toast.makeText(HistoryActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
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

                            if (Arrays.asList(completedStatus).contains(job.getJob_status())) {
                                jobList.add(job);
                            }

                            adapter.notifyDataSetChanged();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Toast.makeText(HistoryActivity.this, "Problem mapping data", Toast.LENGTH_SHORT).show();
                }

            });
        }

    }

}