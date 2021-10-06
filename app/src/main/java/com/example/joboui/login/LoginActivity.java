package com.example.joboui.login;

import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.CLIENT_ROLE;
import static com.example.joboui.globals.GlobalVariables.LOCAL_SERVICE_PROVIDER_ROLE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joboui.R;
import com.example.joboui.admin.AdminActivity;
import com.example.joboui.clientUi.ClientActivity;
import com.example.joboui.databinding.ActivityLoginBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.serviceProviderUi.ServiceProviderActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    /*private final FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            Domain.User user = userRepository.getUser(firebaseAuth.getUid());
            if (user != null) {
                proceed(user.getRole());
            } else {
                user = userRepository.refreshUserDetails(firebaseAuth.getUid());
                if (user != null) {
                    proceed(user.getRole());
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to get user info . Please Sign in", Toast.LENGTH_SHORT).show();
                    goToSignInPage();
                }
            }
        } else {
            Toast.makeText(LoginActivity.this, "Sign in to continue", Toast.LENGTH_SHORT).show();
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        TextView adminSecret = loginBinding.adminSecret;
        adminSecret.setOnLongClickListener(view -> {
            goToAdminPage();
            return false;
        });

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
      ////  FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void removeListener() {
      //  FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
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

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.white));
            getWindow().setNavigationBarColor(getColor(R.color.white));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        }
    }


    private void goToSignInPage() {
        startActivity(new Intent(this, SignInActivity.class));
       // finish();
    }

    private void goToServiceProviderPage() {
        startActivity(new Intent(this, ServiceProviderActivity.class));
       // finish();
    }

    private void goToClientPage() {
        startActivity(new Intent(this, ClientActivity.class));
        finish();
    }

    private void goToRegisterPage() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void goToAdminPage() {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }
}