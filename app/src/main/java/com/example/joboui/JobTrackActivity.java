package com.example.joboui;

import static android.graphics.Color.RED;
import static com.example.joboui.adapters.JobsRvAdapter.getUnderlinedSpannableBuilder;
import static com.example.joboui.clientUi.request.LocationRequest.getAddressFromLocation;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.JOB;
import static com.example.joboui.globals.GlobalVariables.REPORTED;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.tutorial.VerificationActivity.editSingleValue;
import static com.example.joboui.utils.DataOps.getBoldSpannable;
import static com.example.joboui.utils.JobStatus.CLIENT_REPORTED;
import static com.example.joboui.utils.JobStatus.SERVICE_COMPLETE;
import static com.example.joboui.utils.JobStatus.SERVICE_REPORTED;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.joboui.databinding.ActivityJobTrackBinding;
import com.example.joboui.db.job.JobViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.globals.GlobalVariables;
import com.example.joboui.model.Models;
import com.example.joboui.model.Models.Review;
import com.example.joboui.serviceProviderUi.pages.ChatActivity;
import com.example.joboui.serviceProviderUi.pages.JobRequests;
import com.example.joboui.utils.AppRolesEnum;
import com.example.joboui.utils.JobStatus;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JobTrackActivity extends AppCompatActivity {

    private ActivityJobTrackBinding binding;
    private Models.Job job;
    private Domain.User me;
    private boolean isClient;
    private TextView statusTitle, statusContent, jdTitle, jdContent, partyTitle, clientPartyContent, providerPartyContent, locationTitle, locationContent;
    private TextView specialityTitle, specialityContent, priceTv, priceContent, timeTv, createdAtContent, completedAtContent, urgency, pricePaidContent;
    private ImageButton edit, chat;
    private LinearLayout providerLayout;
    private Button decline, accept, negotiation;
    private JobViewModel jobViewModel;
    private View line1,line2,line3,line4;
    private ImageView circle1,circle2,circle3,circle4,circle5;
    public static MutableLiveData<Optional<Boolean>> refreshJobTracking = new MutableLiveData<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJobTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);


        job = (Models.Job) getIntent().getExtras().get(JOB);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v->finish());

        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        AppBarLayout appbar = binding.appBar;
        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            binding.jobInfoLayout.setAlpha(jobInfoAlpha(Math.abs(verticalOffset), appBarLayout.getTotalScrollRange()));
            binding.jobInfoLayout.setAlpha(jobInfoAlpha(Math.abs(verticalOffset), appBarLayout.getTotalScrollRange()));

            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                //Collapsed
                toolbar.setTitle(job.getSpecialities());
            } else if (verticalOffset == 0) {
                //Expanded
                toolbar.setTitle("");
            } else {
                //Between
                toolbar.setTitle("");
            }

        });

        statusTitle = binding.statusTitle;
        statusContent = binding.statusContent;
        jdTitle = binding.jdTitle;
        jdContent = binding.jdContent;
        partyTitle = binding.partyTitle;
        clientPartyContent = binding.clientPartyContent;
        providerPartyContent = binding.providerPartyContent;
        locationTitle = binding.locationTitle;
        locationContent = binding.locationContent;
        specialityTitle = binding.specialityTitle;
        specialityContent = binding.specialityContent;
        priceTv = binding.priceTv;
        priceContent = binding.priceContent;
        timeTv = binding.timeTv;
        createdAtContent = binding.createdAtContent;
        completedAtContent = binding.completedAtContent;
        urgency = binding.urgency;
        pricePaidContent = binding.pricePaidContent;


        chat = binding.chat;
        edit = binding.edit;
        providerLayout = binding.serviceProviderControls;
        decline = binding.decline;
        accept = binding.accept;
        negotiation = binding.negotiate;

        line1 = binding.line1;
        line2 = binding.line2;
        line3 = binding.line3;
        line4 = binding.line4;

        circle1 = binding.circle1;
        circle2 = binding.circle2;
        circle3 = binding.circle3;
        circle4 = binding.circle4;
        circle5 = binding.circle5;

        if (userRepository != null) {
            userRepository.getUserLive().observe(this, optionalUser -> optionalUser.ifPresent(u -> {
                me = u;
                isClient = u.getRole().equals(AppRolesEnum.ROLE_CLIENT.name());
                setData(job);
                addRefreshListener();
            }));
        }


    }

    private void addRefreshListener() {
        refreshData().observe(this, refresh -> {
            if (refresh.isPresent()) {
                if (!JobTrackActivity.this.isDestroyed() && !JobTrackActivity.this.isFinishing()) {
                    refreshJob();
                }
            }
        });
    }

    private LiveData<Optional<Boolean>> refreshData() {
        return refreshJobTracking;
    }

    private void setData(Models.Job job) {

        statusTitle.setText(getUnderlinedSpannableBuilder(getString(R.string.status)));
        jdTitle.setText(getUnderlinedSpannableBuilder(getString(R.string.job_description)));
        partyTitle.setText(getUnderlinedSpannableBuilder(getString(R.string.parties)));
        locationTitle.setText(getUnderlinedSpannableBuilder(getString(R.string.location)));
        specialityTitle.setText(getUnderlinedSpannableBuilder(getString(R.string.speciality)));
        priceTv.setText(getUnderlinedSpannableBuilder(getString(R.string.price)));
        timeTv.setText(getUnderlinedSpannableBuilder(getString(R.string.time)));

        FloatingActionButton edit = binding.edit;
        if (!isClient) {
            edit.setVisibility(View.GONE);
        }

        if (job.getJob_status() != null) {
            String statusHolderLabel = getString(R.string.job_status_label);

            Optional<JobStatus> jobStatus = Arrays.stream(JobStatus.values()).filter(s -> s.code == job.getJob_status()).findFirst();
            statusContent.setText(jobStatus.isPresent() ? jobStatus.get().getDescription() : String.valueOf(job.getJob_status()));


        }

        if (job.getJob_description() != null) {
            String jdLabel = getString(R.string.job_description_label);
            jdContent.setText(job.getJob_description());
        }


        if (job.getJob_location() != null) {
            String locationLabel = getString(R.string.location_label);

            JsonObject object = new JsonObject(job.getJob_location().replace("\\", ""));
            LatLng latLng = new LatLng(object.getDouble("latitude"), object.getDouble("longitude"));

            System.out.println("LAT : " + latLng.longitude + " LONG : " + latLng.latitude);

            Address location = getAddressFromLocation(this, latLng);
            locationContent.setText(location != null ? location.getAddressLine(0) : job.getJob_location().replace("\\", ""));
        }

        if (job.getSpecialities() != null) {
            String specialityLabel = getString(R.string.speciality_label);
            specialityContent.setText(job.getSpecialities());
        }

        if (job.getJob_price() != null) {
            String priceLabel = getString(R.string.price_label);
            priceContent.setText(getBoldSpannable(priceLabel, String.valueOf(job.getJob_price())));
        }


        if (job.getCreated_at() != null) {
            String createdAtLabel = getString(R.string.createdat_label);
            createdAtContent.setText(getBoldSpannable(createdAtLabel, job.getCreated_at()));
        }

        if (job.getCompleted_at() != null) {
            String completedAtLabel = getString(R.string.completedat_label);
            completedAtContent.setText(getBoldSpannable(completedAtLabel, job.getCompleted_at()));
        }

        if (job.getScheduled_at() != null) {
            String urgencyLabel = getString(R.string.urgency);
            urgency.setText(getBoldSpannable(urgencyLabel, job.getScheduled_at()));
        }

        if (job.getClient_username() != null) {
            String clientLabel = getString(R.string.client_label);
            clientPartyContent.setText(getBoldSpannable(clientLabel, job.getClient_username()));

            if (job.getClient_username().equals(me.getUsername())) {
                setClient(job);
            }
        }

        if (job.getLocal_service_provider_username() != null) {
            String providerLabel = getString(R.string.service_provider_label);
            providerPartyContent.setText(getBoldSpannable(providerLabel, job.getLocal_service_provider_username()));


            if (job.getLocal_service_provider_username().equals(me.getUsername())) {
                setServiceProvider(job);
            }
        }

        if (!job.getPayments().isEmpty()) {
            pricePaidContent.setVisibility(View.VISIBLE);
            final double[] price = {0.0};
            job.getPayments().forEach(p -> {
                if (p.getAmount() != null) {
                    double amount = Double.parseDouble(p.getAmount());
                    price[0] = price[0] + amount;
                }

            });

            pricePaidContent.setText(getBoldSpannable("Amount paid : ", price[0] + " Ksh"));

        }

    }

    public void setServiceProvider(Models.Job job) {

        negotiation.setVisibility(View.GONE);
        chat.setVisibility(View.GONE);
        accept.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        providerLayout.setVisibility(View.VISIBLE);


        circle1.setImageResource(R.drawable.circle);
        circle2.setImageResource(R.drawable.circle);
        circle3.setImageResource(R.drawable.circle);
        circle4.setImageResource(R.drawable.circle);
        circle5.setImageResource(R.drawable.circle);
        line1.setBackgroundColor(getColor(R.color.white));
        line2.setBackgroundColor(getColor(R.color.white));
        line3.setBackgroundColor(getColor(R.color.white));
        line4.setBackgroundColor(getColor(R.color.white));
        line1.setBackgroundColor(getColor(R.color.white));

        negotiation.setOnClickListener(v -> {
            Toast.makeText(JobTrackActivity.this, "Negotiation", Toast.LENGTH_SHORT).show();
            confirmDialog("Do you want to negotiate for price ?", job, job12 -> {
                changeJobStatus(job12, JobStatus.NEGOTIATING);
                return null;
            });
        });
        chat.setOnClickListener(v -> goToChat(job));

        switch (job.getJob_status()) {
            //Requested
            default:
            case 0:
                //todo set price user

                //can chat
                //can negotiate
                //can accept / decline
                negotiation.setVisibility(View.VISIBLE);
                negotiation.setOnClickListener(v -> confirmDialog("Do you want to suggest your price ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.NEGOTIATING);
                    return null;
                }));

                accept.setVisibility(View.VISIBLE);
                accept.setOnClickListener(v -> confirmDialog("Do you want to accept this job for " + job.getJob_price_range() + " KSH ?", job, job12 -> {
                    updatePrice(job12, job12.getJob_price_range(), null, JobStatus.ACCEPTED.code);
                    return null;
                }));

                decline.setVisibility(View.VISIBLE);
                decline.setOnClickListener(v -> confirmDialog("Do you want to decline this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.DECLINED);
                    return null;
                }));

                chat.setVisibility(View.VISIBLE);


                if (job.getJob_price_range() != null) {
                    priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }

                circle1.setImageResource(R.drawable.circle_day_bg_selected);

                break;

            //Accepted
            case 1:
                //cannot decline / accept
                //can chat
                //cannot negotiate

                negotiation.setVisibility(View.GONE);

                accept.setVisibility(View.GONE);
                decline.setVisibility(View.VISIBLE);
                decline.setText("Abort");
                decline.setOnClickListener(v -> confirmDialog("Do you want to abort this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED);
                    return null;
                }));

                chat.setVisibility(View.VISIBLE);


                break;


            //Declined //CANCELLED //SERVICE_CANCELLED_IN_PROGRESS //CLIENT_CANCELLED_IN_PROGRESS
            case 2:
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle1.setImageTintList(ColorStateList.valueOf(RED));
                break;
            case 7:
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageTintList(ColorStateList.valueOf(RED));
                break;

            //service i cancelled
            case 12:
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.red));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3. setImageTintList(ColorStateList.valueOf(RED));


                break;
            case 13:

                //cannot decline / accept
                //cannot chat
                //cannot negotiate

                statusContent.setTextColor(RED);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);

                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));

                negotiation.setVisibility(View.VISIBLE);
                negotiation.setText("Review");
                negotiation.setOnClickListener(v -> goToReview(job));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.red));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3. setImageTintList(ColorStateList.valueOf(RED));


                break;

            //negotiating
            case 3:
                //can chat
                //can accept / decline
                // cannot negotiate button

                negotiation.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);

                accept.setVisibility(View.VISIBLE);
                accept.setOnClickListener(v -> confirmDialog("Do you want to accept this job for " + job.getJob_price_range() + " KSH ?", job, job12 -> {
                    updatePrice(job12, job12.getJob_price_range(), null, JobStatus.ACCEPTED.code);
                    return null;
                }));

                decline.setVisibility(View.VISIBLE);
                decline.setOnClickListener(v -> confirmDialog("Do you want to decline this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.DECLINED);
                    return null;
                }));

                if (job.getJob_price_range() != null) {
                    priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }

                circle1.setImageResource(R.drawable.circle_day_bg_selected);


                break;

            //Ready
            case 4:

                //cannot accept / decline
                //can cancel
                //can chat
                //cannot negotiate / button

                negotiation.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);

                accept.setVisibility(View.VISIBLE);
                accept.setText("Start Job");
                accept.setOnClickListener(v -> confirmDialog("Do you want to start this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.PROGRESS);
                    return null;
                }));

                //cancel
                decline.setVisibility(View.VISIBLE);
                decline.setText("Cancel");
                decline.setOnClickListener(v3 -> decline.setOnClickListener(v4 -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED);
                    return null;
                })));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);


                break;

            //Progress
            case 5:

                //can cancel // complete
                //cannot accept / decline
                //can chat
                // cannot negotiate / button

                negotiation.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);

                accept.setVisibility(View.VISIBLE);
                accept.setText("Complete");
                accept.setOnClickListener(v3 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, SERVICE_COMPLETE);
                    return null;
                }));


                //cancel
                decline.setVisibility(View.VISIBLE);
                decline.setText("Abort");
                decline.setOnClickListener(v5 -> confirmDialog("Do you want to abort this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_CANCELLED_IN_PROGRESS);
                    return null;
                }));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);

                break;

            //Completed
            case 6:
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                circle5.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));
                break;

            //SERVICE_COMPLETE
            case 8:
                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                //report
                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));


                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));


                break;
            case 16:
            case 10:

                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                //report
                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));

                //review
                negotiation.setVisibility(View.VISIBLE);
                negotiation.setText("Review");
                negotiation.setOnClickListener(v -> goToReview(job));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));


                break;

            //CLIENT_COMPLETE // i want to complete or complain
            case 9:


                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_REPORTED);
                    return null;
                }));

                accept.setText("Confirm Completion");
                accept.setVisibility(View.VISIBLE);
                accept.setOnClickListener(v5 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.PAYING);
                    return null;
                }));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));

                break;

            case 17:

                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getClient_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_REPORTED);
                    return null;
                }));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));

                break;

            //SERVICE_RATING
            case 11:
                //todo i service have rated
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));

                break;

            //SERVICE_REPORTED
            case 14:
                //todo i service have reported
                //todo show complains

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageTintList(ColorStateList.valueOf(RED));

                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.red));

                break;

            //CLIENT_REPORTED
            case 15:
                //todo i service have been reported
                //todo show complains

                statusContent.setTextColor(RED);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                negotiation.setText("Review");
                negotiation.setVisibility(View.VISIBLE);
                negotiation.setOnClickListener(v -> goToReview(job));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageTintList(ColorStateList.valueOf(RED));

                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.red));

                break;

        }

    }

    @SuppressLint("SetTextI18n")
    private void setClient(Models.Job job) {

        negotiation.setVisibility(View.GONE);
        chat.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        accept.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);
        providerLayout.setVisibility(View.VISIBLE);




        chat.setOnClickListener(v -> goToChat(job));

        edit.setOnClickListener(v -> editSingleValue(InputType.TYPE_CLASS_NUMBER, "Enter new price", JobTrackActivity.this, price -> {
            updatePrice(job, null, price, null);
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

                decline.setVisibility(View.VISIBLE);
                decline.setText("Cancel");
                decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED);
                    return null;
                }));

                accept.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);

                negotiation.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);

                if (job.getJob_price_range() != null) {
                    priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }


                circle1.setImageResource(R.drawable.circle_day_bg_selected);



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

                edit.setVisibility(View.GONE);
                negotiation.setVisibility(View.GONE);

                decline.setVisibility(View.VISIBLE);
                decline.setText("Cancel");
                decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.SERVICE_CANCELLED_IN_PROGRESS);
                    return null;
                }));

                accept.setVisibility(View.VISIBLE);
                accept.setText("READY TO START");
                accept.setOnClickListener(v -> confirmDialog("Do you want to start this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.READY);
                    return null;
                }));

                chat.setVisibility(View.VISIBLE);

                circle1.setImageResource(R.drawable.circle_day_bg_selected);

                break;

            //DECLINED // CANCELLED //
            case 2:
                //todo send to someone else
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle1.setImageTintList(ColorStateList.valueOf(RED));
                break;
            case 7:
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageTintList(ColorStateList.valueOf(RED));
                break;

            //SERVICE_CANCELLED_IN_PROGRESS //CLIENT_CANCELLED_IN_PROGRESS
            case 12:

                statusContent.setTextColor(RED);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                //just a red bg

                completedAtContent.setVisibility(View.VISIBLE);
                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);

                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));

                negotiation.setVisibility(View.VISIBLE);
                negotiation.setText("Review");
                negotiation.setOnClickListener(v -> goToReview(job));


                if (job.getJob_price_range() != null) {
                    priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.red));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3. setImageTintList(ColorStateList.valueOf(RED));



                break;

            //I cancelled - ... client
            case 13:

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.red));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3. setImageTintList(ColorStateList.valueOf(RED));

                //NEGOTIATING
            case 3:
                //can cancel
                //can chat
                //cannot negotiate button
                //cannot accept / decline
                //can edit price

                //todo price change from chat

                decline.setVisibility(View.VISIBLE);
                decline.setText("Cancel");
                decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED);
                    return null;
                }));
                chat.setVisibility(View.VISIBLE);
                providerLayout.setVisibility(View.VISIBLE);
                negotiation.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);

                edit.setVisibility(View.VISIBLE);

                if (job.getJob_price_range() != null) {
                    priceContent.setText(getBoldSpannable("Suggested Price : ", String.valueOf(job.getJob_price_range())));
                }

                circle1.setImageResource(R.drawable.circle_day_bg_selected);

                break;

            //READY
            case 4:

                //can chat
                //can cancel
                //cannot negotiate
                //cannot edit price
                //cannot accept / decline

                chat.setVisibility(View.VISIBLE);

                decline.setVisibility(View.VISIBLE);
                decline.setText("Cancel");
                decline.setOnClickListener(v -> confirmDialog("Do you want to cancel this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CANCELLED);
                    return null;
                }));

                negotiation.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);

                accept.setVisibility(View.GONE);

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);

                break;

            //PROGRESS
            case 5:
                //can cancel
                //can complete
                //cannot negotiate
                //cannot edit

                negotiation.setVisibility(View.GONE);
                chat.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);


                accept.setVisibility(View.VISIBLE);
                accept.setText("Complete");
                accept.setOnClickListener(v3 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_COMPLETE);
                    return null;
                }));


                //cancel
                decline.setVisibility(View.VISIBLE);
                decline.setText("Abort");
                decline.setOnClickListener(v5 -> confirmDialog("Do you want to abort this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_CANCELLED_IN_PROGRESS);
                    return null;
                }));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);

                break;

            //COMPLETED
            case 6:
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                circle5.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));

                break;

            //SERVICE_COMPLETE
            case 8:
                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));

                accept.setText("Confirm Completion");
                accept.setVisibility(View.VISIBLE);
                accept.setOnClickListener(v5 -> confirmDialog("Do you want to complete this job ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.PAYING);
                    return null;
                }));


                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));


                break;
            //CLIENT_COMPLETE //i have completed
            case 9:
            case 17:
                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));

                negotiation.setVisibility(View.VISIBLE);
                negotiation.setText("Pay Now \n " + job.getJob_price() + " Ksh");
                negotiation.setOnClickListener(v -> confirmDialog("Do you want to pay " + job.getJob_price() + " ?", job, job12 -> {
                    sendPaymentRequest(job12);
                    return null;
                }));

                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));

                break;

            case 16:
            case 11:
                //SERVICE_RATING

                //review
                //complain

                statusContent.setTextColor(Color.GREEN);
                statusContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                chat.setImageResource(R.drawable.ic_customer_complaint);
                chat.setVisibility(View.VISIBLE);
                chat.setOnClickListener(v -> confirmDialog("Do you want to report " + job.getLocal_service_provider_username() + " ?", job, job12 -> {
                    changeJobStatus(job12, JobStatus.CLIENT_REPORTED);
                    return null;
                }));

                negotiation.setVisibility(View.VISIBLE);
                negotiation.setText("Review");
                negotiation.setOnClickListener(v -> goToReview(job));


                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));

                break;

            //CLIENT_RATING
            case 10:
                //todo i have rated
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle4.setImageResource(R.drawable.circle_day_bg_selected);
                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.purple));
                line4.setBackgroundColor(getColor(R.color.purple));
                break;


            //SERVICE_REPORTED
            case 14:
                //todo I client have been reported
                //todo show on problems
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageTintList(ColorStateList.valueOf(RED));

                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.red));
                break;

            //CLIENT_REPORTED
            case 15:
                //todo I client have reported
                //todo show on problems
                circle1.setImageResource(R.drawable.circle_day_bg_selected);
                circle2.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageResource(R.drawable.circle_day_bg_selected);
                circle3.setImageTintList(ColorStateList.valueOf(RED));

                line1.setBackgroundColor(getColor(R.color.purple));
                line2.setBackgroundColor(getColor(R.color.purple));
                line3.setBackgroundColor(getColor(R.color.red));
                break;
        }

    }

    private void goToChat(Models.Job job) {
        startActivity(new Intent(JobTrackActivity.this, ChatActivity.class).putExtra(JOB, job));
    }

    private void goToReview(Models.Job job) {
        startActivity(new Intent(JobTrackActivity.this, ReviewActivity.class).putExtra(JOB, job));
    }

    private void goToReport(Models.Job job) {
        startActivity(new Intent(JobTrackActivity.this, ReviewActivity.class).putExtra(JOB, job).putExtra(REPORTED, true));
    }

    private void refreshJob() {
        jobViewModel.getAJob(job.getId()).observe(this, jsonResponse -> {
            try {
                if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                    Toast.makeText(JobTrackActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject userJson = new JsonArray(getObjectMapper().writeValueAsString(jsonResponse.get().getData())).getJsonObject(0);

                //save user to offline db
                Models.Job updatedJob = getObjectMapper().readValue(userJson.toString(), Models.Job.class);
                setData(updatedJob);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void confirmDialog(String info, Models.Job job, Function<Models.Job, Void> function) {
        Dialog d = new Dialog(JobTrackActivity.this);
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

    private void changeJobStatus(Models.Job job, JobStatus status) {
        Models.JobUpdateForm form = new Models.JobUpdateForm(status.getCode());
        form.setCompleted_at(status == JobStatus.COMPLETED || status == JobStatus.DECLINED || status == JobStatus.CANCELLED);

        if (status == SERVICE_REPORTED || status == CLIENT_REPORTED) {
            form.setReported(true);
        }

        jobViewModel.updateJob(job.getId(), form).observe(this, jsonResponse -> {
            try {
                if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                    Toast.makeText(JobTrackActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    return;
                }


                //save user to offline db
                refreshJob();

                if (status == SERVICE_REPORTED || status == CLIENT_REPORTED) {
                    goToReport(job);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendPaymentRequest(Models.Job job) {

        Models.MakeStkRequest request = new Models.MakeStkRequest(GlobalVariables.transactionType, String.valueOf(Math.round(job.getJob_price().doubleValue())).split("\\.")[0], me.getPhone_number().replace("+", ""), me.getPhone_number().replace("+", ""), job.getId(), "Payment of " + job.getJob_price() + " for job " + job.getId());

        jobViewModel.makePayment(request).observe(this, success -> {
            if (!success.isPresent()) {
                Toast.makeText(JobTrackActivity.this, "Failed to send prompt", Toast.LENGTH_SHORT).show();
                return;
            }

            if (success.get()) {
                Toast.makeText(JobTrackActivity.this, "Successfully initiated payment", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(JobTrackActivity.this, "Failed to send prompt", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updatePrice(Models.Job job, String price, String priceRange, Integer status) {
        jobViewModel.updateJob(job.getId(), new Models.JobUpdateForm(price, priceRange, status)).observe(this, jsonResponse -> {
            try {
                if (!jsonResponse.isPresent() || jsonResponse.get().getData() == null || !jsonResponse.get().isSuccess() || jsonResponse.get().isHas_error()) {
                    Toast.makeText(JobTrackActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    return;
                }


                refreshJob();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private float jobInfoAlpha(int verticalOffset, int max) {
        float maxAlpha = 1.0f;

        float alpha = (verticalOffset * maxAlpha) / max;

        float reverseAlpha = Math.abs(maxAlpha - alpha);

        System.out.println("alpha is " + reverseAlpha);

        return reverseAlpha;
    }
}