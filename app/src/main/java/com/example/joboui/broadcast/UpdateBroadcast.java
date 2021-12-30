package com.example.joboui.broadcast;

import static com.example.joboui.JobTrackActivity.refreshJobTracking;
import static com.example.joboui.admin.AllJobsActivity.refreshJobListAdmin;
import static com.example.joboui.clientUi.MyJobs.refreshJobListClient;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.globals.GlobalVariables.ROLE;
import static com.example.joboui.globals.GlobalVariables.UPDATE;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.serviceProviderUi.ServiceProviderActivity.refreshCount;
import static com.example.joboui.serviceProviderUi.pages.JobRequests.refreshJobListProvider;
import static com.example.joboui.utils.DataOps.getAuthorization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.utils.AppRolesEnum;
import com.example.joboui.utils.DataOps;
import com.example.joboui.utils.JsonResponse;

import java.io.IOException;
import java.util.Optional;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBroadcast extends BroadcastReceiver {

    public static final String UPDATE_INTENT = "com.example.joboui.intent.action.UPDATE";


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("update message received");

        if (UPDATE_INTENT.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            String update = extras.getString(UPDATE);
            String role = extras.getString(ROLE);
            String username = extras.getString(USERNAME);

            switch (update) {
                default:
                    break;

                case JOB:
                    if (role.equals(AppRolesEnum.ROLE_CLIENT.name())) {
                        refreshJobListClient.setValue(Optional.of(true));
                        refreshJobTracking.postValue(Optional.of(true));
                    } else if (role.equals(AppRolesEnum.ROLE_SERVICE_PROVIDER.name())) {
                        refreshJobListProvider.setValue(Optional.of(true));
                        refreshJobTracking.postValue(Optional.of(true));
                        refreshCount.postValue(Optional.of(true));
                    } else if (role.equals(AppRolesEnum.ROLE_ADMIN.name())) {
                        refreshJobListAdmin.setValue(Optional.of(true));
                    } else {
                        System.out.println("role not found");
                    }
                    break;

                case USER_DB:
                    userApi.getUsers(username, getAuthorization()).enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                            System.out.println("=============== SUCCESS GETTING USER " + username + "===================");
                            try {
                                //extract user data
                                JsonResponse jsonResponse = response.body();


                                if (jsonResponse == null) {
                                    System.out.println("NO USER DATA "+response.code());
                                    return;
                                }  else {
                                    System.out.println("USER  DAA");
                                }

                                System.out.println("=============== SUCCESS GETTING USER " + getObjectMapper().writeValueAsString(jsonResponse.getData()) + "===================");


                                if (jsonResponse.getData() == null) {
                                    return;
                                }

                                JsonObject userJson = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData())).getJsonObject(0);

                                //save user to offline db
                                Models.AppUser user = getObjectMapper().readValue(userJson.toString(), Models.AppUser.class);

                                userRepository.insert(DataOps.getDomainUserFromModelUser(user));

                                Thread.sleep(2000);
                                System.out.println("======== ROLE INSERTED " + user.getRole().getName() + "===============");

                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
                    break;
            }
            System.out.println(update + " update message " + role);
        }
    }
}