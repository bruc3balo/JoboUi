package com.example.joboui.services;

import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.getAuthorization;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashSet;
import java.util.Optional;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BulkViewModel extends AndroidViewModel {

    public BulkViewModel(@NonNull Application application) {
        super(application);
    }

    //get notifications
    private MutableLiveData<LinkedHashSet<Models.NotificationModels>> getAllMyNotifications(String username) {
        MutableLiveData<LinkedHashSet<Models.NotificationModels>> mutableLiveData = new MutableLiveData<>();
        LinkedHashSet<Models.NotificationModels> notificationModelsList = new LinkedHashSet<>();

        userApi.getMyNotifications(username, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200 || jsonResponse == null || jsonResponse.isHas_error() || !jsonResponse.isSuccess() || jsonResponse.getData() == null) {
                    mutableLiveData.setValue(new LinkedHashSet<>());
                    return;
                }

                try {
                    JsonArray users = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));
                    users.forEach(u -> {
                        try {
                            Models.NotificationModels models = getObjectMapper().readValue(u.toString(), Models.NotificationModels.class);
                            notificationModelsList.add(models);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                    mutableLiveData.setValue(notificationModelsList);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(new LinkedHashSet<>());
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                mutableLiveData.setValue(new LinkedHashSet<>());
            }
        });

        return mutableLiveData;
    }

    //set notification to seen
    private MutableLiveData<Optional<Models.NotificationModels>> updateNotification(String username, Long id) {
        MutableLiveData<Optional<Models.NotificationModels>> mutableLiveData = new MutableLiveData<>();
        userApi.updateNotifications(username, id, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200 || jsonResponse == null || jsonResponse.isHas_error() || !jsonResponse.isSuccess() || jsonResponse.getData() == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                try {
                    Models.NotificationModels notification = getObjectMapper().readValue(new JsonObject(getObjectMapper().writeValueAsString(jsonResponse.getData())).toString(), Models.NotificationModels.class);
                    mutableLiveData.setValue(Optional.of(notification));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(Optional.empty());
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    public LiveData<Optional<Models.NotificationModels>> updateNotificationLiveData(String username, Long id) {
        return updateNotification(username, id);
    }

    public LiveData<LinkedHashSet<Models.NotificationModels>> getAllMyNotificationsLiveData(String username) {
        System.out.println("Getting notifications");
        return getAllMyNotifications(username);
    }

}
