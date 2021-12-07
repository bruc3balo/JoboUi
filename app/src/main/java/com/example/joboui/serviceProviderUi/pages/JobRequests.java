package com.example.joboui.serviceProviderUi.pages;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.JobsRvAdapter;
import com.example.joboui.databinding.ActivityJobRequestsBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedList;

import io.vertx.core.json.JsonArray;

public class JobRequests extends AppCompatActivity {

    private ActivityJobRequestsBinding binding;
    private static final LinkedList<Models.Job> myJobsList = new LinkedList<>();
    private JobsRvAdapter jobsRvAdapter;


    //todo confirmation prompt


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJobRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView jobsRv = binding.myJobsRv;
        jobsRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        jobsRvAdapter = new JobsRvAdapter(JobRequests.this, myJobsList);
        jobsRv.setAdapter(jobsRvAdapter);

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, user -> {
                if (!user.isPresent()) {
                    Toast.makeText(JobRequests.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                jobsRvAdapter.setUsername(user.get().getUsername());
                populateMyJobs(JobRequests.this, user.get().getUsername(), jobsRvAdapter);
            });
        }

    }

    public static void populateMyJobs(Activity activity, String username, JobsRvAdapter adapter) {
        new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class).getAllProviderJobs(username, null).observe((LifecycleOwner) activity, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(activity, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonResponse response = jsonResponse.get();

            if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                Toast.makeText(activity, "Failed to get user data", Toast.LENGTH_SHORT).show();
                activity.finish();
                return;
            }

            try {
                myJobsList.clear();
                adapter.notifyDataSetChanged();
                JsonArray jobs = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                jobs.forEach(u -> {
                    try {
                        Models.Job job = getObjectMapper().readValue(u.toString(), Models.Job.class);


                        if (!(job.getJob_status().equals(JobStatus.DECLINED.code)) && !(job.getJob_status().equals(JobStatus.CANCELLED.code)) && !(job.getJob_status().equals(JobStatus.SERVICE_CANCELLED_IN_PROGRESS.code))) {
                            myJobsList.add(job);
                            adapter.notifyDataSetChanged();

                        }

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Problem mapping data", Toast.LENGTH_SHORT).show();
            }

        });


    }

    @Override
    protected void onDestroy() {
        myJobsList.clear();
        super.onDestroy();
    }
}