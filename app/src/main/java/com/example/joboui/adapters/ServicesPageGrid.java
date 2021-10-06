package com.example.joboui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.domain.Domain;

import java.util.ArrayList;

public class ServicesPageGrid extends BaseAdapter {
    private final ArrayList<Domain.Services> serviceList;

    public ServicesPageGrid(ArrayList<Domain.Services> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceList.get(position);
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


        System.out.println("Size is "+serviceList.size());

        TextView title = convertView.findViewById(R.id.title_row);
        title.setText(serviceList.get(position).getServiceTitle());
        ImageView icon = convertView.findViewById(R.id.icon_row);
        Glide.with(parent.getContext()).load(serviceList.get(position).getServiceImageUrl()).into(icon);

        return convertView;
    }
}
