package com.example.joboui.adapters;


import static com.example.joboui.adapters.ServicesPageGrid.getServiceDrawables;
import static com.example.joboui.globals.GlobalDb.serviceRepository;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.joboui.R;
import com.example.joboui.admin.ManageServicesAdmin;
import com.example.joboui.model.Models;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.function.Function;


public class ServicesAdminRvAdapter extends RecyclerView.Adapter<ServicesAdminRvAdapter.ViewHolder> {

    private ItemClickListener mClickListener;

    private final ManageServicesAdmin mContext;
    private final LinkedList<Models.ServicesModel> servicesModels = new LinkedList<>();

    public ServicesAdminRvAdapter(ManageServicesAdmin context) {
        this.mContext = context;
        refreshList();
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

        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());

        holder.statusTv.setText(service.getDisabled() ? service.getName() + " is disabled" : service.getName() + " is enabled");
        holder.serviceStatusButton.setText(service.getDisabled() ? "Enable" : "Disable");
        holder.serviceStatusButton.setOnClickListener(v -> confirmDialog(service.getDisabled() ? "Do you want to enable " +  service.getName() : "Do you want to disable     " +  service.getName(), service, serviceModel -> {
            toggleServiceStatus(serviceModel);
            return null;
        }));
        holder.serviceStatusButton.setCompoundDrawableTintList(ColorStateList.valueOf(mContext.getColor(service.getDisabled() ? android.R.color.holo_red_light : android.R.color.holo_green_light)));

        Glide.with(mContext).load(getServiceDrawables().get(service.getName())).into(holder.serviceImage);
    }

    private void confirmDialog(String info, Models.ServicesModel servicesModel, Function<Models.ServicesModel, Void> function) {
        Dialog d = new Dialog(mContext);
        d.setContentView(R.layout.yes_no_info_layout);

        TextView infov = d.findViewById(R.id.newInfoTv);
        infov.setText(info);

        Button no = d.findViewById(R.id.noButton);
        no.setOnClickListener(v -> d.dismiss());

        Button yes = d.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            function.apply(servicesModel);
            d.dismiss();
        });

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    private void toggleServiceStatus(Models.ServicesModel service) {
        serviceRepository.updateAService(service.getName(), new Models.ServiceUpdateForm(!service.getDisabled())).observe(mContext, success -> {
            if (!success.isPresent()) {
                Toast.makeText(mContext, "Failed to send request", Toast.LENGTH_SHORT).show();
                return;
            }

            if (success.get()) {
                refreshList();
            } else {
                Toast.makeText(mContext, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return servicesModels.size();
    }

    private void refreshList() {
        if (serviceRepository != null) {
            serviceRepository.getServicesLiveData().observe(mContext, services -> {
                if (!services.isEmpty()) {
                    servicesModels.clear();
                    servicesModels.addAll(services);
                    notifyDataSetChanged();
                }
            });
        }
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