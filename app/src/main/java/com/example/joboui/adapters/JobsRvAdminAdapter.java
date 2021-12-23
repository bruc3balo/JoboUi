package com.example.joboui.adapters;

import static com.example.joboui.clientUi.request.LocationRequest.getAddressFromLocation;
import static com.example.joboui.globals.GlobalDb.db;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.model.Models.Messages.MESSAGES;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.ReviewActivity;
import com.example.joboui.admin.AllJobsActivity;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.ChatActivity;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.example.joboui.utils.MyLinkedMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JobsRvAdminAdapter extends RecyclerView.Adapter<JobsRvAdminAdapter.ViewHolder> {

    private LinkedList<Models.Job> list;
    private ItemClickListener mClickListener;
    private Activity activity;
    private String username;
    private final JobViewModel jobViewModel;

    public JobsRvAdminAdapter(Activity activity, LinkedList<Models.Job> list) {
        this.list = list;
        this.activity = activity;
        jobViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_admin, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Models.Job job = list.get(position);

        //layouts

        getReported(job, holder);
        getReviews(job, holder);
        getChatCount(job, holder);

        setState(holder.partiesContent, holder.showParty);
        setState(holder.infoContent, holder.showInfo);
        setState(holder.timeContent, holder.showTime);

        holder.partyToggle.setOnClickListener(v -> toggleMore(holder.partiesContent, holder.showParty));
        holder.infoToggle.setOnClickListener(v -> toggleMore(holder.infoContent, holder.showInfo));
        holder.timeToogle.setOnClickListener(v -> toggleMore(holder.timeContent, holder.showTime));


        //Labels
        holder.statusTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.status)));
        holder.jdTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.job_description)));
        holder.partyTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.parties)));
        holder.locationTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.location)));
        holder.specialityTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.speciality)));
        holder.timeTv.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.time)));
        holder.infoTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.info)));

        //content
        if (job.getJob_status() != null) {
            String statusHolderLabel = activity.getString(R.string.job_status_label);

            Optional<JobStatus> jobStatus = Arrays.stream(JobStatus.values()).filter(s -> s.code == job.getJob_status()).findFirst();
            holder.statusContent.setText(jobStatus.isPresent() ? jobStatus.get().getDescription() : String.valueOf(job.getJob_status()));
        }

        if (job.getJob_description() != null) {
            String jdLabel = activity.getString(R.string.job_description_label);
            holder.jdContent.setText(job.getJob_description());
        }

        if (job.getJob_location() != null) {
            String locationLabel = activity.getString(R.string.location_label);

            JsonObject object = new JsonObject(job.getJob_location().replace("\\", ""));
            LatLng latLng = new LatLng(object.getDouble("latitude"), object.getDouble("longitude"));

            System.out.println("LAT : " + latLng.longitude + " LONG : " + latLng.latitude);

            Address location = getAddressFromLocation(activity, latLng);
            holder.locationContent.setText(location != null ? location.getAddressLine(0) : job.getJob_location().replace("\\", ""));
        }

        if (job.getSpecialities() != null) {
            String specialityLabel = activity.getString(R.string.speciality_label);
            holder.specialityContent.setText(job.getSpecialities());
        }

        if (job.getJob_price() != null) {
            String priceLabel = activity.getString(R.string.price_label);
            holder.priceContent.setText(getBoldSpannable("", String.valueOf(job.getJob_price())));
        }

        if (job.getCreated_at() != null) {
            String createdAtLabel = activity.getString(R.string.createdat_label);
            holder.createdAtContent.setText(getBoldSpannable(createdAtLabel, job.getCreated_at()));
        }

        String completedAtLabel = activity.getString(R.string.completedat_label);
        if (job.getCompleted_at() != null) {
            holder.completedAtContent.setText(getBoldSpannable(completedAtLabel, job.getCompleted_at()));
        } else {
            holder.completedAtContent.setText(getBoldSpannable(completedAtLabel, "NOT YET COMPLETE"));
        }


        if (job.getClient_username() != null) {
            String clientLabel = activity.getString(R.string.client_label);
            holder.clientPartyContent.setText(getBoldSpannable(clientLabel, job.getClient_username()));

        }

        if (job.getLocal_service_provider_username() != null) {
            String providerLabel = activity.getString(R.string.service_provider_label);
            holder.providerPartyContent.setText(getBoldSpannable(providerLabel, job.getLocal_service_provider_username()));
        }

    }

    private void goToChat(Models.Job job) {
        activity.startActivity(new Intent(activity, ChatActivity.class).putExtra(JOB, job));
    }


    private void goToReview(Models.Job job) {
        Dialog d = new Dialog(activity);
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
            new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class).getReviewByJobId(job.getId()).observe((LifecycleOwner) activity, optionalReview -> {

                if (d.isShowing()) {
                    if (!optionalReview.isPresent()) {
                    Toast.makeText(activity, "No review available", Toast.LENGTH_SHORT).show();
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
                    String serviceProviderReviewLabel = activity.getString(R.string.service_providers_review_label);
                    String clientReviewLabel = activity.getString(R.string.client_review_label);

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

    private void confirmDialog(String info, Models.Job job, Function<Models.Job, Void> function) {
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.yes_no_info_layout);
        TextView infov = d.findViewById(R.id.newInfoTv);
        infov.setText(info);
        Button no = d.findViewById(R.id.noButton);
        no.setOnClickListener(v -> d.dismiss());

        Button yes = d.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            function.apply(job);
            d.dismiss();
        });

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static SpannableStringBuilder getUnderlinedSpannableBuilder(String s) {
        SpannableStringBuilder content = new SpannableStringBuilder(s);
        content.setSpan(new UnderlineSpan(), 0, s.length(), 0);
        return content;
    }

    public static SpannableStringBuilder getBoldSpannable(String normal, String bold) {
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        bold = " ".concat(bold);
        int end = normal.length() + bold.length();

        SpannableStringBuilder farmNameFormatted = new SpannableStringBuilder(normal.concat(bold));
        farmNameFormatted.setSpan(boldSpan, normal.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return farmNameFormatted;
    }

    private void toggleMore(LinearLayout content, ImageView button) {
        if (content.getVisibility() == View.GONE) {
            content.setVisibility(View.VISIBLE);
            button.setBackgroundResource(R.color.semi_white);
            button.setRotation(270);
        } else {
            content.setVisibility(View.GONE);
            button.setBackgroundResource(R.color.transparent);
            button.setRotation(90);
        }

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
                    holder.chatLayout.setOnClickListener(v -> Toast.makeText(activity, "CHAT", Toast.LENGTH_SHORT).show());
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
        new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class).getReviewByJobId(job.getId()).observe((LifecycleOwner) activity, review -> {
            if (review.isPresent()) {
                holder.reviewLayout.setVisibility(View.VISIBLE);
                holder.reviewLayout.setOnClickListener(v -> goToReview(job));
            } else {
                holder.reviewLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getReported(Models.Job job, ViewHolder holder) {
        if (job.getReported()) {
            holder.reportedLayout.setVisibility(View.VISIBLE);
            holder.reportedLayout.setOnClickListener(v -> Toast.makeText(activity, "REPORTED", Toast.LENGTH_SHORT).show());
        } else {
            holder.reportedLayout.setVisibility(View.GONE);
        }
    }

    private void setState(LinearLayout content, ImageView button) {
        if (content.getVisibility() == View.GONE) {
            content.setVisibility(View.GONE);
            button.setBackgroundResource(R.color.transparent);
            button.setRotation(90);
        } else {
            content.setVisibility(View.VISIBLE);
            button.setBackgroundResource(R.color.semi_white);
            button.setRotation(270);
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView statusTitle, statusContent, jdTitle, jdContent, partyTitle, clientPartyContent, providerPartyContent, locationTitle, locationContent, infoTitle;
        TextView specialityTitle, specialityContent, priceTv, priceContent, timeTv, createdAtContent, completedAtContent, chatCount;
        ImageView chat, report, reviews;
        ImageView showTime, showInfo, showParty;
        LinearLayout providerLayout, partyToggle, partiesContent, infoToggle, infoContent, timeToogle, timeContent, reportedLayout, chatLayout, reviewLayout;
        Button decline, accept, negotiation;
        CardView statusBg;


        ViewHolder(View itemView) {
            super(itemView);
            statusTitle = itemView.findViewById(R.id.statusTitle);
            statusContent = itemView.findViewById(R.id.statusContent);
            jdTitle = itemView.findViewById(R.id.jdTitle);
            jdContent = itemView.findViewById(R.id.jdContent);
            partyTitle = itemView.findViewById(R.id.partyTitle);
            clientPartyContent = itemView.findViewById(R.id.clientPartyContent);
            providerPartyContent = itemView.findViewById(R.id.providerPartyContent);
            locationTitle = itemView.findViewById(R.id.locationTitle);
            locationContent = itemView.findViewById(R.id.locationContent);
            specialityTitle = itemView.findViewById(R.id.specialityTitle);
            specialityContent = itemView.findViewById(R.id.specialityContent);
            priceTv = itemView.findViewById(R.id.priceTv);
            priceContent = itemView.findViewById(R.id.priceContent);
            timeTv = itemView.findViewById(R.id.timeTv);
            createdAtContent = itemView.findViewById(R.id.createdAtContent);
            completedAtContent = itemView.findViewById(R.id.completedAtContent);
            infoTitle = itemView.findViewById(R.id.infoTitle);
            chatCount = itemView.findViewById(R.id.chatCount);

            chat = itemView.findViewById(R.id.chat);
            showTime = itemView.findViewById(R.id.showTime);
            showInfo = itemView.findViewById(R.id.showInfo);
            showParty = itemView.findViewById(R.id.showParty);
            report = itemView.findViewById(R.id.report);
            reviews = itemView.findViewById(R.id.reviews);

            providerLayout = itemView.findViewById(R.id.serviceProviderControls);
            decline = itemView.findViewById(R.id.decline);
            accept = itemView.findViewById(R.id.accept);
            negotiation = itemView.findViewById(R.id.negotiate);
            statusBg = itemView.findViewById(R.id.statusBg);

            partiesContent = itemView.findViewById(R.id.partiesContent);
            infoContent = itemView.findViewById(R.id.infoContent);
            timeContent = itemView.findViewById(R.id.timeContent);


            partyToggle = itemView.findViewById(R.id.partyToggle);
            infoToggle = itemView.findViewById(R.id.infoToggle);
            timeToogle = itemView.findViewById(R.id.timeLayout);

            chatLayout = itemView.findViewById(R.id.chatLayout);
            reportedLayout = itemView.findViewById(R.id.reportedLayout);
            reviewLayout = itemView.findViewById(R.id.reviewLayout);

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}