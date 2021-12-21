package com.example.joboui;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.globals.GlobalVariables.REPORTED;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.JobStatus.CLIENT_CANCELLED_IN_PROGRESS;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.joboui.databinding.ActivityReviewBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.vertx.core.json.JsonArray;

public class ReviewActivity extends AppCompatActivity {

    private ActivityReviewBinding binding;
    private Models.Review review = new Models.Review();
    private Boolean isClient;
    private double rating;
    private Models.Job job;
    private Domain.User user;
    private String commentS = "";
    private boolean exists = false;
    private boolean feedback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            job = (Models.Job) extras.get(JOB);


            review.setClient_username(job.getClient_username());
            review.setLocal_service_provider_username(job.getLocal_service_provider_username());
            review.setJob_id(job.getId());
            review.setReported(extras.get(REPORTED) != null);


            if (userRepository != null) {
                userRepository.getUserLive().observe(ReviewActivity.this, user -> user.ifPresent(c -> {
                    this.user = c;
                    isClient = job.getClient_username().equals(c.getUsername());
                }));

                reviewExists().observe(ReviewActivity.this, review -> {
                    review.ifPresent(r -> {
                        exists = true;
                        ReviewActivity.this.review = review.get();
                    });
                });

            }
        } else {
            feedback = true;
            binding.toolbar.setTitle("Feedback");
            binding.toolbar.setSubtitle("How would you rate us?");
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(u -> user = u));
        }

        //rating
        RatingBar ratingBar = binding.ratingBar;
        if (feedback) ratingBar.setStepSize(1.0F);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> ReviewActivity.this.rating = rating);

        //comment
        EditText comment = binding.commentBox;
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                commentS = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //send
        Button send = binding.sendButton;
        send.setOnClickListener(v -> {
            if (feedback) {
                //send feedback
                inProgress();
                new ViewModelProvider(this).get(JobViewModel.class).saveAFeedback(new Models.FeedbackForm(user.getUsername(), commentS, (int) rating)).observe(this, jsonResponse -> {
                    if (jsonResponse.isPresent()) {
                        Toast.makeText(ReviewActivity.this, "Thank you for your feedback !", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        outProgress();
                        Toast.makeText(ReviewActivity.this, "Failed to pos feedback", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Map<Double, String> rev = new HashMap<>();
                rev.put(rating, commentS);
                try {
                    if (isClient) {
                        review.setClient_review(getObjectMapper().writeValueAsString(rev));
                    } else {
                        review.setLocal_service_provider_review(getObjectMapper().writeValueAsString(rev));
                    }

                    sendReview(review);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        outProgress();

    }

    private void sendReview(Models.Review review) {
        if (exists) {
            System.out.println("UPDATING REVIEW");
            inProgress();
            new ViewModelProvider(this).get(JobViewModel.class).updateReview(review).observe(this, jsonResponse -> {
                if (!jsonResponse.isPresent()) {
                    outProgress();
                    System.out.println("FAILED REVIEW ");
                    Toast.makeText(ReviewActivity.this, "Failed to post review. Try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("REVIEW SUCCESS");
                Toast.makeText(ReviewActivity.this, "Review posted", Toast.LENGTH_SHORT).show();
                changeJobStatus();
            });
        } else {
            System.out.println("NEW REVIEW");
            inProgress();
            new ViewModelProvider(this).get(JobViewModel.class).saveReview(review).observe(this, jsonResponse -> {
                if (!jsonResponse.isPresent()) {
                    outProgress();
                    System.out.println("FAILED REVIEW ");
                    Toast.makeText(ReviewActivity.this, "Failed to post review. Try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("REVIEW SUCCESS");
                Toast.makeText(ReviewActivity.this, "Review updated", Toast.LENGTH_SHORT).show();
                changeJobStatus();
            });
        }
    }

    private void inProgress() {
        binding.pb.setVisibility(View.VISIBLE);
        binding.sendButton.setEnabled(false);
    }

    private void outProgress() {
        binding.pb.setVisibility(View.GONE);
        binding.sendButton.setEnabled(true);
    }

    private void changeJobStatus() {
        System.out.println("DECIDING STATUS " + job.getJob_status());
        switch (job.getJob_status()) {
            default:
                finish();
                break;

            case 8:
            case 9:
                changeJobStatus(isClient ? JobStatus.CLIENT_REPORTED : JobStatus.SERVICE_REPORTED);
                break;


            //JobStatus.RATING
            case 16:
                changeJobStatus(isClient ? JobStatus.CLIENT_RATING : JobStatus.SERVICE_RATING);
                break;

            //JobStatus.CLIENT_RATING
            case 10:
                changeJobStatus(JobStatus.COMPLETED);
                break;
            //JobStatus.SERVICE_RATING
            case 11:
                changeJobStatus(JobStatus.COMPLETED);
                break;
        }
    }

    private void changeJobStatus(JobStatus status) {
        System.out.println("CHANGING STATUS to " + status.name());
        Models.JobUpdateForm form = new Models.JobUpdateForm(status.getCode());
        new ViewModelProvider(this).get(JobViewModel.class).updateJob(job.getId(), form).observe(this, jsonResponse -> {
            if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                outProgress();
                System.out.println("CHANGING STATUS FAILED");
                Toast.makeText(ReviewActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }
            System.out.println("CHANGING STATUS SUCCESS");
            finish();
        });

    }

    private MutableLiveData<Optional<Models.Review>> checkIfExisting() {
        MutableLiveData<Optional<Models.Review>> mutableLiveData = new MutableLiveData<>();

        HashMap<String, String> params = new HashMap<>();
        params.put("job_id", String.valueOf(job.getId()));
        new ViewModelProvider(this).get(JobViewModel.class).getReview(params).observe(this, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                mutableLiveData.setValue(Optional.empty());
                return;
            }

            JsonResponse response = jsonResponse.get();

            try {
                JsonArray jobs = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                jobs.forEach(u -> {
                    try {
                        Models.Review review = getObjectMapper().readValue(u.toString(), Models.Review.class);
                        mutableLiveData.setValue(Optional.of(review));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Toast.makeText(ReviewActivity.this, "Problem mapping data", Toast.LENGTH_SHORT).show();
                mutableLiveData.setValue(Optional.empty());
            }
        });

        return mutableLiveData;
    }

    private LiveData<Optional<Models.Review>> reviewExists() {
        return checkIfExisting();
    }

}