package com.example.joboui.services;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static com.example.joboui.broadcast.UpdateBroadcast.UPDATE_INTENT;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.LATITUDE;
import static com.example.joboui.globals.GlobalVariables.LONGITUDE;
import static com.example.joboui.globals.GlobalVariables.ROLE;
import static com.example.joboui.globals.GlobalVariables.UPDATE;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.getAuthorization;
import static com.example.joboui.utils.NotificationChannelClass.SYNCH_NOTIFICATION_CHANNEL;
import static com.example.joboui.utils.NotificationChannelClass.USER_NOTIFICATION_CHANNEL;
import static com.example.joboui.utils.NotificationChannelClass.getPopUri;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.joboui.NotificationActivity;
import com.example.joboui.R;
import com.example.joboui.broadcast.UpdateBroadcast;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.DataOps;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
    private Domain.User myUser;
    public static boolean notificationServiceRunning = false;

    public static LatLng myLocation = null;

    public NotificationService () {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("NOTIFICATION STARTED START");

        notificationServiceRunning = true;
        bulkViewModel = getDefaultViewModelProviderFactory().create(BulkViewModel.class);
        notificationManager = getSystemService(NotificationManager.class);

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(user -> {
                myUser = user;
                getAllNotifications(user.getUsername());
                getDeviceLocation();
            }));
        }

        return START_STICKY;
    }

    //get notifications per 10 sec
    private void getAllNotifications(String username) {

        try {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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
                                System.out.println("Getting notifications " + users.size());
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
        } catch (NullPointerException ignore) {

        }
    }

    //get location
    private void getDeviceLocation () {
        //todo distance limit
        //todo fencing
        //todo remind scheduled jobs

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) { //check if location is allowed
            try {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NotificationService.this);
                        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Location currentLocation = task.getResult();
                                if (currentLocation != null) {
                                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                    myLocation = latLng;
                                    LinkedHashMap<String, String> locationMap = new LinkedHashMap<>();
                                    locationMap.put(LATITUDE, String.valueOf(latLng.latitude));
                                    locationMap.put(LONGITUDE, String.valueOf(latLng.longitude));

                                    String loc = DataOps.getStringFromMap(locationMap);

                                    userApi.updateUser(myUser.getUsername(), getAuthorization(), new Models.UserUpdateForm(null, loc)).enqueue(new Callback<JsonResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                                            System.out.println("Updated user location");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                                            t.printStackTrace();
                                            System.out.println("Failed to update user location");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }, 0, 10000);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {}
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

        sendBroadcast(new Intent(this, UpdateBroadcast.class).setAction(UPDATE_INTENT).putExtra(USERNAME, myUser.getUsername()).putExtra(UPDATE, notificationModel.getUpdating()).putExtra(ROLE, myUser.getRole()));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, USER_NOTIFICATION_CHANNEL)
                .setContentTitle(notificationModel.getTitle())
                .setContentText(notificationModel.getDescription())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setGroup(SYNCH_NOTIFICATION_CHANNEL)
                .setGroupSummary(true)
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationServiceRunning = false;
    }
}