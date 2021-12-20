package com.example.joboui.adapters;


import static com.example.joboui.adapters.ServicesPageGrid.getServiceDrawables;
import static com.example.joboui.globals.GlobalDb.serviceRepository;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.utils.DataOps.getListFromString;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.ManageServicesProvider;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.function.Function;


public class ServicesProviderRvAdapter extends RecyclerView.Adapter<ServicesProviderRvAdapter.ViewHolder> {

    private ItemClickListener mClickListener;

    private final ManageServicesProvider mContext;
    private final LinkedList<Models.ServicesModel> servicesModels = new LinkedList<>();
    private Domain.User user;


    public ServicesProviderRvAdapter(ManageServicesProvider context) {
        this.mContext = context;
        userRepository.getUserLive().observe(mContext, optionalUser -> optionalUser.ifPresent(value -> user = value));
        refreshServiceList(context);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_admin_layout, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "UseCompatTextViewDrawableApis"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Models.ServicesModel service = servicesModels.get(position);
        LinkedList<String> services = new LinkedList<>();

        if (user != null) {
            services.addAll(getListFromString(user.getSpecialities()));
            System.out.println(services);
        }

        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());

        holder.statusTv.setText(services.contains(service.getName()) ? " is registered to " + service.getName() : " is not registered to " + service.getName());
        holder.serviceStatusButton.setText(services.contains(service.getName()) ? "Deregister" : "Register");
        holder.serviceStatusButton.setOnClickListener(v -> confirmDialog(services.contains(service.getName()) ? "Do you want to deregister from " + service.getName() : "Do you want to register to " + service.getName(), services, specialityList -> {

            if (specialityList.contains(service.getName())) {
                specialityList.remove(service.getName());
            } else {
                specialityList.add(service.getName());
            }

            toggleServiceRegisterStatus(specialityList);
            return null;
        }));
        holder.serviceStatusButton.setCompoundDrawableTintList(ColorStateList.valueOf(mContext.getColor(services.contains(service.getName()) ? android.R.color.holo_red_light : android.R.color.holo_green_light)));

        Glide.with(mContext).load(getServiceDrawables().get(service.getName())).into(holder.serviceImage);
    }

    private void confirmDialog(String info, LinkedList<String> specialities, Function<LinkedList<String>, LinkedList<String>> function) {
        Dialog d = new Dialog(mContext);
        d.setContentView(R.layout.yes_no_info_layout);

        TextView infov = d.findViewById(R.id.newInfoTv);
        infov.setText(info);

        Button no = d.findViewById(R.id.noButton);
        no.setOnClickListener(v -> d.dismiss());

        Button yes = d.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            function.apply(specialities);
            d.dismiss();
        });

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    private void toggleServiceRegisterStatus(LinkedList<String> specialities) {

        new ViewModelProvider(mContext).get(UserViewModel.class).updateAnExistingUser(user.getUsername(), new Models.UserUpdateForm(specialities)).observe(mContext, userOptional -> {
            if (!userOptional.isPresent()) {
                Toast.makeText(mContext, "Failed to send request", Toast.LENGTH_SHORT).show();
                return;
            }

            notifyDataSetChanged();
            refreshUser();
        });
    }

    private void refreshServiceList(Activity activity) {
        if (serviceRepository != null) {
            serviceRepository.getServicesLiveData().observe((LifecycleOwner) activity, services -> {
                if (!services.isEmpty()) {
                    servicesModels.clear();
                    servicesModels.addAll(services);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return servicesModels.size();
    }


    private void refreshUser() {
        new ViewModelProvider(mContext).get(UserViewModel.class).refreshMyData().observe(mContext, userOptional -> {
            if (userOptional.isPresent()) {
                System.out.println("User updated");
                notifyDataSetChanged();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView serviceName, serviceDescription, statusTv;
        Button serviceStatusButton;
        RoundedImageView serviceImage;

        ViewHolder(View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            statusTv = itemView.findViewById(R.id.statusTv);
            serviceStatusButton = itemView.findViewById(R.id.serviceStatusButton);
            serviceImage = itemView.findViewById(R.id.serviceImage);


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