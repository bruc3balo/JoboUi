package com.example.joboui.clientUi.request;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.ProviderRVAdapter;
import com.example.joboui.databinding.FragmentProviderChoosingBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProviderChoosing extends Fragment {

    private FragmentProviderChoosingBinding binding;
    private ProviderRVAdapter adapter;
    private final ArrayList<Models.AppUser> providerList = new ArrayList<>();
    private Domain.Services services;


    public ProviderChoosing() {
        // Required empty public constructor
    }

    public ProviderChoosing(Domain.Services services) {
        this.services = services;
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
        providerRv.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false));
        adapter = new ProviderRVAdapter(requireContext(),providerList);
        providerRv.setAdapter(adapter);

        try {
            getProviders(services.getName(),0,1);
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getProviders (String speciality, Integer page, Integer pageSize) throws JSONException, JsonProcessingException {
        new ViewModelProvider(this).get(UserViewModel.class).getProviders(speciality,page,pageSize).observe(getViewLifecycleOwner(), appUsers -> {
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