package com.example.joboui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.example.joboui.databinding.ActivitySplashScreenBinding;
import com.example.joboui.login.LoginActivity;

public class SplashScreen extends AppCompatActivity {

    public static final int SPLASH_WAIT_TIME = 2000;
    private ActivitySplashScreenBinding screenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(screenBinding.getRoot());

        setWindowColors();

        new Handler().postDelayed(this::goToLoginPage,SPLASH_WAIT_TIME);

    }

    private void setWindowColors () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getColor(R.color.deep_purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.deep_purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_purple));
        }

    }

    private void goToLoginPage () {
        startActivity(new Intent(this, LoginActivity.class));
    }
}