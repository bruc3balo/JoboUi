package com.example.joboui.clientUi;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.JobsRvAdapter;
import com.example.joboui.databinding.ActivityMyJobsBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedList;
import java.util.Optional;

import io.vertx.core.json.JsonArray;

public class MyJobs extends AppCompatActivity {

    private ActivityMyJobsBinding binding;
    private final LinkedList<Models.Job> myJobsList = new LinkedList<>();
    private JobsRvAdapter jobsRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyJobsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView jobsRv = binding.myJobsRv;
        jobsRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        jobsRvAdapter = new JobsRvAdapter(MyJobs.this, myJobsList);
        jobsRv.setAdapter(jobsRvAdapter);

        populateMyJobs();
    }

    private void populateMyJobs() {
        userRepository.getUserLive().observe(this, user -> {
            if (!user.isPresent()) {
                Toast.makeText(MyJobs.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            new ViewModelProvider(MyJobs.this).get(JobViewModel.class).getAllClientJobs(user.get().getUsername(), null).observe(MyJobs.this, new Observer<Optional<JsonResponse>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onChanged(Optional<JsonResponse> jsonResponse) {
                    if (!jsonResponse.isPresent()) {
                        Toast.makeText(MyJobs.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JsonResponse response = jsonResponse.get();

                    if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                        Toast.makeText(MyJobs.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    try {
                        JsonArray jobs = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                        jobs.forEach(u -> {
                            try {
                                Models.Job job = getObjectMapper().readValue(u.toString(), Models.Job.class);
                                myJobsList.add(job);
                                jobsRvAdapter.notifyDataSetChanged();
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        Toast.makeText(MyJobs.this, "Problem mapping data", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        });
    }
}