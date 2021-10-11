package com.example.joboui.login;

import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.login.SignInActivity.checkToLoginUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.joboui.R;
import com.example.joboui.admin.AdminActivity;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivityLoginBinding;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private Timer loginTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        Button signInButton = loginBinding.signInButton;
        signInButton.setOnClickListener(view -> goToSignInPage());

        Button registerButton = loginBinding.registerButton;
        registerButton.setOnClickListener(view -> goToRegisterPage());

        setWindowColors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void addListener() {
        loginTimer = new Timer();
        loginTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { checkToLoginUser(LoginActivity.this, getApplication()); }}, 1000, 1000);
    }

    private void removeListener() {
        loginTimer.cancel();
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.white));
        getWindow().setNavigationBarColor(getColor(R.color.white));
    }

    private void goToSignInPage() {
        startActivity(new Intent(this, SignInActivity.class));
        // finish();
    }

    private void goToRegisterPage() {
        startActivity(new Intent(this, RegisterActivity.class));
        // finish();
    }

    public static void proceed(String role, Activity activity) {
        if (role == null) {
            directToLogin(activity);
        } else {
            switch (role) {
                case "ROLE_CLIENT":
                    goToClientPage(activity);
                    break;
                case "ROLE_SERVICE_PROVIDER":
                    goToServiceProviderPage(activity);
                    break;

                case "ROLE_ADMIN":
                case "ROLE_ADMIN_TRAINEE":
                    goToAdminPage(activity);
                    break;
            }
        }
    }

    public static void goToServiceProviderPage(Activity activity) {
        activity.startActivity(new Intent(activity, ServiceProviderActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }

    public static void goToClientPage(Activity activity) {
        activity.startActivity(new Intent(activity, ClientActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }

    public static void goToAdminPage(Activity activity) {
        activity.startActivity(new Intent(activity, AdminActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
    }


}