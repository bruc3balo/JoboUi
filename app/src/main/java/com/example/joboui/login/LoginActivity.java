package com.example.joboui.login;

import static com.example.joboui.SplashScreen.directToLogin;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.HY;
import static com.example.joboui.login.RegisterActivity.goToAdditionalInfoActivity;
import static com.example.joboui.login.RegisterActivity.goToVerificationPage;
import static com.example.joboui.services.NotificationService.notificationServiceRunning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.example.joboui.Disabled;
import com.example.joboui.R;
import com.example.joboui.admin.AdminActivity;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivityLoginBinding;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;
import com.example.joboui.services.NotificationService;


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
                return;
            }


            if (appUser.get().isIs_deleted() || appUser.get().isIs_disabled()) {
                if (!notificationServiceRunning) {
                    activity.startService(new Intent(activity, NotificationService.class));
                }
                goToDisabledPage(activity);
            } else {
                switch (appUser.get().getRole()) {
                    case "ROLE_CLIENT":
                        if (appUser.get().isVerified()) {

                            if (!notificationServiceRunning) {
                                activity.startService(new Intent(activity, NotificationService.class));
                            }
                            goToClientPage(activity);
                        } else {
                            goToVerificationPage(activity);
                        }
                        break;
                    case "ROLE_SERVICE_PROVIDER":

                        boolean hasNoData = appUser.get().getPreferred_working_hours().equals(HY) || appUser.get().getSpecialities().equals(HY) || appUser.get().getPreferred_working_hours().equals("") || appUser.get().getSpecialities().equals("");

                        System.out.println(appUser.get().getPreferred_working_hours() + " :: "+appUser.get().getSpecialities());

                        if (appUser.get().isVerified()) {
                            if (hasNoData) {
                                goToAdditionalInfoActivity(activity);
                            } else {
                                goToServiceProviderPage(activity);
                                if (!notificationServiceRunning) {
                                    activity.startService(new Intent(activity, NotificationService.class));
                                }
                            }
                        } else {
                            goToVerificationPage(activity);
                        }
                        break;

                    case "ROLE_ADMIN":
                    case "ROLE_ADMIN_TRAINEE":
                        if (!notificationServiceRunning) {
                            activity.startService(new Intent(activity, NotificationService.class));
                        }
                        goToAdminPage(activity);
                        break;
                }
            }
        });
    }

    public static void goToDisabledPage(Activity activity) {
        activity.startActivity(new Intent(activity, Disabled.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        activity.finish();
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