package com.example.joboui.login;

import static android.content.ContentValues.TAG;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.CLIENT_ROLE;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_ROLE;
import static com.example.joboui.login.RegisterActivity.getProgress;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivitySignInBinding;
import com.example.joboui.databinding.CodeDialogBinding;
import com.example.joboui.models.Models;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding activity_sign_in;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            Models.User user = userRepository.getUser(firebaseAuth.getUid());
            if (user != null) {
                proceed(user.getRole());
            } else {
                user = userRepository.refreshUserDetails(firebaseAuth.getUid());
                if (user != null) {
                    proceed(user.getRole());
                } else {
                    Toast.makeText(SignInActivity.this, "Failed to get user info", Toast.LENGTH_SHORT).show();
                    removeListener();
                }
            }
        } else {
            Toast.makeText(SignInActivity.this, "Sign in to continue", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_sign_in = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(activity_sign_in.getRoot());

        EditText phoneNumberField = activity_sign_in.phoneNumberField;


        Button signInUserButton = activity_sign_in.signInUserButton;
        signInUserButton.setOnClickListener(view -> {
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
            } else {
                signInUser(phoneNumberField.getText().toString());
            }
        });

        setWindowColors();

    }

    private void signInUser(String phoneNumber) {

        final boolean[] countDownTimerShowing = {false};
        final String[] verificationId = {""};

        long TIME_OUT = 2 * 1000 * 60;
        long INTERVAL_TIME = 1000;

        Dialog d = new Dialog(SignInActivity.this);
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
                Toast.makeText(SignInActivity.this, "PHONE CODE VERIFIED", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    // Update UI
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        System.out.println("CODE WAS INVALID");
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    private void addListener() {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void removeListener() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    private void proceed(int role) {
        switch (role) {
            case CLIENT_ROLE:
                goToClientPage();
                break;
            case LOCAL_SERVICE_PROVIDER_ROLE:
                goToServiceProviderPage();
                break;
        }
    }

    private void goToServiceProviderPage() {
        startActivity(new Intent(this, ServiceProviderActivity.class));
        finish();
    }

    private void goToClientPage() {
        startActivity(new Intent(this, ClientActivity.class));
        finish();
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