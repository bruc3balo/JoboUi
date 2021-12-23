package com.example.joboui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.model.Models;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class NotificationRvAdapter extends RecyclerView.Adapter<NotificationRvAdapter.ViewHolder> {

    private LinkedList<Models.NotificationModels> list;
    private ItemClickListener mClickListener;
    private Context mContext;


    public NotificationRvAdapter(Context context, LinkedList<Models.NotificationModels> list) {
        this.list = list;
        this.mContext = context;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Models.NotificationModels notification = list.get(position);
        holder.title.setText(notification.getTitle());
        holder.time.setText(notification.getCreated_at().toString());
        holder.description.setText(notification.getDescription());
    }



    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title,time,description;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
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