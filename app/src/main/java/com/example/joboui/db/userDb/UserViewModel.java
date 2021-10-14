package com.example.joboui.db.userDb;


import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.ACCESS_TOKEN;
import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.AUTHORIZATION;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;
import static com.example.joboui.globals.GlobalVariables.PASSWORD;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.decodeToken;
import static com.example.joboui.login.SignInActivity.editSp;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;

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
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserViewModel extends AndroidViewModel {

    private final Application application;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MutableLiveData<Optional<Models.AppUser>> getUserMutableByUsername(String username) {
        MutableLiveData<Optional<Models.AppUser>> userMutableLiveData = new MutableLiveData<>();


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
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    System.out.println("=============== SUCCESS GETTING USER " + getObjectMapper().writeValueAsString(jsonResponse.getData()) + "===================");


                    if (jsonResponse.getData() == null) {
                        userMutableLiveData.setValue(Optional.empty());
                        return;
                    }

                    JSONArray userArray = new JSONArray(jsonResponse.getData());

                    //save user to offline db
                    Models.AppUser user = getObjectMapper().readValue(userArray.get(0).toString(), Models.AppUser.class);

                    userRepository.insert(new Domain.User(user.getId(), user.getId_number(), user.getPhone_number(), user.getBio(), user.getEmail_address(), user.getNames(), user.getUsername(), user.getRole().getName(), user.getCreated_at().toString(), user.getUpdated_at().toString(), user.getDeleted(), user.getDisabled(), user.getSpecialities(), user.getPreferred_working_hours(), user.getLast_known_location(), user.getPassword()));

                    System.out.println("======== ROLE INSERTED " + user.getRole().getName() + "===============");

                    userMutableLiveData.setValue(Optional.of(user));
                } catch (IOException | JSONException e) {
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

    private MutableLiveData<Optional<LoginResponse>> accessToken(Models.UsernameAndPasswordAuthenticationRequest request) {
        Toast.makeText(application, "Sign in ", Toast.LENGTH_SHORT).show();

        Map<String, String> map = new HashMap<>();
        MutableLiveData<Optional<LoginResponse>> mutableLiveData = new MutableLiveData<>();


        ObjectMapper mapper = getObjectMapper();

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
                    Models.AppUser createdUser = mapper.readValue(jsonResponse.getData().toString(), Models.AppUser.class);
                    Domain.User user = new Domain.User(createdUser.getId(), createdUser.getId_number(), createdUser.getPhone_number(), createdUser.getBio(), createdUser.getEmail_address(), createdUser.getNames(), createdUser.getUsername(), createdUser.getRole().getName(), createdUser.getCreated_at().toString(), createdUser.getUpdated_at().toString(), createdUser.getDeleted(), createdUser.getDisabled(), createdUser.getSpecialities(), createdUser.getPreferred_working_hours(), createdUser.getLast_known_location(), createdUser.getPassword());
                    userRepository.insert(user);
                    mutableLiveData.setValue(Optional.of(user));
                } catch (JsonProcessingException e) {
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


    private MutableLiveData<List<String>> getUsernames() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        List<String> usernames = new ArrayList<>();
        mutableLiveData.setValue(usernames);

        ObjectMapper mapper = getObjectMapper();

        userApi.getUsernames().enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                if (response.body() == null) {
                    return;
                }

                if (response.body().getData() == null) {
                    return;
                }


                try {
                    JsonResponse jsonResponse = response.body();
                    List jsonUsernames = mapper.readValue(new JSONArray(jsonResponse.getData().toString()).toString(), List.class);
                    for (Object name : jsonUsernames) {
                        usernames.add(name.toString());
                    }
                } catch (JsonProcessingException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Toast.makeText(application, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("FAILED TO GET USERNAMES");
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<List<String>> getPhoneNumbers() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        List<String> numbers = new ArrayList<>();
        mutableLiveData.setValue(numbers);

        ObjectMapper mapper = getObjectMapper();


        userApi.getNumbers().enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                if (response.body() == null) {
                    return;
                }

                if (response.body().getData() == null) {
                    return;
                }


                try {
                    JsonResponse jsonResponse = response.body();
                    List jsonNumbers = mapper.readValue(new JSONArray(jsonResponse.getData().toString()).toString(), List.class);
                    for (Object number : jsonNumbers) {
                        numbers.add(number.toString());
                    }
                } catch (JsonProcessingException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                Toast.makeText(application, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("FAILED TO GET NUMBERS");
            }
        });

        return mutableLiveData;
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

    public LiveData<Optional<Domain.User>> createNewUser(Models.NewUserForm form) throws JSONException, JsonProcessingException {
        return createUser(form);
    }


}
