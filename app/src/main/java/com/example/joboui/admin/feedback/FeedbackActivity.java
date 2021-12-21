package com.example.joboui.admin.feedback;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.joboui.adapters.FeedbackPagerAdapter;
import com.example.joboui.databinding.FeedbackActivityBinding;
import com.example.joboui.pagerTransformers.DepthPageTransformer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FeedbackActivity extends AppCompatActivity {

    private FeedbackActivityBinding binding;
    private ViewPager2 feedbackPagerAdmin;
    private FeedbackPagerAdapter feedbackPagerAdapter;
    private TabLayout feedbackTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FeedbackActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v->finish());
        binding.toolbar.setTitle("Feedbacks");

        feedbackPagerAdmin = binding.feedbackPagerAdmin;
        feedbackTab = binding.feedbackTab;



        setUpFeedbackPager();

    }

    private void setUpFeedbackPager() {
        feedbackPagerAdapter = new FeedbackPagerAdapter(getSupportFragmentManager(), getLifecycle());
        feedbackPagerAdmin.setUserInputEnabled(true);
        feedbackPagerAdmin.setAdapter(feedbackPagerAdapter);
        feedbackPagerAdmin.setPageTransformer(new DepthPageTransformer());
        feedbackTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setText(feedbackPagerAdapter.getLoginTitles(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new TabLayoutMediator(feedbackTab, feedbackPagerAdmin, true, true, (tab, position) -> {

        }).attach();
        feedbackPagerAdapter.setAllTabIcons(feedbackTab);
    }





}