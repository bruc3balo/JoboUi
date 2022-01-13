package com.example.joboui.admin.feedback;

import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.FeedbackRvAdapter;
import com.example.joboui.databinding.FragmentFeedbackListBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedList;

import io.vertx.core.json.JsonArray;


public class FeedbackList extends Fragment {

    private FragmentFeedbackListBinding binding;
    private boolean filterRating;
    private int rating;
    private final LinkedList<Models.Feedback> feedbackList = new LinkedList<>();
    private FeedbackRvAdapter feedbackRvAdapter;

    public FeedbackList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackListBinding.inflate(inflater);


        //filter
        RatingBar ratingBar = binding.ratingBar;
        ratingBar.setStepSize(1.0F);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, r, fromUser) -> {
            rating = (int) r;
            if (filterRating) {
                refreshList();
            }
        });


        //filter checkbox
        CheckBox filter = binding.ratingFilter;
        filter.setChecked(filterRating);
        filter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterRating = isChecked;
            refreshList();
        });


        //list of feedbacks
        RecyclerView feedbackRv = binding.feedbackRv;
        feedbackRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        feedbackRvAdapter = new FeedbackRvAdapter(requireContext(), feedbackList);
        feedbackRv.setAdapter(feedbackRvAdapter);

        refreshList();

        return binding.getRoot();
    }


    //get feedback data
    private void refreshList() {
        binding.feedbackPb.setVisibility(View.VISIBLE);
        new ViewModelProvider(this).get(JobViewModel.class).getAFeedback(filterRating ? rating : null, null, null, null, null).observe(getViewLifecycleOwner(), jsonResponse -> {
            binding.feedbackPb.setVisibility(View.GONE);
            if (jsonResponse.isPresent()) {
                try {
                    JsonArray feedbacks = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.get().getData()));
                    feedbackList.clear();
                    feedbackRvAdapter.notifyDataSetChanged();

                    feedbacks.forEach(f -> {
                        try {
                            Models.Feedback feedback = getObjectMapper().readValue(f.toString(), Models.Feedback.class);
                            feedbackList.add(feedback);
                            feedbackRvAdapter.notifyDataSetChanged();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}