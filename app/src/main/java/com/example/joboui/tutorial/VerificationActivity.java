package com.example.joboui.tutorial;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.admin.AdminActivity.logout;
import static com.example.joboui.globals.GlobalDb.userApi;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.LoginActivity.proceed;
import static com.example.joboui.login.SignInActivity.getObjectMapper;
import static com.example.joboui.utils.DataOps.getAuthorization;
import static com.example.joboui.utils.DataOps.getDomainUserFromModelUser;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityTutorialBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private ActivityTutorialBinding tutorialBinding;
    private Domain.User user;
    private boolean requestSent = false;
    private UserViewModel userViewModel;
    private final ArrayList<String> phoneNumberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialBinding = ActivityTutorialBinding.inflate(getLayoutInflater());
        setContentView(tutorialBinding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        LinearLayout otpLayout = tutorialBinding.optLayout;
        otpLayout.setVisibility(View.GONE);

        Button sendOPT = tutorialBinding.sendOPT;
        sendOPT.setOnClickListener(v -> isSafetyNetEnabled());


        Button changeNumber = tutorialBinding.changeNumber;
        changeNumber.setOnClickListener(v -> showDialogNumber());

        Button checkStatusB = tutorialBinding.checkStatusB;
        checkStatusB.setOnClickListener(v -> checkStatus());

        Button logoutB = tutorialBinding.logoutB;
        logoutB.setOnClickListener(v -> logout(getApplication()));

        userRepository.getUserLive().observe(this, user -> {
            if (!user.isPresent()) {
                Toast.makeText(VerificationActivity.this, "User account not preset", Toast.LENGTH_SHORT).show();
                logout(getApplication());
                return;
            }

            this.user = user.get();

            if (!requestSent) {
                checkStatus();
            }

        });

        hidePb();
        setWindowColors();
        populatePhoneNumberList();
    }


    private void populatePhoneNumberList() {
        userViewModel.getAllPhoneNumbers().observe(this, numbers -> {
            phoneNumberList.clear();
            phoneNumberList.addAll(numbers);
            System.out.println("NUMBERS " + phoneNumberList.toString());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
        addLogoutListener(VerificationActivity.this);
        try {
            Thread.sleep(3000);
            checkStatus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void sendOTP() {
        showPb();
        long TIME_OUT = 60L * 1000;


        CountDownTimer timer = new CountDownTimer(TIME_OUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tutorialBinding.timeOut.setIndeterminate(false);
                tutorialBinding.timeOut.setProgress(getReverseProgress(millisUntilFinished, TIME_OUT));
                tutorialBinding.confirmOTP.setRotation(getRotation(millisUntilFinished, TIME_OUT));
                if (millisUntilFinished == 0) {
                    timeOut(true);
                }
            }

            @Override
            public void onFinish() {
                hidePb();
            }
        };

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("en");

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(user.getPhone_number())       // Phone number to verify
                .setTimeout(TIME_OUT, TimeUnit.MILLISECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        verifyUser();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        tutorialBinding.optLayout.setVisibility(View.GONE);
                        hidePb();
                        Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        timer.start();
                        enterPin(verificationId, timer);
                        hidePb();
                        Toast.makeText(VerificationActivity.this, "Code send to " + user.getPhone_number(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                })          // OnVerificationStateChangedCallbacks
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyUser() {
        showPb();
        userViewModel.updateExistingUser(new Models.UserUpdateForm(null, true)).observe(VerificationActivity.this, user -> {
            hidePb();
            if (!user.isPresent()) {
                Toast.makeText(VerificationActivity.this, "Failed to update verification of number", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(VerificationActivity.this, "Verified", Toast.LENGTH_SHORT).show();
            checkStatus();
        });
    }

    private void isSafetyNetEnabled() {

        SafetyNet.getClient(this)
                .isVerifyAppsEnabled()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                        if (result.isVerifyAppsEnabled()) {
                            Log.d("MY_APP_TAG", "The Verify Apps feature is enabled.");
                            sendOTP();
                        } else {
                            Log.d("MY_APP_TAG", "The Verify Apps feature is disabled.");
                            enableSafetyNet();
                        }
                    } else {
                        Log.e("MY_APP_TAG", "A general error occurred.");
                    }
                });

    }

    private void enableSafetyNet() {
        SafetyNet.getClient(this)
                .enableVerifyApps()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                        if (result.isVerifyAppsEnabled()) {
                            Log.d("MY_APP_TAG", "The user gave consent " + "to enable the Verify Apps feature.");
                            sendOTP();
                        } else {
                            Log.d("MY_APP_TAG", "The user didn't give consent " + "to enable the Verify Apps feature.");
                        }
                    } else {
                        Log.e("MY_APP_TAG", "A general error occurred.");
                    }
                });
    }

    private int getReverseProgress(long millisUntilFinished, long totalTime) {
        int percentage = (int) ((millisUntilFinished * 100) / totalTime);
        System.out.println("TIMEOUT " + percentage + "%");
        return percentage;
    }

    private int getRotation(long millisUntilFinished, long totalTime) {
        int rotation = (getReverseProgress(millisUntilFinished,totalTime) * 360) / 100;
        System.out.println("ROTATION " + rotation + "%");
        return rotation;
    }

    private void enterPin(String verificationId, CountDownTimer timer) {
        tutorialBinding.optLayout.setVisibility(View.VISIBLE);
        tutorialBinding.timeOut.setIndeterminate(false);
        EditText otp = tutorialBinding.otp;
        tutorialBinding.confirmOTP.setOnClickListener(v -> {
            if (otp.getText().toString().isEmpty()) {
                otp.setError("code required. check sms");
                otp.requestFocus();
            } else {
                showPb();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.getText().toString());
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
                    hidePb();
                    timer.cancel();
                    if (task.isSuccessful()) {
                        Toast.makeText(VerificationActivity.this, "Successfully verified", Toast.LENGTH_SHORT).show();
                        verifyUser();
                    } else {
                        Toast.makeText(VerificationActivity.this, "Failed to verify", Toast.LENGTH_SHORT).show();
                        timeOut(false);
                    }
                });
            }
        });
    }

    private void timeOut(boolean timedOut) {
        tutorialBinding.optLayout.setVisibility(View.GONE);
        tutorialBinding.timeOut.setProgress(0);
        tutorialBinding.timeOut.setSecondaryProgress(0);
        if (timedOut) {
            tutorialBinding.sendOPT.setText("Your OTP has expired \n Click to get a new one");
        }
        hidePb();
    }

    private void checkStatus() {
        if (user != null) {

            requestSent = true;

            showPb();

            userApi.getUsers(user.getUsername(), getAuthorization()).enqueue(new Callback<JsonResponse>() {
                @Override
                public void onResponse(@NonNull Call<JsonResponse> call, @NonNull Response<JsonResponse> response) {
                    hidePb();

                    JsonResponse jsonResponse = response.body();

                    if (jsonResponse == null) {
                        Toast.makeText(VerificationActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (jsonResponse.isHas_error() && !jsonResponse.isSuccess()) {
                        Toast.makeText(VerificationActivity.this, "Error getting verification status", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (jsonResponse.getData() == null) {
                        Toast.makeText(VerificationActivity.this, "User info not found", Toast.LENGTH_SHORT).show();
                        logout(getApplication());
                        return;
                    }

                    ObjectMapper mapper = getObjectMapper();

                    try {

                        JsonObject userJson = new JsonArray(mapper.writeValueAsString(jsonResponse.getData())).getJsonObject(0);


                        //save user to offline db
                        Models.AppUser apiUser = mapper.readValue(userJson.toString(), Models.AppUser.class);

                        userRepository.insert(getDomainUserFromModelUser(apiUser));

                        Thread.sleep(2000);

                        if (apiUser.getVerified()) {
                            proceed(VerificationActivity.this);
                        } else {
                            Toast.makeText(VerificationActivity.this, "You have not verified your phone number", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JsonProcessingException | InterruptedException e) {

                        if (e instanceof JsonProcessingException) {
                            Toast.makeText(VerificationActivity.this, "Problem mapping user data", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonResponse> call, @NonNull Throwable t) {
                    Toast.makeText(VerificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    hidePb();
                }
            });
        }
    }

    private void showDialogNumber() {
        Dialog d = new Dialog(VerificationActivity.this);
        d.setContentView(R.layout.number_dialog);
        d.show();

        EditText phoneNumberField = d.findViewById(R.id.phoneNumberField);
        Button confirm_button = d.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumberField.getText().toString().isEmpty()) {
                    phoneNumberField.setError("Required");
                    phoneNumberField.requestFocus();
                } else if (!phoneNumberField.getText().toString().startsWith("+254")) {
                    phoneNumberField.setError("Must start with +254");
                    phoneNumberField.setText("+254");
                    phoneNumberField.setSelection(4);
                    phoneNumberField.requestFocus();
                } else if (phoneNumberField.getText().toString().length() < 12) {
                    phoneNumberField.setError("Invalid phone number");
                    phoneNumberField.requestFocus();
                } else if (phoneNumberList.contains(phoneNumberField.getText().toString()) || phoneNumberList.contains(phoneNumberField.getText().toString().replace("+", ""))) {
                    phoneNumberField.setError("Phone number already added");
                    phoneNumberField.requestFocus();
                } else {
                    changePhoneNumber(phoneNumberField.getText().toString());
                    d.dismiss();
                }
            }
        });

    }

    private void changePhoneNumber(String phoneNumber) {
        showPb();
        userViewModel.updateExistingUser(new Models.UserUpdateForm(phoneNumber, null)).observe(this, user -> {
            hidePb();
            if (!user.isPresent()) {
                Toast.makeText(VerificationActivity.this, "Failed to change phone number", Toast.LENGTH_SHORT).show();
                return;
            }


            tutorialBinding.changeNumber.setText("Number changed to " + user.get().getPhone_number() + " \n click to change again");
        });
    }

    private void showPb() {
        tutorialBinding.sendOPT.setEnabled(false);
        tutorialBinding.timeOut.setEnabled(false);
        tutorialBinding.confirmOTP.setEnabled(false);
        tutorialBinding.changeNumber.setEnabled(false);
        tutorialBinding.checkStatusB.setEnabled(false);
        tutorialBinding.tutorialPb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
        tutorialBinding.tutorialPb.setVisibility(View.GONE);
        tutorialBinding.sendOPT.setEnabled(true);
        tutorialBinding.timeOut.setEnabled(true);
        tutorialBinding.confirmOTP.setEnabled(true);
        tutorialBinding.changeNumber.setEnabled(true);
        tutorialBinding.checkStatusB.setEnabled(true);
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.white));
        getWindow().setNavigationBarColor(getColor(R.color.white));
    }

}