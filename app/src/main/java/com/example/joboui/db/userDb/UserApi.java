package com.example.joboui.db.userDb;

import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.AUTHORIZATION;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;
import static com.example.joboui.globals.GlobalVariables.ID;
import static com.example.joboui.globals.GlobalVariables.SPECIALITIES;
import static com.example.joboui.globals.GlobalVariables.USERNAME;

import com.example.joboui.model.Models;
import com.example.joboui.model.Models.*;
import com.example.joboui.utils.JsonResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface UserApi {

    String base = "user";

    //login
    @POST(API_URL + CONTEXT_URL + "login")
    Call<Models.LoginResponse> getToken(@Body UsernameAndPasswordAuthenticationRequest request);

    //users
    @GET(base + "/all")
    Call<JsonResponse> getUsers(@Query(USERNAME) String username, @Header(AUTHORIZATION) String token);

    @GET(base + "/all")
    Call<JsonResponse> getAllUsers(@Header(AUTHORIZATION) String token);

    @GET(base + "/numbers")
    Call<JsonResponse> getNumbers();

    @GET(base + "/usernames")
    Call<JsonResponse> getUsernames();

    @GET(base + "/providers")
    Call<JsonResponse> getProviders(@QueryMap HashMap<String, String> parameters, @Header(AUTHORIZATION) String token);

    @POST(base + "/save")
    Call<JsonResponse> saveUser(@Body NewUserForm newUserForm);

    @PUT(base + "/update")
    Call<JsonResponse> updateUser(@Query(USERNAME) String username, @Header(AUTHORIZATION) String header, @Body UserUpdateForm updateForm);

    //roles
    @POST(base + "saveRole")
    Call<JsonResponse> saveRole(@Body RoleCreationForm form, @HeaderMap Map<String, String> headers);

    @POST(base + "/role2user")
    Call<JsonResponse> addRoleToUser(@Body RoleToUserForm form, @HeaderMap Map<String, String> headers);

    @DELETE(base + "/delete")
    Call<JsonResponse> deleteUser(@QueryMap Map<String, String> parameters, @HeaderMap Map<String, String> headers);

    @DELETE(base + "/disable")
    Call<JsonResponse> disableUser(@QueryMap Map<String, String> parameters, @HeaderMap Map<String, String> headers);

    @GET(API_URL + CONTEXT_URL + "notification")
    Call<JsonResponse> getMyNotifications(@Query(USERNAME) String username, @Header(AUTHORIZATION) String header);

    @POST(API_URL + CONTEXT_URL + "notification")
    Call<JsonResponse> updateNotifications(@Query(USERNAME) String username, @Query(ID) Long id, @Header(AUTHORIZATION) String header);
}
