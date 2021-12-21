package com.example.joboui.adapters;

import static com.example.joboui.adapters.JobsRvAdminAdapter.getUnderlinedSpannableBuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.model.Models;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class FeedbackRvAdapter extends RecyclerView.Adapter<FeedbackRvAdapter.ViewHolder> {

    private LinkedList<Models.Feedback> list;
    private ItemClickListener mClickListener;
    private Context mContext;


    public FeedbackRvAdapter(Context context, LinkedList<Models.Feedback> list) {
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
        Models.Feedback feedback = list.get(position);
        holder.user.setText(getUnderlinedSpannableBuilder(feedback.getUser().getUsername()));
        holder.comment.setText(feedback.getComment());
        holder.ratingBar.setRating(feedback.getRating());
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                holder.ratingBar.setRating(feedback.getRating());
            }
        });
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