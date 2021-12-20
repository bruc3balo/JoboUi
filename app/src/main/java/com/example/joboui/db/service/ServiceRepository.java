package com.example.joboui.db.service;

import static com.example.joboui.globals.GlobalDb.application;
import static com.example.joboui.globals.GlobalDb.jobApi;
import static com.example.joboui.globals.GlobalDb.serviceApi;
import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.example.joboui.utils.DataOps.getAuthorization;

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

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ServiceRepository {
    private final ServiceDao serviceDao;
    private final MutableLiveData<Optional<List<Services>>> servicesMutable = new MutableLiveData<>();

    //methods are to store just one users data, not many
    //to be used to cache

    public ServiceRepository(Application application) {
        ServiceDB database = ServiceDB.getInstance(application);
        serviceDao = database.serviceDao();
        servicesMutable.setValue(Optional.of(serviceDao.getServicesList()));
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

        List<Domain.Services> servicesList = new ArrayList<>();
        Map<String, String> parameters = new HashMap<>();

        String header = "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();


        serviceApi.getAllServices(header).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null) {
                    //servicesMutable.setValue(Optional.empty());
                    System.out.println("FAILED TO GET SERVICE RESPONSE");
                    return;
                }

                if (jsonResponse.getData() == null) {
                    // servicesMutable.setValue(Optional.empty());
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

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }


                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } finally {
                    if (!servicesList.isEmpty()) {
                        try {
                            clearService();
                        } catch (Exception ignored) {
                        } finally {
                            servicesList.forEach(s -> insert(s));
                        }
                    }
                }

                servicesMutable.setValue(Optional.of(servicesList));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                //servicesMutable.setValue(Optional.empty());
            }
        });

    }


    private MutableLiveData<List<Models.ServicesModel>> getServicesLive() {
        MutableLiveData<List<Models.ServicesModel>> allServices = new MutableLiveData<>();
        List<Models.ServicesModel> servicesList = new ArrayList<>();
        serviceApi.getAllServices(getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null) {
                    //servicesMutable.setValue(Optional.empty());
                    System.out.println("FAILED TO GET SERVICE RESPONSE");
                    return;
                }

                if (jsonResponse.getData() == null) {
                    // servicesMutable.setValue(Optional.empty());
                    System.out.println("FAILED TO GET SERVICE DATA");
                    return;
                }

                try {
                    JsonArray serviceArray = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));
                    for (int i = 0; i < serviceArray.size(); i++) {
                        try {
                            System.out.println("count " + i);
                            Models.ServicesModel service = getObjectMapper().readValue(new JsonObject(serviceArray.getJsonObject(i).getMap()).toString(), Models.ServicesModel.class);
                            servicesList.add(service);

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                    allServices.setValue(servicesList);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                //servicesMutable.setValue(Optional.empty());
            }
        });
        return allServices;
    }

    private MutableLiveData<Optional<Boolean>> updateService(String currentServiceName, Models.ServiceUpdateForm form) {
        MutableLiveData<Optional<Boolean>> mutableLiveData = new MutableLiveData<>();

        serviceApi.updateAService(currentServiceName, form, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null || jsonResponse.getData() == null || !jsonResponse.isSuccess() || jsonResponse.isHas_error()) {
                    mutableLiveData.setValue(Optional.of(false));
                    return;
                }

                mutableLiveData.setValue(Optional.of(true));

            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<List<Models.ServiceCashFlow>> getServiceFlows () {
        MutableLiveData<List<Models.ServiceCashFlow>> mutableLiveData = new MutableLiveData<>();
        List<Models.ServiceCashFlow> serviceCashFlowList = new ArrayList<>();

        jobApi.getPaymentFlows(getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null || jsonResponse.getData() == null || !jsonResponse.isSuccess() || jsonResponse.isHas_error()) {
                    mutableLiveData.setValue(new ArrayList<>());
                    return;
                }


                try {
                    JsonArray serviceArray = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));
                    for (int i = 0; i < serviceArray.size(); i++) {
                        try {
                            System.out.println("count " + i);
                            Models.ServiceCashFlow service = getObjectMapper().readValue(new JsonObject(serviceArray.getJsonObject(i).getMap()).toString(), Models.ServiceCashFlow.class);
                            serviceCashFlowList.add(service);

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                    mutableLiveData.setValue(serviceCashFlowList);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(new ArrayList<>());
                }


            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(new ArrayList<>());
            }
        });

        return mutableLiveData;
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

    public LiveData<Optional<List<Domain.Services>>> getServiceLive() {
        return servicesMutable;
    }

    public Optional<Domain.Services> getServiceByName(String name) {
        return serviceDao.findServiceByName(name);
    }

    public LiveData<List<Models.ServicesModel>> getServicesLiveData() {
        return getServicesLive();
    }

    public LiveData<Optional<Boolean>> updateAService (String currentServiceName, Models.ServiceUpdateForm form) {
        return updateService(currentServiceName, form);
    }

    public LiveData<List<Models.ServiceCashFlow>> getServiceFlowsLiveData () {
        return getServiceFlows();
    }


}
