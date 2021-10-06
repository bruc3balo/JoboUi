package com.example.joboui.db.userDb;

import static com.example.joboui.globals.GlobalVariables.USER_DB;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.joboui.models.Models;


@Database(entities = {Models.User.class}, version = 1, exportSchema = false)
public abstract class UserDB extends RoomDatabase {

    private static UserDB instance;

    public abstract UserDao userDao();

    //only 1 instance of db and thread
    static synchronized UserDB getInstance(Context context) {
        if (instance == null) {
            //use builder due to abstract
            instance = Room.databaseBuilder(context.getApplicationContext(), UserDB.class, USER_DB)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .allowMainThreadQueries()
                    .build();
            System.out.println("User Room instance");
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

    private static void populateDb(UserDB db) {
        UserDao userDao = db.userDao();
        System.out.println("User Database populated");
    }

}
