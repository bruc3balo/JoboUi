package com.example.joboui.admin;

import static com.example.joboui.globals.GlobalVariables.PAGE_NO;
import static com.example.joboui.globals.GlobalVariables.PAGE_SIZE;
import static com.example.joboui.globals.GlobalVariables.REPORTED;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.JobsRvAdminAdapter;
import com.example.joboui.clientUi.MyJobs;
import com.example.joboui.databinding.ActivityAllJobsBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import io.vertx.core.json.JsonArray;

public class AllJobsActivity extends AppCompatActivity {

    private ActivityAllJobsBinding binding;
    private final LinkedList<Models.Job> allJobs = new LinkedList<>();
    private JobsRvAdminAdapter adapter;
    public static MutableLiveData<Optional<Boolean>> refreshJobListAdmin = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllJobsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new JobsRvAdminAdapter(this,allJobs);
        binding.jobsRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.jobsRv.setAdapter(adapter);

        getAllJobs(null,null,null);
        addRefreshListener();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllJobs(Integer pageSize, Integer pageNo, Boolean reported) {
        HashMap<String, String> params = new HashMap<>();

        if (pageSize != null) {
            params.put(PAGE_SIZE, String.valueOf(pageSize));
        }

        if (pageNo != null) {
            params.put(PAGE_NO, String.valueOf(pageNo));
        }

        if (reported != null) {
            params.put(REPORTED, String.valueOf(reported));
        }


        new ViewModelProvider(this).get(JobViewModel.class).getAllTheJobs(params).observe(AllJobsActivity.this, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(AllJobsActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonResponse response = jsonResponse.get();

            if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                Toast.makeText(AllJobsActivity.this, "No jobs available", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            try {
                allJobs.clear();
                adapter.notifyDataSetChanged();
                JsonArray jobs = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                jobs.forEach(u -> {
                    try {
                        Models.Job job = getObjectMapper().readValue(u.toString(), Models.Job.class);
                        allJobs.add(job);
                        adapter.notifyDataSetChanged();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(AllJobsActivity.this, "Problem mapping data", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addRefreshListener() {
        refreshData().observe(this, refresh -> {
            if (refresh.isPresent()) {
                if (!AllJobsActivity.this.isDestroyed() && !AllJobsActivity.this.isFinishing()) {
                    System.out.println("update message refresh");
                    getAllJobs(null, null, null);
                }
            }
        });
    }

    private LiveData<Optional<Boolean>> refreshData() {
        return refreshJobListAdmin;
    }

}