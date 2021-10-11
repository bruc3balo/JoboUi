package com.example.joboui.serviceProviderUi;

import static com.example.joboui.clientUi.ClientActivity.checkToLogoutUser;
import static com.example.joboui.clientUi.ClientActivity.goToLoginPage;
import static com.example.joboui.globals.GlobalDb.userRepository;
import static com.example.joboui.globals.GlobalVariables.LOGGED_IN;
import static com.example.joboui.globals.GlobalVariables.USER_DB;
import static com.example.joboui.login.SignInActivity.editSp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.joboui.R;
import com.example.joboui.admin.AdminActivity;
import com.example.joboui.databinding.ActivityServiceProviderBinding;
import com.example.joboui.domain.Domain;
import com.example.joboui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class ServiceProviderActivity extends AppCompatActivity {

    private ActivityServiceProviderBinding serviceProviderBinding;
    private Timer loginTimer;

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
            Map<String, Boolean> map = new HashMap<>();
            map.put(LOGGED_IN, false);
            editSp(USER_DB, map, getApplication());
            return false;
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
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
        loginTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkToLogoutUser(ServiceProviderActivity.this, getApplication());
            }
        }, 1000, 1000);
    }

    private void removeListener() {
        loginTimer.cancel();
    }

    private void setWindowColors() {
        getWindow().setStatusBarColor(getColor(R.color.purple));
        getWindow().setNavigationBarColor(getColor(R.color.purple));

    }

}