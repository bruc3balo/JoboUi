package com.example.joboui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.joboui.clientUi.request.DetailsFragment;
import com.example.joboui.clientUi.request.LocationRequest;
import com.example.joboui.clientUi.request.ProviderChoosing;
import com.example.joboui.domain.Domain;

import org.jetbrains.annotations.NotNull;


public class RequestPagerAdapter extends FragmentStateAdapter {


    public static final String[] mainTitles = new String[]{"Location", " Details ", "Provider"};
    private Domain.Services services;


    public RequestPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle,Domain.Services services) {
        super(fm, lifecycle);
        this.services = services;
    }



    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
            case 0:
                return new LocationRequest();

            case 1:
                return new DetailsFragment();

            case 2:
                return new ProviderChoosing();
        }
    }

    @Override
    public int getItemCount() {
        return mainTitles.length;
    }
}


