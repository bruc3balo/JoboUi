package com.example.joboui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.joboui.R;
import com.example.joboui.admin.AdminActivity;
import com.example.joboui.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;

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
    }

    private void goToRegisterPage() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void goToAdminPage() {
        startActivity(new Intent(this, AdminActivity.class));
    }
}