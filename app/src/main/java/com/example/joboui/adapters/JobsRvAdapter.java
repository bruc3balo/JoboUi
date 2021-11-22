package com.example.joboui.adapters;

import static com.example.joboui.clientUi.MyJobs.populateClientJobs;
import static com.example.joboui.clientUi.request.LocationRequest.getAddressFromLocation;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.serviceProviderUi.pages.JobRequests.populateMyJobs;
import static com.example.joboui.tutorial.VerificationActivity.editSingleValue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.ChatActivity;
import com.example.joboui.serviceProviderUi.pages.JobRequests;
import com.example.joboui.serviceProviderUi.pages.NegotiationActivity;
import com.example.joboui.utils.JobStatus;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JobsRvAdapter extends RecyclerView.Adapter<JobsRvAdapter.ViewHolder> {

    private LinkedList<Models.Job> list;
    private ItemClickListener mClickListener;
    private Activity activity;
    private String username;
    private final JobViewModel jobViewModel;

    public JobsRvAdapter(Activity activity, LinkedList<Models.Job> list) {
        this.list = list;
        this.activity = activity;
        jobViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Models.Job job = list.get(position);


        //Labels
        holder.statusTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.status)));
        holder.jdTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.job_description)));
        holder.partyTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.parties)));
        holder.locationTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.location)));
        holder.specialityTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.speciality)));
        holder.priceTv.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.price)));
        holder.timeTv.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.time)));

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

        if (job.getClient_username() != null) {
            String clientLabel = activity.getString(R.string.client_label);
            holder.clientPartyContent.setText(getBoldSpannable(clientLabel,job.getClient_username()));

            if (job.getClient_username().equals(getUsername())) {
                setClient(holder,job);
            }
        }

        if (job.getLocal_service_provider_username() != null) {
            String providerLabel = activity.getString(R.string.service_provider_label);
            holder.providerPartyContent.setText(getBoldSpannable(providerLabel,job.getLocal_service_provider_username()));


            if (job.getLocal_service_provider_username().equals(getUsername())) {
                setServiceProvider(holder, job);
            }
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
            holder.priceContent.setText(getBoldSpannable(priceLabel, String.valueOf(job.getJob_price())));
        }

        if (job.getJob_price_range() != null) {
            String priceRangeLabel = activity.getString(R.string.price_range_label);
            holder.priceRangeContent.setText(getBoldSpannable(priceRangeLabel, job.getJob_price_range()));
        }

        if (job.getCreated_at() != null) {
            String createdAtLabel = activity.getString(R.string.createdat_label);
            holder.createdAtContent.setText(getBoldSpannable(createdAtLabel, job.getCreated_at()));
        }

        if (job.getCompleted_at() != null) {
            String completedAtLabel = activity.getString(R.string.completedat_label);
            holder.completedAtContent.setText(getBoldSpannable(completedAtLabel, job.getCompleted_at()));
        }

        if (job.getScheduled_at() != null) {
            String urgencyLabel = activity.getString(R.string.urgency);
            holder.urgency.setText(getBoldSpannable(urgencyLabel,job.getScheduled_at()));
        }

        holder.chat.setOnClickListener(v -> activity.startActivity(new Intent(activity, ChatActivity.class)));

    }

    public void setServiceProvider(ViewHolder holder, Models.Job job) {

        holder.edit.setVisibility(View.GONE);

        if (job.getJob_status() == JobStatus.REQUESTED.code) {

            holder.providerLayout.setVisibility(View.VISIBLE);

            holder.decline.setOnClickListener(v -> declineJob(job.getId()));

            holder.accept.setOnClickListener(v -> acceptJob(job.getId()));

            holder.negotiation.setOnClickListener(v -> {
                //todo dialog
                Toast.makeText(activity, "Negotiation", Toast.LENGTH_SHORT).show();
            });
        } else if (job.getJob_status() == JobStatus.ACCEPTED.code) {
            holder.providerLayout.setVisibility(View.GONE);
            holder.decline.setOnClickListener(null);
            holder.accept.setOnClickListener(null);
            holder.negotiation.setOnClickListener(null);
        }

    }

    private void acceptJob(Long jobId) {
        jobViewModel.updateJob(jobId, new Models.JobUpdateForm(JobStatus.ACCEPTED.code)).observe((LifecycleOwner) activity, new Observer<Optional<JsonResponse>>() {
            @Override
            public void onChanged(Optional<JsonResponse> jsonResponse) {
                if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                    Toast.makeText(activity, "Failed to get response", Toast.LENGTH_SHORT).show();
                    return;
                }

                populateMyJobs(activity,getUsername(),JobsRvAdapter.this);

            }
        });
    }

    private void declineJob(Long jobId) {
        jobViewModel.updateJob(jobId, new Models.JobUpdateForm(JobStatus.DECLINED.code)).observe((LifecycleOwner) activity, jsonResponse -> {
            if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                Toast.makeText(activity, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            populateMyJobs(activity,getUsername(),JobsRvAdapter.this);
        });
    }

    private void updatePrice(Long jobId,String price) {
        jobViewModel.updateJob(jobId, new Models.JobUpdateForm(price)).observe((LifecycleOwner) activity, jsonResponse -> {
            if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                Toast.makeText(activity, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }

            populateClientJobs(activity,getUsername(),JobsRvAdapter.this);
        });
    }

    private void negotiation() {
        activity.startActivity(new Intent(activity, NegotiationActivity.class));
    }

    private void setClient(ViewHolder holder, Models.Job job) {
        holder.providerLayout.setVisibility(View.GONE);

        if (job.getJob_status() == JobStatus.NEGOTIATING.code || job.getJob_status() == JobStatus.REQUESTED.code) {
            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setOnClickListener(v -> editSingleValue(InputType.TYPE_CLASS_NUMBER, "Enter new price",activity, price -> {
                updatePrice(job.getId(),price);
                return null;
            }));
        } else {
            holder.edit.setVisibility(View.GONE);
        }

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
        int end = normal.length() + bold.length();

        SpannableStringBuilder farmNameFormatted = new SpannableStringBuilder(normal.concat(bold));
        farmNameFormatted.setSpan(boldSpan, normal.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return farmNameFormatted;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView statusTitle, statusContent, jdTitle, jdContent, partyTitle, clientPartyContent, providerPartyContent, locationTitle, locationContent;
        TextView specialityTitle, specialityContent, priceTv, priceContent, priceRangeContent, timeTv, createdAtContent, completedAtContent,urgency;
        ImageButton edit, chat;
        LinearLayout providerLayout;
        Button decline, accept, negotiation;

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
            priceRangeContent = itemView.findViewById(R.id.priceRangeContent);
            timeTv = itemView.findViewById(R.id.timeTv);
            createdAtContent = itemView.findViewById(R.id.createdAtContent);
            completedAtContent = itemView.findViewById(R.id.completedAtContent);
            edit = itemView.findViewById(R.id.editPrice);
            chat = itemView.findViewById(R.id.chat);
            providerLayout = itemView.findViewById(R.id.serviceProviderControls);
            decline = itemView.findViewById(R.id.decline);
            accept = itemView.findViewById(R.id.accept);
            negotiation = itemView.findViewById(R.id.negotiate);
            urgency = itemView.findViewById(R.id.urgency);
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