package com.example.joboui.db.job;

import static com.example.joboui.globals.GlobalDb.jobApi;
import static com.example.joboui.globals.GlobalVariables.CLIENT_ID;
import static com.example.joboui.globals.GlobalVariables.CLIENT_USERNAME;
import static com.example.joboui.globals.GlobalVariables.ID;
import static com.example.joboui.globals.GlobalVariables.JOB_STATUS;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_ID;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_USERNAME;
import static com.example.joboui.utils.DataOps.*;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.joboui.model.Models;
import com.example.joboui.utils.DataOps;
import com.example.joboui.utils.JsonResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Optional;

import io.vertx.core.json.Json;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class JobViewModel extends AndroidViewModel {

    public JobViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<Optional<JsonResponse>> sendJobRequest(Models.JobRequestForm jobRequestForm) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        System.out.println(new Gson().toJson(jobRequestForm));

        jobApi.saveJob(jobRequestForm, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> getAllJobs() {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        jobApi.getAllJobs(null, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> getClientJobs(String username, Integer jobStatus) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        jobApi.getMyClientJobs(username, jobStatus,getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> getProviderJobs(String username, Integer jobStatus) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        jobApi.getMyProviderJobs(username, jobStatus,getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> getAJobs(Long jobId) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();


        jobApi.getAJob(jobId, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> updateAJob(Long jobId, Models.JobUpdateForm jobUpdateForm) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();


        jobApi.updateAJob(jobId, jobUpdateForm, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private MutableLiveData<Optional<JsonResponse>> deleteAJob(Long jobId) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();


        jobApi.deleteAJob(jobId, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                mutableLiveData.setValue(Optional.of(jsonResponse));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }


    //expose
    public LiveData<Optional<JsonResponse>> sendJobRequestLive(Models.JobRequestForm jobRequestForm) {
        return sendJobRequest(jobRequestForm);
    }

    public LiveData<Optional<JsonResponse>> getAllTheJobs() {
        return getAllJobs();
    }

    public LiveData<Optional<JsonResponse>> getAllClientJobs(String username, Integer jobStatus) {
        return getClientJobs(username,jobStatus);
    }

    public LiveData<Optional<JsonResponse>> getAllProviderJobs(String username, Integer jobStatus) {
        return getProviderJobs(username,jobStatus);
    }

    public LiveData<Optional<JsonResponse>> getAJob(Long jobId) {
        return getAJobs(jobId);
    }

    public LiveData<Optional<JsonResponse>> updateJob(Long jobId, Models.JobUpdateForm jobUpdateForm) {
        return updateAJob(jobId,jobUpdateForm);
    }

    public LiveData<Optional<JsonResponse>> deleteJob(Long jobId) {
        return deleteAJob(jobId);
    }
}
