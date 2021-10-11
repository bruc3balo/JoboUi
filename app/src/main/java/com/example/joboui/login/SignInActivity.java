package com.example.joboui.login;

import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;
import com.example.joboui.R;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivitySignInBinding;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;
import com.example.joboui.utils.JsonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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
            if (validateForm(usernameF, passwordF)) {
                try {
                    postSignInRequest();
                } catch (JSONException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        setWindowColors();

        hidePb();
    }

    public boolean validateForm(EditText usernameF, EditText passwordF) {
        boolean valid = false;
        ;
        if (usernameF.getText().toString().isEmpty()) {
            usernameF.setError("required");
            usernameF.requestFocus();
        } else if (passwordF.getText().toString().isEmpty()) {
            passwordF.setError("required");
            passwordF.requestFocus();
        } else {
            request = new Models.UsernameAndPasswordAuthenticationRequest(usernameF.getText().toString(), passwordF.getText().toString());
            valid = true;
        }
        return valid;
    }

    private void postSignInRequest() throws JSONException, JsonProcessingException {
        Toast.makeText(this, "Sign in ", Toast.LENGTH_SHORT).show();
        showPb();
        RequestQueue queue = Volley.newRequestQueue(this);


        String url = API_URL + CONTEXT_URL + "/login";

        ObjectMapper mapper = getObjectMapper();

        String data = mapper.writeValueAsString(request);
        JSONObject object = new JSONObject(data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, response -> {
            hidePb();
            try {
                Models.LoginResponse loginResponse = mapper.readValue(response.toString(), Models.LoginResponse.class);
                decodeToken(loginResponse.getRefresh_token());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }, error -> {
            hidePb();
            Toast.makeText(this, "Failed to login " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };


        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    private void decodeToken(String token) {
        try {
            JWT jwt = new JWT(token);
            String subject = jwt.getSubject();

            System.out.println(subject);
            System.out.println(token);

            getUser(subject, token);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Token invalid", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUser(String username, String token) {
        String url = API_URL + CONTEXT_URL + "/user/all";
        RequestQueue queue = Volley.newRequestQueue(this);

        ObjectMapper mapper = getObjectMapper();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            hidePb();

            try {
                JsonResponse jsonResponse = mapper.readValue(response.toString(), JsonResponse.class);
                System.out.println("response " + mapper.writeValueAsString(jsonResponse));

                JsonObject userJson = new JsonArray(mapper.writeValueAsString(jsonResponse.getData())).getJsonObject(0);

                //System.out.println("user " + mapper.writeValueAsString(jsonResponse.getData()));
                Models.AppUser user = mapper.readValue(userJson.toString(), Models.AppUser.class);
                System.out.println("myUser " + user.getBio()); //
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, error -> {
            hidePb();
            Toast.makeText(this, "Failed to login " + error, Toast.LENGTH_SHORT).show();
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put(AUTHORIZATION, "Bearer " + token);
                header.put(CONTENT_TYPE, APPLICATION_JSON);
                return header;
            }
        };


        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper = mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper = mapper.findAndRegisterModules();
        return mapper;
    }

    private void showPb() {
        activity_sign_in.signInUserButton.setEnabled(false);
        activity_sign_in.signInPb.setVisibility(View.VISIBLE);
    }

    private void hidePb() {
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