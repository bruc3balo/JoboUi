package com.example.joboui.adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.admin.UserActivity;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.model.Models;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.function.Function;


public class UserRvAdapter extends RecyclerView.Adapter<UserRvAdapter.ViewHolder> {

    private ItemClickListener mClickListener;

    private final UserActivity mContext;
    private final LinkedList<Models.AppUser> userLinkedList;
    private UserViewModel userViewModel;


    public UserRvAdapter(UserActivity context, LinkedList<Models.AppUser> userLinkedList) {
        this.mContext = context;
        this.userLinkedList = userLinkedList;
        userViewModel = new ViewModelProvider(context).get(UserViewModel.class);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Models.AppUser user = userLinkedList.get(position);


        if (user.getNames() != null) {
            holder.usernameItem.setText(user.getNames());
        }

        if (user.getEmail_address() != null) {
            holder.emailItem.setText(user.getEmail_address());
        }

        if (user.getRole() != null) {
            holder.roleItem.setText(user.getRole().getName());
        }

        if (user.getVerified() != null) {
            if (user.getVerified()) {
                holder.verify.setImageResource(R.drawable.tick);
                holder.verify.setOnClickListener(null);
            } else {
                holder.verify.setImageResource(R.drawable.ic_cancel);
                holder.verify.setOnClickListener(v -> verifyUser(user));
            }
        }

        if (user.getDeleted() != null) {
            holder.deleted.setOnClickListener(v -> showConfirmationDialog(user.getDeleted() ? "Restore " + user.getUsername().concat("?") : "Delete " + user.getUsername().concat(" ?"),currentStateDeleted -> {
                toggleDeleted(user.getUsername(),user.getDeleted());
                return null;
            },user.getDeleted()));

            setStatus(user.getDeleted(),holder.deleted);

        }

        if (user.getDisabled() != null) {
            holder.disabled.setOnClickListener(v -> showConfirmationDialog(user.getDisabled() ? "Enable " + user.getUsername().concat("?") : "Disable " + user.getUsername().concat(" ?"),currentStateDeleted -> {
                toggleDisabled(user.getUsername(),user.getDisabled());
                return null;
            },user.getDisabled()));

            setStatus(user.getDisabled(),holder.disabled);
        }

    }


    private void showConfirmationDialog(String infoS, Function<Boolean, Void> yesFunction, Boolean currentState) {
        Dialog d = new Dialog(mContext);
        d.setContentView(R.layout.yes_no_info_layout);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();

        TextView info = d.findViewById(R.id.newInfoTv);
        info.setText(infoS);
        Button no = d.findViewById(R.id.noButton);
        no.setOnClickListener(v -> d.dismiss());

        Button yes = d.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            yesFunction.apply(currentState);
            d.dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    private void verifyUser(Models.AppUser user) {
            Dialog d = new Dialog(mContext);
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            d.setContentView(R.layout.yes_no_info_layout);
            d.show();

            TextView info = d.findViewById(R.id.newInfoTv);
            info.setText("Ar6e you sure you want to verify " + user.getNames());
            Button no = d.findViewById(R.id.noButton);
            no.setOnClickListener(v -> d.dismiss());
            Button yes = d.findViewById(R.id.yesButton);
            yes.setOnClickListener(v -> {
                d.dismiss();
                userViewModel.updateAnExistingUser(user.getUsername(), new Models.UserUpdateForm(null,null,true)).observe(mContext, jsonResponseResponse -> {
                    if (!jsonResponseResponse.isPresent()) {
                        Toast.makeText(mContext, "Failed verification of " + user.getNames(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Models.AppUser verifiedUser = jsonResponseResponse.get();

                    if (verifiedUser.getVerified()) {
                        Toast.makeText(mContext, "User is verified", Toast.LENGTH_SHORT).show();
                        refreshList();
                    } else {
                        Toast.makeText(mContext, "Failed to verify user", Toast.LENGTH_SHORT).show();
                    }
                });
            });


    }

    private void toggleDeleted(String username, Boolean currentState) {
        userViewModel.updateAnExistingUser(username, new Models.UserUpdateForm(null,!currentState,null)).observe(mContext, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(mContext, "Failed update " + username, Toast.LENGTH_SHORT).show();
                return;
            }

            Models.AppUser verifiedUser = jsonResponse.get();

            Toast.makeText(mContext, verifiedUser.getDeleted() ? verifiedUser.getUsername() + " Deleted" : verifiedUser.getUsername() + " Restored", Toast.LENGTH_SHORT).show();
            refreshList();
        });
    }


    private void toggleDisabled(String username, Boolean currentState) {
        userViewModel.updateAnExistingUser(username, new Models.UserUpdateForm(!currentState,null,null)).observe(mContext, jsonResponse -> {
            if (!jsonResponse.isPresent()) {
                Toast.makeText(mContext, "Failed update " + username, Toast.LENGTH_SHORT).show();
                return;
            }

            Models.AppUser disabledUser = jsonResponse.get();

            Toast.makeText(mContext, disabledUser.getDisabled() ? disabledUser.getUsername() + " Disabled" : disabledUser.getUsername() + " Enabled", Toast.LENGTH_SHORT).show();
            refreshList();
        });
    }

    private void setStatus(boolean deleted, ImageButton button) {
        if (deleted) {
            button.setImageTintList(ColorStateList.valueOf(Color.RED));
        } else {
            button.setImageTintList(ColorStateList.valueOf(Color.GREEN));
        }
    }


    private void refreshList() {
        mContext.getUsers();
    }

    @Override
    public int getItemCount() {
        return userLinkedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RoundedImageView userDp;
        TextView usernameItem, emailItem, roleItem;
        ImageButton verify, disabled, deleted;

        ViewHolder(View itemView) {
            super(itemView);

            userDp = itemView.findViewById(R.id.profilePicItem);
            usernameItem = itemView.findViewById(R.id.usernameItem);
            emailItem = itemView.findViewById(R.id.emailItem);
            roleItem = itemView.findViewById(R.id.roleItem);

            verify = itemView.findViewById(R.id.verify);
            disabled = itemView.findViewById(R.id.disabled);
            deleted = itemView.findViewById(R.id.deleted);

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