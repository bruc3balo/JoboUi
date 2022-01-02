package com.example.joboui.clientUi.request;

import static com.example.joboui.clientUi.ServiceRequestActivity.service;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_USERNAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.ProviderRVAdapter;
import com.example.joboui.clientUi.review.LSPReviews;
import com.example.joboui.databinding.FragmentProviderChoosingBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.model.Models;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import java.util.ArrayList;


public class ProviderChoosing extends Fragment {

    private FragmentProviderChoosingBinding binding;
    private ProviderRVAdapter adapter;
    private final ArrayList<Models.AppUser> providerList = new ArrayList<>();


    public ProviderChoosing() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProviderChoosingBinding.inflate(inflater);

        RecyclerView providerRv = binding.providerRv;
        providerRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        adapter = new ProviderRVAdapter(requireContext(), providerList);
        providerRv.setAdapter(adapter);

        try {
            getProviders(service.getName(), 0, 1);
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        adapter.setClickListener((view, position) -> {
            startActivity(new Intent(requireContext(), LSPReviews.class).putExtra(LOCAL_SERVICE_PROVIDER_USERNAME,providerList.get(position)));
        });

        return binding.getRoot();
    }



    @SuppressLint("NotifyDataSetChanged")
    private void getProviders(String speciality, Integer page, Integer pageSize) throws JSONException, JsonProcessingException {
        new ViewModelProvider(this).get(UserViewModel.class).getProviders(speciality, page, pageSize).observe(getViewLifecycleOwner(), appUsers -> {
            if (!appUsers.isPresent()) {
                Toast.makeText(requireContext(), "Failed to get service providers", Toast.LENGTH_SHORT).show();
                return;
            }

            if (appUsers.get().isEmpty()) {
                Toast.makeText(requireContext(), "No service providers available", Toast.LENGTH_SHORT).show();
                return;
            }

            providerList.clear();
            providerList.addAll(appUsers.get());
            adapter.notifyDataSetChanged();

        });
    }
}