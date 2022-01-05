package com.example.joboui.adapters;

import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalVariables.*;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.domain.Domain;
import com.example.joboui.utils.MyLinkedMap;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Optional;

public class ServicesPageGrid extends BaseAdapter {

    public final static MyLinkedMap<Integer, Domain.Services> allServiceList = new MyLinkedMap<>();
    public final static MyLinkedMap<Integer, Domain.Services> serviceList = new MyLinkedMap<>();



    public ServicesPageGrid(LifecycleOwner lifecycleOwner) {
        try {
            serviceRepository.updateServices();
        } catch (Exception ignored) {
        } finally {
            if (serviceRepository != null) {
                serviceRepository.getServiceLive().observe(lifecycleOwner, services -> {
                    if (services.isPresent()) {
                        if (!services.get().isEmpty()) {
                            allServiceList.clear();

                            for (Domain.Services s: services.get()) {
                                if (!s.getDisabled()) {
                                    allServiceList.put(services.get().indexOf(s), s);
                                }
                            }

                            serviceList.putAll(allServiceList);
                            try {
                                System.out.println(getObjectMapper().writeValueAsString(serviceList) + " all");
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            System.out.println("THE SIZE IS " + services.get().size());
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Optional<Domain.Services> getItem(int position) {
        return Optional.of(serviceList.getValue(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_row, null);
        }


        System.out.println("Size is " + serviceList.size());

        String name = serviceList.getValue(position).getName();

        TextView title = convertView.findViewById(R.id.title_row);
        title.setText(name);
        ImageView icon = convertView.findViewById(R.id.icon_row);
        Glide.with(parent.getContext()).load(getServiceDrawables().get(name)).into(icon);


        return convertView;
    }

    public static MyLinkedMap<String, Integer> getServiceDrawables() {

        MyLinkedMap<String, Integer> map = new MyLinkedMap<>();

        map.put(PLUMBING, R.drawable.ic_plumbing);
        map.put(ELECTRICAL, R.drawable.ic_electrician);
        map.put(MECHANICAL, R.drawable.ic_mechanic);

        map.put(LAUNDRY, R.drawable.ic_laundry);
        map.put(GARDENING, R.drawable.ic_gardening);
        map.put(CLEANING, R.drawable.ic_cleaning);

        map.put(PAINT_JOB, R.drawable.ic_painting);
        map.put(MOVING, R.drawable.ic_moving_);
        map.put(GENERAL_REPAIRS, R.drawable.ic_general_repair);


        return map;
    }

}
