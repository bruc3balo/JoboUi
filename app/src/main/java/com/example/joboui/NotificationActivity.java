package com.example.joboui;

import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.getAuthorization;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.NotificationRvAdapter;
import com.example.joboui.databinding.ActivityNotificationBinding;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedList;

import io.vertx.core.json.JsonArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        userApi.getMyNotifications(username, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200 || jsonResponse == null || jsonResponse.isHas_error() || !jsonResponse.isSuccess() || jsonResponse.getData() == null) {
                    return;
                }

                try {
                    JsonArray users = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));
                    if (!users.isEmpty()) notificationList.clear();
                    System.out.println("Getting notifications " + users.size());
                    users.forEach(u -> {
                        try {
                            Models.NotificationModels models = getObjectMapper().readValue(u.toString(), Models.NotificationModels.class);
                            notificationList.add(models);
                            notificationRvAdapter.notifyDataSetChanged();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userRepository != null) {
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(u->getAllNotifications(u.getUsername())));
        }
    }
}