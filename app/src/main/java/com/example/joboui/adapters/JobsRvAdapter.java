package com.example.joboui.adapters;

import static com.example.joboui.clientUi.MyJobs.populateClientJobs;
import static com.example.joboui.clientUi.request.LocationRequest.getAddressFromLocation;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.globals.GlobalVariables.REPORTED;
import static com.example.joboui.serviceProviderUi.pages.JobRequests.populateMyJobs;
import static com.example.joboui.tutorial.VerificationActivity.editSingleValue;
import static com.example.joboui.utils.JobStatus.CLIENT_REPORTED;
import static com.example.joboui.utils.JobStatus.SERVICE_COMPLETE;
import static com.example.joboui.utils.JobStatus.SERVICE_REPORTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.joboui.R;
import com.example.joboui.ReviewActivity;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.globals.GlobalVariables;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.pages.ChatActivity;
import com.example.joboui.utils.JobStatus;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

import io.vertx.core.json.JsonObject;

public class JobsRvAdapter extends RecyclerView.Adapter<JobsRvAdapter.ViewHolder> {

    private final LinkedList<Models.Job> list;
    private ItemClickListener mClickListener;
    private final Activity activity;
    private String username;
    private final JobViewModel jobViewModel;
    private Domain.User user;

    public JobsRvAdapter(Activity activity, LinkedList<Models.Job> list) {
        this.list = list;
        this.activity = activity;
        jobViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(JobViewModel.class);
        userRepository.getUser().ifPresent(u -> this.user = u);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Models.Job job = list.get(position);


        //Labels
        holder.statusTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.status)));
        holder.jdTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.job_description)));
        holder.partyTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.parties)));
        holder.locationTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.location)));
        holder.specialityTitle.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.speciality)));
        holder.priceTv.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.price)));
        holder.timeTv.setText(getUnderlinedSpannableBuilder(activity.getString(R.string.time)));

        //content
        if (job.getJob_status() != null) {
            String statusHolderLabel = activity.getString(R.string.job_status_label);

            Optional<JobStatus> jobStatus = Arrays.stream(JobStatus.values()).filter(s -> s.code == job.getJob_status()).findFirst();
            holder.statusContent.setText(jobStatus.isPresent() ? jobStatus.get().getDescription() : String.valueOf(job.getJob_status()));


        }

        if (job.getJob_description() != null) {
            String jdLabel = activity.getString(R.string.job_description_label);
            holder.jdContent.setText(job.getJob_description());
        }


        if (job.getJob_location() != null) {
            String locationLabel = activity.getString(R.string.location_label);

            JsonObject object = new JsonObject(job.getJob_location().replace("\\", ""));
            LatLng latLng = new LatLng(object.getDouble("latitude"), object.getDouble("longitude"));

            System.out.println("LAT : " + latLng.longitude + " LONG : " + latLng.latitude);

            Address location = getAddressFromLocation(activity, latLng);
            holder.locationContent.setText(location != null ? location.getAddressLine(0) : job.getJob_location().replace("\\", ""));
        }

        if (job.getSpecialities() != null) {
            String specialityLabel = activity.getString(R.string.speciality_label);
            holder.specialityContent.setText(job.getSpecialities());
        }

        if (job.getJob_price() != null) {
            String priceLabel = activity.getString(R.string.price_label);
            holder.priceContent.setText(getBoldSpannable(priceLabel, String.valueOf(job.getJob_price())));
        }


        if (job.getCreated_at() != null) {
            String createdAtLabel = activity.getString(R.string.createdat_label);
            holder.createdAtContent.setText(getBoldSpannable(createdAtLabel, job.getCreated_at()));
        }

        if (job.getCompleted_at() != null) {
            String completedAtLabel = activity.getString(R.string.completedat_label);
            holder.completedAtContent.setText(getBoldSpannable(completedAtLabel, job.getCompleted_at()));
        }

        if (job.getScheduled_at() != null) {
            String urgencyLabel = activity.getString(R.string.urgency);
            holder.urgency.setText(getBoldSpannable(urgencyLabel, job.getScheduled_at()));
        }

        if (job.getClient_username() != null) {
            String clientLabel = activity.getString(R.string.client_label);
            holder.clientPartyContent.setText(getBoldSpannable(clientLabel, job.getClient_username()));

            if (job.getClient_username().equals(getUsername())) {
                setClient(holder, job);
            }
        }

        if (job.getLocal_service_provider_username() != null) {
            String providerLabel = activity.getString(R.string.service_provider_label);
            holder.providerPartyContent.setText(getBoldSpannable(providerLabel, job.getLocal_service_provider_username()));


            if (job.getLocal_service_provider_username().equals(getUsername())) {
                setServiceProvider(holder, job);
            }
        }

        if (!job.getPayments().isEmpty()) {
            holder.pricePaidContent.setVisibility(View.VISIBLE);
            final double[] price = {0.0};
            job.getPayments().forEach(p -> {
                if (p.getAmount() != null) {
                    double amount = Double.parseDouble(p.getAmount());
                    price[0] = price[0] + amount;
                }

            });
            holder.pricePaidContent.setText(getBoldSpannable("Amount paid : ", price[0] + " Ksh"));

        }

    }

    private void goToChat(Models.Job job) {
        activity.startActivity(new Intent(activity, ChatActivity.class).putExtra(JOB, job));
    }

    private void goToReview(Models.Job job) {
        activity.startActivity(new Intent(activity, ReviewActivity.class).putExtra(JOB, job));
    }

    private void goToReport(Models.Job job) {
        activity.startActivity(new Intent(activity, ReviewActivity.class).putExtra(JOB, job).putExtra(REPORTED, true));
    }

    private void confirmDialog(String info, Models.Job job, Function<Models.Job, Void> function) {
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.yes_no_info_layout);
        TextView infov = d.findViewById(R.id.newInfoTv);
        infov.setText(info);
        Button no = d.findViewById(R.id.noButton);
        no.setOnClickListener(v -> d.dismiss());

        Button yes = d.findViewById(R.id.yesButton);
        yes.setOnClickListener(v -> {
            function.apply(job);
            d.dismiss();
        });

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    private void changeJobStatus(Models.Job job, JobStatus status, boolean isClient) {
        Models.JobUpdateForm form = new Models.JobUpdateForm(status.getCode());
        form.setCompleted_at(status == JobStatus.COMPLETED || status == JobStatus.DECLINED || status == JobStatus.CANCELLED);

        if (status == SERVICE_REPORTED || status == CLIENT_REPORTED) {
            form.setReported(true);
        }

        jobViewModel.updateJob(job.getId(), form).observe((LifecycleOwner) activity, jsonResponse -> {
            if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                Toast.makeText(activity, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }


            if (isClient) {
                populateClientJobs(activity, getUsername(), JobsRvAdapter.this);
            } else {
                populateMyJobs(activity, getUsername(), JobsRvAdapter.this);
            }

            if (status == SERVICE_REPORTED || status == CLIENT_REPORTED) {
                goToReport(job);
            }

        });
    }

    private void sendPaymentRequest(Models.Job job) {

        Models.MakeStkRequest request = new Models.MakeStkRequest(GlobalVariables.transactionType, String.valueOf(Math.round(job.getJob_price().doubleValue())).split("\\.")[0], user.getPhone_number().replace("+", ""), user.getPhone_number().replace("+", ""), job.getId(), "Payment of " + job.getJob_price() + " for job " + job.getId());

        jobViewModel.makePayment(request).observe((LifecycleOwner) activity, success -> {
            if (!success.isPresent()) {
                Toast.makeText(activity, "Failed to send prompt", Toast.LENGTH_SHORT).show();
                return;
            }

            if (success.get()) {
                Toast.makeText(activity, "Successfully initiated payment", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Failed to send prompt", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updatePrice(Models.Job job, String price, String priceRange, Integer status, boolean isClient) {
        jobViewModel.updateJob(job.getId(), new Models.JobUpdateForm(price, priceRange, status)).observe((LifecycleOwner) activity, jsonResponse -> {
            if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                Toast.makeText(activity, "Failed to get response", Toast.LENGTH_SHORT).show();
                return;
            }


            if (isClient) {
                populateClientJobs(activity, getUsername(), JobsRvAdapter.this);

            } else {
                populateMyJobs(activity, getUsername(), JobsRvAdapter.this);

            }
        });
    }

    public void setServiceProvider(ViewHolder holder, Models.Job job) {

        holder.negotiation.setVisibility(View.GONE);
        holder.chat.setVisibility(View.GONE);
        holder.accept.setVisibility(View.GONE);
        holder.decline.setVisibility(View.GONE);
        holder.edit.setVisibility(View.GONE);
        holder.providerLayout.setVisibility(View.VISIBLE);

        holder.negotiation.setOnClickListener(v -> {
            Toast.makeText(activity, "Negotiation", Toast.LENGTH_SHORT).show();
            confirmDialog("Do you want to negotiate for price ?", job, job12 -> {
                changeJobStatus(job12, JobStatus.NEGOTIATING, false);
                return null;
            });
        });
        holder.chat.setOnClickListener(v -> goToChat(job));

        switch (job.getJob_status()) {
            //Requested
            default:
            case 0:
                //todo set price user

                //can chat
                //can negotiate
                //can accept / decline
                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setOnClickListener(v -> confirmDialog("Do you want to suggest your price ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.NEGOTIATING, false);
                    return null;
                }));

                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setOnClickListener(v -> confirmDialog("Do you want to accept this job for " + job.getJob_price_range() + " KSH ?", job, job12 -> {
                    updatePrice(job12, job12.getJob_price_range(), null, JobStatus.ACCEPTED.code, false);
                    return null;
                }));

                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to decline this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.DECLINED, false);
                    return null;
                }));

                holder.chat.setVisibility(View.VISIBLE);


                if (job.getJob_price_range() != null) {
                    holder.priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }


                break;

            //Accepted
            case 1:
                //cannot decline / accept
                //can chat
                //cannot negotiate

                holder.negotiation.setVisibility(View.GONE);

                holder.accept.setVisibility(View.GONE);
                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Abort");
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to abort this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED, false);
                    return null;
                }));

                holder.chat.setVisibility(View.VISIBLE);


                break;


            //Declined //CANCELLED //SERVICE_CANCELLED_IN_PROGRESS //CLIENT_CANCELLED_IN_PROGRESS
            case 2:
            case 7:
                break;

            //service i cancelled
            case 12:

                break;
            case 13:

                //cannot decline / accept
                //cannot chat
                //cannot negotiate

                holder.statusContent.setTextColor(Color.RED);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);

                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));

                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setText("Review");
                holder.negotiation.setOnClickListener(v -> goToReview(job));


                break;

            //negotiating
            case 3:
                //can chat
                //can accept / decline
                // cannot negotiate button

                holder.negotiation.setVisibility(View.GONE);
                holder.chat.setVisibility(View.VISIBLE);

                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setOnClickListener(v -> confirmDialog("Do you want to accept this job for " + job.getJob_price_range() + " KSH ?", job, job12 -> {
                    updatePrice(job12, job12.getJob_price_range(), null, JobStatus.ACCEPTED.code, false);
                    return null;
                }));

                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to decline this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.DECLINED, false);
                    return null;
                }));

                if (job.getJob_price_range() != null) {
                    holder.priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }


                break;

            //Ready
            case 4:

                //cannot accept / decline
                //can cancel
                //can chat
                //cannot negotiate / button

                holder.negotiation.setVisibility(View.GONE);
                holder.chat.setVisibility(View.VISIBLE);

                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setText("Start Job");
                holder.accept.setOnClickListener(v -> confirmDialog("Do you want to start this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.PROGRESS, false);
                    return null;
                }));

                //cancel
                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Cancel");
                holder.decline.setOnClickListener(v3 -> holder.decline.setOnClickListener(v4 -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED, false);
                    return null;
                })));

                break;

            //Progress
            case 5:

                //can cancel // complete
                //cannot accept / decline
                //can chat
                // cannot negotiate / button

                holder.negotiation.setVisibility(View.GONE);
                holder.chat.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.GONE);

                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setText("Complete");
                holder.accept.setOnClickListener(v3 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, SERVICE_COMPLETE, false);
                    return null;
                }));


                //cancel
                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Abort");
                holder.decline.setOnClickListener(v5 -> confirmDialog("Do you want to abort this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_CANCELLED_IN_PROGRESS, false);
                    return null;
                }));

                break;

            //Completed
            case 6:

                break;

            //SERVICE_COMPLETE
            case 8:
                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                //report
                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));
                break;
            case 16:
            case 10:

                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                //report
                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));

                //review
                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setText("Review");
                holder.negotiation.setOnClickListener(v -> goToReview(job));

                break;

            //CLIENT_COMPLETE // i want to complete or complain
            case 9:


                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_REPORTED, false);
                    return null;
                }));

                holder.accept.setText("Confirm Completion");
                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setOnClickListener(v5 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.PAYING, false);
                    return null;
                }));

                break;

            case 17:

                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_REPORTED, false);
                    return null;
                }));
                break;

            //SERVICE_RATING
            case 11:
                //todo i service have rated

                break;

            //SERVICE_REPORTED
            case 14:
                //todo i service have reported
                //todo show complains

                break;

            //CLIENT_REPORTED
            case 15:
                //todo i service have been reported
                //todo show complains

                holder.statusContent.setTextColor(Color.RED);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                holder.negotiation.setText("Review");
                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setOnClickListener(v -> goToReview(job));

                break;

        }

    }

    @SuppressLint("SetTextI18n")
    private void setClient(ViewHolder holder, Models.Job job) {

        holder.negotiation.setVisibility(View.GONE);
        holder.chat.setVisibility(View.GONE);
        holder.edit.setVisibility(View.GONE);
        holder.accept.setVisibility(View.GONE);
        holder.decline.setVisibility(View.GONE);
        holder.providerLayout.setVisibility(View.VISIBLE);

        holder.chat.setOnClickListener(v -> goToChat(job));

        holder.edit.setOnClickListener(v -> editSingleValue(InputType.TYPE_CLASS_NUMBER, "Enter new price", activity, price -> {
            updatePrice(job, null, price, null, true);
            return null;
        }));

        switch (job.getJob_status()) {
            //REQUESTED
            default:
            case 0:
                //can chat
                //can cancel
                //can change price
                //cannot decline / accept
                //cannot negotiate

                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Cancel");
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED, true);
                    return null;
                }));

                holder.accept.setVisibility(View.GONE);
                holder.edit.setVisibility(View.VISIBLE);

                holder.negotiation.setVisibility(View.GONE);
                holder.chat.setVisibility(View.VISIBLE);

                if (job.getJob_price_range() != null) {
                    holder.priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }


                break;

            //ACCEPTED
            case 1:
                //cannot change price
                //cannot negotiate
                //cannot negotiate button
                //can cancel
                //cannot accept / decline
                //can chat
                //cannot edit price button

                holder.edit.setVisibility(View.GONE);
                holder.negotiation.setVisibility(View.GONE);

                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Cancel");
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_CANCELLED_IN_PROGRESS, true);
                    return null;
                }));

                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setText("READY TO START");
                holder.accept.setOnClickListener(v -> confirmDialog("Do you want to start this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.READY, true);
                    return null;
                }));

                holder.chat.setVisibility(View.VISIBLE);


                break;

            //DECLINED // CANCELLED //
            case 2:
                holder.statusBg.setCardBackgroundColor(Color.RED);
                //todo send to someone else
                break;
            case 7:
                holder.statusBg.setCardBackgroundColor(Color.RED);
                break;

            //SERVICE_CANCELLED_IN_PROGRESS //CLIENT_CANCELLED_IN_PROGRESS
            case 12:

                holder.statusContent.setTextColor(Color.RED);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                //just a red bg
                holder.statusBg.setCardBackgroundColor(Color.RED);

                holder.completedAtContent.setVisibility(View.VISIBLE);
                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);

                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));

                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setText("Review");
                holder.negotiation.setOnClickListener(v -> goToReview(job));


                if (job.getJob_price_range() != null) {
                    holder.priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }

                break;

            //I cancelled - ... client
            case 13:


                //NEGOTIATING
            case 3:
                //can cancel
                //can chat
                //cannot negotiate button
                //cannot accept / decline
                //can edit price

                //todo price change from chat

                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Cancel");
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED, true);
                    return null;
                }));
                holder.chat.setVisibility(View.VISIBLE);
                holder.providerLayout.setVisibility(View.VISIBLE);
                holder.negotiation.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);

                holder.edit.setVisibility(View.VISIBLE);

                if (job.getJob_price_range() != null) {
                    holder.priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }


                break;

            //READY
            case 4:

                //can chat
                //can cancel
                //cannot negotiate
                //cannot edit price
                //cannot accept / decline

                holder.chat.setVisibility(View.VISIBLE);

                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Cancel");
                holder.decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED, true);
                    return null;
                }));

                holder.negotiation.setVisibility(View.GONE);
                holder.edit.setVisibility(View.GONE);

                holder.accept.setVisibility(View.GONE);

                break;

            //PROGRESS
            case 5:
                //can cancel
                //can complete
                //cannot negotiate
                //cannot edit

                holder.negotiation.setVisibility(View.GONE);
                holder.chat.setVisibility(View.VISIBLE);
                holder.edit.setVisibility(View.GONE);


                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setText("Complete");
                holder.accept.setOnClickListener(v3 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_COMPLETE, true);
                    return null;
                }));


                //cancel
                holder.decline.setVisibility(View.VISIBLE);
                holder.decline.setText("Abort");
                holder.decline.setOnClickListener(v5 -> confirmDialog("Do you want to abort this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_CANCELLED_IN_PROGRESS, true);
                    return null;
                }));

                break;

            //COMPLETED
            case 6:

                break;

            //SERVICE_COMPLETE
            case 8:
                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));

                holder.accept.setText("Confirm Completion");
                holder.accept.setVisibility(View.VISIBLE);
                holder.accept.setOnClickListener(v5 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.PAYING, true);
                    return null;
                }));


                break;
            //CLIENT_COMPLETE //i have completed
            case 9:
            case 17:
                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));

                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setText("Pay Now \n " + job.getJob_price() + " Ksh");
                holder.negotiation.setOnClickListener(v -> confirmDialog("Do you want to pay " + job.getJob_price() + " ?", job, job12 -> {
                    sendPaymentRequest(job12);
                    return null;
                }));
                break;

            case 16:
            case 11:
                //SERVICE_RATING

                //review
                //complain

                holder.statusContent.setTextColor(Color.GREEN);
                holder.statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                holder.chat.setImageResource(R.drawable.ic_customer_complaint);
                holder.chat.setVisibility(View.VISIBLE);
                holder.chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED, true);
                    return null;
                }));

                holder.negotiation.setVisibility(View.VISIBLE);
                holder.negotiation.setText("Review");
                holder.negotiation.setOnClickListener(v -> goToReview(job));

                break;

            //CLIENT_RATING
            case 10:
                //todo i have rated

                break;


            //SERVICE_REPORTED
            case 14:
                //todo I client have been reported
                //todo show on problems
                break;

            //CLIENT_REPORTED
            case 15:
                //todo I client have reported
                //todo show on problems
                break;
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static SpannableStringBuilder getUnderlinedSpannableBuilder(String s) {
        SpannableStringBuilder content = new SpannableStringBuilder(s);
        content.setSpan(new UnderlineSpan(), 0, s.length(), 0);
        return content;
    }

    public static SpannableStringBuilder getBoldSpannable(String normal, String bold) {
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        int end = normal.length() + bold.length();

        SpannableStringBuilder farmNameFormatted = new SpannableStringBuilder(normal.concat(bold));
        farmNameFormatted.setSpan(boldSpan, normal.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return farmNameFormatted;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView statusTitle, statusContent, jdTitle, jdContent, partyTitle, clientPartyContent, providerPartyContent, locationTitle, locationContent;
        TextView specialityTitle, specialityContent, priceTv, priceContent, timeTv, createdAtContent, completedAtContent, urgency, pricePaidContent;
        ImageButton edit, chat;
        LinearLayout providerLayout;
        Button decline, accept, negotiation;
        CardView statusBg;

        ViewHolder(View itemView) {
            super(itemView);
            statusTitle = itemView.findViewById(R.id.statusTitle);
            statusContent = itemView.findViewById(R.id.statusContent);
            jdTitle = itemView.findViewById(R.id.jdTitle);
            jdContent = itemView.findViewById(R.id.jdContent);
            partyTitle = itemView.findViewById(R.id.partyTitle);
            clientPartyContent = itemView.findViewById(R.id.clientPartyContent);
            providerPartyContent = itemView.findViewById(R.id.providerPartyContent);
            locationTitle = itemView.findViewById(R.id.locationTitle);
            locationContent = itemView.findViewById(R.id.locationContent);
            specialityTitle = itemView.findViewById(R.id.specialityTitle);
            specialityContent = itemView.findViewById(R.id.specialityContent);
            priceTv = itemView.findViewById(R.id.priceTv);
            priceContent = itemView.findViewById(R.id.priceContent);
            timeTv = itemView.findViewById(R.id.timeTv);
            createdAtContent = itemView.findViewById(R.id.createdAtContent);
            completedAtContent = itemView.findViewById(R.id.completedAtContent);
            chat = itemView.findViewById(R.id.chat);
            edit = itemView.findViewById(R.id.edit);
            providerLayout = itemView.findViewById(R.id.serviceProviderControls);
            decline = itemView.findViewById(R.id.decline);
            accept = itemView.findViewById(R.id.accept);
            negotiation = itemView.findViewById(R.id.negotiate);
            urgency = itemView.findViewById(R.id.urgency);
            statusBg = itemView.findViewById(R.id.statusBg);
            pricePaidContent = itemView.findViewById(R.id.pricePaidContent);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}