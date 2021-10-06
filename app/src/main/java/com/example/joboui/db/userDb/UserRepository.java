package com.example.joboui.db.userDb;

import static com.example.joboui.globals.GlobalDb.fireStoreDb;
import static com.example.joboui.globals.GlobalVariables.PHONE_NUMBER;
import static com.example.joboui.globals.GlobalVariables.USER_DB;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.joboui.models.Models;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserRepository {
    private final UserDao userDao;

    //methods are to store just one users data, not many
    //to be used to cache

    public UserRepository(Application application) {
        UserDB database = UserDB.getInstance(application);
        userDao = database.userDao();

    }

    //Abstraction layer for encapsulation

    private void insertUser(Models.User user) {
        new Thread(() -> {
            try {
                userDao.insert(user);
                System.out.println(user.getUid() + " inserted");
            } catch (Exception e) {
                System.out.println(user.getUid() + " updated instead");
                userDao.update(user);
            }
        }).start();
    }

    private Models.User updateUser(Models.User user) {
        new Thread(() -> {
            try {

                userDao.update(user);
                System.out.println(user.getUid() + " updated");
            } catch (Exception e) {
                System.out.println(user.getUid() + " inserted instead");
                userDao.insert(user);
            }
        }).start();
        return user;
    }

    private void deleteUser(Models.User user) {
        new Thread(() -> {
            userDao.delete(user);
            System.out.println(user.getUid() + " deleted");
        }).start();
    }

    private void clearUser() {
        userDao.clear();
    }

    private Models.User updateLocalData(String uid) {
        final Models.User[] user = {null};
        fireStoreDb.collection(USER_DB).document(uid).get().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                user[0] = Objects.requireNonNull(task.getResult()).toObject(Models.User.class);
            } else {
                System.out.println(Objects.requireNonNull(task.getException()).toString());
            }
        });
        return user[0];
    }

    private MutableLiveData<List<String>> getPhoneNumbers() {
        MutableLiveData<List<String>> mutableLiveData = new MutableLiveData<>();
        List<String> phoneList = new ArrayList<>();
        fireStoreDb.collection(USER_DB).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               for (QueryDocumentSnapshot qs: Objects.requireNonNull(task.getResult())) {
                   phoneList.add(Objects.requireNonNull(qs.get(PHONE_NUMBER)).toString());
               }
           }
           mutableLiveData.setValue(phoneList);
        });
        return mutableLiveData;
    }


    //Used Methods
    public void insert(Models.User user) {
        insertUser(user);
    }

    public void update(Models.User user) {
        updateUser(user);
    }

    public void delete(Models.User user) {
        deleteUser(user);
    }

    public void deleteUserDb() {
        clearUser();
    }

    public Models.User getUser(String uid) {
        return userDao.getUserObject(uid);
    }

    public LiveData<Models.User> getUserLive() {
        return userDao.getUserLiveData(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    public Models.User refreshUserDetails(String uid) {
        return updateLocalData(uid);
    }

    public LiveData<List<String>> getMobileNumbers () {
        return getPhoneNumbers();
    }


}
