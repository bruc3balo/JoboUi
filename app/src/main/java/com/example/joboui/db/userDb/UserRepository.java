package com.example.joboui.db.userDb;

import static com.example.joboui.globals.GlobalDb.fireStoreDb;
import static com.example.joboui.globals.GlobalVariables.PHONE_NUMBER;
import static com.example.joboui.globals.GlobalVariables.USER_DB;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.joboui.domain.Domain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    private void insertUser(Domain.User user) {
        new Thread(() -> {
            try {
                clearUser();
                userDao.insert(user);
                System.out.println(user.getUsername() + " inserted");
            } catch (Exception e) {
                System.out.println(user.getUsername() + " failed instead");
            }
        }).start();
    }

    private Domain.User updateUser(Domain.User user) {
        new Thread(() -> {
            try {
                userDao.update(user);
                System.out.println(user.getUsername() + " updated");
            } catch (Exception e) {
                System.out.println(user.getUsername() + " inserted instead");
                insert(user);
            }
        }).start();
        return user;
    }

    private void deleteUser(Domain.User user) {
        new Thread(() -> {
            userDao.delete(user);
            System.out.println(user.getUsername() + " deleted");
        }).start();
    }

    private void clearUser() {
        userDao.clear();
    }

    private Domain.User updateLocalData(String uid) {
        final Domain.User[] user = {null};
        fireStoreDb.collection(USER_DB).document(uid).get().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                user[0] = Objects.requireNonNull(task.getResult()).toObject(Domain.User.class);
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
    public void insert(Domain.User user) {
        insertUser(user);
    }

    public void update(Domain.User user) {
        updateUser(user);
    }

    public void delete(Domain.User user) {
        deleteUser(user);
    }

    public void deleteUserDb() {
        clearUser();
    }

    public Domain.User getUser() {
        return userDao.getUserObject();
    }

    public LiveData<Domain.User> getUserLive() {
        return userDao.getUserLiveData();
    }

    public Domain.User refreshUserDetails(String uid) {
        return updateLocalData(uid);
    }

    public LiveData<List<String>> getMobileNumbers () {
        return getPhoneNumbers();
    }


}
