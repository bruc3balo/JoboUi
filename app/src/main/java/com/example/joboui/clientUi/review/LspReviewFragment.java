package com.example.joboui.clientUi.review;

import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_USERNAME;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.JobStatus.CLIENT_CANCELLED_IN_PROGRESS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.adapters.ReviewRvAdapter;
import com.example.joboui.databinding.FragmentLspReviewBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import io.vertx.core.json.JsonArray;


public class LspReviewFragment extends Fragment {

    private FragmentLspReviewBinding binding;
    private ReviewRvAdapter rvAdapter;
    private final LinkedList<Models.Review> reviewLinkedList = new LinkedList<>();
    private final Models.AppUser lsp;

    public LspReviewFragment(Models.AppUser lsp) {
        // Required empty public constructor
        this.lsp = lsp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLspReviewBinding.inflate(inflater);

        RecyclerView reviewRv = binding.reviewRv;
        reviewRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rvAdapter = new ReviewRvAdapter(requireContext(), reviewLinkedList);
        reviewRv.setAdapter(rvAdapter);

        getReviews();

        return binding.getRoot();
    }


    private void getReviews() {
        HashMap<String, String> params = new HashMap<>();
        params.put(LOCAL_SERVICE_PROVIDER_USERNAME, lsp.getUsername());

        new ViewModelProvider(this).get(JobViewModel.class).getReview(params).observe(requireActivity(), jsonResponse -> {
            binding.pb.setVisibility(View.GONE);

            if (jsonResponse.isPresent()) {
                JsonResponse response = jsonResponse.get();

                try {
                    reviewLinkedList.clear();
                    rvAdapter.notifyDataSetChanged();
                    JsonArray reviews = new JsonArray(getObjectMapper().writeValueAsString(response.getData()));
                    if (reviews.isEmpty()) {
                        Toast.makeText(requireContext(), "No reviews", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reviews.forEach(u -> {
                        System.out.println("JOBS ARE " + reviews.size());

                        try {
                            Models.Review review = getObjectMapper().readValue(u.toString(), Models.Review.class);
                            reviewLinkedList.add(review);
                            rvAdapter.notifyDataSetChanged();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Problem mapping data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}