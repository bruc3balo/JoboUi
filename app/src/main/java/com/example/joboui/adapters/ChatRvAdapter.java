package com.example.joboui.adapters;

import static com.example.joboui.utils.DataOps.getRoundTransformation;
import static com.example.joboui.utils.DataOps.truncate;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;


public class ChatRvAdapter extends RecyclerView.Adapter<ChatRvAdapter.ViewHolder> {

    public static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context mContext;

    private final LinkedList<Models.Messages> messagesList;

    private boolean fullSize;
    private final Domain.User person1;
    private final Models.AppUser person2;


    public ChatRvAdapter(Context context, Domain.User person1, Models.AppUser person2, LinkedList<Models.Messages> messagesList) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.person1 = person1;
        this.person2 = person2;
        this.messagesList = messagesList;
        System.out.println("messages "+messagesList.size() + " p1 "+person1.getUsername() + " p2 "+person2.getUsername());
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_LEFT) {
            return new ViewHolder(mInflater.inflate(R.layout.chat_left, parent, false));
        } else {
            return new ViewHolder(mInflater.inflate(R.layout.chat_right, parent, false));
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fullSize = false;



        Models.Messages messageModel = messagesList.get(position);



        holder.message.setText(messageModel.getMessageContent());
        holder.timeStamp.setText(truncate(messageModel.getCreatedAt(), 16));

        if (messageModel.getSenderUsername().equals(person1.getUsername())) {
            Glide.with(mContext).load(getUserDrawable(person1.getRole())).fitCenter().into(holder.chatUserImage);
        } else {
            Glide.with(mContext).load(getUserDrawable(person2.getRole().getName())).fitCenter().into(holder.chatUserImage);
        }

        holder.message.setOnClickListener(v -> {
            fullSize = !fullSize;
            if (fullSize) {
                holder.timeStamp.setVisibility(View.GONE);
            } else {
                holder.timeStamp.setVisibility(View.VISIBLE);
            }
        });

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private int getUserDrawable(String role) {

        System.out.println("sender role is "+role );


        switch (role) {
            default:
            case "ROLE_CLIENT":
                return R.drawable.ic_person_circle;

            case "ROLE_ADMIN":
            case "ROLE_ADMIN_TRAINEE":
                return R.drawable.ic_admin;

            case "ROLE_SERVICE_PROVIDER":
                return R.drawable.ic_worker;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        System.out.println("count is "+messagesList.size());
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (messagesList.get(position).getSenderUsername().equals(person1.getUsername())) {
            System.out.println("I am sender");
            return TYPE_RIGHT;
        } else {
            System.out.println("I am receiver");

            return TYPE_LEFT;
        }

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView message;
        TextView timeStamp;
        RoundedImageView chatUserImage;

        ViewHolder(View itemView) {
            super(itemView);

            chatUserImage = itemView.findViewById(R.id.chatUserImage);
            message = itemView.findViewById(R.id.messageContent);
            timeStamp = itemView.findViewById(R.id.timeStamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}