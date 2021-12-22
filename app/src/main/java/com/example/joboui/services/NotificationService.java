package com.example.joboui.services;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.getAuthorization;
import static com.example.joboui.utils.NotificationChannelClass.SYNCH_NOTIFICATION_CHANNEL;
import static com.example.joboui.utils.NotificationChannelClass.USER_NOTIFICATION_CHANNEL;
import static com.example.joboui.utils.NotificationChannelClass.getPopUri;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.joboui.NotificationActivity;
import com.example.joboui.R;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import io.vertx.core.json.JsonArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends LifecycleService implements ViewModelStoreOwner, HasDefaultViewModelProviderFactory {


    final ViewModelStore mViewModelStore = new ViewModelStore();
    private ViewModelProvider.Factory mFactory;
    private NotificationManager notificationManager;
    private BulkViewModel bulkViewModel;
    private final LinkedHashSet<Models.NotificationModels> notificationList = new LinkedHashSet<>();


    public NotificationService() {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("NOTIFICATION STARTED START");

        bulkViewModel = getDefaultViewModelProviderFactory().create(BulkViewModel.class);
        notificationManager = getSystemService(NotificationManager.class);

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(user -> getAllNotifications(user.getUsername())));
        }

        return START_STICKY;
    }

    private void getAllNotifications(String username) {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Getting notifications");
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
                            System.out.println("Getting notifications "+users.size());
                            users.forEach(u -> {
                                try {
                                    Models.NotificationModels models = getObjectMapper().readValue(u.toString(), Models.NotificationModels.class);
                                    notificationList.add(models);
                                    showNotification(models);
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
        }, 0, 10000);
    }


    private void showAllNotifications() {
        notificationList.forEach(this::showNotification);
    }

    private void showNotification(Models.NotificationModels notificationModel) {
        //todo change logo
        if (notificationModel.isNotified()) {
            return;
        }

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, USER_NOTIFICATION_CHANNEL)
                .setContentTitle(notificationModel.getTitle())
                .setContentText(notificationModel.getDescription())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setGroup(SYNCH_NOTIFICATION_CHANNEL)
                .setOnlyAlertOnce(true)
                .setSubText(notificationModel.getSub_text() != null ? notificationModel.getSub_text() : notificationModel.getUid())
                .setContentIntent(pendingIntent)
                .setSound(getPopUri(getApplicationContext()).getKey(0));

        notificationManager.notify(Integer.parseInt(String.valueOf(notificationModel.getId())), notificationBuilder.build());
        updateNotification(notificationModel);
    }

    private void updateNotification(Models.NotificationModels notificationModel) {
        notificationModel.setNotified(true);
        bulkViewModel.updateNotificationLiveData(notificationModel.getUid(), notificationModel.getId()).observe(this, notification -> {
            notification.ifPresent(n -> {
                notificationList.remove(notificationModel);
                notificationList.add(notification.get());
            });
        });
    }

    private int getPercentageProgress(Long bytesTransferred, Long totalByteCount) {
        return (int) ((bytesTransferred * 100) / totalByteCount);
    }

    @NonNull
    @Override
    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        return mFactory != null ? mFactory : (mFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication()));
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }

}