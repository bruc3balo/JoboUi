package com.example.joboui.login;

import static android.content.ContentValues.TAG;
import static com.example.joboui.globals.GlobalDb.fireStoreDb;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.CLIENT;
import static com.example.joboui.globals.GlobalVariables.CLIENT_ROLE;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_ROLE;
import static com.example.joboui.globals.GlobalVariables.SERVICE_PROVIDER;
import static com.example.joboui.globals.GlobalVariables.USER_DB;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.databinding.CodeDialogBinding;
import com.example.joboui.databinding.RoleDialogBinding;
import com.example.joboui.models.Models;
import com.example.joboui.tutorial.TutorialActivity;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegister;
    private int role = 0;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private SafetyNetClient safetyNetClient;
    private boolean safetyNetEnabled = false;
    private final Models.User user = new Models.User();
    private final ArrayList<String> phoneNumberList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegister = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegister.getRoot());

        Button registerUserButton = activityRegister.registerUserButton;
        //registerUserButton.setOnClickListener(view -> showRoleDialog());


        EditText phoneNumberField = activityRegister.phoneNumberField;
        registerUserButton.setOnClickListener(view -> {
            if (phoneNumberField.getText().toString().isEmpty()) {
                phoneNumberField.setError("Required");
                phoneNumberField.requestFocus();
            } else if (!phoneNumberField.getText().toString().startsWith("+254")) {
                phoneNumberField.setError("Must start with +254");
                phoneNumberField.setText("+254");
                phoneNumberField.requestFocus();
            } else if (phoneNumberField.getText().toString().length() != 13) {
                phoneNumberField.setError("Invalid phone number");
                phoneNumberField.requestFocus();
            } else if (phoneNumberList.contains(phoneNumberField.getText().toString())) {
                phoneNumberField.setError("Already added. Sign in");
                phoneNumberField.requestFocus();
            } else {
                initPhoneVerification(phoneNumberField.getText().toString());
            }
        });

        isSafetyNetEnabled();

        setWindowColors();

        populatePhoneNumberList();
    }


    private void populatePhoneNumberList () {
        userRepository.getMobileNumbers().observe(this, strings -> {
            phoneNumberList.clear();
            phoneNumberList.addAll(strings);
        });
    }


    private void showRoleDialog(FirebaseUser firebaseUser) {
        Dialog d = new Dialog(RegisterActivity.this);
        RoleDialogBinding roleDialogBinding = RoleDialogBinding.inflate(getLayoutInflater());
        d.setContentView(roleDialogBinding.getRoot());
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button confirmRoleButton = roleDialogBinding.confirmRoleButton;
        RadioGroup roleRadioGroup = roleDialogBinding.roleRadioGroup;
        ImageButton neverMindButton = roleDialogBinding.neverMindButton;

        EditText fName = roleDialogBinding.fName;
        EditText sName = roleDialogBinding.sName;

        d.show();

        roleRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == roleDialogBinding.clientRadio.getId()) {
                role = CLIENT_ROLE;
            } else {
                role = LOCAL_SERVICE_PROVIDER_ROLE;
            }
            user.setRole(role);
        });

        confirmRoleButton.setOnClickListener(view -> {
            if (role == 0) {
                Toast.makeText(RegisterActivity.this, "Pick a role", Toast.LENGTH_SHORT).show();
            } else if (fName.getText().toString().isEmpty()) {
                fName.setError("First name required");
                fName.requestFocus();
            } else if (sName.getText().toString().isEmpty()) {
                sName.setError("Second name required");
                sName.requestFocus();
            } else {
                user.setFirstName(fName.getText().toString());
                user.setLastName(sName.getText().toString());
                user.setRole(role);
                d.dismiss();
                getAdditionalData(firebaseUser);
            }
        });
        neverMindButton.setOnClickListener(view -> d.dismiss());
    }

    private void initPhoneVerification(String phoneNumber) {

        final boolean[] countDownTimerShowing = {false};
        final String[] verificationId = {""};

        long TIME_OUT = 2 * 1000 * 60;
        long INTERVAL_TIME = 1000;

        Dialog d = new Dialog(RegisterActivity.this);
        CodeDialogBinding codeDialogBinding = CodeDialogBinding.inflate(getLayoutInflater());
        d.setContentView(codeDialogBinding.getRoot());
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);


        ProgressBar progressBar = codeDialogBinding.progressCountDown;
        progressBar.setIndeterminate(false);


        EditText codeField = codeDialogBinding.codeField;
        Button confirmB = codeDialogBinding.confirmCodeButton;
        confirmB.setOnClickListener(view -> {
            if (codeField.getText().toString().isEmpty()) {
                codeField.setError("Required");
                codeField.requestFocus();
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId[0], codeField.getText().toString());
                signInWithPhoneAuthCredential(credential);
                System.out.println("SIGN IN FROM CODE");
            }
        });
        ImageButton cancel = codeDialogBinding.neverMindButton;
        cancel.setOnClickListener(view -> d.dismiss());

        CountDownTimer countDownTimer = new CountDownTimer(TIME_OUT, INTERVAL_TIME) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long l) {
                countDownTimerShowing[0] = true;
                if (d.isShowing()) {
                    progressBar.setProgress(getProgress(l, TIME_OUT));
                }
            }

            @Override
            public void onFinish() {
                progressBar.setIndeterminate(true);
                if (d.isShowing()) {
                    d.dismiss();
                }
            }
        }.start();

        d.show();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                System.out.println("PHONE CODE VERIFIED");
                Toast.makeText(RegisterActivity.this, "PHONE CODE VERIFIED", Toast.LENGTH_SHORT).show();
                // System.out.println("SIGN IN FROM CALLBACK");
                // signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (countDownTimerShowing[0]) {
                    countDownTimer.cancel();
                }

                if (d.isShowing()) {
                    d.cancel();
                }

                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationId[0] = s;
                super.onCodeSent(s, forceResendingToken);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                System.out.println("CODE TIMEOUT " + s);

                if (countDownTimerShowing[0]) {
                    countDownTimer.cancel();
                }

                if (d.isShowing()) {
                    d.dismiss();
                }

                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(TIME_OUT, TimeUnit.MILLISECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(phoneCallback)          // OnVerificationStateChangedCallbacks
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getProgress(long timeRemaining, long timeOut) {
        return Math.toIntExact((timeRemaining * 100) / timeOut);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");

                    FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                    System.out.println("User has been signed in with CODE");
                    assert user != null;
                    showRoleDialog(user);

                    // Update UI
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        System.out.println("CODE WAS INVALID");
                    }
                }
            }
        });
    }


    private void getAdditionalData(FirebaseUser firebaseUser) {
        user.setCreatedAt(Calendar.getInstance().getTime().toString());
        user.setUid(firebaseUser.getUid());
        user.setPhoneNumber(firebaseUser.getPhoneNumber());

        saveUserDetails(user);
    }

    private void saveUserDetails(Models.User user) {
        userRepository.insert(user);
        fireStoreDb.collection(USER_DB).document(user.getUid()).set(user).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                proceed();
            } else {
                Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceed() {
        switch (role) {
            case CLIENT_ROLE:
                goToTutorialsPage();
                break;
            case LOCAL_SERVICE_PROVIDER_ROLE:
                goToAdditionalInfoActivity();
                break;
        }
    }

    private void isSafetyNetEnabled() {
        firebaseAuth.setLanguageCode("en");
        safetyNetClient = SafetyNet.getClient(this);

        safetyNetClient.isVerifyAppsEnabled().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                assert result != null;
                if (result.isVerifyAppsEnabled()) {
                    Log.d("MY_APP_TAG", "The Verify Apps feature is enabled.");
                    safetyNetEnabled = true;

                } else {
                    safetyNetEnabled = false;
                    Log.d("MY_APP_TAG", "The Verify Apps feature is disabled.");
                    enableSafetyNet();
                }
            } else {
                Log.e("MY_APP_TAG", "A general error occurred.");
            }
        });
    }

    private void enableSafetyNet() {
        safetyNetClient.enableVerifyApps().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                assert result != null;
                if (result.isVerifyAppsEnabled()) {
                    Log.d("MY_APP_TAG", "The user gave consent " + "to enable the Verify Apps feature.");
                    safetyNetEnabled = true;
                } else {
                    safetyNetEnabled = false;
                    Log.d("MY_APP_TAG", "The user didn't give consent " + "to enable the Verify Apps feature.");
                }
            } else {
                Log.e("MY_APP_TAG", "A general error occurred.");
            }
        });
    }

    private void goToAdditionalInfoActivity() {
        startActivity(new Intent(this, ServiceProviderAdditionalActivity.class));
    }

    private void goToTutorialsPage() {
        startActivity(new Intent(this, TutorialActivity.class));
    }

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple));
        }

    }
}