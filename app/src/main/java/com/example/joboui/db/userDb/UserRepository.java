package com.example.joboui.db.userDb;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.example.joboui.domain.Domain;

import java.util.List;
import java.util.Optional;


public class UserRepository {
    private final UserDao userDao;

    //methods are to store just one users data, not many
    //to be used to cache

    public UserRepository(Application application) {
        UserDB database = UserDB.getInstance(application);
        userDao = database.userDao();

    }

    //Abstraction layer for encapsulation

    @Transaction
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

    @Transaction
    private Domain.User updateUser(Domain.User user) {
        new Thread(() -> {
            try {
                userDao.update(user);
                System.out.println(user.getUsername() + " updated");
            } catch (Exception e) {
                System.out.println(user.getUsername() + " inserted instead");
                insertUser(user);
            }
        }).start();
        return user;
    }

    @Transaction
    private void deleteUser(Domain.User user) {
        new Thread(() -> {
            userDao.delete(user);
            System.out.println(user.getUsername() + " deleted");
        }).start();
    }

    @Transaction
    private void clearUser() {
      new Thread(() -> {
          userDao.clear();
          System.out.println("CLEARING USER DB");
      }).start();
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

    public Optional<Domain.User> getUser() {
        List<Domain.User> user = userDao.getUserObject();
        if (user == null || user.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(user.get(0));
        }
    }

    public LiveData<Domain.User> getUserLive() {
        return userDao.getUserLiveData();
    }


}
