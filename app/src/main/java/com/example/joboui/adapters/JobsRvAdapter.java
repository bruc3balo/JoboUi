package com.example.joboui.adapters;

import static com.example.joboui.clientUi.request.LocationRequest.getAddressFromLocation;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.model.Models;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import io.vertx.core.json.JsonObject;

public class JobsRvAdapter extends RecyclerView.Adapter<JobsRvAdapter.ViewHolder> {

    private LinkedList<Models.Job> list;
    private ItemClickListener mClickListener;
    private Context mContext;


    public JobsRvAdapter(Context context, LinkedList<Models.Job> list) {
        this.list = list;
        this.mContext = context;
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
        holder.statusTitle.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.status)));
        holder.jdTitle.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.job_description)));
        holder.partyTitle.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.parties)));
        holder.locationTitle.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.location)));
        holder.specialityTitle.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.speciality)));
        holder.priceTv.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.price)));
        holder.timeTv.setText(getUnderlinedSpannableBuilder(mContext.getString(R.string.time)));

        //content
        if (job.getJob_status() != null) {
            String statusHolderLabel = mContext.getString(R.string.job_status_label);
            holder.statusContent.setText(getBoldSpannable(statusHolderLabel, String.valueOf(job.getJob_status())));
        }
        if (job.getJob_description() != null) {
            String jdLabel = mContext.getString(R.string.job_description_label);
            holder.jdContent.setText(getBoldSpannable(jdLabel, job.getJob_description()));
        }
        if (job.getClient_username() != null) {
            String clientLabel = mContext.getString(R.string.client_label);
            holder.clientPartyContent.setText(getBoldSpannable(clientLabel, job.getClient_username()));
        }
        if (job.getLocal_service_provider_username() != null) {
            String providerLabel = mContext.getString(R.string.service_provider_label);
            holder.providerPartyContent.setText(getBoldSpannable(providerLabel, job.getLocal_service_provider_username()));
        }
        if (job.getJob_location() != null) {
            String locationLabel = mContext.getString(R.string.location_label);

            JsonObject object = new JsonObject(job.getJob_location().replace("\\", ""));
            LatLng latLng = new LatLng(object.getDouble("latitude"),object.getDouble("longitude"));

            System.out.println("LAT : "+latLng.longitude + " LONG : "+latLng.latitude);

            Address location = getAddressFromLocation(mContext, latLng);
            holder.locationContent.setText(getBoldSpannable(locationLabel, location != null ? location.getAddressLine(0) : job.getJob_location().replace("\\", "")));
        }

        if (job.getSpecialities() != null) {
            String specialityLabel = mContext.getString(R.string.speciality_label);
            holder.specialityContent.setText(getBoldSpannable(specialityLabel, job.getSpecialities()));
        }
        if (job.getJob_price() != null) {
            String priceLabel = mContext.getString(R.string.price_label);
            holder.priceContent.setText(getBoldSpannable(priceLabel, String.valueOf(job.getJob_price())));
        }
        if (job.getJob_price_range() != null) {
            String priceRangeLabel = mContext.getString(R.string.price_range_label);
            holder.priceRangeContent.setText(getBoldSpannable(priceRangeLabel, job.getJob_price_range()));
        }
        if (job.getCreated_at() != null) {
            String createdAtLabel = mContext.getString(R.string.createdat_label);
            holder.createdAtContent.setText(getBoldSpannable(createdAtLabel, job.getCreated_at()));
        }
        if (job.getCompleted_at() != null) {
            String completedAtLabel = mContext.getString(R.string.completedat_label);
            holder.completedAtContent.setText(getBoldSpannable(completedAtLabel, job.getCompleted_at()));
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
        TextView specialityTitle, specialityContent, priceTv, priceContent, priceRangeContent, timeTv, createdAtContent, completedAtContent;

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