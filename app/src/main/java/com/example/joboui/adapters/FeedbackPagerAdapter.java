package com.example.joboui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.joboui.R;
import com.example.joboui.admin.feedback.FeedbackChart;
import com.example.joboui.admin.feedback.FeedbackList;
import com.google.android.material.tabs.TabLayout;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class FeedbackPagerAdapter extends FragmentStateAdapter {

    //control pages for admin feedback

    private final String[] loginTitles = new String[]{"Chart", "Feedback"};
    private final int[] loginIcons = new int[]{R.drawable.ic_chart, R.drawable.ic_list};

    public FeedbackPagerAdapter(@NonNull FragmentManager fm, Lifecycle lifecycle) {
        super(fm, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FeedbackChart();
        } else {
            return new FeedbackList();
        }
    }

    public String getLoginTitles(int position) {
        return loginTitles[position];
    }

    public int getLoginIcons(int position) {
        return loginIcons[position];
    }

    @Override
    public int getItemCount() {
        return loginIcons.length;
    }

    public void setAllTabIcons(TabLayout tab) {
        for (int i = 0; i <= loginIcons.length - 1; i++) {
            Objects.requireNonNull(tab.getTabAt(i)).setIcon(loginIcons[i]);
            Objects.requireNonNull(tab.getTabAt(i)).setText(loginTitles[i]);
        }
    }

}


