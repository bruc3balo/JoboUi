package com.example.joboui.clientUi.request;

import static com.example.joboui.clientUi.ServiceRequestActivity.jobRequestForm;
import static com.example.joboui.clientUi.ServiceRequestActivity.service;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.adapters.ProviderRVAdapter;
import com.example.joboui.clientUi.ServiceRequestActivity;
import com.example.joboui.databinding.FragmentProviderChoosingBinding;
import com.example.joboui.databinding.YesNoInfoLayoutBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
            jobRequestForm.setLocal_service_provider_id(providerList.get(position).getId().toString());
            if (jobRequestForm.getLocal_service_provider_id() == null) {
                Toast.makeText(requireContext(), "You need to pick a service provider", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getClient_id() == null) {
                Toast.makeText(requireContext(), "We had a problem getting your data", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getJob_location() == null) {
                Toast.makeText(requireContext(), "Pick a location for the job", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getSpecialities() == null) {
                Toast.makeText(requireContext(), "We had a problem picking your job", Toast.LENGTH_SHORT).show();
            } else if (jobRequestForm.getJob_description() == null || jobRequestForm.getJob_description().isEmpty()) {
                Toast.makeText(requireContext(), "You need to give a description of the job", Toast.LENGTH_SHORT).show();
            } else {
                confirmationDialog(providerList.get(position).getNames() + " is going to be requested with for service " + service.getName() + " scheduled for " + jobRequestForm.getScheduled_at() + " with description " + jobRequestForm.getJob_description());
            }
        });

        return binding.getRoot();
    }

    private void confirmationDialog(String info) {
        Dialog d = new Dialog(requireContext());
        d.getWindow().setBackgroundDrawableResource(R.color.transparent);
        YesNoInfoLayoutBinding binding = YesNoInfoLayoutBinding.inflate(getLayoutInflater());
        d.setContentView(binding.getRoot());
        d.show();

        TextView infoTv = binding.newInfoTv;
        infoTv.setText(info);

        Button no = binding.noButton;
        Button yes = binding.yesButton;
        yes.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Sending your request", Toast.LENGTH_SHORT).show();
            d.dismiss();
            requireActivity().finish();
        });


        no.setOnClickListener(v -> d.dismiss());


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