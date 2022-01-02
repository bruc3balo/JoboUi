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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityTutorialBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.utils.CutCopyPasteEditText;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    private static final int PERMISSION_REQUEST_SEND_SMS = 5;
    private ActivityTutorialBinding tutorialBinding;
    private Domain.User user;
    private boolean requestSent = false;
    private boolean verificationSent = false;
    private UserViewModel userViewModel;
    private final ArrayList<String> phoneNumberList = new ArrayList<>();
    private boolean isLoading = false;
    private String verificationIdS;
    private int RESOLVE_HINT = 13;
    private CountDownTimer timer;
    //TODO SEND verification on phone and notify

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialBinding = ActivityTutorialBinding.inflate(getLayoutInflater());
        setContentView(tutorialBinding.getRoot());

        setWindowColors();

        Toolbar toolbar = tutorialBinding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.more));

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        LinearLayout otpLayout = tutorialBinding.optLayout;


        hidePb();
        populatePhoneNumberList();

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

            toolbar.setSubtitle(user.get().getPhone_number());
            toolbar.setTitle("Verify your phone number " + user.get().getUsername());

            enterPin();

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isLoading) {
            menu.add("Change number").setTitle("Change number").setOnMenuItemClickListener(item -> {
                showDialogNumber();
                return false;
            }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

            menu.add("Logout").setTitle("Logout").setOnMenuItemClickListener(item -> {
                logout(getApplication());
                return false;
            }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return super.onCreateOptionsMenu(menu);
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

        tutorialBinding.countdownTv.setText("We will use your browser to confirm that you are not a robot");

        showPb();
        long TIME_OUT = 120L * 1000;


        timer = new CountDownTimer(TIME_OUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tutorialBinding.infoOpt.setText("Code has been sent");
                tutorialBinding.countdownTv.setText("Didn't receive the code ? Resend in " + calculateTime(millisUntilFinished));
                tutorialBinding.countdownTv.setTextColor(Color.BLACK);

                if (millisUntilFinished == 0) {
                    timeOut(true);
                    tutorialBinding.infoOpt.setText("Timeout");
                    tutorialBinding.countdownTv.setText("Timeout , click to resend code");
                    tutorialBinding.countdownTv.setOnClickListener(v -> isSafetyNetEnabled());
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
                .setActivity(this)
                // Activity (for callback binding)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        verifyUser();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        hidePb();
                        Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        tutorialBinding.infoOpt.setText(e.getLocalizedMessage());
                        tutorialBinding.countdownTv.setText("Failed , click to resend code");
                        tutorialBinding.countdownTv.setTextColor(Color.RED);
                        tutorialBinding.countdownTv.setOnClickListener(v -> isSafetyNetEnabled());
                        e.printStackTrace();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        timer.start();
                        verificationIdS = verificationId;
                        enterPin();
                        hidePb();
                        Toast.makeText(VerificationActivity.this, "Code send to " + user.getPhone_number(), Toast.LENGTH_SHORT).show();
                        tutorialBinding.infoOpt.setText("Code has been sent");
                        tutorialBinding.infoOpt.setTextColor(Color.BLACK);
                        tutorialBinding.countdownTv.setOnClickListener(null);
                        tutorialBinding.countdownTv.setText("Your code has been sent");

                        smsPermissions();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        timeOut(true);
                        tutorialBinding.infoOpt.setTextColor(Color.BLACK);
                        tutorialBinding.infoOpt.setText("Timeout");
                        tutorialBinding.countdownTv.setText("Your code has timed out ... click to resend");
                        tutorialBinding.countdownTv.setOnClickListener(v -> isSafetyNetEnabled());
                    }
                })          // OnVerificationStateChangedCallbacks
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void smsPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(VerificationActivity.this, "Show rationale", Toast.LENGTH_SHORT).show();
                System.out.println("RATIONALLE");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_SEND_SMS);
            }
        } else {
            autoFill();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "SMS permission granted .", Toast.LENGTH_LONG).show();
                autoFill();
            } else {
                Toast.makeText(getApplicationContext(), "SMS permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    private GoogleApiClient getGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, connectionResult -> {

                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }

    private void requestHint(GoogleApiClient apiClient) throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                apiClient, hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }

    // Obtain the phone number from the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }


    private void autoFill() {
        SmsRetrieverClient smsRetrieverClient = SmsRetriever.getClient(this);
        smsRetrieverClient.startSmsRetriever().addOnSuccessListener(unused -> {
            tutorialBinding.infoOpt.setVisibility(View.VISIBLE);
            tutorialBinding.infoOpt.setText("Waiting for sms ... ");
            Toast.makeText(VerificationActivity.this, "Waiting for sms", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            tutorialBinding.infoOpt.setText(e.getLocalizedMessage());
            Toast.makeText(VerificationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        });
    }


    public static String calculateTime(long milliseconds) {

        int day = (int) TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - (day * 24L);
        long minute = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - (TimeUnit.MILLISECONDS.toHours(milliseconds) * 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - (TimeUnit.MILLISECONDS.toMinutes(milliseconds) * 60);

        String time = hours + "hr : " + minute + " : min" + second + " sec";

        if (hours == 0 && minute != 0) {
            return minute + " min " + second + " sec";
        } else if (minute == 0 && hours == 0 && second != 0) {
            return second + " sec";
        } else {
            return time;
        }
    }


    private void verifyUser() {
        tutorialBinding.infoOpt.setText("Verifying");

        showPb();
        userViewModel.updateExistingUser(new Models.UserUpdateForm(null, null, true)).observe(VerificationActivity.this, user -> {

            hidePb();

            if (!user.isPresent()) {
                tutorialBinding.infoOpt.setText("Failed to update verification of number");
                Toast.makeText(VerificationActivity.this, "Failed to update verification of number", Toast.LENGTH_SHORT).show();
                return;
            }

            tutorialBinding.infoOpt.setText("Verified Successfully");

            Toast.makeText(VerificationActivity.this, "Verified", Toast.LENGTH_SHORT).show();
            checkStatus();
        });
    }

    private void isSafetyNetEnabled() {
        tutorialBinding.countdownTv.setText("Sending OTP, Hang on");

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
                            tutorialBinding.infoOpt.setText("Safety net isn't enabled");
                            enableSafetyNet();
                        }
                    } else {
                        Log.e("MY_APP_TAG", "A general error occurred.");
                        tutorialBinding.infoOpt.setText("Safety error");
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
                            Log.d("MY_APP_TAG", "You gave consent to enable the Verify Apps feature.");
                            tutorialBinding.infoOpt.setText("You gave consent to enable the Verify Apps feature.");
                            sendOTP();
                        } else {
                            Log.d("MY_APP_TAG", "You didn't give consent " + "to enable the Verify Apps feature.");
                            tutorialBinding.infoOpt.setText("You didn't give consent to enable the Verify Apps feature.");

                        }
                    } else {
                        Log.e("MY_APP_TAG", "A general error occurred.");
                        tutorialBinding.infoOpt.setText("Safety error");
                    }
                });
    }

    private int getReverseProgress(long millisUntilFinished, long totalTime) {
        int percentage = (int) ((millisUntilFinished * 100) / totalTime);
        System.out.println("TIMEOUT " + percentage + "%");
        return percentage;
    }

    private int getRotation(long millisUntilFinished, long totalTime) {
        int rotation = (getReverseProgress(millisUntilFinished, totalTime) * 360) / 100;
        System.out.println("ROTATION " + rotation + "%");
        return rotation;
    }

    private void enterPin() {

        boolean pasted1 = false;

        CutCopyPasteEditText otp1 = tutorialBinding.code1;
        CutCopyPasteEditText otp2 = tutorialBinding.code2;
        CutCopyPasteEditText otp3 = tutorialBinding.code3;
        CutCopyPasteEditText otp4 = tutorialBinding.code4;
        CutCopyPasteEditText otp5 = tutorialBinding.code5;
        CutCopyPasteEditText otp6 = tutorialBinding.code6;

        CutCopyPasteEditText[] list = new CutCopyPasteEditText[]{
                otp1,
                otp2,
                otp3,
                otp4,
                otp5,
                otp6
        };


        TextWatcher codeWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code1 = Objects.requireNonNull(otp1.getText()).toString();
                int length = code1.length();

                System.out.println("CODE 1 " + code1);

                if (length > 0) {
                    String current = String.valueOf(code1.charAt(0));

                    System.out.println("CODE 1 CURRENT " + current);


                    if (length > 1) {

                        System.out.println("CODE REST " + code1);

                        for (int i = 0; i < code1.length(); i++) {
                            System.out.println("CODE DIST " + i +" " + code1.charAt(i));
                            list[i].setText(String.valueOf(code1.charAt(i)));
                        }

                        otp1.setError("only 1 digit required");
                    }
                }

                if (otp1.getText().toString().length() != 0) {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher codeWatcher2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code1 = Objects.requireNonNull(otp2.getText()).toString();
                int length = code1.length();

                if (length > 0) {
                    String current = String.valueOf(code1.charAt(0));
                    if (length > 1) {
                        String toSend = code1.substring(1);
                        otp2.setError("only 1 digit required");
                        //otp3.setText(toSend);
                    }
                }

                if (otp2.getText().toString().length() != 0) {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher codeWatcher3 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code1 = Objects.requireNonNull(otp3.getText()).toString();
                int length = code1.length();

                if (length > 0) {
                    String current = String.valueOf(code1.charAt(0));
                    if (length > 1) {
                        String toSend = code1.substring(1);
                        otp3.setError("only 1 digit required");
                        //otp4.setText(toSend);
                    }
                }

                if (otp3.getText().toString().length() != 0) {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher codeWatcher4 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code1 = Objects.requireNonNull(otp4.getText()).toString();
                int length = code1.length();

                if (length > 0) {
                    String current = String.valueOf(code1.charAt(0));
                    if (length > 1) {
                        String toSend = code1.substring(1);
                        otp4.setError("only 1 digit required");
                        //otp5.setText(toSend);
                    }
                }

                if (otp4.getText().toString().length() != 0) {
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher codeWatcher5 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code1 = Objects.requireNonNull(otp5.getText()).toString();
                int length = code1.length();

                if (length > 0) {
                    String current = String.valueOf(code1.charAt(0));
                    if (length > 1) {
                        String toSend = code1.substring(1);
                        otp5.setError("only 1 digit required");
                        //otp6.setText(toSend);
                    }
                }

                if (otp5.getText().toString().length() != 0) {
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher codeWatcher6 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String code = Objects.requireNonNull(otp1.getText()).toString().concat(Objects.requireNonNull(otp2.getText()).toString()).concat(Objects.requireNonNull(otp3.getText()).toString()).concat(Objects.requireNonNull(otp4.getText()).toString()).concat(Objects.requireNonNull(otp5.getText()).toString()).concat(Objects.requireNonNull(otp6.getText()).toString());
                int length = code.length();


                if (otp6.getText().toString().length() > 0) {
                    if (otp6.getText().toString().length() > 1) {
                        otp6.setError("only 1 digit required");
                        return;
                    }

                    if (length == 6) {
                        try {
                            confirmCode(code);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(VerificationActivity.this, "Code is " + code, Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        CutCopyPasteEditText.OnCutCopyPasteListener pasteEar = new CutCopyPasteEditText.OnCutCopyPasteListener() {
            @Override
            public void onCut() {

            }

            @Override
            public void onCopy() {

            }

            @Override
            public void onPaste() {
                int length = Objects.requireNonNull(otp1.getText()).toString().length();
                String pastedCode = Objects.requireNonNull(otp1.getText()).toString();

                System.out.println(pastedCode + " pasted code");
                List<String> pastedCodeList = new ArrayList<>();
                for (int i = 0; i < pastedCode.length(); i++) {
                    final String code = String.valueOf(pastedCode.charAt(i)).trim();
                    pastedCodeList.add(code);
                    list[i].setText(code);
                    list[i].setError(null);
                }

                System.out.println("pasted code list is " + pastedCodeList);
            }
        };

        // otp1.setOnCutCopyPasteListener(pasteEar);
        otp1.addTextChangedListener(codeWatcher1);
        otp2.addTextChangedListener(codeWatcher2);
        otp3.addTextChangedListener(codeWatcher3);
        otp4.addTextChangedListener(codeWatcher4);
        otp5.addTextChangedListener(codeWatcher5);
        otp6.addTextChangedListener(codeWatcher6);
    }

    private void confirmCode(String code) {
        showPb();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationIdS, code);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
            hidePb();
            timer.cancel();
            if (task.isSuccessful()) {
                Toast.makeText(VerificationActivity.this, "Successfully verified", Toast.LENGTH_SHORT).show();
                verifyUser();
            } else {
                //Toast.makeText(VerificationActivity.this, "Failed to verify", Toast.LENGTH_SHORT).show();
                timeOut(false);
                tutorialBinding.infoOpt.setVisibility(View.VISIBLE);
                tutorialBinding.infoOpt.setText("Wrong code");
                tutorialBinding.countdownTv.setVisibility(View.VISIBLE);
                tutorialBinding.countdownTv.setText("Click to resend");
                tutorialBinding.countdownTv.setTextColor(Color.RED);
                tutorialBinding.countdownTv.setOnClickListener(v2 -> isSafetyNetEnabled());
            }
        });
    }

    private void timeOut(boolean timedOut) {
        tutorialBinding.optLayout.setVisibility(View.GONE);

        if (timedOut) {
            tutorialBinding.infoOpt.setText("Your OTP has expired \n Click to get a new one");
        }
        hidePb();
    }

    private void checkStatus() {
        tutorialBinding.infoOpt.setText("Checking verification");
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
                        tutorialBinding.infoOpt.setText("Failed to get response");
                        return;
                    }

                    if (jsonResponse.isHas_error() && !jsonResponse.isSuccess()) {
                        Toast.makeText(VerificationActivity.this, "Error getting verification status", Toast.LENGTH_SHORT).show();
                        tutorialBinding.infoOpt.setText("Error getting verification status");

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
                            tutorialBinding.infoOpt.setText("You have not verified your phone number");

                            if (!verificationSent) {
                                verificationSent = true;
                                 isSafetyNetEnabled();
                            }
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
                    tutorialBinding.infoOpt.setText(t.getLocalizedMessage());
                }
            });
        }
    }


    public static void editSingleValue(int inputType, String hint, Activity activity, Function<String, Void> function) {
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.number_dialog);
        d.show();

        EditText field = d.findViewById(R.id.phoneNumberField);
        field.setInputType(inputType);
        field.setHint(hint);

        Button confirm_button = d.findViewById(R.id.confirm_button);
        confirm_button.setOnClickListener(v -> {
            d.dismiss();
            function.apply(field.getText().toString());
        });

        ImageButton cancel = d.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> d.dismiss());

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

        ImageButton cancel = d.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> d.dismiss());

    }

    private void changePhoneNumber(String phoneNumber) {
        showPb();
        userViewModel.updateExistingUser(new Models.UserUpdateForm(phoneNumber,null)).observe(this, user -> {
            hidePb();
            if (!user.isPresent()) {
                Toast.makeText(VerificationActivity.this, "Failed to change phone number", Toast.LENGTH_SHORT).show();
                tutorialBinding.infoOpt.setText("Failed to change phone number");

                return;
            }


            tutorialBinding.infoOpt.setText("Number changed to " + user.get().getPhone_number());
            isSafetyNetEnabled();
        });
    }

    private void showPb() {
        tutorialBinding.tutorialPb.setVisibility(View.VISIBLE);
        isLoading = true;
        invalidateOptionsMenu();
    }

    private void hidePb() {
        tutorialBinding.tutorialPb.setVisibility(View.GONE);
        isLoading = false;
        invalidateOptionsMenu();
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.deep_purple));
        getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}