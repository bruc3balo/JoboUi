package com.example.joboui.serviceProviderUi;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.clearSp;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.joboui.R;
import com.example.joboui.ReviewActivity;
import com.example.joboui.adapters.JobsRvAdapter;
import com.example.joboui.adapters.ServiceProviderPageGrid;
import com.example.joboui.databinding.ActivityServiceProviderBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.JobRequests;
import com.example.joboui.serviceProviderUi.pages.ManageServicesProvider;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.json.JsonArray;


public class ServiceProviderActivity extends AppCompatActivity {

    private ActivityServiceProviderBinding serviceProviderBinding;
    private ServiceProviderPageGrid serviceProviderPageGrid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceProviderBinding = ActivityServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(serviceProviderBinding.getRoot());

        Toolbar serviceProviderToolbar = serviceProviderBinding.serviceProviderToolbar;
        setSupportActionBar(serviceProviderToolbar);
        serviceProviderToolbar.setOverflowIcon(getDrawable(R.drawable.more));

        GridView serviceProviderGrid = serviceProviderBinding.serviceProviderGrid;
        serviceProviderGrid.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                default:
                    break;

                case 0:
                    goToRequests();
                    break;

                case 3:
                    goToServices();
                    break;

                case 4:
                    goToFeedback();
                    break;
            }
        });

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, user -> {
                if (user.isPresent()) {
                    serviceProviderToolbar.setTitle(user.get().getRole());
                    serviceProviderToolbar.setSubtitle(user.get().getUsername());
                    getJobCount(user.get().getUsername()).observe(ServiceProviderActivity.this, count -> {
                        serviceProviderPageGrid = new ServiceProviderPageGrid(user.get().getUsername(), count);
                        serviceProviderGrid.setAdapter(serviceProviderPageGrid);
                    });
                }
            });
        }

        setWindowColors();

    }

    private MutableLiveData<Integer> getJobCount(String username) {

        MutableLiveData<Integer> count = new MutableLiveData<>();

        new ViewModelProvider(this).get(JobViewModel.class).getAllProviderJobs(username, null).observe(this, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(this, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonResponse response = jsonResponse.get();

            if (response.isHas_error() && !response.isSuccess() || response.getData() == null) {
                count.setValue(0);
                return;
            }

            try {

                JsonArray jobs = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                final int[] c = {0};
                jobs.forEach(u -> {
                    try {
                        Models.Job job = getObjectMapper().readValue(u.toString(), Models.Job.class);
                        if (!(job.getJob_status().equals(JobStatus.DECLINED.code)) && !(job.getJob_status().equals(JobStatus.CANCELLED.code)) && !(job.getJob_status().equals(JobStatus.SERVICE_REPORTED.code)) || !(job.getJob_status() == JobStatus.SERVICE_REPORTED.getCode() || !(job.getJob_status() == JobStatus.CLIENT_REPORTED.getCode()))) {
                            c[0]++;
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
                count.setValue(c[0]);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Problem mapping data", Toast.LENGTH_SHORT).show();
            }

        });

        return count;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            userRepository.deleteUserDb();
            clearSp(USER_DB, getApplication());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    private void goToRequests() {
        startActivity(new Intent(ServiceProviderActivity.this, JobRequests.class));
    }

    private void goToServices() {
        startActivity(new Intent(ServiceProviderActivity.this, ManageServicesProvider.class));
    }

    private void goToFeedback() {
        startActivity(new Intent(ServiceProviderActivity.this, ReviewActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
        addLogoutListener(ServiceProviderActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }


    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }


}