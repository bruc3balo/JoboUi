package com.example.joboui.db.userDb;


import static com.example.joboui.globals.GlobalVariables.USER_DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.joboui.domain.Domain;

import java.util.List;

@Dao
public interface UserDao {

    String GET_ALL_USER = "SELECT * FROM " + USER_DB;
    String CLEAR_USER = "DELETE FROM " + USER_DB;


    @Insert
    void insert(Domain.User user);

    @Update
    void update(Domain.User user);

    @Delete
    void delete(Domain.User user);

    @Query(CLEAR_USER)
    @Transaction
    void clear();

    @Query(GET_ALL_USER)
    List<Domain.User> getUserObject();

    @Query(GET_ALL_USER)
    LiveData<Domain.User> getUserLiveData ();


}
