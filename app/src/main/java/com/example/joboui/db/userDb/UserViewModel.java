package com.example.joboui.db.userDb;

import static com.example.joboui.adapters.ProviderRVAdapter.getDistanceInKm;
import static com.example.joboui.adapters.ProviderRVAdapter.isOpen;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.ACCESS_TOKEN;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.LATITUDE;
import static com.example.joboui.globals.GlobalVariables.LONGITUDE;
import static com.example.joboui.globals.GlobalVariables.PAGE_NO;
import static com.example.joboui.globals.GlobalVariables.PASSWORD;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.SPECIALITIES;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.decodeToken;
import static com.example.joboui.login.SignInActivity.editSp;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.example.joboui.services.NotificationService.myLocation;
import static com.example.joboui.utils.DataOps.getMapFromString;
import static com.example.joboui.utils.FlatEarthDist.distance;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.auth0.android.jwt.JWT;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.model.Models.LoginResponse;
import com.example.joboui.model.Models.UserUpdateForm;
import com.example.joboui.utils.DataOps;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {

    private final Application application;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MutableLiveData<Optional<Models.AppUser>> getUserMutableByUsername(String username) {
        MutableLiveData<Optional<Models.AppUser>> userMutableLiveData = new MutableLiveData<>();

        ObjectMapper mapper = getObjectMapper();

        String header = "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();

        System.out.println("headers " + header);

        userApi.getUsers(username, header).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                System.out.println("=============== SUCCESS GETTING USER " + username + "===================");
                try {
                    //extract user data
                    JsonResponse jsonResponse = response.body();


                    if (jsonResponse == null) {
                        System.out.println("NO USER DATA " + response.code());
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    } else {
                        System.out.println("USER  DAA");
                    }

                    System.out.println("=============== SUCCESS GETTING USER " + getObjectMapper().writeValueAsString(jsonResponse.getData()) + "===================");


                    if (jsonResponse.getData() == null) {
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    JsonObject userJson = new JsonArray(mapper.writeValueAsString(jsonResponse.getData())).getJsonObject(0);

                    //save user to offline db
                    Models.AppUser user = mapper.readValue(userJson.toString(), Models.AppUser.class);

                    userRepository.insert(DataOps.getDomainUserFromModelUser(user));

                    Thread.sleep(2000);


                    System.out.println("======== ROLE INSERTED " + user.getRole().getName() + "===============");

                    userMutableLiveData.setValue(Optional.of(user));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
                    userMutableLiveData.setValue(Optional.empty());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(application, "Failed to login " + t, Toast.LENGTH_SHORT).show();
                userMutableLiveData.setValue(Optional.empty());

            }
        });

        return userMutableLiveData;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MutableLiveData<Optional<Models.AppUser>> refreshUserData() {
        MutableLiveData<Optional<Models.AppUser>> userMutableLiveData = new MutableLiveData<>();

        ObjectMapper mapper = getObjectMapper();

        String header = "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();

        System.out.println("headers " + header);

        userApi.getUsers(userRepository.getUser().isPresent() ? userRepository.getUser().get().getUsername() : "", header).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                try {
                    //extract user data
                    JsonResponse jsonResponse = response.body();


                    if (jsonResponse == null) {
                        System.out.println("NO USER DATA " + response.code());
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    } else {
                        System.out.println("USER  DAA");
                    }

                    System.out.println("=============== SUCCESS GETTING USER " + getObjectMapper().writeValueAsString(jsonResponse.getData()) + "===================");


                    if (jsonResponse.getData() == null) {
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    JsonObject userJson = new JsonArray(mapper.writeValueAsString(jsonResponse.getData())).getJsonObject(0);

                    //save user to offline db
                    Models.AppUser user = mapper.readValue(userJson.toString(), Models.AppUser.class);

                    userRepository.insert(DataOps.getDomainUserFromModelUser(user));

                    Thread.sleep(2000);


                    System.out.println("======== ROLE INSERTED " + user.getRole().getName() + "===============");

                    userMutableLiveData.setValue(Optional.of(user));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
                    userMutableLiveData.setValue(Optional.empty());

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(application, "Failed to login " + t, Toast.LENGTH_SHORT).show();
                userMutableLiveData.setValue(Optional.empty());

            }
        });

        return userMutableLiveData;
    }

    private MutableLiveData<Optional<List<Models.AppUser>>> getServiceProviders(String speciality, Integer page, Integer pageSize, Boolean scheduled) throws JSONException, JsonProcessingException {
        MutableLiveData<Optional<List<Models.AppUser>>> mutableLiveData = new MutableLiveData<>();
        List<Models.AppUser> userList = new ArrayList<>();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(PAGE_NO, String.valueOf(page));
        //parameters.put(PAGE_SIZE, String.valueOf(pageSize));
        parameters.put(SPECIALITIES, speciality);

        Map<String, ?> map = getSp(USER_DB, application);

        String header = "Bearer " + Objects.requireNonNull(getSp(USER_DB, application).get(REFRESH_TOKEN)).toString();

        userApi.getProviders(parameters, header).enqueue(new Callback<JsonResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse.getData() == null) {
                    mutableLiveData.setValue(Optional.empty());
                }

                try {

                    JsonArray users = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));
                    users.forEach(u -> {
                        try {
                            boolean distanceMet = true;
                            Models.AppUser user = getObjectMapper().readValue(u.toString(), Models.AppUser.class);

                            if (!user.getLast_known_location().equals(HY)) {
                                LinkedHashMap<String, String> map = getMapFromString(user.getLast_known_location());
                                LatLng userLocation = new LatLng(Double.parseDouble(Objects.requireNonNull(map.get(LATITUDE))), Double.parseDouble(Objects.requireNonNull(map.get(LONGITUDE))));
                                double distance = distance(userLocation, myLocation);
                                //distanceMet = (distance / 1000) < 50.0;
                            }

                            LinkedHashMap<String, String> workingHours = getMapFromString(user.getPreferred_working_hours());

                            //only get lsp if distance is met

                            if (distanceMet) {
                                //if not open and scheduled
                                //if open
                                if ((!isOpen(workingHours) && scheduled) || isOpen(workingHours)) {
                                    userList.add(user);
                                }

                            }
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });

                    mutableLiveData.setValue(Optional.of(userList));
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

    private MutableLiveData<Optional<LoginResponse>> accessToken(Models.UsernameAndPasswordAuthenticationRequest request) {
        Toast.makeText(application, "Sign in ", Toast.LENGTH_SHORT).show();

        Map<String, String> map = new HashMap<>();
        MutableLiveData<Optional<LoginResponse>> mutableLiveData = new MutableLiveData<>();

        userApi.getToken(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                if (response.body() == null) {
                    return;
                }


                LoginResponse loginResponse = response.body();

                System.out.println("====================== Access token =======================" + loginResponse.getAccess_token());
                System.out.println("====================== Access token =======================" + loginResponse.getAccess_token());

                JWT jwt = decodeToken(loginResponse.getRefresh_token());

                if (jwt == null) {
                    Toast.makeText(application, "invalid Token ", Toast.LENGTH_SHORT).show();
                    System.out.println("====================== INVALID TOKEN =======================");
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                map.put(ACCESS_TOKEN, loginResponse.getAccess_token());
                map.put(REFRESH_TOKEN, loginResponse.getRefresh_token());
                map.put(PASSWORD, request.getPassword());
                map.put(USERNAME, request.getUsername());

                editSp(USER_DB, map, application);
                mutableLiveData.setValue(Optional.of(loginResponse));
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                System.out.println("============================ ERROR SENDING LOGIN REQUEST ==============================" + t.getMessage());
                Toast.makeText(application, "Failed to login " + t.getMessage(), Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(Optional.empty());
            }
        });
        return mutableLiveData;
    }

    private MutableLiveData<Optional<Domain.User>> createUser(Models.NewUserForm form) {
        Toast.makeText(application, "Creating user " + form.getUsername(), Toast.LENGTH_SHORT).show();
        ObjectMapper mapper = getObjectMapper();

        MutableLiveData<Optional<Domain.User>> mutableLiveData = new MutableLiveData<>();

        userApi.saveUser(form).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                if (response.code() == 409) {
                    Toast.makeText(application, "Account already created . Login", Toast.LENGTH_SHORT).show();
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (response.body() == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (response.body().getData() == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                JsonResponse jsonResponse = response.body();
                try {
                    Models.AppUser user = mapper.readValue(new JsonObject(mapper.writeValueAsString(jsonResponse.getData())).toString(), Models.AppUser.class);
                    Domain.User createdUser = DataOps.getDomainUserFromModelUser(user);

                    userRepository.insert(DataOps.getDomainUserFromModelUser(user));

                    System.out.println("NEW USER : " + mapper.writeValueAsString(user));


                    Map<String, String> map = new HashMap<>();

                    map.put(PASSWORD, form.getPassword());
                    map.put(USERNAME, form.getUsername());

                    editSp(USER_DB, map, application);

                    Thread.sleep(2000);

                    mutableLiveData.setValue(Optional.of(createdUser));
                } catch (JsonProcessingException | InterruptedException e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(Optional.empty());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                System.out.println("============================ ERROR SENDING CREATE REQUEST " + t.getMessage() + "==============================");
                Toast.makeText(application, "Failed to create user " + form.getUsername(), Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<Domain.User>> updateUser(UserUpdateForm form) {
        MutableLiveData<Optional<Domain.User>> mutableLiveData = new MutableLiveData<>();

        Toast.makeText(application, "Updating request", Toast.LENGTH_SHORT).show();
        Optional<Domain.User> repositoryUser = userRepository.getUser();

        if (!repositoryUser.isPresent()) {
            mutableLiveData.setValue(Optional.empty());
            return mutableLiveData;
        }

        ObjectMapper mapper = getObjectMapper();
        String token = "Bearer " + getSp(USER_DB, getApplication()).get(REFRESH_TOKEN);

        userApi.updateUser(repositoryUser.get().getUsername(), token, form).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                try {

                    if (response.body() == null) {
                        Toast.makeText(application, "Failed to get response", Toast.LENGTH_SHORT).show();
                        mutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    if (response.body().getData() == null) {
                        Toast.makeText(application, "Failed to get data", Toast.LENGTH_SHORT).show();
                        mutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    JsonResponse jsonResponse = response.body();
                    Models.AppUser user = mapper.readValue(new JsonObject(mapper.writeValueAsString(jsonResponse.getData())).toString(), Models.AppUser.class);
                    Domain.User updatedUser = DataOps.getDomainUserFromModelUser(user);

                    userRepository.update(DataOps.getDomainUserFromModelUser(user));

                    Thread.sleep(2000);

                    mutableLiveData.setValue(Optional.of(updatedUser));
                } catch (JsonProcessingException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(application, "Error updating account", Toast.LENGTH_SHORT).show();
                    System.out.println("====================== Error updating account =======================");
                    mutableLiveData.setValue(Optional.empty());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                System.out.println("============================ ERROR SENDING UPDATE REQUEST " + t.getMessage() + "==============================");
            }
        });
        return mutableLiveData;
    }

    private MutableLiveData<Optional<Models.AppUser>> updateAUser(String username, UserUpdateForm form) {
        MutableLiveData<Optional<Models.AppUser>> mutableLiveData = new MutableLiveData<>();

        Toast.makeText(application, "Updating request", Toast.LENGTH_SHORT).show();


        ObjectMapper mapper = getObjectMapper();
        String token = "Bearer " + getSp(USER_DB, getApplication()).get(REFRESH_TOKEN);

        userApi.updateUser(username, token, form).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                try {

                    if (response.body() == null) {
                        Toast.makeText(application, "Failed to get response", Toast.LENGTH_SHORT).show();
                        mutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    if (response.body().getData() == null) {
                        Toast.makeText(application, "Failed to get data", Toast.LENGTH_SHORT).show();
                        mutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    JsonResponse jsonResponse = response.body();
                    Models.AppUser updatedUser = mapper.readValue(new JsonObject(mapper.writeValueAsString(jsonResponse.getData())).toString(), Models.AppUser.class);


                    Thread.sleep(2000);

                    mutableLiveData.setValue(Optional.of(updatedUser));
                } catch (JsonProcessingException | InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(application, "Error updating account", Toast.LENGTH_SHORT).show();
                    System.out.println("====================== Error updating account =======================");
                    mutableLiveData.setValue(Optional.empty());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                System.out.println("============================ ERROR SENDING UPDATE REQUEST " + t.getMessage() + "==============================");
            }
        });
        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> getAllUserData() {
        MutableLiveData<Optional<JsonResponse>> userMutableLiveData = new MutableLiveData<>();
        ObjectMapper mapper = getObjectMapper();
        String token = "Bearer " + getSp(USER_DB, getApplication()).get(REFRESH_TOKEN);


        userApi.getAllUsers(token).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null || jsonResponse.getData() == null || jsonResponse.isHas_error() || !jsonResponse.isSuccess()) {
                    userMutableLiveData.setValue(Optional.empty());
                    return;
                }

                userMutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                userMutableLiveData.setValue(Optional.empty());
            }
        });

        return userMutableLiveData;
    }

    private MutableLiveData<List<String>> getUsernames() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        List<String> usernames = new ArrayList<>();


        ObjectMapper mapper = getObjectMapper();

        userApi.getUsernames().enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                if (response.body() == null) {
                    System.out.println("NO RESPONSE USERS");
                    return;
                }

                if (response.body().getData() == null) {
                    System.out.println("NO DATA USERS");
                    return;
                }


                try {
                    JsonResponse jsonResponse = response.body();


                    System.out.println("USERNAMES BODY ================= " + mapper.writeValueAsString(response.body().getData()));

                    List jsonUsernames = mapper.readValue(new JSONArray(jsonResponse.getData().toString()).toString(), List.class);

                    System.out.println("USERNAMES LIST ================= " + Collections.singletonList(jsonUsernames));

                    for (Object name : jsonUsernames) {
                        System.out.println("ADDING NAME ================= " + name);

                        usernames.add(name.toString());
                    }
                    mutableLiveData.setValue(usernames);
                } catch (JsonProcessingException | JSONException e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(usernames);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Toast.makeText(application, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("FAILED TO GET USERNAMES");
                mutableLiveData.setValue(usernames);
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<List<String>> getPhoneNumbers() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        List<String> numbers = new ArrayList<>();

        ObjectMapper mapper = getObjectMapper();

        userApi.getNumbers().enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                if (response.body() == null) {
                    System.out.println("NO RESPONSE NUMBERS");
                    return;
                }

                if (response.body().getData() == null) {
                    System.out.println("NO DATA NUMBERS");
                    return;
                }


                try {
                    JsonResponse jsonResponse = response.body();
                    List jsonNumbers = mapper.readValue(new JSONArray(jsonResponse.getData().toString()).toString(), List.class);


                    System.out.println("NUMBERS DATA" + jsonResponse.getData().toString());


                    for (Object number : jsonNumbers) {

                        numbers.add(number.toString());

                    }
                    mutableLiveData.setValue(numbers);
                } catch (JsonProcessingException | JSONException e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(numbers);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Toast.makeText(application, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("FAILED TO GET NUMBERS");
                mutableLiveData.setValue(numbers);
            }
        });

        return mutableLiveData;
    }

    public void refreshToken() {
        Map<String, ?> map = getSp(USER_DB, application);
        String username = Objects.requireNonNull(map.get(USERNAME)).toString();
        String password = Objects.requireNonNull(getSp(USER_DB, application).get(PASSWORD)).toString();
        accessToken(new Models.UsernameAndPasswordAuthenticationRequest(username, password));
    }

    //expose
    public LiveData<Optional<LoginResponse>> getAccessToken(Models.UsernameAndPasswordAuthenticationRequest request) throws JSONException, JsonProcessingException {
        return accessToken(request);
    }

    public LiveData<Optional<Models.AppUser>> getUserByUsername(String username) {
        return getUserMutableByUsername(username);
    }

    public LiveData<List<String>> getAllUsernames() {
        return getUsernames();
    }

    public LiveData<List<String>> getAllPhoneNumbers() {
        return getPhoneNumbers();
    }

    public LiveData<Optional<Domain.User>> createNewUser(Models.NewUserForm form) {
        return createUser(form);
    }

    public LiveData<Optional<Domain.User>> updateExistingUser(UserUpdateForm form) {
        return updateUser(form);
    }

    public LiveData<Optional<Models.AppUser>> updateAnExistingUser(String username, UserUpdateForm form) {
        return updateAUser(username, form);
    }

    public LiveData<Optional<List<Models.AppUser>>> getProviders(String speciality, Integer page, Integer pageSize,Boolean scheduled) throws JSONException, JsonProcessingException {
        return getServiceProviders(speciality, page, pageSize,scheduled);
    }

    public LiveData<Optional<JsonResponse>> getLiveAllUsers() {
        return getAllUserData();
    }

    public LiveData<Optional<Models.AppUser>> refreshMyData() {
        return refreshUserData();
    }

}
