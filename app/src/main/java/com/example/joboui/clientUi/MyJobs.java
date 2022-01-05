package com.example.joboui.clientUi;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.serviceProviderUi.pages.HistoryActivity.completedStatus;
import static com.example.joboui.utils.JobStatus.CLIENT_CANCELLED_IN_PROGRESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.joboui.JobTrackActivity;
import com.example.joboui.adapters.JobsRvAdapter;
import com.example.joboui.databinding.ActivityMyJobsBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.JobRequests;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import io.vertx.core.json.JsonArray;

public class MyJobs extends AppCompatActivity {

    private ActivityMyJobsBinding binding;
    private static final LinkedList<Models.Job> myJobsList = new LinkedList<>();
    private JobsRvAdapter jobsRvAdapter;
    public static MutableLiveData<Optional<Boolean>> refreshJobListClient = new MutableLiveData<>();

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


        if (userRepository != null) {
            userRepository.getUserLive().observe(this, user -> {
                if (!user.isPresent()) {
                    Toast.makeText(MyJobs.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                jobsRvAdapter.setUsername(user.get().getUsername());
                populateClientJobs(MyJobs.this, user.get().getUsername(), jobsRvAdapter);
                addRefreshListener(user.get());
            });
        }
    }

    public static void populateClientJobs(Activity activity, String username, JobsRvAdapter adapter) {
        new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class).getAllClientJobs(username, null).observe((LifecycleOwner) activity, jsonResponse -> {
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
                    System.out.println("JOBS ARE " + jobs.size());

                    try {
                        Models.Job job = getObjectMapper().readValue(u.toString(), Models.Job.class);
                        if (!Arrays.asList(completedStatus).contains(job.getJob_status())) {
                            myJobsList.add(job);
                            System.out.println("PAYMENT FOR JOB " + job.getId() + " is " + getObjectMapper().writeValueAsString(job.getPayments()));
                        }

                        adapter.notifyDataSetChanged();
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

    private void addRefreshListener(Domain.User user) {
        refreshData().observe(this, refresh -> {
            if (refresh.isPresent()) {
                if (!MyJobs.this.isDestroyed() && !MyJobs.this.isFinishing()) {
                    System.out.println("update message refresh");
                    populateClientJobs(MyJobs.this, user.getUsername(), jobsRvAdapter);
                }
            }
        });
    }

    private LiveData<Optional<Boolean>> refreshData() {
        return refreshJobListClient;
    }

    @Override
    protected void onDestroy() {
        myJobsList.clear();
        super.onDestroy();
    }

}