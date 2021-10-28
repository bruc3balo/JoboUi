package com.example.joboui.db.service;

import static com.example.joboui.globals.GlobalDb.application;
import static com.example.joboui.globals.GlobalDb.serviceApi;
import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Transaction;

import com.example.joboui.domain.Domain;
import com.example.joboui.domain.Domain.Services;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ServiceRepository {
    private final ServiceDao serviceDao;

    //methods are to store just one users data, not many
    //to be used to cache

    public ServiceRepository(Application application) {
        ServiceDB database = ServiceDB.getInstance(application);
        serviceDao = database.serviceDao();

    }

    //Abstraction layer for encapsulation

    @Transaction
    private void insertService(Services services) {
        new Thread(() -> {
            try {
                serviceDao.insert(services);
                System.out.println(services.getName() + " inserted");
            } catch (Exception e) {
                System.out.println(services.getName() + " failed instead");
            }
        }).start();
    }

    @Transaction
    private Services updateService(Services services) {
        new Thread(() -> {
            try {
                serviceDao.update(services);
                System.out.println(services.getName() + " updated");
            } catch (Exception e) {
                System.out.println(services.getName() + " inserted instead");
                insertService(services);
            }
        }).start();
        return services;
    }

    @Transaction
    private void deleteService(Services service) {
        new Thread(() -> {
            serviceDao.delete(service);
            System.out.println(service.getName() + " deleted");
        }).start();
    }

    @Transaction
    private void clearService() {
        new Thread(() -> {
            serviceDao.clear();
            System.out.println("CLEARING SERVICE DB");
        }).start();
    }

    public void updateServices() {
        MutableLiveData<Optional<List<Services>>> servicesMutable = new MutableLiveData<>();

        List<Domain.Services> servicesList = new ArrayList<>();
        Map<String, String> parameters = new HashMap<>();

        String header = "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();


        serviceApi.getAllServices(header).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null) {
                    servicesMutable.setValue(Optional.empty());
                    System.out.println("FAILED TO GET SERVICE RESPONSE");
                    return;
                }

                if (jsonResponse.getData() == null) {
                    servicesMutable.setValue(Optional.empty());
                    System.out.println("FAILED TO GET SERVICE DATA");
                    return;
                }


                try {

                    JsonArray serviceArray = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));


                    for (int i = 0; i < serviceArray.size(); i++) {
                        try {
                            System.out.println("count " + i);
                            Models.ServicesModel ser = getObjectMapper().readValue(new JsonObject(serviceArray.getJsonObject(i).getMap()).toString(), Models.ServicesModel.class);
                            Domain.Services service = new Services(ser.getId(), ser.getName(), ser.getDescription(), ser.getDisabled(), ser.getCreated_at().toString(), ser.getUpdated_at().toString());
                            servicesList.add(service);

                            servicesMutable.setValue(Optional.of(servicesList));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                if (!servicesList.isEmpty()) {
                    clearService();
                    servicesList.forEach(s -> insert(s));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                servicesMutable.setValue(Optional.empty());
            }
        });

    }


    //Used Methods
    public void insert(Domain.Services services) {
        insertService(services);
    }

    public void update(Domain.Services services) {
        updateService(services);
    }

    public void delete(Domain.Services services) {
        deleteService(services);
    }

    public void deleteServiceDb() {
        clearService();
    }

    public Optional<Domain.Services> getService() {
        List<Domain.Services> user = serviceDao.getServicesList();
        if (user == null || user.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(user.get(0));
        }
    }

    public LiveData<List<Domain.Services>> getServiceLive() {
        return serviceDao.getServiceListLiveData();
    }

    public Optional<Domain.Services> getServiceByName(String name) {
        return serviceDao.findServiceByName(name);
    }


}
