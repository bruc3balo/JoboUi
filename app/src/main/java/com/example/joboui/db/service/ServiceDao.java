package com.example.joboui.db.service;


import static com.example.joboui.globals.GlobalVariables.SERVICE_DB;
import static com.example.joboui.globals.GlobalVariables.USER_DB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.joboui.domain.Domain;

import java.util.List;
import java.util.Optional;

@Dao
public interface ServiceDao {

    String GET_ALL_SERVICES = "SELECT * FROM " + SERVICE_DB;
    String FIND_BY_NAME = "SELECT * FROM " + SERVICE_DB + " WHERE :name LIKE name";
    String CLEAR_SERVICES = "DELETE FROM " + SERVICE_DB;


    @Insert
    void insert(Domain.Services services);

    @Update
    void update(Domain.Services services);

    @Delete
    void delete(Domain.Services services);

    @Query(CLEAR_SERVICES)
    @Transaction
    void clear();

    @Query(GET_ALL_SERVICES)
    List<Domain.Services> getServicesList();

    @Query(GET_ALL_SERVICES)
    LiveData<List<Domain.Services>> getServiceListLiveData ();

    @Query(FIND_BY_NAME)
    Optional<Domain.Services> findServiceByName(String name);

}
