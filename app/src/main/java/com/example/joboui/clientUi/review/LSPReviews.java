package com.example.joboui.clientUi.review;

import static com.example.joboui.clientUi.ServiceRequestActivity.jobRequestForm;
import static com.example.joboui.clientUi.ServiceRequestActivity.service;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_USERNAME;
import static com.example.joboui.globals.GlobalVariables.USERNAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.ReviewPagerAdapter;
import com.example.joboui.databinding.ActivityLspreviewsBinding;
import com.example.joboui.databinding.YesNoInfoLayoutBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.pagerTransformers.DepthPageTransformer;
import com.example.joboui.utils.JsonResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

public class LSPReviews extends AppCompatActivity {

    private ActivityLspreviewsBinding binding;
    public static boolean serviceRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLspreviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v->finish());

        Models.AppUser lsp = (Models.AppUser) getIntent().getExtras().get(LOCAL_SERVICE_PROVIDER_USERNAME);
        serviceRequested = false;

        binding.confirm.setOnClickListener(v -> {
            jobRequestForm.setLocal_service_provider_username(lsp.getUsername());

            if (jobRequestForm.getLocal_service_provider_username() == null) {
                Toast.makeText(LSPReviews.this, "You need to pick a service provider", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getClient_username() == null) {
                Toast.makeText(LSPReviews.this, "We had a problem getting your data", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getJob_location() == null) {
                Toast.makeText(LSPReviews.this, "Pick a location for the job", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getSpecialities() == null) {
                Toast.makeText(LSPReviews.this, "We had a problem picking your job", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getJob_description() == null || jobRequestForm.getJob_description().isEmpty()) {
                Toast.makeText(LSPReviews.this, "You need to give a description of the job", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getJob_price_range() == null) {
                Toast.makeText(LSPReviews.this, "You need to suggest a price for the job", Toast.LENGTH_SHORT).show();
            } else {
                confirmationDialog(lsp.getNames() + " is going to be requested with for service " + service.getName() + " scheduled for " + jobRequestForm.getScheduled_at() + " with description " + jobRequestForm.getJob_description());
            }
        });

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        setUpLoginPager(lsp);
    }

    private void setUpLoginPager(Models.AppUser lsp) {
        ReviewPagerAdapter reviewPagerAdapter = new ReviewPagerAdapter(getSupportFragmentManager(), getLifecycle(),lsp);
        ViewPager2 reviewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        reviewPager.setUserInputEnabled(true);
        reviewPager.setAdapter(reviewPagerAdapter);
        reviewPager.setPageTransformer(new DepthPageTransformer());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setText(reviewPagerAdapter.getLoginTitles(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        new TabLayoutMediator(tabLayout, reviewPager, true, true, (tab, position) -> {

        }).attach();

        reviewPagerAdapter.setAllTabIcons(tabLayout);
    }

    private void confirmationDialog(String info) {
        Dialog d = new Dialog(this);
        d.getWindow().setBackgroundDrawableResource(R.color.transparent);
        YesNoInfoLayoutBinding binding = YesNoInfoLayoutBinding.inflate(getLayoutInflater());
        d.setContentView(binding.getRoot());
        d.show();

        TextView infoTv = binding.newInfoTv;
        infoTv.setText(info);

        Button no = binding.noButton;
        Button yes = binding.yesButton;
        yes.setOnClickListener(v -> {
            addJobRequest();
            d.dismiss();
        });


        no.setOnClickListener(v -> d.dismiss());
    }


    private void addJobRequest() {
        Toast.makeText(this, "Sending your request", Toast.LENGTH_SHORT).show();

        System.out.println(new Gson().toJson(jobRequestForm));

        new ViewModelProvider(this).get(JobViewModel.class).sendJobRequestLive(jobRequestForm).observe(this, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(LSPReviews.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonResponse response = jsonResponse.get();

            if (response.isHas_error() && !response.isSuccess()) {
                Toast.makeText(LSPReviews.this, response.getApi_code_description(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (response.getData() == null) {
                Toast.makeText(LSPReviews.this, "Failed to get data from server", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(LSPReviews.this, "Request successfully created", Toast.LENGTH_SHORT).show();
            serviceRequested = true;
            finish();
        });

    }

}