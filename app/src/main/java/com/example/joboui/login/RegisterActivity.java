package com.example.joboui.login;

import static com.example.joboui.globals.GlobalDb.userRepository;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityRegisterBinding;
import com.example.joboui.databinding.RoleDialogBinding;
import com.example.joboui.model.Models;
import com.example.joboui.tutorial.TutorialActivity;
import com.example.joboui.utils.AppRolesEnum;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegister;
    private String role = "";
    //private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
   // private SafetyNetClient safetyNetClient;
    private final ArrayList<String> phoneNumberList = new ArrayList<>();
    private Models.NewUserForm newUserForm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegister = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegister.getRoot());

        Button registerUserButton = activityRegister.registerUserButton;

        EditText namesField = activityRegister.namesField;
        EditText userNameField = activityRegister.usernameField;
        EditText emailAddressField = activityRegister.emailAddressField;
        EditText passwordF = activityRegister.passwordF;
        EditText cPasswordF = activityRegister.cPasswordF;
        EditText phoneNumberField = activityRegister.phoneNumberField;

        registerUserButton.setOnClickListener(view -> {
            if (validateForm(namesField, userNameField,emailAddressField, passwordF, cPasswordF, phoneNumberField)) {
                showRoleDialog();
            }
        });


        setWindowColors();

        populatePhoneNumberList();

        hidePb();
    }


    private boolean validateForm(EditText namesField, EditText usernameField,EditText emailAddressField, EditText passwordF, EditText cPasswordF, EditText phoneNumberField) {
        boolean valid = false;

        if (namesField.getText().toString().isEmpty()) {
            namesField.setError("required");
            namesField.requestFocus();
        } else if (emailAddressField.getText().toString().isEmpty()) {
            emailAddressField.setError("required");
            emailAddressField.requestFocus();
        } else if (!emailAddressField.getText().toString().contains("@")) {
            emailAddressField.setError("Invalid email format");
            passwordF.requestFocus();
        } else if (passwordF.getText().toString().length() < 6) {
            passwordF.setError("Min characters 6");
            passwordF.requestFocus();
        } else if (!cPasswordF.getText().toString().equals(passwordF.getText().toString())) {
            cPasswordF.setError("Passwords don't match");
            cPasswordF.requestFocus();
        } else if (phoneNumberField.getText().toString().isEmpty()) {
            phoneNumberField.setError("Required");
            phoneNumberField.requestFocus();
        } else if (!phoneNumberField.getText().toString().startsWith("+254")) {
            phoneNumberField.setError("Must start with +254");
            phoneNumberField.setText("+254");
            phoneNumberField.requestFocus();
        } else if (phoneNumberField.getText().toString().length() < 12) {
            phoneNumberField.setError("Invalid phone number");
            phoneNumberField.requestFocus();
        } else if (phoneNumberList.contains(phoneNumberField.getText().toString())) {
            phoneNumberField.setError("Already added. Sign in");
            phoneNumberField.requestFocus();
        } else {
            //initPhoneVerification(phoneNumberField.getText().toString());String names, String username, String emailAddress, String password, String phone_number
            newUserForm = new Models.NewUserForm(namesField.getText().toString(),usernameField.getText().toString(),emailAddressField.getText().toString(),cPasswordF.getText().toString(),phoneNumberField.getText().toString());
            valid = true;
        }

        return valid;
    }


    private void populatePhoneNumberList() {
        userRepository.getMobileNumbers().observe(this, strings -> {
            phoneNumberList.clear();
            phoneNumberList.addAll(strings);
        });
    }


    private void showRoleDialog() {
        Dialog d = new Dialog(RegisterActivity.this);
        RoleDialogBinding roleDialogBinding = RoleDialogBinding.inflate(getLayoutInflater());
        d.setContentView(roleDialogBinding.getRoot());
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);

        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button confirmRoleButton = roleDialogBinding.confirmRoleButton;
        RadioGroup roleRadioGroup = roleDialogBinding.roleRadioGroup;
        Button neverMindButton = roleDialogBinding.cancelButton;

        d.show();

        roleRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == roleDialogBinding.clientRadio.getId()) {
                role = AppRolesEnum.valueOf("ROLE_CLIENT").name();
            } else {
                role = AppRolesEnum.valueOf("ROLE_SERVICE_PROVIDER").name();
            }
            newUserForm.setRole(role);
        });

        confirmRoleButton.setOnClickListener(view -> {
            if (role.equals("")) {
                Toast.makeText(RegisterActivity.this, "Pick a role", Toast.LENGTH_SHORT).show();
            } else {
                d.dismiss();
                sendRegisterRequest();
            }
        });
        neverMindButton.setOnClickListener(view -> d.dismiss());
    }

    private void sendRegisterRequest() {
        Toast.makeText(this, "Sending request", Toast.LENGTH_SHORT).show();
        showPb();

        new Handler().postDelayed(this::hidePb,2500);

        proceed();
    }

    private void showPb () {
        activityRegister.registerUserButton.setEnabled(false);
        activityRegister.registerPb.setVisibility(View.VISIBLE);
    }
    private void hidePb () {
        activityRegister.registerPb.setVisibility(View.GONE);
        activityRegister.registerUserButton.setEnabled(true);

    }

    /*private void initPhoneVerification(String phoneNumber) {

        isSafetyNetEnabled();

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
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    public static int getProgress(long timeRemaining, long timeOut) {
        return Math.toIntExact((timeRemaining * 100) / timeOut);
    }*/

    /* private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
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
    }*/

    /*private void getAdditionalData(FirebaseUser firebaseUser) {
        user.setCreated_at(Calendar.getInstance().getTime().toString());
        user.setId(firebaseUser.getUid());
        user.setPhone_number(firebaseUser.getPhoneNumber());

        saveUserDetails(user);
    }*/

    /*
        private void saveUserDetails(Domain.User user) {
            userRepository.insert(user);
            fireStoreDb.collection(USER_DB).document(user.getUid()).set(user).addOnCompleteListener(task -> {
                if (task.isComplete()) {
                    proceed();
                } else {
                    Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }*/

    private void proceed() {
        switch (role) {
            case "ROLE_CLIENT":
                goToTutorialsPage();
                break;
            case "ROLE_SERVICE_PROVIDER":
                goToAdditionalInfoActivity();
                break;
        }
    }

   /* private void isSafetyNetEnabled() {
        firebaseAuth.setLanguageCode("en");
        safetyNetClient = SafetyNet.getClient(this);

        safetyNetClient.isVerifyAppsEnabled().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                assert result != null;
                if (result.isVerifyAppsEnabled()) {
                    Log.d("MY_APP_TAG", "The Verify Apps feature is enabled.");

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
        safetyNetClient.enableVerifyApps().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SafetyNetApi.VerifyAppsUserResponse result = task.getResult();
                assert result != null;
                if (result.isVerifyAppsEnabled()) {
                    Log.d("REGISTRATION", "The user gave consent " + "to enable the Verify Apps feature.");
                } else {
                    Log.d("REGISTRATION", "The user didn't give consent " + "to enable the Verify Apps feature.");
                }
            } else {
                Log.e("REGISTRATION", "A general error occurred.");
            }
        });
    }
*/
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