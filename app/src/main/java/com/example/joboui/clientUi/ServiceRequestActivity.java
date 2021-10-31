package com.example.joboui.clientUi;

import static com.example.joboui.adapters.RequestPagerAdapter.mainTitles;
import static com.example.joboui.globals.GlobalVariables.SERVICE_DB;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.example.joboui.R;
import com.example.joboui.adapters.RequestPagerAdapter;
import com.example.joboui.databinding.ActivityServiceReuestBinding;
import com.example.joboui.domain.Domain;

public class ServiceRequestActivity extends AppCompatActivity {

    private ActivityServiceReuestBinding binding;
    private Domain.Services service;
    private Button nextButton;
    private boolean onBackPressed = false;
    private ViewPager2 pager2;
    private RequestPagerAdapter adapter;
    public static String speciality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceReuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());


        Intent intent = getIntent();
        if (intent.getExtras() != null && !intent.getExtras().isEmpty()) {
            service = (Domain.Services) intent.getSerializableExtra(SERVICE_DB);
            toolbar.setSubtitle(service.getName());
            speciality = service.getName();
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
                Toast.makeText(ServiceRequestActivity.this, "Page changed", Toast.LENGTH_SHORT).show();
                super.onPageSelected(position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("BACK").setIcon(R.drawable.ic_back).setOnMenuItemClickListener(item -> {
            goPrevious();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("NEXT").setIcon(R.drawable.ic_forward).setOnMenuItemClickListener(item -> {
           goNext();
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
        //todo
        if (pager2.getCurrentItem() < mainTitles.length) {
            pager2.setCurrentItem(pager2.getCurrentItem() + 1);
        } else {
            Toast.makeText(ServiceRequestActivity.this, "Are you sure ?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onBackPressed = false;
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