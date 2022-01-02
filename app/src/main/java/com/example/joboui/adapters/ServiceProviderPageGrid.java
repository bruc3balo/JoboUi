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
import com.example.joboui.utils.MyLinkedMap;

import java.util.Optional;

public class ServiceProviderPageGrid extends BaseAdapter {

    private final String username;
    private final int jobs;

    public ServiceProviderPageGrid(String username,int jobs) {
        this.username = username;
        this.jobs = jobs;
    }

    @Override
    public int getCount() {
        return getServiceProviderDrawables().size();
    }

    @Override
    public Object getItem(int position) {
        return Optional.of(getServiceProviderDrawables().getEntry(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_row_card, null);
        }



        String name = getServiceProviderDrawables().getKey(position);

        TextView title = convertView.findViewById(R.id.title_row);
        title.setText(name);
        ImageView icon = convertView.findViewById(R.id.icon_row);
        Glide.with(parent.getContext()).load(getServiceProviderDrawables().getValue(position)).into(icon);


        return convertView;
    }

    private MyLinkedMap<String, Integer> getServiceProviderDrawables() {

        MyLinkedMap<String, Integer> map = new MyLinkedMap<>();

        map.put("Job Offers ("+jobs +")", R.drawable.ic_mailbox);
        map.put(username, R.drawable.ic_worker);
        map.put("History", R.drawable.ic_history);

        map.put("Services", R.drawable.ic_service);
        map.put("Feedback", R.drawable.ic_person_feedback);
        map.put("Notification", R.drawable.ic_bell_notification);

        return map;
    }

}
