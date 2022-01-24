package com.example.joboui;

import static com.example.joboui.globals.GlobalDb.userRepository;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.NotificationRvAdapter;
import com.example.joboui.databinding.ActivityNotificationBinding;
import com.example.joboui.model.Models;
import com.example.joboui.services.BulkViewModel;

import java.util.Comparator;
import java.util.LinkedList;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationRvAdapter notificationRvAdapter;
    private final LinkedList<Models.NotificationModels> notificationList = new LinkedList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.more));
        toolbar.setNavigationOnClickListener(v -> finish());


        RecyclerView notificationRv = binding.notificationRv;
        notificationRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        notificationRvAdapter = new NotificationRvAdapter(this, notificationList);
        notificationRv.setAdapter(notificationRvAdapter);

    }

    private void getAllNotifications(String username) {
        System.out.println("Getting notifications activity");
        new ViewModelProvider(this).get(BulkViewModel.class).getAllMyNotificationsLiveData(username).observe(this, notificationModels -> {
            notificationList.clear();
            notificationList.addAll(notificationModels);
            notificationList.sort(Comparator.comparing(Models.NotificationModels::getCreated_at).reversed());
            notificationRvAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userRepository != null) {
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(u -> getAllNotifications(u.getUsername())));
        } else {
            finish();
        }
    }
}