package com.example.joboui.globals;

import android.annotation.SuppressLint;
import android.app.Application;

import com.example.joboui.db.userDb.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class GlobalDb extends Application {

    //if static doesn't work then declare it when you use it... but settings should only be declared once

    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore fireStoreDb;
    public static boolean initialized = false;
    public static UserRepository userRepository;

    public GlobalDb() {

    }


    public static void init(Application application) {
        if (!initialized) {
            FirebaseApp.initializeApp(application);
            fireStoreDb = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build();
            fireStoreDb.setFirestoreSettings(settings);
            userRepository = new UserRepository(application);
            initialized = true;
        }
    }
}
