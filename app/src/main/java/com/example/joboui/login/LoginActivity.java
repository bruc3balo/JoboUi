package com.example.joboui.login;

import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.login.RegisterActivity.goToAdditionalInfoActivity;
import static com.example.joboui.login.RegisterActivity.goToTutorialsPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.joboui.R;
import com.example.joboui.admin.AdminActivity;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivityLoginBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;

import java.util.Optional;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    public static void proceed(Activity activity) {
        userRepository.getUserLive().observe((LifecycleOwner) activity, appUser -> {
            if (!appUser.isPresent()) {
                System.out.println("COULD NOT PROCEED ... user is not present");
                directToLogin(activity);
            } else {
                switch (appUser.get().getRole()) {
                    case "ROLE_CLIENT":
                        if (appUser.get().isTutorial()) {
                            goToClientPage(activity);
                        } else {
                            goToTutorialsPage(activity);
                        }
                        break;
                    case "ROLE_SERVICE_PROVIDER":

                        if (appUser.get().isTutorial()) {
                            goToServiceProviderPage(activity);
                        } else {
                            if (appUser.get().getPreferred_working_hours() == null || appUser.get().getPreferred_working_hours().isEmpty() || appUser.get().getSpecialities() == null || appUser.get().getSpecialities().isEmpty()) {
                                goToAdditionalInfoActivity(activity);
                            } else {
                                goToTutorialsPage(activity);
                            }
                        }
                        break;

                    case "ROLE_ADMIN":
                    case "ROLE_ADMIN_TRAINEE":
                        goToAdminPage(activity);
                        break;
                }
            }
        });
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