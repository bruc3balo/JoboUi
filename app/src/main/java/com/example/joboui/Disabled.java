package com.example.joboui;

import static com.example.joboui.SplashScreen.addListener;
import static com.example.joboui.SplashScreen.addLogoutListener;
import static com.example.joboui.SplashScreen.removeListener;
import static com.example.joboui.admin.AdminActivity.logout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.joboui.admin.AdminActivity;

public class Disabled extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled);

        setWindowColors();

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(v->logout(getApplication()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
        addLogoutListener(Disabled.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));
    }
}