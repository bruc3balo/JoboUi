package com.example.joboui.globals;

import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;

import android.annotation.SuppressLint;
import android.app.Application;

import com.example.joboui.db.userDb.UserApi;
import com.example.joboui.db.userDb.UserRepository;
import com.example.joboui.utils.JsonResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class GlobalDb extends Application {

    //if static doesn't work then declare it when you use it... but settings should only be declared once

    @SuppressLint("StaticFieldLeak")
    public static boolean initialized = false;
    public static UserRepository userRepository;
    public static UserApi userApi;

    public GlobalDb() {

    }


    public static void init(Application application) {
        if (!initialized) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL + CONTEXT_URL).addConverterFactory(JacksonConverterFactory.create()).build();
            //instance for interface
            userApi = retrofit.create(UserApi.class);
            userRepository = new UserRepository(application);
            initialized = true;

            System.out.println("======================== INITIALIZED ========================");
        }
    }
}
