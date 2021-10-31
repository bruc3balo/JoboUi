package com.example.joboui.adapters;


import static com.example.joboui.clientUi.ServiceRequestActivity.speciality;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.getBoldSpannable;
import static com.example.joboui.utils.DataOps.getMapFromString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.DataOps;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ProviderRVAdapter extends RecyclerView.Adapter<ProviderRVAdapter.ViewHolder> {

    private ItemClickListener mClickListener;

    private final Context mContext;
    private final ArrayList<Models.AppUser> userList;


    public ProviderRVAdapter(Context context, ArrayList<Models.AppUser> userList) {
        this.mContext = context;
        this.userList = userList;

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_item, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Models.AppUser user = userList.get(position);

        Integer p = ServicesPageGrid.getServiceDrawables().get(speciality);

        if (p != null) {
            Glide.with(mContext).load(mContext.getDrawable(p)).into(holder.providerImage);
        }

        String nameLabel = "Name : ";
        holder.name.setText(getBoldSpannable(nameLabel,userList.get(position).getNames()));

        String bioLabel = "Bio : ";
        holder.bio.setText(getBoldSpannable(bioLabel,userList.get(position).getBio()));

        String ratingLabel = "Rating : ";
        holder.rating.setText(getBoldSpannable(ratingLabel,"0"));

        StringBuilder workingHours = new StringBuilder();
        getMapFromString(user.getPreferred_working_hours()).forEach((d, t) -> workingHours.append(d).append("=").append(t).append("\n"));

        String workingHoursLabel = "Working hours : ";
        holder.workingHours.setText(getBoldSpannable(workingHoursLabel,workingHours.toString()));

    /*    try {
            System.out.println(getObjectMapper().writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView providerImage;
        TextView name, bio, rating, workingHours;

        ViewHolder(View itemView) {
            super(itemView);

            providerImage = itemView.findViewById(R.id.providerImage);
            name = itemView.findViewById(R.id.name);
            bio = itemView.findViewById(R.id.bio);
            rating = itemView.findViewById(R.id.rating);
            workingHours = itemView.findViewById(R.id.workingHours);

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