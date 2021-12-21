package com.example.joboui.db.job;

import static com.example.joboui.globals.GlobalDb.application;
import static com.example.joboui.globals.GlobalDb.jobApi;
import static com.example.joboui.globals.GlobalVariables.CLIENT_ID;
import static com.example.joboui.globals.GlobalVariables.CLIENT_USERNAME;
import static com.example.joboui.globals.GlobalVariables.ID;
import static com.example.joboui.globals.GlobalVariables.JOB_STATUS;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_ID;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_USERNAME;
import static com.example.joboui.globals.GlobalVariables.PAGE_NO;
import static com.example.joboui.globals.GlobalVariables.PAGE_SIZE;
import static com.example.joboui.globals.GlobalVariables.UID;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.*;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.joboui.model.Models;
import com.example.joboui.utils.DataOps;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Optional;

import javax.xml.transform.sax.SAXResult;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

public class JobViewModel extends AndroidViewModel {

    public JobViewModel(@NonNull Application application) {
        super(application);
    }


    private MutableLiveData<Optional<JsonResponse>> saveFeedBack(Models.FeedbackForm form) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        jobApi.saveFeedback(form, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null || jsonResponse.getData() == null) {
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

    private MutableLiveData<Optional<JsonResponse>> getFeedBack(Integer rating, Long userId, Integer pageNo, Integer pageSize, Long id) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        HashMap params = new HashMap();
        if (rating != null) params.put("rating", rating);
        if (userId != null) params.put(UID, userId);
        if (pageNo != null) params.put(PAGE_NO, pageNo);
        if (pageSize != null) params.put(PAGE_SIZE, pageSize);
        if (id != null) params.put(ID, id);

        jobApi.getFeedback(params, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null || jsonResponse.getData() == null) {
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

    private MutableLiveData<Optional<JsonResponse>> getFeedBackChart() {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        jobApi.getFeedbackChart(getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null || jsonResponse.getData() == null) {
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


    private MutableLiveData<Optional<Boolean>> requestPayment(Models.MakeStkRequest request) {
        MutableLiveData<Optional<Boolean>> mutableLiveData = new MutableLiveData<>();

        System.out.println(new Gson().toJson(request));

        jobApi.postPayment(request, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                mutableLiveData.setValue(Optional.of(response.code() == 200));
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                mutableLiveData.setValue(Optional.of(false));
            }
        });
        return mutableLiveData;
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

                if (jsonResponse == null || jsonResponse.getData() == null) {
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

    private MutableLiveData<Optional<JsonResponse>> getAllJobs(HashMap<String, String> params) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();

        jobApi.getAllJobs(params, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                if (jsonResponse == null || jsonResponse.getData() == null) {
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

        jobApi.getMyClientJobs(username, jobStatus, getAuthorization()).enqueue(new Callback<JsonResponse>() {
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

        jobApi.getMyProviderJobs(username, jobStatus, getAuthorization()).enqueue(new Callback<JsonResponse>() {
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

    private MutableLiveData<Optional<JsonResponse>> saveAReview(Models.Review review) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();
        jobApi.postAReview(review, getAuthorization()).enqueue(new Callback<JsonResponse>() {
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

    private MutableLiveData<Optional<JsonResponse>> updateAReview(Models.Review review) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();
        jobApi.updateAReview(review, getAuthorization()).enqueue(new Callback<JsonResponse>() {
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

    private MutableLiveData<Optional<JsonResponse>> getAReview(HashMap<String, String> params) {
        MutableLiveData<Optional<JsonResponse>> mutableLiveData = new MutableLiveData<>();
        jobApi.getAllReviews(params, getAuthorization()).enqueue(new Callback<JsonResponse>() {
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

    private MutableLiveData<Optional<Models.Review>> getAReviewJobId(Long id) {
        MutableLiveData<Optional<Models.Review>> mutableLiveData = new MutableLiveData<>();

        HashMap<String, String> params = new HashMap<>();
        params.put("job_id", String.valueOf(id));

        jobApi.getAllReviews(params, getAuthorization()).enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {

                if (response.code() != 200) {
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                JsonResponse jsonResponse = response.body();

                if (jsonResponse == null || jsonResponse.isHas_error() && !jsonResponse.isSuccess() || jsonResponse.getData() == null) {
                    Toast.makeText(application, "No review available", Toast.LENGTH_SHORT).show();
                    mutableLiveData.setValue(Optional.empty());
                    return;
                }

                try {
                    JsonArray reviews = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.getData()));
                    reviews.forEach(u -> {
                        try {
                            Models.Review review = getObjectMapper().readValue(u.toString(), Models.Review.class);
                            mutableLiveData.setValue(Optional.of(review));
                            return;
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Toast.makeText(application, "Problem mapping data", Toast.LENGTH_SHORT).show();
                }
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

    public LiveData<Optional<JsonResponse>> getAllTheJobs(HashMap<String, String> params) {
        return getAllJobs(params);
    }

    public LiveData<Optional<JsonResponse>> getAllClientJobs(String username, Integer jobStatus) {
        return getClientJobs(username, jobStatus);
    }

    public LiveData<Optional<JsonResponse>> getAllProviderJobs(String username, Integer jobStatus) {
        return getProviderJobs(username, jobStatus);
    }

    public LiveData<Optional<JsonResponse>> getAJob(Long jobId) {
        return getAJobs(jobId);
    }

    public LiveData<Optional<JsonResponse>> updateJob(Long jobId, Models.JobUpdateForm jobUpdateForm) {
        return updateAJob(jobId, jobUpdateForm);
    }

    public LiveData<Optional<JsonResponse>> deleteJob(Long jobId) {
        return deleteAJob(jobId);
    }

    public LiveData<Optional<JsonResponse>> saveReview(Models.Review review) {
        return saveAReview(review);
    }

    public LiveData<Optional<JsonResponse>> updateReview(Models.Review review) {
        return updateAReview(review);
    }

    public LiveData<Optional<JsonResponse>> getReview(HashMap<String, String> params) {
        return getAReview(params);
    }

    public LiveData<Optional<Models.Review>> getReviewByJobId(Long jobId) {
        return getAReviewJobId(jobId);
    }

    public LiveData<Optional<Boolean>> makePayment(Models.MakeStkRequest request) {
        return requestPayment(request);
    }

    public LiveData<Optional<JsonResponse>> saveAFeedback(Models.FeedbackForm form) {
        return saveFeedBack(form);
    }

    public LiveData<Optional<JsonResponse>> getAFeedback(Integer rating, Long userId, Integer pageNo, Integer pageSize, Long id) {
        return getFeedBack(rating, userId, pageNo, pageSize, id);
    }

    public LiveData<Optional<JsonResponse>> getAFeedbackChart () {
        return getFeedBackChart();
    }
}
