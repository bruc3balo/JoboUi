package com.example.joboui.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.joboui.adapters.ServicesAdminRvAdapter;
import com.example.joboui.databinding.ActivityManageServicesAdminBinding;
import com.example.joboui.pagerTransformers.DepthPageTransformer;

public class ManageServicesAdmin extends AppCompatActivity {

    private ActivityManageServicesAdminBinding binding;
    private ServicesAdminRvAdapter servicesAdminRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageServicesAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarServices);
        binding.toolbarServices.setNavigationOnClickListener(v -> finish());

        ViewPager2 servicesPager = binding.servicesPager;
        servicesAdminRvAdapter = new ServicesAdminRvAdapter(ManageServicesAdmin.this);
        servicesPager.setAdapter(servicesAdminRvAdapter);

        servicesAdminRvAdapter.registerAdapterDataObserver(binding.indicator.getAdapterDataObserver());

        servicesPager.setUserInputEnabled(true);
        servicesPager.setPageTransformer(new DepthPageTransformer());
        servicesPager.setOffscreenPageLimit(3);
        servicesPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        servicesPager.requestTransform();
        servicesPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        servicesPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        servicesPager.setClipToPadding(false);
        servicesPager.setClipChildren(false);
        servicesPager.setPadding(0, 20, 0, 20);

        binding.indicator.setViewPager(servicesPager);

    }



}