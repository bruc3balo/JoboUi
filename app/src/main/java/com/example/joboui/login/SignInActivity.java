package com.example.joboui.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.joboui.R;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivitySignInBinding;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;

public class SignInActivity extends AppCompatActivity {

   /* private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            Domain.User user = userRepository.getUser(firebaseAuth.getUid());
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
    };*/

    private ActivitySignInBinding activity_sign_in;
    private Models.UsernameAndPasswordAuthenticationRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_sign_in = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(activity_sign_in.getRoot());

        EditText usernameF = activity_sign_in.usernameF;
        EditText passwordF = activity_sign_in.passwordF;


        Button signInUserButton = activity_sign_in.signInUserButton;
        signInUserButton.setOnClickListener(view -> {
            if (validateForm(usernameF,passwordF)) {
                postSignInRequest();
            }
        });

        setWindowColors();

        hidePb();
    }

    public boolean validateForm (EditText usernameF,EditText passwordF) {
        boolean valid = false;;
        if (usernameF.getText().toString().isEmpty()) {
            usernameF.setError("required");
            usernameF.requestFocus();
        } else if (passwordF.getText().toString().isEmpty()) {
            passwordF.setError("required");
            passwordF.requestFocus();
        } else {
            request = new Models.UsernameAndPasswordAuthenticationRequest(usernameF.getText().toString(),passwordF.getText().toString());
            valid = true;
        }
        return valid;
    }

    private void postSignInRequest() {
        Toast.makeText(this, "Sign in ", Toast.LENGTH_SHORT).show();
        showPb();

        new Handler().postDelayed(this::hidePb,2500);
    }

    private void showPb () {
        activity_sign_in.signInUserButton.setEnabled(false);
        activity_sign_in.signInPb.setVisibility(View.VISIBLE);
    }
    private void hidePb () {
        activity_sign_in.signInPb.setVisibility(View.GONE);
        activity_sign_in.signInUserButton.setEnabled(true);

    }

    /*private void signInUser(String phoneNumber) {

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
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    private void addListener() {
       // FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void removeListener() {
       // FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    private void proceed(String role) {
        switch (role) {
            case "ROLE_CLIENT":
                goToClientPage();
                break;
            case "ROLE_SERVICE_PROVIDER":
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