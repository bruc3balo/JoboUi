package com.example.joboui.login;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.ACCESS_TOKEN;
import static com.example.joboui.globals.GlobalVariables.API_URL;
import static com.example.joboui.globals.GlobalVariables.CONTEXT_URL;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.PASSWORD;
import static com.example.joboui.globals.GlobalVariables.REFRESH_TOKEN;
import static com.example.joboui.globals.GlobalVariables.USERNAME;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.LoginActivity.goToAdminPage;
import static com.example.joboui.login.LoginActivity.goToClientPage;
import static com.example.joboui.login.LoginActivity.goToServiceProviderPage;
import static com.example.joboui.login.LoginActivity.proceed;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;
import com.example.joboui.R;
import com.example.joboui.SplashScreen;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivitySignInBinding;
import com.example.joboui.db.userDb.UserViewModel;
import com.example.joboui.domain.Domain;
import com.example.joboui.model.Models;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SignInActivity extends AppCompatActivity {


    private ActivitySignInBinding activity_sign_in;
    private Models.UsernameAndPasswordAuthenticationRequest request;
    private UserViewModel userViewModel;
    private Timer loginTimer;
    boolean oneTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_sign_in = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(activity_sign_in.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        EditText usernameF = activity_sign_in.usernameF;
        EditText passwordF = activity_sign_in.passwordF;


        Button signInUserButton = activity_sign_in.signInUserButton;
        signInUserButton.setOnClickListener(view -> {
            if (validateForm(usernameF, passwordF)) {
                try {
                    oneTime = false;
                    showPb();
                    userViewModel.getAccessToken(request).observe(this, map -> {
                        if (map == null) {
                            hidePb();
                        } else {
                            if (map.isEmpty()) {
                                hidePb();
                                Toast.makeText(SignInActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                            } else {
                                userViewModel.getUserByUsername(map.get(USERNAME)).observe(SignInActivity.this, appUser -> {
                                    if (appUser != null) {
                                        if (appUser.getRole() != null) {
                                            Toast.makeText(SignInActivity.this, appUser.getUsername(), Toast.LENGTH_SHORT).show();
                                            proceed(appUser.getRole().getName(), SignInActivity.this);
                                        } else {
                                            //todo update role to client // cron does it
                                        }
                                    }
                                });
                            }
                        }
                    });
                } catch (JSONException | JsonProcessingException e) {
                    e.printStackTrace();
                    hidePb();
                }

                try {
                    new Handler().postDelayed(this::hidePb, 5000);
                }catch (Exception ignored) {}
            }
        });

        setWindowColors();

        hidePb();
    }

    public boolean validateForm(EditText usernameF, EditText passwordF) {
        boolean valid = false;
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


    public static JWT decodeToken(String token) {
        try {
            return new JWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public static Map<String, ?> getSp(String name, Application application) {
        SharedPreferences sh = application.getSharedPreferences(name, MODE_PRIVATE);
        return sh.getAll();
    }

    public static void editSp(String name, Map<String, ?> sp, Application application) {
        SharedPreferences sh = application.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        sp.forEach((itemName, item) -> {
            if (item instanceof Boolean) {
                editor.putBoolean(itemName, (Boolean) item);
            } else if (item instanceof String) {
                editor.putString(itemName, (String) item);
            } else if (item instanceof Integer) {
                editor.putInt(itemName, (Integer) item);
            } else if (item instanceof Long) {
                editor.putLong(itemName, (Long) item);
            } else if (item instanceof Float) {
                editor.putFloat(itemName, (Float) item);
            }
        });

        System.out.println(" ==============  SP MAP " + sp + " ================ ");

        editor.apply();
    }

    public static void clearSp(String name, Application application) {
        SharedPreferences sh = application.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.clear();

        System.out.println(" ==============  SP MAP CLEARED ================ ");

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //addListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //removeListener();
    }

    public static void checkToLoginUser(Activity activity, Application application) {

        Map<String, ?> map = getSp(USER_DB, application);

        if (map == null) {
            System.out.println("No user sp");
            return;
        }

        Boolean loggedIn = (Boolean) map.get(LOGGED_IN);
        if (loggedIn == null) {
            System.out.println("No logged in value sp");
            return;
        }

        if (loggedIn) {
            proceed(userRepository.getUser().map(Domain.User::getRole).orElse(null), activity);
        } else {
            System.out.println("User not logged in");
        }
    }


    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.deep_purple));
        getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
    }
}