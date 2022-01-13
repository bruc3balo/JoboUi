package com.example.joboui.serviceProviderUi.pages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.joboui.adapters.ServicesProviderRvAdapter;
import com.example.joboui.databinding.ActivityManageServicesAdminBinding;
import com.example.joboui.pagerTransformers.DepthPageTransformer;

public class ManageServicesProvider extends AppCompatActivity {

    private ActivityManageServicesAdminBinding binding;
    private ServicesProviderRvAdapter servicesProviderRvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageServicesAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarServices);
        binding.toolbarServices.setNavigationOnClickListener(v -> finish());

        //ser up service page list
        ViewPager2 servicesPager = binding.servicesPager;
        servicesProviderRvAdapter = new ServicesProviderRvAdapter(ManageServicesProvider.this);
        servicesPager.setAdapter(servicesProviderRvAdapter);
        servicesProviderRvAdapter.registerAdapterDataObserver(binding.indicator.getAdapterDataObserver());
        servicesProviderRvAdapter.notifyDataSetChanged();


        //set options for page
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}