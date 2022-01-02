package com.example.joboui.adapters;

import static com.example.joboui.adapters.JobsRvAdminAdapter.getBoldSpannable;
import static com.example.joboui.login.SignInActivity.getObjectMapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.model.Models;
import com.example.joboui.utils.MyLinkedMap;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Map;

public class ReviewRvAdapter extends RecyclerView.Adapter<ReviewRvAdapter.ViewHolder> {

    private LinkedList<Models.Review> list;
    private ItemClickListener mClickListener;
    private Context mContext;


    public ReviewRvAdapter(Context context, LinkedList<Models.Review> list) {
        this.list = list;
        this.mContext = context;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedack_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
        Models.Review review = list.get(position);
        holder.user.setVisibility(View.GONE);
        MyLinkedMap  clientRatingObj = getObjectMapper().readValue(review.getClient_review(), MyLinkedMap.class);
        Map.Entry clientEntry = clientRatingObj.getEntry(0);
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(clientEntry.getKey())));
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                holder.ratingBar.setRating(Float.parseFloat(String.valueOf(clientEntry.getKey())));
            }
        });

        holder.comment.setText(getBoldSpannable("",String.valueOf(clientEntry.getValue())));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    // total number of rows
    @Override
    public int getItemCount() {

        return list.size();

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RatingBar ratingBar;
        TextView user, comment;

        ViewHolder(View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            user = itemView.findViewById(R.id.user);
            comment = itemView.findViewById(R.id.comment);

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