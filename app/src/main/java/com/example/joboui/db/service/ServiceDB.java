package com.example.joboui.db.service;

import static com.example.joboui.globals.GlobalVariables.SERVICE_DB;
import static com.example.joboui.globals.GlobalVariables.USER_DB;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.joboui.db.userDb.UserDao;
import com.example.joboui.domain.Domain;


@Database(entities = {Domain.Services.class}, version = 2, exportSchema = false)
public abstract class ServiceDB extends RoomDatabase {

    private static ServiceDB instance;

    public abstract ServiceDao serviceDao();

    //only 1 instance of db and thread
    static synchronized ServiceDB getInstance(Context context) {
        if (instance == null) {
            //use builder due to abstract
            instance = Room.databaseBuilder(context.getApplicationContext(), ServiceDB.class, SERVICE_DB)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .allowMainThreadQueries()
                    .build();
            System.out.println("Service Room instance");
        }
        return instance;
    }

    private static final Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Handler(Looper.getMainLooper()).post(() -> populateDb(instance));
        }
    };

    private static void populateDb(ServiceDB db) {
        ServiceDao serviceDao = db.serviceDao();
        System.out.println("Service Database populated");
    }

}
