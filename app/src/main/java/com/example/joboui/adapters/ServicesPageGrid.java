package com.example.joboui.adapters;

import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalVariables.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.domain.Domain;
import com.example.joboui.utils.MyLinkedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ServicesPageGrid extends BaseAdapter {

    private final Set<Domain.Services> serviceList = new HashSet<>();

    //todo solve bug

    public ServicesPageGrid(LifecycleOwner lifecycleOwner) {
        try {
            serviceRepository.updateServices();
        } catch (Exception ignored) {
        } finally {
            serviceRepository.getServiceLive().observe(lifecycleOwner, services -> {
                if (!services.isEmpty()) {
                    serviceList.addAll(services);
                    System.out.println("THE SIZE IS " + services.size());
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Optional<Domain.Services> getItem(int position) {
        return serviceList.stream().filter(i -> Integer.valueOf(i.getId().toString()).equals(position)).findFirst();
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
        Optional<Domain.Services> service = serviceList.stream().filter(i -> Integer.valueOf(i.getId().toString()).equals(position)).findFirst();

        if (service.isPresent()) {

            System.out.println("Size is " + serviceList.size());

            TextView title = convertView.findViewById(R.id.title_row);
            title.setText(service.get().getName());
            ImageView icon = convertView.findViewById(R.id.icon_row);
            Glide.with(parent.getContext()).load(getServiceDrawables().get(service.get().getName())).into(icon);

        }

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
