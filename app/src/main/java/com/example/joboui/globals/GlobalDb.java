package com.example.joboui.globals;

import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;

import com.example.joboui.db.job.JobApi;
import com.example.joboui.db.service.ServiceApi;
import com.example.joboui.db.service.ServiceRepository;
import com.example.joboui.db.userDb.UserApi;
import com.example.joboui.db.userDb.UserRepository;
import com.example.joboui.services.NotificationService;
import com.google.firebase.database.FirebaseDatabase;


import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@SuppressLint("StaticFieldLeak")
public class GlobalDb extends Application {

    //if static doesn't work then declare it when you use it... but settings should only be declared once

    public static boolean initialized = false;
    public static UserRepository userRepository;
    public static ServiceRepository serviceRepository;
    public static UserApi userApi;
    public static JobApi jobApi;
    public static ServiceApi serviceApi;
    public static Application application;
    public static FirebaseDatabase db;

    public GlobalDb() {

    }



    //initialize the offline db and api interfaces
    public static void init(Application application) {
        if (!initialized) {
            GlobalDb.application = application;
            Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL + CONTEXT_URL).addConverterFactory(JacksonConverterFactory.create()).build();
            //instance for interface
            userApi = retrofit.create(UserApi.class);
            serviceApi = retrofit.create(ServiceApi.class);
            jobApi = retrofit.create(JobApi.class);
            userRepository = new UserRepository(application);
            serviceRepository = new ServiceRepository(application);
            
            db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(false);

            initialized = true;


            application.startService(new Intent(application, NotificationService.class));

            System.out.println("========= SERVICE STARTED ===============");


            System.out.println("======================== INITIALIZED ========================");
        }
    }
}
