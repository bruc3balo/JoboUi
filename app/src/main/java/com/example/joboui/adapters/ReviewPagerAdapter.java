package com.example.joboui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.joboui.clientUi.review.LspProfileFragment;
import com.example.joboui.clientUi.review.LspReviewFragment;
import com.example.joboui.model.Models;
import com.google.android.material.tabs.TabLayout;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ReviewPagerAdapter extends FragmentStateAdapter {


    private final String[] loginTitles = new String[]{"Profile", "Review"};
    private Models.AppUser lsp;

    public ReviewPagerAdapter(@NonNull FragmentManager fm, Lifecycle lifecycle,Models.AppUser lsp) {
        super(fm, lifecycle);
        this.lsp = lsp;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LspProfileFragment(lsp);
        } else {
            return new LspReviewFragment(lsp);
        }
    }

    public String getLoginTitles(int position) {
        return loginTitles[position];
    }


    @Override
    public int getItemCount() {
        return loginTitles.length;
    }



    public void setAllTabIcons(TabLayout tab) {
        for (int i = 0; i <= loginTitles.length - 1; i++) {
            Objects.requireNonNull(tab.getTabAt(i)).setText(loginTitles[i]);
        }
    }


}


