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

public class ServicesPageGrid extends BaseAdapter {
    public static final Domain.Services[] serviceList = new Domain.Services[]{
            new Domain.Services(R.drawable.ic_plumbing,"Plumbing"),
            new Domain.Services(R.drawable.ic_electrician,"Electrical"),
            new Domain.Services(R.drawable.ic_mechanic,"Mechanic"),

            new Domain.Services(R.drawable.ic_laundry,"Laundry"),
            new Domain.Services(R.drawable.ic_gardening,"Gardening"),
            new Domain.Services(R.drawable.ic_cleaning,"Cleaning"),

            new Domain.Services(R.drawable.ic_painting,"Paint Job"),
            new Domain.Services(R.drawable.ic_moving_,"Moving"),
            new Domain.Services(R.drawable.ic_general_repair,"General repairs"),
    };

    public ServicesPageGrid() {

    }

    @Override
    public int getCount() {
        return serviceList.length;
    }

    @Override
    public Domain.Services getItem(int position) {
        return serviceList[position];
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


        System.out.println("Size is "+serviceList.length);

        TextView title = convertView.findViewById(R.id.title_row);
        title.setText(serviceList[position].getServiceTitle());
        ImageView icon = convertView.findViewById(R.id.icon_row);
        Glide.with(parent.getContext()).load(serviceList[position].getImageDrawable()).into(icon);

        return convertView;
    }
}
