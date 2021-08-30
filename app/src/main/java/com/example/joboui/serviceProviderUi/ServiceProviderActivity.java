package com.example.joboui.serviceProviderUi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityServiceProviderBinding;

public class ServiceProviderActivity extends AppCompatActivity {

    ActivityServiceProviderBinding serviceProviderBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceProviderBinding = ActivityServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(serviceProviderBinding.getRoot());

        Toolbar serviceProviderToolbar = serviceProviderBinding.serviceProviderToolbar;
        setSupportActionBar(serviceProviderToolbar);

        setWindowColors();

    }

    private void setWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.purple));
            getWindow().setNavigationBarColor(getColor(R.color.purple));
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.purple));
        }

    }
    
}