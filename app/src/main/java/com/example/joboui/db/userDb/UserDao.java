package com.example.joboui.db.userDb;


import static com.example.joboui.globals.GlobalVariables.UID;
import static com.example.joboui.globals.GlobalVariables.USER_DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.joboui.models.Models;

@Dao
public interface UserDao {

    String GET_ALL_USER = "SELECT * FROM " + USER_DB;
    String GET_USER = "SELECT * FROM " + USER_DB + " WHERE " + UID + " LIKE " + ":uid";
    String CLEAR_USER = "DELETE FROM " + USER_DB;


    @Insert
    void insert(Models.User user);

    @Update
    void update(Models.User user);

    @Delete
    void delete(Models.User user);

    @Query(CLEAR_USER)
    void clear();

    @Query(GET_USER)
    Models.User getUserObject(String uid);

    @Query(GET_USER)
    LiveData<Models.User> getUserLiveData (String uid);


}
