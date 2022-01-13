package com.example.joboui.clientUi;

import static com.example.joboui.adapters.RequestPagerAdapter.mainTitles;
import static com.example.joboui.clientUi.review.LSPReviews.serviceRequested;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.SERVICE_DB;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.joboui.R;
import com.example.joboui.adapters.RequestPagerAdapter;
import com.example.joboui.databinding.ActivityServiceReuestBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.model.Models.JobRequestForm;

import java.util.Optional;

public class ServiceRequestActivity extends AppCompatActivity {

    private ActivityServiceReuestBinding binding;
    public static Domain.Services service;
    private Button nextButton;
    private boolean onBackPressed = false;
    private ViewPager2 pager2;
    private RequestPagerAdapter adapter;
    public static String speciality;
    public static JobRequestForm jobRequestForm = new JobRequestForm();
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceReuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jobRequestForm = new JobRequestForm();
        serviceRequested = false;

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());


        Intent intent = getIntent();
        //get service info transferred from previous page
        if (intent.getExtras() != null && !intent.getExtras().isEmpty()) {
            service = (Domain.Services) intent.getSerializableExtra(SERVICE_DB);
            toolbar.setSubtitle(service.getName());
            speciality = service.getName();
            jobRequestForm.setSpecialities(service.getName());

            userRepository.getUserLive().observe(this, user -> user.ifPresent(value -> jobRequestForm.setClient_username(value.getUsername())));
        }

        nextButton = binding.nextButton;
        nextButton.setOnClickListener(v -> goNext());

        pager2 = binding.requestViewPager;
        pager2.setUserInputEnabled(false);
        adapter = new RequestPagerAdapter(getSupportFragmentManager(),getLifecycle(),service);
        pager2.setAdapter(adapter);
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (position == mainTitles.length - 1) {
                    nextButton.setVisibility(View.GONE);
                } else {
                    nextButton.setText("Next");
                    nextButton.setVisibility(View.VISIBLE);
                }

                //reset menu with new options
                invalidateOptionsMenu();
                super.onPageSelected(position);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("BACK").setIcon(R.drawable.ic_back).setOnMenuItemClickListener(item -> {
            if (!(currentPosition == 0)) {
                goPrevious();
            }
            return false;
        }).setIconTintList(currentPosition == 0 ? ColorStateList.valueOf(Color.GRAY) : ColorStateList.valueOf(Color.WHITE)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("NEXT").setIcon(R.drawable.ic_forward).setIconTintList(currentPosition == mainTitles.length - 1 ? ColorStateList.valueOf(Color.GRAY) : ColorStateList.valueOf(Color.WHITE)).setOnMenuItemClickListener(item -> {

            if (!(currentPosition == mainTitles.length - 1)) {
                goNext();
            }

            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    private void goPrevious () {
        if (pager2.getCurrentItem() > 0) {
            pager2.setCurrentItem(pager2.getCurrentItem() - 1);
        } else {
            Toast.makeText(ServiceRequestActivity.this, "This is the first step", Toast.LENGTH_SHORT).show();
        }
    }

    private void goNext () {
        if (pager2.getCurrentItem() < mainTitles.length - 1) {
            pager2.setCurrentItem(pager2.getCurrentItem() + 1);
        } else {
            Toast.makeText(ServiceRequestActivity.this, "Are you sure ?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onBackPressed = false;

        if (serviceRequested) {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        if (!onBackPressed) {
            onBackPressed = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}