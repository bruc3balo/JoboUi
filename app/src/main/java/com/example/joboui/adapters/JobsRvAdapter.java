package com.example.joboui.adapters;

import static com.example.joboui.clientUi.request.LocationRequest.getAddressFromLocation;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.JOB;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.text.SpannableString;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.JobTrackActivity;
import com.example.joboui.R;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.AppRolesEnum;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import io.vertx.core.json.JsonObject;

public class JobsRvAdapter extends RecyclerView.Adapter<JobsRvAdapter.ViewHolder> {

    private final LinkedList<Models.Job> list;
    private ItemClickListener mClickListener;
    private final Activity activity;
    private String username;
    private final JobViewModel jobViewModel;
    private Domain.User user;

    public JobsRvAdapter(Activity activity, LinkedList<Models.Job> list) {
        this.list = list;
        this.activity = activity;
        jobViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class);
        userRepository.getUser().ifPresent(u -> this.user = u);
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

        //set job data

        if (job.getJob_description() != null) {
            String jdLabel = activity.getString(R.string.job_description_label);
            holder.jdContent.setText(getBoldSpannable(jdLabel,job.getJob_description()));
        }


        if (job.getJob_location() != null) {
            String locationLabel = activity.getString(R.string.location_label);

            JsonObject object = new JsonObject(job.getJob_location().replace("\\", ""));
            LatLng latLng = new LatLng(object.getDouble("latitude"), object.getDouble("longitude"));

            System.out.println("LAT : " + latLng.longitude + " LONG : " + latLng.latitude);

            Address location = getAddressFromLocation(activity, latLng);
            holder.locationContent.setText(location != null ? getBoldSpannable(locationLabel,location.getAddressLine(0)) : getBoldSpannable(locationLabel,job.getJob_location().replace("\\", "")));
        }

        if (job.getSpecialities() != null) {
            String specialityLabel = activity.getString(R.string.speciality_label);
            holder.specialityContent.setText(getBoldSpannable(specialityLabel,job.getSpecialities()));
        }


        if (job.getScheduled_at() != null) {
            String urgencyLabel = activity.getString(R.string.urgency);
            holder.urgency.setText(getBoldSpannable(urgencyLabel,job.getScheduled_at()));
        }

        if (job.getClient_username() != null) {
            String clientLabel = activity.getString(R.string.client_label);
            holder.clientPartyContent.setText(getBoldSpannable(clientLabel, job.getClient_username()));

        }

        if (job.getLocal_service_provider_username() != null) {
            String providerLabel = activity.getString(R.string.service_provider_label);
            holder.providerPartyContent.setText(getBoldSpannable(providerLabel, job.getLocal_service_provider_username()));



        }

        if (user.getRole().equals(AppRolesEnum.ROLE_CLIENT.name())) {
            holder.providerPartyContent.setVisibility(View.GONE);
            holder.clientPartyContent.setVisibility(View.VISIBLE);
        } else {
            holder.clientPartyContent.setVisibility(View.GONE);
            holder.providerPartyContent.setVisibility(View.VISIBLE);
        }

        holder.more.setOnClickListener(v -> activity.startActivity(new Intent(activity, JobTrackActivity.class).putExtra(JOB,job)));

    }




    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }


    //extra methods

    public static SpannableStringBuilder getUnderlinedSpannableBuilder(String s) {
        SpannableStringBuilder content = new SpannableStringBuilder(s);
        content.setSpan(new UnderlineSpan(), 0, s.length(), 0);
        return content;
    }

    public static SpannableStringBuilder getUnderlinedSpannableBuilder(SpannableString s) {
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

    public static SpannableStringBuilder getBoldSpannable(SpannableStringBuilder normal, String bold) {
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        int end = normal.length() + bold.length();

        SpannableStringBuilder farmNameFormatted = new SpannableStringBuilder(normal.toString().concat(bold));
        farmNameFormatted.setSpan(boldSpan, normal.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return farmNameFormatted;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  jdContent, partyTitle, clientPartyContent, providerPartyContent, locationContent, specialityContent,  urgency;
        ImageButton edit, chat,more;
        LinearLayout providerLayout;
        Button decline, accept, negotiation;
        CardView statusBg;

        ViewHolder(View itemView) {
            super(itemView);

            jdContent = itemView.findViewById(R.id.jdContent);
            partyTitle = itemView.findViewById(R.id.partyTitle);
            clientPartyContent = itemView.findViewById(R.id.clientPartyContent);
            providerPartyContent = itemView.findViewById(R.id.providerPartyContent);
            locationContent = itemView.findViewById(R.id.locationContent);
            specialityContent = itemView.findViewById(R.id.specialityContent);
            chat = itemView.findViewById(R.id.chat);
            edit = itemView.findViewById(R.id.edit);
            providerLayout = itemView.findViewById(R.id.serviceProviderControls);
            decline = itemView.findViewById(R.id.decline);
            accept = itemView.findViewById(R.id.accept);
            negotiation = itemView.findViewById(R.id.negotiate);
            urgency = itemView.findViewById(R.id.urgency);
            statusBg = itemView.findViewById(R.id.statusBg);
            more = itemView.findViewById(R.id.more);
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