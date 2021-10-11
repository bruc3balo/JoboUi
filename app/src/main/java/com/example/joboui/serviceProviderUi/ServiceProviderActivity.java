package com.example.joboui.serviceProviderUi;

import static com.example.joboui.clientUi.ClientActivity.goToLoginPage;
import static com.example.joboui.globals.GlobalDb.userRepository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.joboui.R;
import com.example.joboui.databinding.ActivityServiceProviderBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.login.LoginActivity;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Logout").setOnMenuItemClickListener(menuItem -> {
            userRepository.deleteUserDb();
            updateUi();
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }


    private void updateUi() {
        Domain.User localUser = userRepository.getUser();
        if (localUser != null) {

        } else {
            goToLoginPage(this);
        }
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
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }

}