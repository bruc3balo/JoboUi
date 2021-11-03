package com.example.joboui.db.job;

import static com.example.joboui.globals.GlobalVariables.AUTHORIZATION;
import static com.example.joboui.globals.GlobalVariables.CLIENT_USERNAME;
import static com.example.joboui.globals.GlobalVariables.ID;
import static com.example.joboui.globals.GlobalVariables.JOB_STATUS;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_USERNAME;
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

public interface JobApi {


    String base = "job";

    @GET(base + "/all")
    Call<JsonResponse> getAllJobs(@QueryMap HashMap<String, String> params, @Header(AUTHORIZATION) String token);

    @GET(base + "/all")
    Call<JsonResponse> getMyClientJobs(@Query(CLIENT_USERNAME) String clientUsername, @Query(JOB_STATUS) Integer jobStatus, @Header(AUTHORIZATION) String token);

    @GET(base + "/all")
    Call<JsonResponse> getMyProviderJobs(@Query(LOCAL_SERVICE_PROVIDER_USERNAME) String clientUsername, @Query(JOB_STATUS) Integer jobStatus, @Header(AUTHORIZATION) String token);

    @GET(base + "/all")
    Call<JsonResponse> getAJob(@Query(ID) Long id, @Header(AUTHORIZATION) String token);

    @POST(base + "/new")
    Call<JsonResponse> saveJob(@Body Models.JobRequestForm jobRequestForm, @Header(AUTHORIZATION) String token);

    @PUT(base + "/update")
    Call<JsonResponse> updateAJob(@Query(ID) Long id, @Body Models.JobUpdateForm jobUpdateForm, @Header(AUTHORIZATION) String token);

    @PUT(base + "/delete")
    Call<JsonResponse> deleteAJob(@Query(ID) Long jobId, @Header(AUTHORIZATION) String token);


}
