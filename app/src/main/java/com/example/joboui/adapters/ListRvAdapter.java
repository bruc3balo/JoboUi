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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

public class ListRvAdapter extends RecyclerView.Adapter<ListRvAdapter.ViewHolder> {

    private LinkedList<String> list;
    private ItemClickListener mClickListener;
    private Context mContext;


    public ListRvAdapter(Context context, LinkedList<String> list) {
        this.list = list;
        this.mContext = context;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    //list with remove function

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageUri.setText(list.get(position));
        holder.removeImage.setOnClickListener(v -> {
            list.remove(list.get(position));
            notifyItemRemoved(position);
        });

    }


    // total number of rows
    @Override
    public int getItemCount() {

        return list.size();

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton removeImage;
        TextView imageUri;

        ViewHolder(View itemView) {
            super(itemView);
            removeImage = itemView.findViewById(R.id.removeItem);
            imageUri = itemView.findViewById(R.id.itemText);

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