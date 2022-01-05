package com.example.joboui.adapters;

import static com.example.joboui.adapters.JobsRvAdminAdapter.getBoldSpannable;
import static com.example.joboui.adapters.JobsRvAdminAdapter.getUnderlinedSpannableBuilder;
import static com.example.joboui.globals.GlobalDb.db;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.model.Models.Messages.MESSAGES;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.HistoryActivity;
import com.example.joboui.utils.ConvertDate;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.MyLinkedMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class HistoryRvAdapter extends RecyclerView.Adapter<HistoryRvAdapter.ViewHolder> {

    private LinkedList<Models.Job> list;
    private ItemClickListener mClickListener;
    private Activity mContext;


    public HistoryRvAdapter(Activity context, LinkedList<Models.Job> list) {
        this.list = list;
        this.mContext = context;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Models.Job job = list.get(position);

        getReviews(job, holder);
        getChatCount(job, holder);

        if (job.getReported()) {
            holder.cardBg.setCardBackgroundColor(Color.RED);
        }

        holder.lastUpdated.setText(ConvertDate.formatDateReadable(job.getUpdated_at()));

        Optional<JobStatus> jobStatus = Arrays.stream(JobStatus.values()).filter(s -> s.code == job.getJob_status()).findFirst();
        holder.status.setText(jobStatus.isPresent() ? jobStatus.get().getDescription() : String.valueOf(job.getJob_status()));

        holder.specialityContent.setText(job.getSpecialities());
    }

    private void getChatCount(Models.Job job, ViewHolder holder) {
        db.getReference().child(MESSAGES).child(String.valueOf(job.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }


                int chatCount = (int) snapshot.getChildrenCount();

                if (chatCount == 0) {
                    holder.chatLayout.setVisibility(View.GONE);
                } else {
                    holder.chatLayout.setVisibility(View.VISIBLE);
                    holder.chatCount.setText("Chat ( "+chatCount + " )");
                    holder.chatLayout.setOnClickListener(v -> Toast.makeText(mContext, "CHAT", Toast.LENGTH_SHORT).show());
                }

                System.out.println("CHILDREN COUNT " + chatCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.chatLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getReviews(Models.Job job, ViewHolder holder) {
        new ViewModelProvider((ViewModelStoreOwner) mContext).get(JobViewModel.class).getReviewByJobId(job.getId()).observe((LifecycleOwner) mContext, review -> {
            if (review.isPresent()) {
                holder.reviewLayout.setVisibility(View.VISIBLE);
                holder.reviewLayout.setOnClickListener(v -> goToReview(job));
            } else {
                holder.reviewLayout.setVisibility(View.GONE);
            }
        });
    }

    private void goToReview(Models.Job job) {
        Dialog d = new Dialog(mContext);
        d.setContentView(R.layout.job_review);
        d.setCancelable(true);
        d.setCanceledOnTouchOutside(true);

        RatingBar clientRating = d.findViewById(R.id.clientRating);


        RatingBar serviceRating = d.findViewById(R.id.serviceRating);


        TextView serviceRatingContent = d.findViewById(R.id.serviceRatingContent);
        TextView clientRatingContent = d.findViewById(R.id.clientRatingContent);


        TextView clientPartyContent = d.findViewById(R.id.clientPartyContent);
        TextView servicePartyContent = d.findViewById(R.id.servicePartyContent);


        TextView clientReview = d.findViewById(R.id.clientReview);
        TextView serviceReview = d.findViewById(R.id.providerReview);

        LinearLayout ratingLayout = d.findViewById(R.id.ratingLayout);
        ratingLayout.setVisibility(View.GONE);

        ProgressBar progressBar = d.findViewById(R.id.reviewPb);
        Button cancel_button = d.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(v -> d.dismiss());

        d.setOnShowListener(dialog -> {
            progressBar.setVisibility(View.VISIBLE);
            new ViewModelProvider((ViewModelStoreOwner) mContext).get(JobViewModel.class).getReviewByJobId(job.getId()).observe((LifecycleOwner) mContext, optionalReview -> {

                if (d.isShowing()) {
                    if (!optionalReview.isPresent()) {
                        Toast.makeText(mContext, "No review available", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        ratingLayout.setVisibility(View.GONE);
                        d.dismiss();
                        return;
                    }

                    Models.Review review = optionalReview.get();
                    ratingLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    //services
                    if (review.getClient_username() != null) {
                        clientReview.setText("Client : " + getUnderlinedSpannableBuilder(review.getClient_username()));
                    }

                    if (review.getLocal_service_provider_review() != null) {
                        clientPartyContent.setText(review.getLocal_service_provider_review());
                    }


                    //clients
                    if (review.getLocal_service_provider_username() != null) {
                        serviceReview.setText("Service Provider : " + getUnderlinedSpannableBuilder(review.getLocal_service_provider_username()));
                    }

                    if (review.getClient_review() != null) {
                        servicePartyContent.setText(review.getClient_review());
                    }

                    //rating
                    try {
                        String serviceProviderReviewLabel = mContext.getString(R.string.service_providers_review_label);
                        String clientReviewLabel = mContext.getString(R.string.client_review_label);

                        //service provider
                        if (review.getClient_review() != null) {
                            MyLinkedMap clientRatingObj = getObjectMapper().readValue(review.getClient_review(), MyLinkedMap.class);
                            Map.Entry clientEntry = clientRatingObj.getEntry(0);
                            servicePartyContent.setText(getBoldSpannable(serviceProviderReviewLabel,String.valueOf(clientEntry.getValue())));
                            serviceRatingContent.setText(String.valueOf(clientEntry.getKey()));
                            serviceRating.setRating(Float.parseFloat(String.valueOf(clientEntry.getKey())));

                            serviceRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                                if (fromUser) {
                                    serviceRating.setRating(Float.parseFloat(String.valueOf(clientEntry.getKey())));
                                }
                            });
                        } else {
                            servicePartyContent.setText(getBoldSpannable(serviceProviderReviewLabel,""));
                            serviceRating.setEnabled(false);
                        }

                        //client
                        if (review.getLocal_service_provider_review() != null) {
                            MyLinkedMap serviceRatingObj = getObjectMapper().readValue(review.getLocal_service_provider_review(), MyLinkedMap.class);
                            Map.Entry serviceEntry = serviceRatingObj.getEntry(0);
                            clientPartyContent.setText(getBoldSpannable(clientReviewLabel,String.valueOf(serviceEntry.getValue())));
                            clientRatingContent.setText(String.valueOf(serviceEntry.getKey()));
                            clientRating.setRating(Float.parseFloat(String.valueOf(serviceEntry.getKey())));
                            clientRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                                if (fromUser) {
                                    clientRating.setRating(Float.parseFloat(String.valueOf(serviceEntry.getKey())));
                                }
                            });
                        } else {
                            clientPartyContent.setText(getBoldSpannable(clientReviewLabel,"NO RATING GIVEN"));
                            clientRating.setEnabled(false);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                }

            });
        });
        d.show();
    }

    // total number of rows
    @Override
    public int getItemCount() {

        return list.size();

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView lastUpdated,status,specialityContent,chatCount;
        CardView cardBg;
        LinearLayout chatLayout,reviewLayout;
        ViewHolder(View itemView) {
            super(itemView);

            lastUpdated = itemView.findViewById(R.id.lastUpdated);
            status = itemView.findViewById(R.id.statusContent);
            cardBg = itemView.findViewById(R.id.cardBg);
            specialityContent = itemView.findViewById(R.id.specialityContent);
            chatLayout = itemView.findViewById(R.id.chatLayout);
            reviewLayout = itemView.findViewById(R.id.reviewLayout);
            chatCount = itemView.findViewById(R.id.chatCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}