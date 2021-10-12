package com.example.joboui.db.userDb;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.ACCESS_TOKEN;
import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.PASSWORD;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.decodeToken;
import static com.example.joboui.login.SignInActivity.editSp;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.login.SignInActivity.getSp;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UserViewModel extends AndroidViewModel {

    private final Application application;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MutableLiveData<Models.AppUser> getUserMutableByUsername(String username) {
        MutableLiveData<Models.AppUser> userMutableLiveData = new MutableLiveData<>();

        String url = API_URL + CONTEXT_URL + "/user/all";
        RequestQueue queue = Volley.newRequestQueue(application);

        ObjectMapper mapper = getObjectMapper();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {
                //extract user data
                JsonResponse jsonResponse = mapper.readValue(response.toString(), JsonResponse.class);
                JsonObject userJson = new JsonArray(mapper.writeValueAsString(jsonResponse.getData())).getJsonObject(0);

                //save user to offline db
                Models.AppUser user = mapper.readValue(userJson.toString(), Models.AppUser.class);

                userRepository.insert(new Domain.User(user.getId(), user.getId_number(), user.getPhone_number(), user.getBio(), user.getEmail_address(), user.getNames(), user.getUsername(), user.getRole().getName(), user.getCreated_at().toString(), user.getUpdated_at().toString(), user.getDeleted(), user.getDisabled(), user.getSpecialities(), user.getPreferred_working_hours(), user.getLast_known_location(), user.getPassword()));

                System.out.println("======== ROLE INSERTED " + user.getRole().getName() + "===============");

                //update login status
               /* Map<String, Boolean> map = new HashMap<>();
                map.put(LOGGED_IN, true);
                editSp(USER_DB, map, application);*/

                userMutableLiveData.setValue(user);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(application, "Failed to login " + error, Toast.LENGTH_SHORT).show()) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                //todo fix param bug
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(AUTHORIZATION, "Bearer " + getSp(USER_DB, application).get(ACCESS_TOKEN));
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };


        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
        return userMutableLiveData;
    }

    private MutableLiveData<Map<String, String>> accessToken(Models.UsernameAndPasswordAuthenticationRequest request) throws JSONException, JsonProcessingException {
        Toast.makeText(application, "Sign in ", Toast.LENGTH_SHORT).show();

        Map<String, String> map = new HashMap<>();
        MutableLiveData<Map<String, String>> mutableLiveData = new MutableLiveData<>();
        RequestQueue queue = Volley.newRequestQueue(application);
        String url = API_URL + CONTEXT_URL + "/login";
        ObjectMapper mapper = getObjectMapper();

        String data = mapper.writeValueAsString(request);
        JSONObject object = new JSONObject(data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, response -> {

            try {
                Models.LoginResponse loginResponse = mapper.readValue(response.toString(), Models.LoginResponse.class);
                JWT jwt = decodeToken(loginResponse.getRefresh_token());

                if (jwt != null) {

                    map.put(ACCESS_TOKEN, loginResponse.getAccess_token());
                    map.put(REFRESH_TOKEN, loginResponse.getRefresh_token());
                    map.put(PASSWORD, request.getPassword());
                    map.put(USERNAME, request.getUsername());

                    editSp(USER_DB, map, application);

                    mutableLiveData.setValue(map);
                } else {
                    Toast.makeText(application, "invalid Token ", Toast.LENGTH_SHORT).show();
                    System.out.println("====================== INVALID TOKEN =======================");
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(application, "Error Logging in", Toast.LENGTH_SHORT).show();
                System.out.println("====================== Error Logging in =======================");
            }

        }, error -> {
            System.out.println("============================ ERROR SENDING LOGIN REQUEST ==============================");
            mutableLiveData.setValue(new HashMap<>());
            Toast.makeText(application, "Failed to login " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
        return mutableLiveData;
    }


    private MutableLiveData<List<String>> getUsernames() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        RequestQueue queue = Volley.newRequestQueue(application);
        List<String> usernames = new ArrayList<>();
        String url = API_URL + CONTEXT_URL + "/user/usernames";
        ObjectMapper mapper = getObjectMapper();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {
                //extract user data
                JsonResponse jsonResponse = mapper.readValue(response.toString(), JsonResponse.class);
                List jsonUsernames = mapper.readValue(new JSONArray(jsonResponse.getData().toString()).toString(), List.class);

                System.out.println("USERNAMELISTList : " + jsonUsernames);
                usernames.addAll(jsonUsernames);
                mutableLiveData.setValue(usernames);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(application, "Failed to login " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };

        queue.add(jsonObjectRequest);
        return mutableLiveData;
    }

    private MutableLiveData<List<String>> getPhoneNumbers() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        RequestQueue queue = Volley.newRequestQueue(application);
        List<String> numbers = new ArrayList<>();

        String url = API_URL + CONTEXT_URL + "/user/numbers";
        ObjectMapper mapper = getObjectMapper();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {
                //extract user data
                JsonResponse jsonResponse = mapper.readValue(response.toString(), JsonResponse.class);
                List jsonUsernames = mapper.readValue(new JSONArray(jsonResponse.getData().toString()).toString(), List.class);
                numbers.addAll(jsonUsernames);
                mutableLiveData.setValue(numbers);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(application, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(application, "Failed to login " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };

        queue.add(jsonObjectRequest);

        return mutableLiveData;
    }

    //expose
    public LiveData<Map<String, String>> getAccessToken(Models.UsernameAndPasswordAuthenticationRequest request) throws JSONException, JsonProcessingException {
        return accessToken(request);
    }

    public LiveData<Models.AppUser> getUserByUsername(String username) {
        return getUserMutableByUsername(username);
    }

    public LiveData<List<String>> getAllUsernames() {
        return getUsernames();
    }

    public LiveData<List<String>> getAllPhoneNumbers() {
        return getPhoneNumbers();
    }


}
