package com.example.joboui.db.service;

import static com.example.joboui.globals.GlobalVariables.AUTHORIZATION;
import static com.example.joboui.globals.GlobalVariables.NAME;

import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


//api calls
public interface ServiceApi {

    String base = "service";

    @GET(base + "/all")
    Call<JsonResponse> getAllServices(@Header(AUTHORIZATION) String token);

    @GET(base + "/all")
    Call<JsonResponse> getAService(@QueryMap HashMap<String, String> params, @Header(AUTHORIZATION) String token);

    @POST(base + "/new")
    Call<JsonResponse> saveService(@Body Models.ServiceRequestForm serviceRequestForm, @Header(AUTHORIZATION) String token);

    @PUT(base + "/update")
    Call<JsonResponse> updateAService(@Query("name") String name, @Body Models.ServiceUpdateForm serviceUpdateForm, @Header(AUTHORIZATION) String token);


}
