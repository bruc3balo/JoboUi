package com.example.joboui.adapters;


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
import com.example.joboui.models.Models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TutorialVpAdapter extends RecyclerView.Adapter<TutorialVpAdapter.ViewHolder> {

    private ItemClickListener mClickListener;

    private final Context mContext;
    private final ArrayList<Models.TutorialModel> tutorialList;


    public TutorialVpAdapter(Context context, ArrayList<Models.TutorialModel> tutorialList) {
        this.mContext = context;
        this.tutorialList = tutorialList;

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_item, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(mContext).load(tutorialList.get(position).getImageId()).into(holder.tutorialImage);
        holder.title.setText(tutorialList.get(position).getTitle());
        holder.description.setText(tutorialList.get(position).getExplanation());

    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView tutorialImage;
        TextView title, description;

        ViewHolder(View itemView) {
            super(itemView);

            tutorialImage = itemView.findViewById(R.id.tutorialImage);
            title = itemView.findViewById(R.id.tutorialTitle);
            description = itemView.findViewById(R.id.tutorialExplanation);

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